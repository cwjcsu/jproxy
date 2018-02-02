package com.cwjcsu.jproxy.socks;

import com.cwjcsu.jproxy.NettyUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.SocksMessage;
import io.netty.handler.codec.socksx.v4.DefaultSocks4CommandResponse;
import io.netty.handler.codec.socksx.v4.Socks4CommandRequest;
import io.netty.handler.codec.socksx.v4.Socks4CommandStatus;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5AddressType;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 处理socks5协议中的connect请求
 */
public final class SocksServerConnectHandler extends SimpleChannelInboundHandler<SocksMessage> {

    private static final Logger logger = LoggerFactory.getLogger(SocksServerConnectHandler.class);


    private final Bootstrap b = new Bootstrap();

    private SocksConfig socksConfig;

    public SocksServerConnectHandler(SocksConfig socksConfig) {
        this.socksConfig = socksConfig;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final SocksMessage message) throws Exception {
        if (message instanceof Socks4CommandRequest) {
            final Socks4CommandRequest request = (Socks4CommandRequest) message;
            Promise<Channel> promise = ctx.executor().newPromise();
            promise.addListener(
                    new FutureListener<Channel>() {
                        @Override
                        public void operationComplete(final Future<Channel> future) throws Exception {
                            final Channel outboundChannel = future.getNow();
                            if (future.isSuccess()) {
                                if (logger.isTraceEnabled()) {
                                    logger.trace("socks 4 connect success for client {},localAddress is {},target is {}", new Object[]{ctx.channel().remoteAddress(), outboundChannel.localAddress(), request.dstAddr() + ":" + request.dstPort()});
                                }
                                ChannelFuture responseFuture = ctx.channel().writeAndFlush(
                                        new DefaultSocks4CommandResponse(Socks4CommandStatus.SUCCESS));

                                responseFuture.addListener(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture channelFuture) {
                                        ctx.pipeline().remove(SocksServerConnectHandler.this);
                                        outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));
                                        ctx.pipeline().addLast(new RelayHandler(outboundChannel));
                                    }
                                });
                            } else {
                                ctx.channel().writeAndFlush(
                                        new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED));
                                NettyUtil.closeOnFlush(ctx.channel());
                            }
                        }
                    });

            final Channel inboundChannel = ctx.channel();
            b.group(inboundChannel.eventLoop())
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.AUTO_READ, false)
                    .handler(new DirectClientHandler(promise));

            b.connect(request.dstAddr(), request.dstPort()).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        // Connection established use handler provided results
                    } else {
                        // Close the connection if the connection attempt has failed.
                        ctx.channel().writeAndFlush(
                                new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED)
                        );
                        NettyUtil.closeOnFlush(ctx.channel());
                    }
                }
            });
        } else if (message instanceof Socks5CommandRequest) {
            final Socks5CommandRequest request = (Socks5CommandRequest) message;
            Promise<Channel> promise = ctx.executor().newPromise();
            promise.addListener(
                    new FutureListener<Channel>() {
                        @Override
                        public void operationComplete(final Future<Channel> future) throws Exception {
                            final Channel outboundChannel = future.getNow();
                            if (future.isSuccess()) {
                                ctx.channel().config().setAutoRead(false);//socks协议的包接收完成，设置为auto read false
                                if (logger.isTraceEnabled()) {
                                    logger.trace("socks 5 connect success for client {},localAddress is {},target is {}", new Object[]{ctx.channel().remoteAddress(), outboundChannel.localAddress(), request.dstAddrType() + ":" + outboundChannel.remoteAddress()});
                                }
                                InetSocketAddress socketAddress = (InetSocketAddress) outboundChannel.remoteAddress();
                                //README 返回addrType必须为ipv4，因为jre SocksSocketImpl有bug，无法正确处理响应DOMAIN_NAME和IPV6的响应
                                ChannelFuture responseFuture =
                                        ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(
                                                Socks5CommandStatus.SUCCESS,
                                                Socks5AddressType.IPv4,
                                                socketAddress.getAddress().getHostAddress(),//如果用getHostName()，有时候（linux下）会返回域名，从而抛出异常，客户端看到是超时
                                                socketAddress.getPort()));

                                responseFuture.addListener(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture channelFuture) {
                                        ctx.pipeline().remove(SocksServerConnectHandler.this);
                                        outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));
                                        ctx.pipeline().addLast(new RelayHandler(outboundChannel));
                                    }
                                });
                            } else {
                                if (logger.isTraceEnabled()) {
                                    logger.trace("socks connect fail for client {},target is {}", ctx.channel().remoteAddress(), request.dstAddrType() + ":" + request.dstAddr() + ":" + request.dstPort());
                                }
                                ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(
                                        Socks5CommandStatus.FAILURE, request.dstAddrType()));
                                NettyUtil.closeOnFlush(ctx.channel());
                            }
                        }
                    });

            final Channel inboundChannel = ctx.channel();
            b.group(inboundChannel.eventLoop())
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(socksConfig.getTargetWriteBufferLowWaterMark(), socksConfig.getTargetWriteBufferHighWaterMark()))
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, socksConfig.getConnectTimeout())
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(socksConfig.getTargetAdaptiveReceiveBufferMinSize(), socksConfig.getTargetAdaptiveReceiveBufferIniSize(), socksConfig.getAdaptiveReceiveBufferMaxSize()))
                    .option(ChannelOption.SO_SNDBUF, socksConfig.getTargetTcpSendBufferSize())
                    .option(ChannelOption.SO_RCVBUF, socksConfig.getTargetTcpReceiveBufferSize())
                    .handler(new DirectClientHandler(promise));

            if (socksConfig.getLocalBindAddress() != null && !socksConfig.getLocalBindAddress().equalsIgnoreCase("0.0.0.0")) {
                b.localAddress(new InetSocketAddress(this.socksConfig.getLocalBindAddress(), 0));
            }

            b.connect(request.dstAddr(), request.dstPort()).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        // Connection established use handler provided results
                    } else {
                        // Close the connection if the connection attempt has failed.
                        ctx.channel().writeAndFlush(
                                new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, request.dstAddrType()));
                        NettyUtil.closeOnFlush(ctx.channel());
                    }
                }
            });
        } else {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NettyUtil.closeOnFlush(ctx.channel());
    }
}

package com.cwjcsu.jproxy.socks;

import com.cwjcsu.jproxy.NettyUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RelayHandler extends ChannelInboundHandlerAdapter {

    private final Channel relayChannel;

    private static Logger logger = LoggerFactory.getLogger(RelayHandler.class);

    public RelayHandler(Channel relayChannel) {
        relayChannel.config().setAutoRead(false);
        this.relayChannel = relayChannel;
        this.relayChannel.read();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (relayChannel.isActive()) {
            relayChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        future.channel().close();
                    }
                }
            });
        } else {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (relayChannel.isActive()) {
            NettyUtil.closeOnFlush(relayChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("socks server error on channel {},relayChannel is {} :{}", new Object[]{ctx.channel(), relayChannel, cause});
        ctx.close();
    }
}

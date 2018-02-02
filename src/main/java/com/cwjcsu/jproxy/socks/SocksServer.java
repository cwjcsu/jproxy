/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.cwjcsu.jproxy.socks;

import com.cwjcsu.common.NamedThreadFactory;
import com.cwjcsu.jproxy.NettyUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;

public final class SocksServer {

    private static Logger logger = LoggerFactory.getLogger(SocksServer.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private SocksServerConfig socksConfig;
    private Channel channel;
    private boolean shutdownWorkerGroup = false;
    private InetSocketAddress bindAddr;

    public SocksServer(SocksServerConfig socksConfig) {
        this(new NioEventLoopGroup(4, new NamedThreadFactory("NioWorkerSocksServer")), socksConfig);
        this.shutdownWorkerGroup = true;
    }


    public SocksServer(EventLoopGroup bossGroup, EventLoopGroup workerGroup, SocksServerConfig socksConfig) {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.socksConfig = socksConfig;
    }

    public SocksServer(EventLoopGroup workerGroup, SocksServerConfig socksConfig) {
        this(new NioEventLoopGroup(1, new NamedThreadFactory("NioBossSocksServer")), workerGroup, socksConfig);
    }

    public synchronized void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(socksConfig.getWriteBufferLowWaterMark(), socksConfig.getWriteBufferHighWaterMark()))
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(socksConfig.getAdaptiveReceiveBufferMinSize(), socksConfig.getAdaptiveReceiveBufferIniSize(), socksConfig.getAdaptiveReceiveBufferMaxSize()))
                .option(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new SocksServerInitializer(socksConfig));
        if (socksConfig.getBindAddress() != null) {
            bindAddr = new InetSocketAddress(socksConfig.getBindAddress(), socksConfig.getBindPort());
        } else {
            bindAddr = new InetSocketAddress(socksConfig.getBindPort());
        }
        channel = b.bind(bindAddr).sync().channel();
        logger.info("Socks server {} is started.", bindAddr);
    }

    public synchronized void stop() {
        if (channel != null) {
            NettyUtil.closeChannel(channel, true);
            channel = null;
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
            bossGroup = null;
        }
        if (workerGroup != null && shutdownWorkerGroup) {
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }
        logger.info("Socks server {} is stopped.", bindAddr);
    }

    public void sync() {
        Channel channel = null;
        synchronized (this) {
            channel = this.channel;
        }
        if (channel != null) {
            try {
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
            }
        }
    }

    public static void main(String[] args) {
        SocksServerConfig socksConfig = new SocksServerConfig();
        socksConfig.setBindPort(1080);
//        socksConfig.setAuthentication(new PasswordAuthentication("admin", "123456".toCharArray()));
        SocksServer socksServer = new SocksServer(socksConfig);
        try {
            socksServer.start();
            socksServer.sync();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

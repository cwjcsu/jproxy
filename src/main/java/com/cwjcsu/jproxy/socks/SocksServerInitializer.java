package com.cwjcsu.jproxy.socks;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;

public final class SocksServerInitializer extends ChannelInitializer<SocketChannel> {
    private SocksServerConfig socksConfig;

    public SocksServerInitializer(SocksServerConfig socksConfig) {
        this.socksConfig = socksConfig;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {

        ch.pipeline().addLast(
                new SocksPortUnificationServerHandler(),
                new SocksServerHandler(socksConfig));
    }
}

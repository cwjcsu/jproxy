/*$Id: $
 --------------------------------------
  Skybility
 ---------------------------------------
  Copyright By Skybility ,All right Reserved
 * author   date   comment
 * jeremy  2012-12-17  Created
 */
package com.cwjcsu.jproxy;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.ReferenceCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class NettyUtil {
	private static Logger logger = LoggerFactory.getLogger(NettyUtil.class);

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    public static void closeChannel(Channel ch) {
        closeChannel(ch, false);
    }

    public static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

	public static void closeChannel(final Channel ch, boolean force){
		if(ch != null){
			if(force){
				try{
                    if(ch.isActive()) {
                        logger.debug("closing channel " + ch);
                        ch.close();
                    }
				} catch (Throwable e){
				}
			} else {
				if (ch.isActive() && ch.isWritable()) {
                    try {
                        logger.debug("flush and closing channel " + ch);
                        ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                    } catch (Throwable e){
                    }
				} else {
                    try {
                        if(ch.isActive()) {
                            logger.debug("closing channel " + ch);
                            ch.close();
                        }
                    } catch (Throwable e){
                    }
				}
			}
		}
	}

    public static InetAddress getInetSocketAddress(SocketAddress socketAddress) {
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress sa = (InetSocketAddress) socketAddress;
            return sa.getAddress();
        }
        return null;
    }

    public static boolean release(Object msg) {
        if (msg instanceof ReferenceCounted) {
            ReferenceCounted rc = (ReferenceCounted) msg;
            if (rc.refCnt() > 0) {
                return rc.release(rc.refCnt());
            }
            return false;
        }
        return false;
    }
}

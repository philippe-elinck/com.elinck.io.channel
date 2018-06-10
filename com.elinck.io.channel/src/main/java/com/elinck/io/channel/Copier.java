package com.elinck.io.channel;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class Copier {

	public final static int copy(final SocketChannel from, final SocketChannel to) {
		int numberOfCopiedBytes;
		if (from != null && to != null && from.isOpen() && to.isOpen()) {
			try {
				final ByteBuffer bb = ByteBuffer.allocateDirect(ProxyConfig.BYTE_BUFFER_SIZE);
				numberOfCopiedBytes = from.read(bb);
				if (numberOfCopiedBytes > 0) {
					bb.flip();
					to.write(bb);
				}
			} catch (final Exception e) {
				numberOfCopiedBytes = -1;
				System.out.println(e.getMessage());
			}
		} else {
			numberOfCopiedBytes = -1;
		}
		return numberOfCopiedBytes;
	}

}

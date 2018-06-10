package com.elinck.io.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class Handler {
	//
	public final static String TO_HOST = ProxyConfig.TO_HOST;
	public final static int TO_PORT = ProxyConfig.TO_PORT;
	//
	private OutgoingChannels _outgoingChannels = new OutgoingChannels();
	private final Tunnels _tunnels = new Tunnels();

	public Handler() throws IOException {
	}

	public final void handleIncomingChannelAccept(final SocketChannel incomingChannel) throws Exception {
		final SocketChannel outgoingChannel = SocketChannel.open(new InetSocketAddress(TO_HOST, TO_PORT));
		outgoingChannel.configureBlocking(false);
		outgoingChannel.finishConnect();
		this._outgoingChannels.registerForReading(outgoingChannel);
		this._tunnels.addTunnel(incomingChannel, outgoingChannel);
	}

	public final void handleIncomingChannelRead(final IncomingChannels incomingChannels,
			final SelectionKey incomingChannelKey) throws IOException {
		final SocketChannel incomingChannel = (SocketChannel) incomingChannelKey.channel();
		if (incomingChannel != null) {
			final SocketChannel outgoingChannel = this._tunnels.getOutgoingChannelByIncomingChannel(incomingChannel);
			if (outgoingChannel != null) {
				final int read = Copier.copy(incomingChannel, outgoingChannel);
				if (read < 0) {
					// this._tunnels.closeTunnelIncomingChannel(incomingChannel);
				} else if (read >= 0) {
					incomingChannels.registerForReading(incomingChannel);
				}
			}
		}
	}

	public final void handleOutgoingChannelReads() throws IOException {
		if (this._outgoingChannels.hasReadableChannels()) {
			final Iterator<SelectionKey> iter = this._outgoingChannels.iterator();
			while (iter.hasNext()) {
				final SelectionKey outgoingChannelKey = iter.next();
				iter.remove();

				if (outgoingChannelKey.isValid() && outgoingChannelKey.isReadable()) {
					this.handleOutgoingChannelRead(outgoingChannelKey);
				}
			}
		}
	}

	public final void handleOutgoingChannelRead(final SelectionKey outgoingChannelKey) throws IOException {
		final SocketChannel outgoingChannel = (SocketChannel) outgoingChannelKey.channel();
		if (outgoingChannel != null) {
			final SocketChannel incomingChannel = this._tunnels.getIncomingChannelByOutgoingChannel(outgoingChannel);
			if (incomingChannel != null) {
				final int read = Copier.copy(outgoingChannel, incomingChannel);
				if (read < 0) {
					this._tunnels.closeTunnel(incomingChannel);
				}
			}
		}
	}

}

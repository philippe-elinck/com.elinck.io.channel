package com.elinck.io.channel;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Tunnels implements Iterable<SocketChannel> {

	private final Map<SocketChannel, SocketChannel> _incomingToOutgoing = new HashMap<>();
	private final Map<SocketChannel, SocketChannel> _outgoingToIncoming = new HashMap<>();

	public Tunnels() {
	}

	public final void addTunnel(final SocketChannel incomingChannel, final SocketChannel outgoingChannel) {
		this._incomingToOutgoing.put(incomingChannel, outgoingChannel);
		this._outgoingToIncoming.put(outgoingChannel, incomingChannel);
	}

	public final SocketChannel getOutgoingChannelByIncomingChannel(final SocketChannel incomingChannel) {
		return this._incomingToOutgoing.get(incomingChannel);
	}

	public final SocketChannel getIncomingChannelByOutgoingChannel(final SocketChannel outgoingChannel) {
		return this._outgoingToIncoming.get(outgoingChannel);
	}

	public final void closeTunnel(final SocketChannel incomingChannel) {
		final SocketChannel outgoingChannel = this.getOutgoingChannelByIncomingChannel(incomingChannel);
		this.closeTunnelIncomingChannel(incomingChannel);
		this.closeTunnelOutgoingChannel(outgoingChannel);
	}

	public final void closeTunnelIncomingChannel(final SocketChannel incomingChannel) {
		this.closeChannel(this._incomingToOutgoing, incomingChannel);
	}

	public final void closeTunnelOutgoingChannel(final SocketChannel outgoingChannel) {
		this.closeChannel(this._outgoingToIncoming, outgoingChannel);
	}

	private final void closeChannel(final Map<SocketChannel, SocketChannel> map, final SocketChannel channel) {
		map.remove(channel);
		try {
			channel.close();
		} catch (final IOException e) {
			System.out.println("closeChannel ERROR");
		}
	}

	@Override
	public final Iterator<SocketChannel> iterator() {
		return this._incomingToOutgoing.keySet().iterator();
	}

}

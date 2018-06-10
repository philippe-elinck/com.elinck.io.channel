package com.elinck.io.channel;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class OutgoingChannels implements Iterable<SelectionKey> {

	private final Selector _selector = Selector.open();

	public OutgoingChannels() throws IOException {
	}

	@Override
	public final Iterator<SelectionKey> iterator() {
		return this._selector.selectedKeys().iterator();
	}

	public final boolean hasReadableChannels() throws IOException {
		return this._selector.select(1) > 0;
	}

	public final void registerForReading(final SocketChannel outgoingChannel) throws IOException {
		outgoingChannel.register(this._selector, SelectionKey.OP_READ);
	}

}

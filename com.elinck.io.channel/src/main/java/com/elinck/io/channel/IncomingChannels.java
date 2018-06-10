package com.elinck.io.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class IncomingChannels implements Iterable<SelectionKey> {

	private final Selector _selector = Selector.open();
	private final ServerSocketChannel _serverSocketChannel = ServerSocketChannel.open();

	public IncomingChannels() throws IOException {
		this._serverSocketChannel.configureBlocking(false);
		this._serverSocketChannel.socket().bind(new InetSocketAddress(ProxyConfig.FROM_HOST, ProxyConfig.FROM_PORT));
		this._serverSocketChannel.register(this._selector, SelectionKey.OP_ACCEPT);
	}

	@Override
	public final Iterator<SelectionKey> iterator() {
		final Iterator<SelectionKey> result = this._selector.selectedKeys().iterator();
		return result;
	}

	public final boolean hasAcceptableChannels() throws IOException {
		return this._selector.select(1) > 0;
	}

	public final SocketChannel accept(final SelectionKey key) throws IOException {
		SocketChannel result = null;
		if (key.isValid() && key.isAcceptable()) {
			result = this._serverSocketChannel.accept();
			result.configureBlocking(false);
			result.finishConnect();
			System.out.println("Accept remote connection from " + result.socket().getRemoteSocketAddress().toString());
		}
		return result;
	}

	public final void registerForReading(final SocketChannel incomingChannel) throws IOException {
		incomingChannel.register(this._selector, SelectionKey.OP_READ);
	}

}

package com.elinck.io.channel;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class Server {
	//
	public final static String FROM_HOST = ProxyConfig.FROM_HOST;
	public final static int FROM_PORT = ProxyConfig.FROM_PORT;
	//
	private final IncomingChannels _incomingChannels = new IncomingChannels();
	private final Handler _handler;

	public Server(final Handler handler) throws IOException {
		this._handler = handler;
	}

	public final void serve() {

		try {

			while (true) {
				if (this._incomingChannels.hasAcceptableChannels()) {
					final Iterator<SelectionKey> iter = this._incomingChannels.iterator();
					while (iter.hasNext()) {
						final SelectionKey incomingChannelKey = iter.next();
						iter.remove();

						if (incomingChannelKey.isValid() && incomingChannelKey.isAcceptable()) {
							final SocketChannel incomingChannel = this._incomingChannels.accept(incomingChannelKey);

							try {
								this._handler.handleIncomingChannelAccept(incomingChannel);
								this._incomingChannels.registerForReading(incomingChannel);
							} catch (final Exception e) {
								System.out.println("ERROR");
								incomingChannel.close();
								break;
							}
							incomingChannelKey.interestOps(SelectionKey.OP_ACCEPT);
						}
						if (incomingChannelKey.isValid() && incomingChannelKey.isReadable()) {
							this._handler.handleIncomingChannelRead(this._incomingChannels, incomingChannelKey);
						}
					}
				}
				this._handler.handleOutgoingChannelReads();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

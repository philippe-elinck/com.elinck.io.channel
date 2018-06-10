package com.elinck.io.channel;

import java.io.IOException;

public class MAIN {

	public final static void main(final String[] args) throws IOException {
		final Handler handler = new Handler();
		new Server(handler).serve();
	}

}

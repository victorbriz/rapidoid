package org.rapidoid.net;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.rapidoid.net.impl.TCPClientBuilder;
import org.rapidoid.net.impl.Protocol;
import org.rapidoid.net.impl.RapidoidClientLoop;
import org.rapidoid.net.impl.RapidoidServerLoop;
import org.rapidoid.net.impl.TCPServerBuilder;
import org.rapidoid.util.U;

public class TCP {

	public static TCPServerBuilder server() {
		return U.builder(TCPServerBuilder.class, TCPServer.class, RapidoidServerLoop.class);
	}

	public static TCPServer listen(Protocol protocol) {
		TCPServer server = TCP.server().protocol(protocol).build();
		server.start();
		return server;
	}

	public static TCPClientBuilder client() {
		return U.builder(TCPClientBuilder.class, TCPClient.class, RapidoidClientLoop.class);
	}

	public static TCPClient connect(String host, int port, Protocol protocol) {
		TCPClient client = TCP.client().host(host).port(port).protocol(protocol).build();
		client.start();
		return client;
	}

}
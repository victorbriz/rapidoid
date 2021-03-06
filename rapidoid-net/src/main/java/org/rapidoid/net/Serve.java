package org.rapidoid.net;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.net.impl.RapidoidServerLoop;
import org.rapidoid.net.impl.TCPServerBuilder;
import org.rapidoid.wire.Wire;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class Serve {

	public static TCPServerBuilder server() {
		return Wire.builder(TCPServerBuilder.class, TCPServer.class, RapidoidServerLoop.class);
	}

	public static TCPServer listen(Protocol protocol) {
		TCPServer server = Serve.server().protocol(protocol).build();
		server.start();
		return server;
	}

}

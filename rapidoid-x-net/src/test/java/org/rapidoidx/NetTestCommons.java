package org.rapidoidx;

/*
 * #%L
 * rapidoid-x-net
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.UTILS;
import org.rapidoidx.net.Protocol;
import org.rapidoidx.net.TCP;
import org.rapidoidx.net.TCPServer;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public abstract class NetTestCommons extends TestCommons {

	protected void server(Protocol protocol, Runnable client) {
		TCPServer server = TCP.listen(protocol);

		UTILS.sleep(300);
		System.out.println("----------------------------------------");

		try {
			client.run();
		} finally {
			server.shutdown();
			UTILS.sleep(300);
			System.out.println("--- SERVER STOPPED ---");
		}

		Log.info("server finished");
	}

}

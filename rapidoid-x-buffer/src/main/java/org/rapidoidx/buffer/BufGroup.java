package org.rapidoidx.buffer;

/*
 * #%L
 * rapidoid-x-buffer
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

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.pool.ArrayPool;
import org.rapidoid.pool.Pool;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class BufGroup {

	private final int factor;

	private final int capacity;

	private final Pool<ByteBuffer> pool;

	public BufGroup(int factor) {
		this.factor = factor;
		this.capacity = (int) Math.pow(2, factor);

		pool = new ArrayPool<ByteBuffer>(new Callable<ByteBuffer>() {
			@Override
			public ByteBuffer call() {
				return ByteBuffer.allocateDirect(capacity);
			}
		}, 1000);
	}

	public Buf newBuf(String name) {
		return new MultiBuf(pool, factor, name);
	}

	public Buf newBuf() {
		return newBuf("no-name");
	}

	public Buf from(String s, String name) {
		return from(ByteBuffer.wrap(s.getBytes()), name);
	}

	public Buf from(ByteBuffer bbuf, String name) {
		Buf buf = newBuf(name);
		buf.append(bbuf);
		return buf;
	}

	public int instances() {
		return pool.instances();
	}

}

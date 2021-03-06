package org.rapidoid.net.impl;

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

import java.util.Random;
import java.util.concurrent.Callable;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.Ranges;
import org.rapidoid.pool.ArrayPool;
import org.rapidoid.pool.Pool;
import org.rapidoid.wrap.BoolWrap;
import org.rapidoid.wrap.CharWrap;
import org.rapidoid.wrap.DoubleWrap;
import org.rapidoid.wrap.FloatWrap;
import org.rapidoid.wrap.IntWrap;
import org.rapidoid.wrap.LongWrap;
import org.rapidoid.wrap.ShortWrap;

/**
 * Helpers are instantiated per worker node (for thread-safe use), so they contain various data structures that can be
 * used as temporary data holders when implementing protocols, to avoid instantiating objects for each protocol
 * execution.
 */
@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class RapidoidHelper {

	public final Random RND = new Random();

	public final byte[] bytes = new byte[100 * 1024];

	public final byte[] bytes128 = new byte[128];

	public final KeyValueRanges pairs = new KeyValueRanges(100);

	public final Ranges ranges1 = new Ranges(100);

	public final Ranges ranges2 = new Ranges(100);

	public final Ranges ranges3 = new Ranges(100);

	public final Ranges ranges4 = new Ranges(100);

	public final Ranges ranges5 = new Ranges(100);

	public final BoolWrap[] booleans = new BoolWrap[100];

	public final ShortWrap[] shorts = new ShortWrap[100];

	public final CharWrap[] chars = new CharWrap[100];

	public final IntWrap[] integers = new IntWrap[100];

	public final LongWrap[] longs = new LongWrap[100];

	public final FloatWrap[] floats = new FloatWrap[100];

	public final DoubleWrap[] doubles = new DoubleWrap[100];

	public final Pool<?> pool;

	public final Object exchange;

	public RapidoidHelper() {
		this(null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RapidoidHelper(final Class<?> exchangeClass) {

		for (int i = 0; i < booleans.length; i++) {
			booleans[i] = new BoolWrap();
		}

		for (int i = 0; i < shorts.length; i++) {
			shorts[i] = new ShortWrap();
		}

		for (int i = 0; i < chars.length; i++) {
			chars[i] = new CharWrap();
		}

		for (int i = 0; i < integers.length; i++) {
			integers[i] = new IntWrap();
		}

		for (int i = 0; i < longs.length; i++) {
			longs[i] = new LongWrap();
		}

		for (int i = 0; i < floats.length; i++) {
			floats[i] = new FloatWrap();
		}

		for (int i = 0; i < doubles.length; i++) {
			doubles[i] = new DoubleWrap();
		}

		if (exchangeClass != null) {
			exchange = Cls.newInstance(exchangeClass);
			pool = new ArrayPool(new Callable() {
				@Override
				public Object call() throws Exception {
					return Cls.newInstance(exchangeClass);
				}
			}, 1000);
		} else {
			exchange = null;
			pool = null;
		}
	}

	public String randomSHA512() {
		RND.nextBytes(bytes128);
		return Crypto.sha512(bytes128);
	}

}

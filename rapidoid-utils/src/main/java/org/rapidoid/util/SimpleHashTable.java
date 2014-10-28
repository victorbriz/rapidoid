package org.rapidoid.util;

/*
 * #%L
 * rapidoid-utils
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

public class SimpleHashTable<T> {

	public final SimpleList<T>[] base;

	@SuppressWarnings("unchecked")
	public SimpleHashTable(int width) {
		base = new SimpleList[width];
	}

	public void put(long key, T value) {
		int hash = index(key);

		if (base[hash] == null) {
			base[hash] = new SimpleList<T>(5);
		}

		base[hash].add(value);
	}

	public SimpleList<T> get(long key) {
		return base[index(key)];
	}

	private int index(long key) {
		return (int) (Math.abs(key) % base.length);
	}

}
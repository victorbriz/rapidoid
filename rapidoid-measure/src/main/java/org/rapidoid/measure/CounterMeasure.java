package org.rapidoid.measure;

/*
 * #%L
 * rapidoid-measure
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

import java.util.concurrent.atomic.AtomicInteger;

public class CounterMeasure implements Measure {

	private AtomicInteger counter = new AtomicInteger();

	@Override
	public void reset() {
		counter.set(0);
	}

	@Override
	public String get() {
		return counter.get() + "";
	}

	public void increment() {
		counter.incrementAndGet();
	}

	public void add(int delta) {
		counter.addAndGet(delta);
	}

}
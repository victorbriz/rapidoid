package org.rapidoid.measure;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.UTILS;

/*
 * #%L
 * rapidoid-measure
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class MeasuresThread extends Thread {

	private final Measures statistics;

	private String lastInfo = "";

	public MeasuresThread(Measures statistics) {
		this.statistics = statistics;
	}

	@Override
	public void run() {
		try {
			while (true) {
				String info = statistics.info();
				if (!lastInfo.equals(info)) {
					System.out.println(UTILS.getCpuMemStats() + " " + info);
					lastInfo = info;
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			System.out.println("Stats Exception!");
			e.printStackTrace();
		}
	}

}

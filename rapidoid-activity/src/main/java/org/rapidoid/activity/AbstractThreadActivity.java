package org.rapidoid.activity;

import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-activity
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

public abstract class AbstractThreadActivity<T> extends LifecycleActivity<T> implements Runnable {

	protected final Thread thread;

	public AbstractThreadActivity(String name) {
		super(name);

		this.thread = new Thread(this, name);
	}

	@Override
	public T start() {
		checkActive(false);
		thread.start();
		return super.start();
	}

	@SuppressWarnings("deprecation")
	@Override
	public T halt() {
		checkActive(true);
		thread.stop();
		U.joinThread(thread);
		return super.halt();
	}

	@Override
	public T shutdown() {
		checkActive(true);
		thread.interrupt();
		U.joinThread(thread);
		return super.shutdown();
	}

	@Override
	public final void run() {
		U.info("Starting activity thread", "name", name);

		try {
			while (!Thread.interrupted()) {
				try {
					loop();
				} catch (Exception e) {
					U.error("Worker processing error!", "activity", name, "error", e);
				}
			}

		} catch (ThreadDeath e) {
			U.info("Halted activity thread", "name", name);
			return;
		}

		U.info("Finished activity thread", "name", name);
	}

	protected abstract void loop();

}
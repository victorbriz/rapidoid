package org.rapidoid.pages.impl;

/*
 * #%L
 * rapidoid-pages
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

import org.rapidoid.var.Var;

public class BuiltInCmdHandler {

	public void on_set(Var<Integer> var, Integer value) {
		var.set(value);
	}

	public void on_inc(Var<Integer> var, Integer value) {
		var.set(var.get() + value);
	}

	public void on_dec(Var<Integer> var, Integer value) {
		var.set(var.get() - value);
	}

}
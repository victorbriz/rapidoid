package org.rapidoid.pages.html;

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

import org.rapidoid.pages.Tag;
import org.rapidoid.pages.Var;
import org.rapidoid.pages.impl.TagData;
import org.rapidoid.pages.impl.TagInterceptor;
import org.rapidoid.pages.impl.VarImpl;

public class Tags {

	public Object _(String multiLanguageText) {
		// TODO implement internationalization
		return var(multiLanguageText);
	}

	public <T> Var<T> var(T value) {
		return new VarImpl<T>(value);
	}

	public <TAG extends Tag<?>> TAG tag(Class<TAG> clazz, String tagName, Object... contents) {
		return TagInterceptor.create(new TagData<TAG>(clazz, tagName, contents), clazz);
	}

{{tags}}
}
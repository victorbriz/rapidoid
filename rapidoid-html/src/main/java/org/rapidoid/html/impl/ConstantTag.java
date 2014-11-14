package org.rapidoid.html.impl;

/*
 * #%L
 * rapidoid-html
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

import org.rapidoid.html.Tag;
import org.rapidoid.html.TagProcessor;

public class ConstantTag extends UndefinedTag<Tag<?>> {

	private static final long serialVersionUID = -1980930037247789495L;

	private final String code;

	private final byte[] bytes;

	public ConstantTag(String code) {
		this.code = code;
		this.bytes = code.getBytes();
	}

	@Override
	public Tag<?> copy() {
		return this;
	}

	public void traverse(TagProcessor<Tag<?>> processor) {
	}

	@Override
	public String tagKind() {
		return "constant";
	}

	public byte[] bytes() {
		return bytes;
	}

	@Override
	public String toString() {
		return code;
	}

}
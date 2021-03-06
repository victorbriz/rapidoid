package org.rapidoid.widget;

/*
 * #%L
 * rapidoid-widget
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

import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.html.tag.TableTag;

@Authors("Nikolche Mihajlovski")
@Since("2.4.0")
public class KeyValueGridWidget extends AbstractWidget {

	private Map<?, ?> map;

	@Override
	protected Object render() {
		TableTag tbl = table_(tr(th("Key"), th("Value")));

		for (Entry<?, ?> e : map.entrySet()) {
			tbl = tbl.append(tr(td(e.getKey()), td(e.getValue())));
		}

		return tbl;
	}

	public Map<?, ?> map() {
		return map;
	}

	public KeyValueGridWidget map(Map<?, ?> map) {
		this.map = map;
		return this;
	}

}

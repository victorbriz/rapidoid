package org.rapidoid.util;

/*
 * #%L
 * rapidoid-commons
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

import java.util.Map;

import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

public class JSONTest extends TestCommons {

	@Test
	public void json() {
		Person p = new Person("john", 25);

		String json = JSON.stringify(p);
		System.out.println(json);

		Person p2 = JSON.parse(json, Person.class);
		eq(p2.name, p.name);
		eq(p2.age, p.age);

		Map<String, ?> map = JSON.parse(json, Map.class);
		eq(map.get("name"), p.name);
		eq(map.get("age"), p.age);
	}

}
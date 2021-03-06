package org.rapidoid.anyobj;

/*
 * #%L
 * rapidoid-anyobj
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

import java.util.Collection;

import org.rapidoid.arr.Arr;
import org.rapidoid.util.U;

/**
 * @author Nikolche Mihajlovski
 * @since 3.1.0
 */
public class AnyObj {

	public static boolean contains(Object arrOrColl, Object value) {
		if (arrOrColl instanceof Object[]) {
			Object[] arr = (Object[]) arrOrColl;
			return Arr.indexOf(arr, value) >= 0;
		} else if (arrOrColl instanceof Collection<?>) {
			Collection<?> coll = (Collection<?>) arrOrColl;
			return coll.contains(value);
		} else {
			throw U.illegalArg("Expected array or collection, but found: %s", U.readable(arrOrColl));
		}
	}

	@SuppressWarnings("unchecked")
	public static Object include(Object arrOrColl, Object item) {
		if (arrOrColl instanceof Object[]) {
			Object[] arr = (Object[]) arrOrColl;
			return Arr.indexOf(arr, item) < 0 ? Arr.expand(arr, item) : arr;
		} else if (arrOrColl instanceof Collection<?>) {
			Collection<Object> coll = (Collection<Object>) arrOrColl;
			if (!coll.contains(item)) {
				coll.add(item);
			}
			return coll;
		} else {
			throw U.illegalArg("Expected array or collection!");
		}
	}

	@SuppressWarnings("unchecked")
	public static Object exclude(Object arrOrColl, Object item) {
		if (arrOrColl instanceof Object[]) {
			Object[] arr = (Object[]) arrOrColl;
			int ind = Arr.indexOf(arr, item);
			return ind >= 0 ? Arr.deleteAt(arr, ind) : arr;
		} else if (arrOrColl instanceof Collection<?>) {
			Collection<Object> coll = (Collection<Object>) arrOrColl;
			if (coll.contains(item)) {
				coll.remove(item);
			}
			return coll;
		} else {
			throw U.illegalArg("Expected array or collection!");
		}
	}

}

package org.rapidoid.var;

import java.util.Collection;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.var.impl.ArrayContainerVar;
import org.rapidoid.var.impl.CollectionContainerVar;
import org.rapidoid.var.impl.EqualityVar;
import org.rapidoid.var.impl.MandatoryVar;
import org.rapidoid.var.impl.SimpleVar;

/*
 * #%L
 * rapidoid-var
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
public class Vars {

	public static <T> Var<T> var(String name, T value) {
		return new SimpleVar<T>(name, value);
	}

	@SuppressWarnings("unchecked")
	public static <T> Var<T>[] vars(String name, T... values) {
		Var<T>[] vars = new Var[values.length];

		for (int i = 0; i < vars.length; i++) {
			vars[i] = var(name + "[" + i + "]", values[i]);
		}

		return vars;
	}

	@SuppressWarnings("unchecked")
	public static Var<Boolean> eq(Var<?> var, Object value) {
		return new EqualityVar(var.name() + " == " + value, (Var<Object>) var, value);
	}

	@SuppressWarnings("unchecked")
	public static Var<Boolean> has(Var<?> container, Object item) {
		Object arrOrColl = container.get();

		String name = container.name() + "[" + item + "]";
		if (arrOrColl instanceof Collection) {
			return new CollectionContainerVar(name, (Var<Collection<Object>>) container, item);
		} else {
			return new ArrayContainerVar(name, (Var<Object>) container, item);
		}
	}

	public static <T> Var<T> mandatory(Var<T> var) {
		return new MandatoryVar<T>(var.name(), var);
	}

	@SuppressWarnings("unchecked")
	public static <T> T unwrap(T value) {
		return (value instanceof Var) ? (T) ((Var<?>) value).get() : value;
	}

	@SuppressWarnings("unchecked")
	public static <T> Var<T> cast(Object value) {
		return (Var<T>) value;
	}

}

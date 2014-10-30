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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.rapidoid.pages.Action;
import org.rapidoid.pages.Handler;
import org.rapidoid.pages.Tag;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

public class TagInterceptor<TAG extends Tag<?>> implements InvocationHandler {

	public static <TAG extends Tag<?>> TAG create(TagData<TAG> tag, Class<TAG> clazz) {
		return Cls.implement(tag, new TagInterceptor<TAG>(tag, clazz), clazz);
	}

	private final TagData<TAG> tag;

	private final Class<?> clazz;

	public TagInterceptor(TagData<TAG> tag, Class<TAG> clazz) {
		this.tag = tag;
		this.clazz = clazz;
	}

	@SuppressWarnings("unchecked")
	public Object invoke(Object target, Method method, Object[] args) throws Throwable {

		String name = method.getName();
		Class<?> ret = method.getReturnType();
		Class<?>[] paramTypes = method.getParameterTypes();

		// str
		if (name.equals("str") && paramTypes.length == 1 && paramTypes[0].equals(int.class)) {
			return TagRenderer.str(tag, (Integer) args[0]);
		}

		// // action
		// if (Handler.class.isAssignableFrom(ret) && paramTypes.length == 0) {
		// Handler action = tag.handler(name);
		// if (action == null) {
		// action = newAction(tag, name, ret);
		// tag.setHandler(name, action);
		// }
		//
		// return action;
		// }

		boolean returnsTag = ret.isAssignableFrom(clazz);

		if (name.startsWith("on") && name.length() > 2) {

			String event = name.substring(2).toLowerCase();

			// return target; // FIXME
			// handlers setter
			if (returnsTag && paramTypes.length == 1 && Handler.class.isAssignableFrom(paramTypes[0])) {
				tag.setHandler(event, (Handler<TAG>) args[0]);
				return target;
			}

			// action setter
			if (returnsTag && paramTypes.length == 1 && Action[].class.isAssignableFrom(paramTypes[0])) {
				tag.setHandler(event, (Action[]) args[0]);
				return target;
			}
		}

		// value setter
		if (returnsTag && paramTypes.length == 1) {
			tag.set(name, args[0]);
			return target;
		}

		// boolean setter
		if (returnsTag && paramTypes.length == 0) {
			tag.set(name, true);
			return target;
		}

		throw U.rte("Not implemented: " + name);
	}

	// protected Handler newAction(TagData tag, String name, Class<?> clazz) {
	// final ActData prop = new ActData(tag, name);
	//
	// return Cls.implement(prop, new InvocationHandler() {
	//
	// @Override
	// public Object invoke(Object target, Method method, Object[] args) throws Throwable {
	//
	// throw U.notExpected();
	// }
	//
	// }, clazz);
	// }

}
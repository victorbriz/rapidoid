package org.rapidoid.pages;

/*
 * #%L
 * rapidoid-pages
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.aop.AOP;
import org.rapidoid.arr.Arr;
import org.rapidoid.beany.Beany;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.dispatch.PojoDispatchException;
import org.rapidoid.dispatch.PojoHandlerNotFoundException;
import org.rapidoid.html.Cmd;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpExchangeInternals;
import org.rapidoid.http.HttpNotFoundException;
import org.rapidoid.http.HttpSuccessException;
import org.rapidoid.json.JSON;
import org.rapidoid.lambda.Lambdas;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.Log;
import org.rapidoid.pages.impl.BuiltInCmdHandler;
import org.rapidoid.pages.impl.ComplexView;
import org.rapidoid.pages.impl.PageRenderer;
import org.rapidoid.rest.WebPojoDispatcher;
import org.rapidoid.rest.WebReq;
import org.rapidoid.util.D;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoid.wire.Wire;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Pages {

	private static final String PAGE_RELOAD = "<h2>&nbsp;Reloading...</h2><script>location.reload();</script>";

	private static final BuiltInCmdHandler BUILT_IN_HANDLER = new BuiltInCmdHandler();

	public static void registerPages(HTTPServer server) {
		server.serve(new PageHandler());
	}

	public static String pageName(HttpExchange x) {
		String path = x.path();

		if (path.endsWith(".html")) {
			path = U.mid(path, 0, -5);
		}

		if (path.equals("/")) {
			path = "/index";
		}

		return U.capitalized(path.substring(1));
	}

	public static String defaultPageTitle(Class<?> pageClass) {
		String pageName = pageClass.getSimpleName();

		if (pageName.endsWith("Page")) {
			pageName = U.mid(pageName, 0, -4);
		}

		return UTILS.camelPhrase(pageName);
	}

	public static String titleOf(HttpExchange x, Object target) {
		Method m = Cls.findMethod(target.getClass(), "title", HttpExchange.class);

		if (m != null) {
			return AOP.invoke(x, m, target, x);
		}

		try {
			return Beany.getPropValue(target, "title");
		} catch (Exception e) {
			return defaultPageTitle(target.getClass());
		}
	}

	public static Object contentOf(HttpExchange x, Object target) {
		Method m = Cls.findMethod(target.getClass(), "content", HttpExchange.class);
		return m != null ? AOP.invoke(x, m, target, x) : Beany.getPropValue(target, "content", null);
	}

	public static Object headOf(HttpExchange x, Object target) {
		Method m = Cls.findMethod(target.getClass(), "head", HttpExchange.class);
		return m != null ? AOP.invoke(x, m, target, x) : Beany.getPropValue(target, "head", null);
	}

	public static Object page(HttpExchange x, Object page) {

		String pageTitle = titleOf(x, page);
		Object pageHead = U.or(headOf(x, page), "");
		Object content = contentOf(x, page);

		if (content == null) {
			return null;
		}

		if (content instanceof HttpExchange) {
			U.must(x == content, "Different HTTP exchange than expected!");
			return x;
		}

		return PageGUI.page(Conf.dev(), pageTitle, pageHead, content);
	}

	public static Object render(HttpExchange x, Object page) {

		Object fullPage = page(x, page);
		if (fullPage != null) {
			if (fullPage instanceof HttpExchange) {
				return x;
			} else {
				PageRenderer.get().render(fullPage, x);
				return x;
			}
		} else {
			throw x.notFound();
		}
	}

	public static Object dispatch(HttpExchange x, WebPojoDispatcher serviceDispatcher, Map<String, Class<?>> pages) {

		if (x.serveStaticFile()) {
			return x;
		}

		if (serviceDispatcher != null) {
			try {
				return serviceDispatcher.dispatch(new WebReq(x));
			} catch (PojoHandlerNotFoundException e) {
				// / just ignore, will try to dispatch a page next...
			} catch (PojoDispatchException e) {
				return x.errorResponse(e);
			}
		}

		String pageName = pageName(x);
		if (pageName == null) {
			return null;
		}

		String pageClassName = U.capitalized(pageName) + "Page";

		Class<?> pageClass = pages.get(pageClassName);
		if (pageClass == null) {
			return null;
		}

		Object page = Cls.newInstance(pageClass);

		if (isEmiting(x)) {
			return emit(x, page);
		} else {
			return serve(x, page);
		}
	}

	public static boolean isEmiting(HttpExchange x) {
		return x.isPostReq();
	}

	public static Object serve(HttpExchange x, Object view) {
		load(x, view);
		store(x, view);

		Object result = render(x, view);

		x.addToPageStack();
		store(x, view);

		return result;
	}

	public static void load(HttpExchange x, Object target) {
		Mapper<String, Object> sessionMapper = Lambdas.mapper(x.session());

		Map<String, Object> locals = UTILS.cast(x.locals());
		Mapper<String, Object> localsMapper = Lambdas.mapper(locals);

		Wire.autowire(target, sessionMapper, localsMapper);

		if (target instanceof ComplexView) {
			ComplexView complex = (ComplexView) target;
			for (Object subview : complex.getSubViews()) {
				Wire.autowire(subview, sessionMapper, localsMapper);
			}
		}
	}

	public static void store(HttpExchange x, Object target) {
		storeFrom(x, target);

		if (target instanceof ComplexView) {
			ComplexView complex = (ComplexView) target;
			for (Object subview : complex.getSubViews()) {
				storeFrom(x, subview);
			}
		}
	}

	private static void storeFrom(HttpExchange x, Object target) {
		for (Field field : Wire.getSessionFields(target)) {
			Object value = Cls.getFieldValue(field, target);
			x.session().put(field.getName(), UTILS.serializable(value));
		}
	}

	public static final Object reloadOnEmit(HttpExchange x) {
		return changes(x, PAGE_RELOAD);
	}

	public static Object emit(HttpExchange x, Object view) {

		String event = x.posted("event");
		boolean navigational = Boolean.parseBoolean(x.posted("navigational"));
		String evArgs = x.posted("args", null);
		Object[] args = {}; // FIXME
		boolean validEvent = !U.isEmpty(event);

		if (!navigational) {
			doBinding(x, event, validEvent);
		}

		load(x, view);

		Object content;
		try {
			content = contentOf(x, view);
			if (content == null || content instanceof HttpExchange) {
				return content;
			}
		} catch (Exception e) {
			Throwable cause = UTILS.rootCause(e);
			if (cause instanceof HttpSuccessException || cause instanceof HttpNotFoundException) {
				return null;
			} else {
				throw U.rte(e);
			}
		}

		if (x.hasErrors()) {
			x.json();
			return U.map("!errors", x.errors());
		}

		store(x, view);
		load(x, view);

		boolean processView = true;
		if (validEvent) {
			try {
				callCmdHandler(x, view, new Cmd(event, navigational, args));
			} catch (Exception e) {
				Throwable cause = UTILS.rootCause(e);
				if (cause instanceof HttpSuccessException || cause instanceof HttpNotFoundException) {
					processView = false;
				} else {
					throw U.rte(e);
				}
			}
		}

		// store event processing changes into the session
		store(x, view);
		load(x, view);

		String html = processView ? PageRenderer.get().toHTML(content, x) : "Error!";

		if (x.redirectUrl() != null) {
			x.startResponse(200);
			x.json();
			return U.map("_redirect_", x.redirectUrl());
		}

		return changes(x, html);
	}

	@SuppressWarnings("unchecked")
	private static void doBinding(HttpExchange x, String event, boolean validEvent) {
		if (validEvent) {
			String inputs = x.posted("inputs");
			U.notNull(inputs, "inputs");
			Map<String, Object> inputsMap = JSON.parse(inputs, Map.class);
			emitValues(x, inputsMap);
		} else {
			Log.warn("Invalid event!", "event", event);
		}
	}

	private static void emitValues(HttpExchange x, final Map<String, Object> values) {
		for (Entry<String, Object> e : values.entrySet()) {
			String inputId = e.getKey();
			Object value = e.getValue();

			x.locals().put(inputId, UTILS.serializable(value));
		}
	}

	private static Object changes(HttpExchange x, String html) {
		x.json();
		Map<String, String> sel = U.map("body", html);
		return U.map("_sel_", sel, "_state_", stateOf(x));
	}

	public static byte[] stateOf(HttpExchange x) {
		HttpExchangeInternals xi = (HttpExchangeInternals) x;
		return xi.serializeLocals();
	}

	public static void callCmdHandler(HttpExchange x, Object target, Cmd cmd) {
		if (!callCmdHandler(x, target, cmd, false)) {
			callCmdHandler(x, BUILT_IN_HANDLER, cmd, false);
		}
	}

	private static boolean callCmdHandler(HttpExchange x, Object target, Cmd cmd, boolean failIfNotFound) {

		String handlerName = "on" + U.capitalized(cmd.name);
		Method m = Cls.findMethodByArgs(target.getClass(), handlerName, cmd.args);

		if (m != null) {
			AOP.invoke(x, m, target, cmd.args);
			return true;
		}

		Object[] args2 = Arr.expand(cmd.args, x);
		m = Cls.findMethodByArgs(target.getClass(), handlerName, args2);

		if (m != null) {
			AOP.invoke(x, m, target, args2);
			return true;
		}

		args2 = new Object[cmd.args.length + 1];
		args2[0] = x;
		System.arraycopy(cmd.args, 0, args2, 1, cmd.args.length);
		m = Cls.findMethodByArgs(target.getClass(), handlerName, args2);

		if (m != null) {
			AOP.invoke(x, m, target, args2);
			return true;
		}

		Method on = Cls.findMethod(target.getClass(), "on", String.class, Object[].class);
		if (on != null) {
			AOP.invoke(x, on, target, cmd.name, cmd.args);
			return true;
		}

		on = Cls.findMethod(target.getClass(), "on", HttpExchange.class, String.class, Object[].class);
		if (on != null) {
			AOP.invoke(x, on, target, x, cmd.name, cmd.args);
			return true;
		}

		if (failIfNotFound) {
			throw U.rte("Cannot find handler '%s' for the command '%s' and args: %s", handlerName, cmd.name, cmd.args);
		} else {
			return false;
		}
	}

}

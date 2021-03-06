package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
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
import java.util.Set;

import org.rapidoid.annotation.App;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.RESTful;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.Transaction;
import org.rapidoid.aop.AOP;
import org.rapidoid.beany.Beany;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.HttpBuiltins;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.log.Log;
import org.rapidoid.oauth.OAuth;
import org.rapidoid.plugins.Lifecycle;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.plugins.spec.DBPlugin;
import org.rapidoid.plugins.spec.EntitiesPlugin;
import org.rapidoid.plugins.spec.LanguagesPlugin;
import org.rapidoid.plugins.spec.LifecyclePlugin;
import org.rapidoid.plugins.spec.UsersPlugin;
import org.rapidoid.scan.Scan;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoid.util.Usage;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Apps {

	private static final String BUILT_IN_SCREEN_SUFFIX = "BuiltIn";

	private static AppClasses APP_CLASSES;

	public static void main(String[] args) {
		run((Object[]) args);
	}

	public static void run(String[] args) {
		run((Object[]) args);
	}

	public static void run(Object... args) {
		bootstrap(args);
		serve(args);
	}

	public static void bootstrap(Object... args) {
		Plugins.bootstrap();

		Set<String> config = U.set();

		for (Object arg : args) {
			processArg(config, arg);
		}

		String[] configArgs = config.toArray(new String[config.size()]);
		Conf.args(configArgs);
		Log.args(configArgs);

		AOP.register(Transaction.class, new TransactionInterceptor());

		Lifecycle.onStart(args);
	}

	public static HTTPServer serve(Object... args) {
		HTTPServer server = HTTP.server().build();

		OAuth.register(server);
		HttpBuiltins.register(server);

		server.serve(new AppHandler());

		return server.start();
	}

	private static void processArg(Set<String> config, Object arg) {
		Log.info("Processing start-up argument", "arg", arg);

		if (arg instanceof String) {
			config.add((String) arg);
		} else if (arg instanceof DBPlugin) {
			Plugins.register((DBPlugin) arg);
		} else if (arg instanceof EntitiesPlugin) {
			Plugins.register((EntitiesPlugin) arg);
		} else if (arg instanceof LanguagesPlugin) {
			Plugins.register((LanguagesPlugin) arg);
		} else if (arg instanceof LifecyclePlugin) {
			Plugins.register((LifecyclePlugin) arg);
		} else if (arg instanceof UsersPlugin) {
			Plugins.register((UsersPlugin) arg);
		}
	}

	public static String screenName(Class<?> screenClass) {
		String name = screenClass.getSimpleName();
		if (name.endsWith(BUILT_IN_SCREEN_SUFFIX)) {
			name = U.mid(name, 0, -BUILT_IN_SCREEN_SUFFIX.length());
		}
		return U.mid(name, 0, -6);
	}

	public static String screenUrl(Class<?> screenClass) {
		String url = "/" + screenName(screenClass).toLowerCase();
		return url.equals("/home") ? "/" : url;
	}

	public static AppClasses scanAppClasses(HttpExchange x) {
		return scanAppClasses(x, null);
	}

	public static synchronized AppClasses scanAppClasses(HttpExchange x, ClassLoader classLoader) {

		Map<String, Class<?>> services = Cls.classMap(Scan.annotated(RESTful.class, classLoader));
		Map<String, Class<?>> pages = Cls.classMap(Scan.bySuffix("Page", null, classLoader));
		Map<String, Class<?>> apps = Cls.classMap(Scan.annotated(App.class, classLoader));
		Map<String, Class<?>> screens = Cls.classMap(Scan.bySuffix("Screen", null, classLoader));

		final Class<?> appClass = !apps.isEmpty() ? apps.values().iterator().next() : TheDefaultApp.class;

		AppClasses APP_CLASSES = new AppClasses(appClass, services, pages, screens);
		return APP_CLASSES;
	}

	public static synchronized AppClasses getAppClasses(HttpExchange x, ClassLoader classLoader) {
		// FIXME detect changes and invalidate cache

		if (APP_CLASSES == null) {
			APP_CLASSES = scanAppClasses(x, classLoader);
		}

		return APP_CLASSES;
	}

	@SuppressWarnings("unchecked")
	public static <T> T config(Object obj, String configName, T byDefault) {
		Object val = Beany.getPropValue(obj, configName, null);
		return val != null ? (T) val : byDefault;
	}

	public static boolean addon(Object obj, String configName) {
		return config(obj, configName, false) || config(obj, "full", true);
	}

	public static void terminate(final int afterSeconds) {
		Log.warn("Terminating application in " + afterSeconds + " seconds...");
		new Thread() {
			@Override
			public void run() {
				UTILS.sleep(afterSeconds * 1000);
				terminate();
			}
		}.start();
	}

	public static void terminateIfIdleFor(final int idleSeconds) {
		Log.warn("Will terminate if idle for " + idleSeconds + " seconds...");

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					UTILS.sleep(500);
					long lastUsed = Usage.getLastAppUsedOn();
					long idleSec = (U.time() - lastUsed) / 1000;
					if (idleSec >= idleSeconds) {
						Usage.touchLastAppUsedOn();
						terminate();
					}
				}
			}
		}).start();
	}

	public static void terminate() {
		Log.warn("Terminating application.");
		Lifecycle.onShutdown();
		System.exit(0);
	}

	public static void reset() {
		APP_CLASSES = null;
	}

}

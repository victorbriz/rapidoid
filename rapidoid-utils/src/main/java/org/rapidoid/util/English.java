package org.rapidoid.util;

/*
 * #%L
 * rapidoid-utils
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
import java.util.regex.Pattern;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.IO;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class English {

	private static final Pattern PLURAL1 = Pattern.compile(".*(s|x|z|ch|sh)$");
	private static final Pattern PLURAL1U = Pattern.compile(".*(S|X|Z|CH|SH)$");
	private static final Pattern PLURAL2 = Pattern.compile(".*[bcdfghjklmnpqrstvwxz]o$");
	private static final Pattern PLURAL2U = Pattern.compile(".*[BCDFGHJKLMNPQRSTVWXZ]O$");
	private static final Pattern PLURAL3 = Pattern.compile(".*[bcdfghjklmnpqrstvwxz]y$");
	private static final Pattern PLURAL3U = Pattern.compile(".*[BCDFGHJKLMNPQRSTVWXZ]Y$");

	private static final Map<String, String> IRREGULAR_PLURAL = IO.loadMap("irregular-plural.txt");

	public static String plural(String noun) {
		if (U.isEmpty(noun)) {
			return noun;
		}

		if (IRREGULAR_PLURAL.containsKey(noun.toLowerCase())) {
			boolean capital = Character.isUpperCase(noun.charAt(0));
			boolean upper = Character.isUpperCase(noun.charAt(noun.length() - 1));
			String pl = IRREGULAR_PLURAL.get(noun.toLowerCase());

			if (upper) {
				return pl.toUpperCase();
			} else {
				return (capital ? U.capitalized(pl) : pl);
			}

		} else if (PLURAL1.matcher(noun).matches()) {
			return noun + "es";
		} else if (PLURAL2.matcher(noun).matches()) {
			return noun + "es";
		} else if (PLURAL3.matcher(noun).matches()) {
			return U.mid(noun, 0, -1) + "ies";
		} else if (PLURAL1U.matcher(noun).matches()) {
			return noun + "ES";
		} else if (PLURAL2U.matcher(noun).matches()) {
			return noun + "ES";
		} else if (PLURAL3U.matcher(noun).matches()) {
			return U.mid(noun, 0, -1) + "IES";
		} else {
			boolean upper = Character.isUpperCase(noun.charAt(noun.length() - 1));
			return noun + (upper ? "S" : "s");
		}
	}

	public static String singular(String noun) {
		if (U.isEmpty(noun)) {
			return noun;
		}

		if (noun.toLowerCase().endsWith("s")) {
			String singular = U.mid(noun, 0, -1);
			if (plural(singular).equals(noun)) {
				return singular;
			}
		}

		if (noun.toLowerCase().endsWith("es")) {
			String singular = U.mid(noun, 0, -2);
			if (plural(singular).equals(noun)) {
				return singular;
			}
		}

		if (noun.toLowerCase().endsWith("ies")) {
			String singular = U.mid(noun, 0, -1);
			if (!singular.isEmpty()) {
				boolean upper = Character.isUpperCase(singular.charAt(singular.length() - 1));
				singular += upper ? 'Y' : 'y';
				if (plural(singular).equals(noun)) {
					return singular;
				}
			}
		}

		// FIXME handle irregular plural
		return noun;
	}

}

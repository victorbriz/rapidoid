package org.rapidoidx.compile.impl;

/*
 * #%L
 * rapidoid-x-compile
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class ErrorPolicy implements IErrorHandlingPolicy {

	public boolean stopOnFirstError() {
		return false;
	}

	public boolean proceedOnErrors() {
		return true;
	}

	public boolean ignoreAllErrors() {
		return false;
	}

}

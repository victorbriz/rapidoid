package org.rapidoid.security;

/*
 * #%L
 * rapidoid-security
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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.rapidoid.security.annotation.Admin;
import org.rapidoid.security.annotation.LoggedIn;
import org.rapidoid.security.annotation.Manager;
import org.rapidoid.security.annotation.Moderator;
import org.rapidoid.security.annotation.Role;
import org.rapidoid.security.annotation.Roles;
import org.rapidoid.util.Arr;
import org.rapidoid.util.Cls;
import org.rapidoid.util.CommonRoles;
import org.rapidoid.util.Constants;
import org.rapidoid.util.Metadata;
import org.rapidoid.util.U;

public class AppSecurity implements Constants {

	public String[] getRolesAllowed(Class<?> clazz) {

		Set<String> roles = U.set();
		Map<Class<?>, Annotation> annotations = Metadata.classAnnotations(clazz);

		for (Entry<Class<?>, Annotation> e : annotations.entrySet()) {
			Annotation ann = e.getValue();
			Class<? extends Annotation> type = ann.annotationType();

			if (type.equals(Admin.class)) {
				roles.add(CommonRoles.ADMIN);
			} else if (type.equals(Manager.class)) {
				roles.add(CommonRoles.MANAGER);
			} else if (type.equals(Moderator.class)) {
				roles.add(CommonRoles.MODERATOR);
			} else if (type.equals(LoggedIn.class)) {
				roles.add(CommonRoles.LOGGED_IN);
			} else if (type.equals(Roles.class)) {
				Role[] values = ((Roles) ann).value();
				U.must(values.length > 0, "At least one role must be specified in @Roles annotation!");
				for (Role r : values) {
					roles.add(r.annotationType().getSimpleName().toUpperCase());
				}
			}
		}

		return roles.toArray(new String[roles.size()]);
	}

	public boolean canAccessClass(String username, Class<?> clazz) {
		return true;
	}

	public boolean hasRole(String username, String role, Class<?> clazz, Object record) {

		if (CommonRoles.ANYBODY.equalsIgnoreCase(role)) {
			return true;
		}

		if (U.isEmpty(username) || U.isEmpty(role)) {
			return false;
		}

		if (role.equalsIgnoreCase(CommonRoles.LOGGED_IN)) {
			return !U.isEmpty(username);
		}

		if (record != null) {

			if (role.equalsIgnoreCase(CommonRoles.OWNER)) {
				return isOwnerOf(username, record);
			}

			if (role.equalsIgnoreCase(CommonRoles.SHARED_WITH)) {
				return isSharedWith(username, record);
			}
		}

		return hasRole(username, role);
	}

	protected boolean hasRole(String username, String role) {
		String roleConfig = "role-" + role.toLowerCase();
		String[] usernames = U.option(roleConfig, EMPTY_STRING_ARRAY);
		return !U.isEmpty(username) && Arr.indexOf(usernames, username) >= 0;
	}

	public boolean isAdmin(String username) {
		return hasRole(username, "ADMIN", null, null);
	}

	public boolean isManager(String username) {
		return hasRole(username, "MANAGER", null, null);
	}

	public boolean isModerator(String username) {
		return hasRole(username, "MODERATOR", null, null);
	}

	public DataPermissions classPermissions(String username, Class<?> clazz) {
		return DataPermissions.ALL;
	}

	public DataPermissions recordPermissions(String username, Object record) {
		return DataPermissions.ALL;
	}

	public DataPermissions propertyPermissions(String username, Object record, String propertyName) {
		return DataPermissions.ALL;
	}

	public boolean isOwnerOf(String username, Object record) {
		if (U.isEmpty(username) || record == null) {
			return false;
		}

		Object owner = Cls.getPropValue(record, "owner", null);

		return owner != null && username.equalsIgnoreCase(Cls.getPropValue(owner, "username", ""));
	}

	public boolean isSharedWith(String username, Object record) {
		if (U.isEmpty(username) || record == null) {
			return false;
		}

		Object sharedWith = Cls.getPropValue(record, "sharedWith", null);

		if (sharedWith != null && sharedWith instanceof Collection<?>) {
			for (Object user : (Collection<?>) sharedWith) {
				if (username.equalsIgnoreCase(Cls.getPropValue(user, "username", ""))) {
					return true;
				}
			}
		}

		return false;
	}

}
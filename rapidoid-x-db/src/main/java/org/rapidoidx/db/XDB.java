package org.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db
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
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.lambda.Callback;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class XDB {

	private static final String IMPL_NAME = "org.rapidoidx.db.impl.inmem.DbImpl";

	static final Class<Database> DB_IMPL_CLASS;

	private static Database db;

	static {
		String dbImplName = Conf.option("db", IMPL_NAME);

		DB_IMPL_CLASS = Cls.getClassIfExists(IMPL_NAME);

		U.must(DB_IMPL_CLASS != null, "Cannot find Db implementation (%s)!", dbImplName);
		U.must(Database.class.isAssignableFrom(DB_IMPL_CLASS), "%s must implement %s!", dbImplName,
				Database.class.getCanonicalName());

		start();
	}

	public static String path() {
		String path = Conf.option("db", "");

		if (!path.isEmpty() && !path.endsWith(File.separator)) {
			path += File.separator;
		}

		return path;
	}

	public static void start() {
		db = (Database) Cls.customizable(XDB.DB_IMPL_CLASS, "default", path() + "default.db");
		db.loadAndStart();
	}

	public static Database db() {
		assert U.must(db != null, "Database not initialized!");
		return db;
	}

	public static void setDb(Database db) {
		XDB.db = db;
	}

	public static long insert(Object record) {
		return db().insert(record);
	}

	public static void delete(long id) {
		db().delete(id);
	}

	public static void delete(Object record) {
		db().delete(record);
	}

	public static <T> T get(long id) {
		return db().get(id);
	}

	public static <T> T getIfExists(long id) {
		return db().getIfExists(id);
	}

	public static <T> T get(long id, Class<T> clazz) {
		return db().get(id, clazz);
	}

	public static <E> List<E> getAll(Class<E> clazz) {
		return db().getAll(clazz);
	}

	public static <E> List<E> getAll(long... ids) {
		return db().getAll(ids);
	}

	public static <E> List<E> getAll(Collection<Long> ids) {
		return db().getAll(ids);
	}

	public static <E> void refresh(E record) {
		db().refresh(record);
	}

	public static void update(long id, Object record) {
		db().update(id, record);
	}

	public static void update(Object record) {
		db().update(record);
	}

	public static long persist(Object record) {
		return db().persist(record);
	}

	public static long persistedIdOf(Object record) {
		return db().persistedIdOf(record);
	}

	public static <E> E readColumn(long id, String column) {
		return db().readColumn(id, column);
	}

	public static <E> List<E> find(Predicate<E> match) {
		return db().find(match);
	}

	public static <E> List<E> find(Iterable<Long> ids) {
		return db().find(ids);
	}

	public static <E> List<E> find(Class<E> clazz, Predicate<E> match, Comparator<E> orderBy) {
		return db().find(clazz, match, orderBy);
	}

	public static <E> List<E> find(String searchPhrase) {
		return db().find(searchPhrase);
	}

	public static <E> List<E> find(Class<E> clazz, String query, Object... args) {
		return db().find(clazz, query, args);
	}

	public static <E> void each(Operation<E> lambda) {
		db().each(lambda);
	}

	public static void transaction(Runnable transaction, boolean readOnly) {
		db().transaction(transaction, readOnly);
	}

	public static void transaction(Runnable transaction, boolean readOnly, Callback<Void> callback) {
		db().transaction(transaction, readOnly, callback);
	}

	public static void shutdown() {
		if (db() != null) {
			db().shutdown();
		}
	}

	public static long size() {
		return db().size();
	}

	public static void clear() {
		db().clear();
	}

	public static boolean isActive() {
		return db() != null && db().isActive();
	}

	public static void halt() {
		if (db() != null) {
			db().halt();
		}
	}

	public static void destroy() {
		if (db() != null) {
			db().destroy();
		}
	}

	public static long getIdOf(Object record) {
		return db().getIdOf(record);
	}

	public static long getVersionOf(long id) {
		return db().getVersionOf(id);
	}

	public static <E> DbColumn<E> column(Map<String, Object> map, String name, Class<E> type) {
		return db().column(map, name, type);
	}

	public static <E> DbList<E> list(Object holder, String relation) {
		return db().list(holder, relation);
	}

	public static <E> DbSet<E> set(Object holder, String relation) {
		return db().set(holder, relation);
	}

	public static <E> DbRef<E> ref(Object holder, String relation) {
		return db().ref(holder, relation);
	}

	public static DbSchema schema() {
		return db().schema();
	}

	@SuppressWarnings("unchecked")
	public static <E> E entity(Class<E> entityType) {
		return (E) schema().entity(entityType, Collections.EMPTY_MAP);
	}

	public static <E> E entity(Class<E> entityType, Map<String, ?> properties) {
		return schema().entity(entityType, properties);
	}

	public static <E> E entity(Class<E> entityType, String prop, Object value) {
		return schema().entity(entityType, U.map(prop, value));
	}

	public static <E> E entity(Class<E> entityType, String prop1, Object value1, String prop2, Object value2) {
		return schema().entity(entityType, U.map(prop1, value1, prop2, value2));
	}

	public static <E> E entity(Class<E> entityType, String prop1, Object value1, String prop2, Object value2,
			String prop3, Object value3) {
		return schema().entity(entityType, U.map(prop1, value1, prop2, value2, prop3, value3));
	}

	public static <E> E entity(Class<E> entityType, String prop1, Object value1, String prop2, Object value2,
			String prop3, Object value3, String prop4, Object value4) {
		return schema().entity(entityType, U.map(prop1, value1, prop2, value2, prop3, value3, prop4, value4));
	}

	public static <E> E entity(Class<E> entityType, String prop1, Object value1, String prop2, Object value2,
			String prop3, Object value3, String prop4, Object value4, String prop5, Object value5) {
		return schema().entity(entityType,
				U.map(prop1, value1, prop2, value2, prop3, value3, prop4, value4, prop5, value5));
	}

	public static <E> DbDsl<E> dsl(Class<E> entityType) {
		return schema().dsl(entityType);
	}

	public static Database as(String username) {
		return db().as(username);
	}

	public static Database sudo() {
		return db().sudo();
	}

	public static void init(String data, Object... args) {
		db().init(data, args);
	}

	public static <RESULT> RESULT rql(String rql, Object... args) {
		return db().rql(rql, args);
	}

	public static <E> E entity(String rql, Object... args) {
		return db().entity(rql, args);
	}

}

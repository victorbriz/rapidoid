package org.rapidoid.pages;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rapidoid.html.Bootstrap;
import org.rapidoid.html.FieldType;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpExchanges;
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Model;
import org.rapidoid.model.Property;
import org.rapidoid.util.Cls;
import org.rapidoid.util.TypeKind;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;

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

public abstract class BootstrapWidgets extends Bootstrap {

	public static final ButtonTag SAVE = cmd("SAVE");

	public static final ButtonTag UPDATE = cmd("^UPDATE");

	public static final ButtonTag INSERT = cmd("^INSERT");

	public static final ButtonTag DELETE = cmd("DELETE");

	public static final ButtonTag YES = cmd("^YES");

	public static final ButtonTag NO = cmd("NO");

	public static final ButtonTag OK = cmd("^OK");

	public static final ButtonTag CANCEL = cmd("Cancel");

	public static final ButtonTag BACK = cmd("Back");

	public static final ButtonTag EDIT = cmd("^Edit");

	public static Object i18n(String multiLanguageText, Object... formatArgs) {
		return HtmlWidgets.i18n(multiLanguageText, formatArgs);
	}

	public static <T> Var<T> property(Item item, String property) {
		return HtmlWidgets.property(item, property);
	}

	public static Tag template(String templateFileName, Object... namesAndValues) {
		return HtmlWidgets.template(templateFileName, namesAndValues);
	}

	public static Tag hardcoded(String content) {
		return HtmlWidgets.hardcoded(content);
	}

	public static <T> Tag grid(Class<T> type, Object[] items, String sortOrder, int pageSize, String... properties) {
		return grid(Model.beanItems(type, items), sortOrder, pageSize, properties);
	}

	public static <T> Tag grid(Class<T> type, Collection<T> items, String sortOrder, int pageSize, String... properties) {
		return grid(type, items.toArray(), sortOrder, pageSize, properties);
	}

	public static Tag grid(Items items, String sortOrder, int pageSize, String... properties) {
		final List<Property> props = items.properties(properties);

		int total = items.size();
		int pages = (int) Math.ceil(total / (double) pageSize);

		boolean ordered = !U.isEmpty(sortOrder);
		Var<String> order = null;

		if (ordered) {
			order = localVar("_order_" + items.uri(), sortOrder);
			sortOrder = order.get();
			items = items.orderedBy(sortOrder);
		}

		boolean paging = pageSize > 0;
		Var<Integer> pageNumber = null;
		Items pageOrAll = items;

		if (paging) {
			pageNumber = localVar("_page_" + items.uri(), 1);

			Integer pageN = U.limit(1, pageNumber.get(), pages);
			pageNumber.set(pageN);

			int pageFrom = Math.max((pageN - 1) * pageSize, 0);
			int pageTo = Math.min((pageN) * pageSize, items.size());

			pageOrAll = items.range(pageFrom, pageTo);
		}

		Tag header = tr();

		for (Property prop : props) {
			Tag sortIcon = null;

			Object sort;
			if (ordered) {

				if (sortOrder.equals(prop.name())) {
					sortIcon = glyphicon("chevron-down");
				}

				if (ordered && sortOrder.equals("-" + prop.name())) {
					sortIcon = glyphicon("chevron-up");
				}

				sort = a_void(prop.caption(), " ", sortIcon).cmd("_sort", order, prop.name());
			} else {
				sort = prop.caption();
			}

			header = header.append(th(sort));
		}

		Tag body = tbody();

		for (Item item : pageOrAll) {
			Tag row = itemRow(props, item);
			body = body.append(row);
		}

		Tag pager = paging ? pager(1, pages, pageNumber) : null;
		return row(table_(thead(header), body), pager);
	}

	protected static Tag itemRow(List<Property> properties, Item item) {
		Tag row = tr();

		for (Property prop : properties) {
			Object value = item.get(prop.name());
			row = row.append(td(U.or(value, "")));
		}

		String type = U.uncapitalized(item.value().getClass().getSimpleName());
		String js = U.format("goAt('/%s/%s');", type, item.id());
		row = row.onclick(js).class_("pointer");

		return row;
	}

	public static Tag pager(int from, int to, Var<Integer> pageNumber) {

		int pageN = pageNumber.get();

		Tag firstIcon = span(LAQUO).attr("aria-hidden", "true");
		ATag first = a_void(firstIcon, span("First").class_("sr-only")).cmd("_set", pageNumber, from);

		Tag prevIcon = span(LT).attr("aria-hidden", "true");
		ATag prev = a_void(prevIcon, span("Previous").class_("sr-only")).cmd("_dec", pageNumber, 1);

		ATag current = a_void("Page ", pageN, " of " + to);

		Tag nextIcon = span(GT).attr("aria-hidden", "true");
		ATag next = a_void(nextIcon, span("Next").class_("sr-only")).cmd("_inc", pageNumber, 1);

		Tag lastIcon = span(RAQUO).attr("aria-hidden", "true");
		ATag last = a_void(lastIcon, span("Last").class_("sr-only")).cmd("_set", pageNumber, to);

		Tag firstLi = pageN > from ? li(first) : li(first.cmd(null)).class_("disabled");
		Tag prevLi = pageN > from ? li(prev) : li(prev.cmd(null)).class_("disabled");
		Tag currentLi = li(current);
		Tag nextLi = pageN < to ? li(next) : li(next.cmd(null)).class_("disabled");
		Tag lastLi = pageN < to ? li(last) : li(last.cmd(null)).class_("disabled");

		Tag pagination = nav(ul_li(firstLi, prevLi, currentLi, nextLi, lastLi).class_("pagination"));
		return div(pagination).class_("pull-right");
	}

	public static FormTag show(Object bean, String... properties) {
		Item item = Model.item(bean);
		return show(item, properties);
	}

	public static FormTag show(final Item item, String... properties) {
		final List<Property> props = item.editableProperties(properties);

		int propN = props.size();

		String[] names = new String[propN];
		String[] desc = new String[propN];
		FieldType[] types = new FieldType[propN];
		Object[][] options = new Object[propN][];
		Var<?>[] vars = new Var[propN];

		for (int i = 0; i < propN; i++) {
			Property prop = props.get(i);
			names[i] = prop.name();
			desc[i] = prop.caption();
			types[i] = FieldType.LABEL;
			options[i] = getPropertyOptions(prop);
			vars[i] = property(item, prop.name());
		}

		return form_(FormLayout.VERTICAL, names, desc, types, options, vars);
	}

	public static FormTag edit(Object bean, String... properties) {
		Item item = Model.item(bean);
		return edit(item, properties);
	}

	public static FormTag edit(final Item item, String... properties) {
		final List<Property> props = item.editableProperties(properties);

		int propN = props.size();

		String[] names = new String[propN];
		String[] desc = new String[propN];
		FieldType[] types = new FieldType[propN];
		Object[][] options = new Object[propN][];
		Var<?>[] vars = new Var[propN];

		for (int i = 0; i < propN; i++) {
			Property prop = props.get(i);
			names[i] = prop.name();
			desc[i] = prop.caption();
			types[i] = getPropertyFieldType(prop);
			options[i] = getPropertyOptions(prop);
			vars[i] = property(item, prop.name());
		}

		return form_(FormLayout.VERTICAL, names, desc, types, options, vars);
	}

	protected static FieldType getPropertyFieldType(Property prop) {
		Class<?> type = prop.type();

		if (type.isEnum()) {
			return type.getEnumConstants().length <= 3 ? FieldType.RADIOS : FieldType.DROPDOWN;
		}

		if (prop.name().toLowerCase().contains("email")) {
			return FieldType.EMAIL;
		}

		if (Collection.class.isAssignableFrom(type)) {
			return FieldType.MULTI_SELECT;
		}

		if (Cls.kindOf(type) == TypeKind.OBJECT) {
			return FieldType.DROPDOWN;
		}

		return FieldType.TEXT;
	}

	protected static Object[] getPropertyOptions(Property prop) {
		Class<?> type = prop.type();

		if (type.isEnum()) {
			return type.getEnumConstants();
		}

		if (Cls.kindOf(type) == TypeKind.OBJECT) {
			return new Object[] {};
		}

		return null;
	}

	public static Tag page(boolean devMode, String pageTitle, Object head, Object body) {
		String devOrProd = devMode ? "dev" : "prod";
		return template("bootstrap-page-" + devOrProd + ".html", "title", pageTitle, "head", head, "body", body);
	}

	public static Tag page(boolean devMode, String pageTitle, Object body) {
		return page(devMode, pageTitle, "", body);
	}

	public static Tag media(Object left, Object title, Object body, String targetUrl) {

		Tag mhead = h4(title).class_("media-heading");
		Tag mleft = div(left).class_("media-left");
		Tag mbody = div(mhead, body).class_("media-body");

		String divClass = targetUrl != null ? "media pointer" : "media";
		String js = targetUrl != null ? U.format("goAt('%s');", targetUrl) : null;

		return div(mleft, mbody).class_(divClass).onclick(js);
	}

	public static Tag[] mediaList(List<Object> found) {
		Tag[] items = new Tag[found.size()];
		int ind = 0;

		for (Object result : found) {
			long id = Cls.getId(result);
			String url = urlFor(result);

			Tag left = h6("(ID", NBSP, "=", NBSP, id, ")");
			Object header = span(result.getClass().getSimpleName());
			items[ind++] = media(left, header, small(Cls.beanToStr(result, true)), url);
		}

		return items;
	}

	public static <T> Var<T> sessionVar(String name, T defaultValue) {
		return HttpExchanges.sessionVar(name, defaultValue);
	}

	public static <T> Var<T> localVar(String name, T defaultValue) {
		HttpExchange x = HttpExchanges.getThreadLocalExchange();
		return HttpExchanges.sessionVar(name + ":" + Pages.viewId(x), defaultValue);
	}

	public static String urlFor(Object entity) {
		long id = Cls.getId(entity);
		String className = entity.getClass().getSimpleName();
		return U.format("/%s/%s", U.uncapitalized(className), id);
	}

	public static Object highlight(String text) {
		return mark(text).class_("highlight");
	}

	public static Object highlight(String text, String regex) {
		List<Object> parts = U.list();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);

		int end = 0;
		while (m.find()) {
			String match = m.group();
			parts.add(text.substring(end, m.start()));
			parts.add(highlight(match));
			end = m.end();
		}

		parts.add(text.substring(end));

		return parts;
	}

}

package org.rapidoidx.demo.taskplanner.gui;

/*
 * #%L
 * rapidoid-x-demo
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Order;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.Screen;
import org.rapidoid.html.Tag;
import org.rapidoid.widget.GridWidget;
import org.rapidoidx.demo.taskplanner.model.Task;

@Order(2)
@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class ManageTasksScreen extends Screen {

	public String title = "Tasks Overview";

	public Object content() {
		Tag caption = titleBox("Manage tasks");
		GridWidget grid = grid(Task.class, "-priority", 10, "id", "title", "priority");
		return row(caption, grid);
	}

}

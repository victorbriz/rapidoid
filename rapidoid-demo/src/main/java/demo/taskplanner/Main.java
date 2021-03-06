package demo.taskplanner;

/*
 * #%L
 * rapidoid-demo
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

import javax.transaction.Transactional;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.RESTful;
import org.rapidoid.annotation.Since;
import org.rapidoid.plugins.DB;
import org.rapidoid.quick.Quick;
import org.rapidoid.util.U;

import demo.taskplanner.model.Task;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
@RESTful
public class Main {

	public static void main(String[] args) {
		Quick.run("oauth-no-state");
	}

	@Transactional
	@GET
	public void tx() {
		Task task = new Task();
		task.title = "DON'T GO TO THE DATABASE!";
		String id = DB.insert(task);
		DB.update(id, task);
		throw U.rte("some failure!");
	}

}

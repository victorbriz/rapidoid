package org.rapidoid.oauth;

/*
 * #%L
 * rapidoid-oauth
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class OAuthLoginHandler implements Handler {

	private final OAuthProvider provider;
	private final String oauthDomain;

	public OAuthLoginHandler(OAuthProvider provider, String oauthDomain) {
		this.provider = provider;
		this.oauthDomain = oauthDomain;
	}

	@Override
	public Object handle(HttpExchange x) throws Exception {
		return x.redirect(OAuth.getLoginURL(x, provider, oauthDomain));
	}

}

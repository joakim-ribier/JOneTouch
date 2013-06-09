package fr.rjoakim.android.jonetouch.bean;

import com.google.common.base.Objects;

/**
 * 
 * Copyright 2013 Joakim Ribier
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
public class NoAuthentication extends Authentication {

	private final String login;

	public NoAuthentication(String login, String label) {
		super(AuthenticationTypeEnum.NO_AUTHENTICATION, label);
		this.login = login;
	}
	
	public String getLogin() {
		return login;
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(login);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof NoAuthentication) {
			NoAuthentication that = (NoAuthentication) object;
			return Objects.equal(this.login, that.login);
		}
		return false;
	}
}

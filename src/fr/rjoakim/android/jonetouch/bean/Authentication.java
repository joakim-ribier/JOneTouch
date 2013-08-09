package fr.rjoakim.android.jonetouch.bean;

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
public abstract class Authentication {

	private final AuthenticationTypeEnum authenticationTypeEnum;
	private final String label;
	
	protected Authentication(AuthenticationTypeEnum authenticationTypeEnum, String label) {
		this.authenticationTypeEnum = authenticationTypeEnum;
		this.label = label;
	}
	
	public AuthenticationTypeEnum getAuthenticationTypeEnum() {
		return authenticationTypeEnum;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getLogin() {
		switch (authenticationTypeEnum) {
		case NO_AUTHENTICATION:
			NoAuthentication noAuthentication = (NoAuthentication)this;
			return noAuthentication.getLogin();
		case SSH_AUTHENTICATION_PASSWORD:
			SSHAuthenticationPassword sshAuthenticationPassword = (SSHAuthenticationPassword)this;
			return sshAuthenticationPassword.getLogin();
		default:
			throw new IllegalArgumentException("authentication type not supported");
		}
	}
	
	public String getPassword() {
		switch (authenticationTypeEnum) {
		case NO_AUTHENTICATION:
			return null;
		case SSH_AUTHENTICATION_PASSWORD:
			SSHAuthenticationPassword sshAuthenticationPassword = (SSHAuthenticationPassword)this;
			return sshAuthenticationPassword.getPassword();
		default:
			throw new IllegalArgumentException("authentication type not supported");
		}
	}
}

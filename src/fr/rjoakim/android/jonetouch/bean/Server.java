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
public class Server {

	private final long id;
	private final String title;
	private final String host;
	private final String description;
	private final Authentication authentication;
	private int port;
	
	public Server(long id, String title, String host, int port, String description,
			Authentication authentication) {
		
		this.id = id;
		this.title = title;
		this.host = host;
		this.port = port;
		this.description = description;
		this.authentication = authentication;
	}
	
	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Authentication getAuthentication() {
		return authentication;
	}
		
	@Override
	public int hashCode(){
		return Objects.hashCode(id, title, host, port, description, authentication);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Server) {
			Server that = (Server) object;
			return Objects.equal(this.id, that.id)
				&& Objects.equal(this.title, that.title)
				&& Objects.equal(this.host, that.host)
				&& Objects.equal(this.port, that.port)
				&& Objects.equal(this.description, that.description)
				&& Objects.equal(this.authentication, that.authentication);
		}
		return false;
	}

	@Override
	public String toString() {
		return title;
	}
}

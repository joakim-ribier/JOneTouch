package fr.rjoakim.android.jonetouch.dialog.bean;

import java.util.List;

import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.bean.Server;
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
public class BackupXmlParse {

	private final List<Server> servers;
	private final List<Action> actions;

	public BackupXmlParse(List<Server> servers, List<Action> actions) {
		this.servers = servers;
		this.actions = actions;
	}

	public List<Server> getServers() {
		return servers;
	}

	public List<Action> getActions() {
		return actions;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(servers, actions);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof BackupXmlParse) {
			BackupXmlParse that = (BackupXmlParse) object;
			return Objects.equal(this.servers, that.servers)
				&& Objects.equal(this.actions, that.actions);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("servers", servers)
			.add("actions", actions)
			.toString();
	}
}

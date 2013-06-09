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
public class ActionScript {

	private final long id;
	private final String script;

	public ActionScript(long id, String script) {
		this.id = id;
		this.script = script;
	}

	public long getId() {
		return id;
	}

	public String getScript() {
		return script;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(id, script);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof ActionScript) {
			ActionScript that = (ActionScript) object;
			return Objects.equal(this.id, that.id)
				&& Objects.equal(this.script, that.script);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", id)
			.add("script", script)
			.toString();
	}
}

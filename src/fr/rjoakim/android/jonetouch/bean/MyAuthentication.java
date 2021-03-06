package fr.rjoakim.android.jonetouch.bean;

import java.io.Serializable;

import com.google.common.base.Strings;
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
public class MyAuthentication implements Serializable {
	private static final long serialVersionUID = -5294426029090063894L;
	
	private String key;
	
	public MyAuthentication() {}

	public boolean is() {
		return !Strings.isNullOrEmpty(key);
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(super.hashCode(), key);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof MyAuthentication) {
			MyAuthentication that = (MyAuthentication) object;
			return Objects.equal(this.key, that.key);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("key", key)
			.toString();
	}
}

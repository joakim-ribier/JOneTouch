package fr.rjoakim.android.jonetouch.dialog.bean;

import fr.rjoakim.android.jonetouch.bean.AuthenticationTypeEnum;
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
public class AuthenticationTypeUI {

	private final AuthenticationTypeEnum type;
	private final String label;
	
	public AuthenticationTypeUI(
			AuthenticationTypeEnum authenticationTypeEnum, String label) {
		
		this.type = authenticationTypeEnum;
		this.label = label;
	}

	public AuthenticationTypeEnum getType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(type, label);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof AuthenticationTypeUI) {
			AuthenticationTypeUI that = (AuthenticationTypeUI) object;
			return Objects.equal(this.type, that.type)
				&& Objects.equal(this.label, that.label);
		}
		return false;
	}

	@Override
	public String toString() {
		return label;
	}
}

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
public class Script {

	private final long id;
	private final String title;
	private final String value;

	public Script(long id, String title, String value) {
		this.id = id;
		this.title = title;
		this.value = value;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id, title, value);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Script) {
			Script that = (Script) object;
			return Objects.equal(this.id, that.id)
					&& Objects.equal(this.title, that.title)
					&& Objects.equal(this.value, that.value);
		}
		return false;
	}

	@Override
	public String toString() {
		return title;
	}
}

package fr.rjoakim.android.jonetouch.bean;

import java.util.List;

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
public class Action {

	private final long id;
	private final String title;
	private final String description;
	private final String backgroundHexColor;

	private final Long serverId;
	private final List<ActionScript> actionScripts;

	public Action(long id, String title, String description, String backgroundHexColor,
			Long serverId, List<ActionScript> actionScripts) {

		this.id = id;
		this.title = title;
		this.description = description;
		this.backgroundHexColor = backgroundHexColor;
		this.serverId = serverId;
		this.actionScripts = actionScripts;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getBackgroundHexColor() {
		return backgroundHexColor;
	}

	public Long getServerId() {
		return serverId;
	}

	public List<ActionScript> getActionScripts() {
		return actionScripts;
	}

	public String[] formatScriptsForExecCMD() {
		String[] values = new String[actionScripts.size()];
		int cpt = 0;
		for (ActionScript actionScript: actionScripts) {
			values[cpt] =  actionScript.getScript();
			cpt ++;
		}
		return values;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id, title, description,
				backgroundHexColor, serverId, actionScripts);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Action) {
			Action that = (Action) object;
			return Objects.equal(this.id, that.id)
					&& Objects.equal(this.title, that.title)
					&& Objects.equal(this.description, that.description)
					&& Objects.equal(this.backgroundHexColor, that.backgroundHexColor)
					&& Objects.equal(this.serverId, that.serverId)
					&& Objects.equal(this.actionScripts, that.actionScripts);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("title", title)
				.add("description", description)
				.add("backgroundHexColor", backgroundHexColor)
				.add("serverId", serverId)
				.add("actionScripts", actionScripts)
				.toString();
	}
}

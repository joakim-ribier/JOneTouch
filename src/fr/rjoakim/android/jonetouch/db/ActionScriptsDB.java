package fr.rjoakim.android.jonetouch.db;

import android.content.Context;

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
public class ActionScriptsDB extends DBHelper {

	public static final String TABLE_NAME = "action_scripts";
	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_ACTION = "action";
	public static final String COLUMN_NAME_SCRIPT = "script";

	public static String buildCreateTableQuery() {
		final StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE ").append(TABLE_NAME).append(" (");
		builder.append(COLUMN_NAME_ID).append(" INTEGER PRIMARY KEY, ");
		builder.append(COLUMN_NAME_ACTION).append(" INTEGER NOT NULL, ");
		builder.append(COLUMN_NAME_SCRIPT).append(" TEXT NOT NULL, ");
		builder.append("FOREIGN KEY(");
		builder.append(COLUMN_NAME_ACTION).append(") REFERENCES ");
		builder.append(ActionDB.TABLE_NAME).append("(").append(ActionDB.COLUMN_NAME_ID).append(")");
		builder.append(");");
		return builder.toString();
	}

	public static String buildInsertQueries() {
		final StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO ");
		builder.append(TABLE_NAME).append(" (").
				append(COLUMN_NAME_ID).append(",").
				append(COLUMN_NAME_ACTION).append(",").
				append(COLUMN_NAME_SCRIPT).append(") ");
		builder.append("VALUES ('1', '1', '" + ScriptDB.DEFAULT_SCRIPT_VALUE_1 + "');");
		return builder.toString();
	}
	
	public ActionScriptsDB(Context context) {
		super(new SQLiteHelper(context));
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public String getPrimaryKey() {
		return COLUMN_NAME_ID;
	}
}

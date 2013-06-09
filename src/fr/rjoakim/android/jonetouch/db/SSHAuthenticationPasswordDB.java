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
public class SSHAuthenticationPasswordDB extends DBHelper {

	public static final String TABLE_NAME = "ssh_authentitation_password";
	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_LOGIN = "login";
	public static final String COLUMN_NAME_PWD = "password";
	public static final String COLUMN_NAME_SERVER = "server";

	public static String buildCreateTableQuery() {
		final StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE ").append(TABLE_NAME).append(" (");
		builder.append(COLUMN_NAME_ID).append(" INTEGER PRIMARY KEY, ");
		builder.append(COLUMN_NAME_LOGIN).append(" TEXT NOT NULL, ");
		builder.append(COLUMN_NAME_PWD).append(" TEXT NOT NULL, ");
		builder.append(COLUMN_NAME_SERVER).append(" INTEGER NOT NULL, ");
		builder.append("FOREIGN KEY(");
		builder.append(COLUMN_NAME_SERVER).append(") REFERENCES ");
		builder.append(ServerDB.TABLE_NAME).append("(").append(ServerDB.COLUMN_NAME_ID).append(")");
		builder.append(");");
		return builder.toString();
	}

	public SSHAuthenticationPasswordDB(Context context) {
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

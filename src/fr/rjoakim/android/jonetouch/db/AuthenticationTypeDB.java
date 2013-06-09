package fr.rjoakim.android.jonetouch.db;

import java.util.Collection;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import fr.rjoakim.android.jonetouch.bean.AuthenticationType;
import fr.rjoakim.android.jonetouch.bean.AuthenticationTypeEnum;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
public class AuthenticationTypeDB extends DBHelper {

	public static final String TABLE_NAME = "authentication_type";
	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_TYPE = "type";

	public static String buildCreateTableQuery() {
		final StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE ").append(TABLE_NAME).append(" (");
		builder.append(COLUMN_NAME_ID).append(" INTEGER PRIMARY KEY, ");
		builder.append(COLUMN_NAME_TYPE).append(" TEXT NOT NULL").append(");");
		return builder.toString();
	}
	
	public static Iterable<String> buildInsertQueries() {
		final StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO ");
		builder.append(TABLE_NAME).append(" ("
				).append(COLUMN_NAME_ID).append(",").append(COLUMN_NAME_TYPE).append(") ");
		builder.append("VALUES ('1', 'aucune');#");
		builder.append("INSERT INTO ");
		builder.append(TABLE_NAME).append(" ("
				).append(COLUMN_NAME_ID).append(",").append(COLUMN_NAME_TYPE).append(") ");
		builder.append("VALUES ('2', 'ssh avec mot de passe');");
		return Splitter.on("#").split(builder.toString());
	}

	public AuthenticationTypeDB(Context context) {
		super(new SQLiteHelper(context));
	}

	public Collection<AuthenticationType> findAll() {
		SQLiteDatabase db = getSqliteHelper().getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(getTableName(), null, null, null, null, null, null);
			Collection<AuthenticationType> authenticationTypes = Lists.newArrayList();
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					Long id = cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_ID));
					String type = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TYPE));
					authenticationTypes.add(
							new AuthenticationType(AuthenticationTypeEnum.fromId(id), type));
				} while (cursor.moveToNext());
			}
			return authenticationTypes;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}
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

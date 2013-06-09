package fr.rjoakim.android.jonetouch.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import fr.rjoakim.android.jonetouch.bean.User;

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
public class UserDB extends DBHelper {

	public static final String TABLE_NAME = "user";
	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_EMAIL = "email";
	private static final String COLUMN_NAME_PASSWORD = "password";

	public static String buildCreateTableQuery() {
		final StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE ").append(TABLE_NAME).append(" (");
		builder.append(COLUMN_NAME_ID).append(" INTEGER PRIMARY KEY, ");
		builder.append(COLUMN_NAME_EMAIL).append(" TEXT NOT NULL, ");
		builder.append(COLUMN_NAME_PASSWORD).append(" TEXT NOT NULL, ");
		builder.append("UNIQUE(").append(COLUMN_NAME_EMAIL).append("));");
		return builder.toString();
	}

	public UserDB(Context context) {
		super(new SQLiteHelper(context));
	}

	public long insert(String email, String password) throws DBException {
		final ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_NAME_EMAIL, email);
		contentValues.put(COLUMN_NAME_PASSWORD, password);
		return insert(contentValues);
	}
	
	public Long findUser() {
		return super.find();
	}
	
	public User findUserById(long id) throws DBException {
		SQLiteDatabase db = getSqliteHelper().getReadableDatabase();
		Cursor cursor = null;
		try {
			String where = getPrimaryKey() + " = ?";
			String[] arguments = new String[] { String.valueOf(id) };
			cursor = db.query(getTableName(), null, where, arguments, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				String email = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_EMAIL));
				String pwd = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PASSWORD));
				return new User(email, pwd);
			}
			return null;
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
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

package fr.rjoakim.android.jonetouch.db;

import android.content.ContentValues;
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
public abstract class DBHelper {

	private final SQLiteHelper sqliteHelper;
	
	public abstract String getTableName();
	public abstract String getPrimaryKey();
	
	public DBHelper(SQLiteHelper sqliteHelper) {
		this.sqliteHelper = sqliteHelper;
	}
	
	protected Long find() {
		SQLiteDatabase db = sqliteHelper.getReadableDatabase();
		Cursor c = null;
		try {
			String[] colonnes = new String[] { getPrimaryKey() };
			c = db.query(getTableName(), colonnes, null, null, null, null, null);
			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				return c.getLong(0);
			}
		} finally {
			if (c != null) {
				c.close();
			}
			db.close();
		}
		return null;
	}
	
	protected long insert(ContentValues contentValues) throws DBException {
		SQLiteDatabase db = sqliteHelper.getWritableDatabase();
		try {
			long id = db.insert(getTableName(), null, contentValues);
			if (id == -1) {
				throw new DBException("error to insert row on :" + getTableName());
			}
			return id;
		} finally {
			if (db.isOpen()) {
				db.close();
			}
		}
	}
	
	protected SQLiteHelper getSqliteHelper() {
		return sqliteHelper;
	}
}

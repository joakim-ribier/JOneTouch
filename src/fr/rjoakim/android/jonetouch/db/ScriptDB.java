package fr.rjoakim.android.jonetouch.db;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;

import fr.rjoakim.android.jonetouch.bean.Script;

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
public class ScriptDB extends DBHelper {

	public static final String DEFAULT_SCRIPT_VALUE_1 = "hostname";
	
	public static final String TABLE_NAME = "script";
	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_TITLE = "title";
	public static final String COLUMN_NAME_VALUE = "value";

	public static String buildCreateTableQuery() {
		final StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE ").append(TABLE_NAME).append(" (");
		builder.append(COLUMN_NAME_ID).append(" INTEGER PRIMARY KEY, ");
		builder.append(COLUMN_NAME_TITLE).append(" TEXT NOT NULL, ");
		builder.append(COLUMN_NAME_VALUE).append(" TEXT NOT NULL, ");
		builder.append("UNIQUE(").append(COLUMN_NAME_TITLE).append(") ");
		builder.append(");");
		return builder.toString();
	}

	public static String buildInsertQueries() {
		final StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO ");
		builder.append(TABLE_NAME).append(" (").
				append(COLUMN_NAME_ID).append(",").
				append(COLUMN_NAME_TITLE).append(",").
				append(COLUMN_NAME_VALUE).append(") ");
		builder.append("VALUES ('1', '"+ DEFAULT_SCRIPT_VALUE_1 + "', '" + DEFAULT_SCRIPT_VALUE_1 + "');");
		return builder.toString();
	}
	
	public ScriptDB(Context context) {
		super(new SQLiteHelper(context));
	}

	public List<Script> findAll() throws DBException {
		SQLiteDatabase db = getSqliteHelper().getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(getTableName(), null, null, null, null, null, null);
			final List<Script> scripts = Lists.newArrayList();
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					Long id = cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_ID));
					String title = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TITLE));
					String value = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_VALUE));
					scripts.add(new Script(id, title, value));
				} while (cursor.moveToNext());
			}
			return scripts;
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}
	}
	
	public long insert(String title, String value) throws DBException {
		SQLiteDatabase db = getSqliteHelper().getWritableDatabase();
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put(ScriptDB.COLUMN_NAME_TITLE, title.trim());
			values.put(ScriptDB.COLUMN_NAME_VALUE, value.trim());
			long id = db.insert(ScriptDB.TABLE_NAME, null, values);
			if (id == -1) {
				throw new DBException("insert row error on :" + ScriptDB.TABLE_NAME);
			}
			db.setTransactionSuccessful();
			return id;
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		} finally {
			db.endTransaction();
			if (db.isOpen()) {
				db.close();
			}
		}
	}
	
	public void update(long scriptId, String title, String value) throws DBException {
		SQLiteDatabase db = getSqliteHelper().getWritableDatabase();
		db.beginTransaction();
		try {
			String whereClause = COLUMN_NAME_ID + " = ?";
			String[] whereArgs = new String[] { String.valueOf(scriptId) };
			
			ContentValues values = new ContentValues();
			values.put(ScriptDB.COLUMN_NAME_TITLE, title.trim());
			values.put(ScriptDB.COLUMN_NAME_VALUE, value.trim());
			long id = db.update(TABLE_NAME, values, whereClause, whereArgs);
			if (id != 1) {
				throw new DBException("update row error on :" + ScriptDB.TABLE_NAME);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		} finally {
			db.endTransaction();
			if (db.isOpen()) {
				db.close();
			}
		}
	}

	public void delete(long scriptId) throws DBException {
		SQLiteDatabase db = getSqliteHelper().getWritableDatabase();
		db.beginTransaction();
		try {
			String[] whereArgs = new String[] { String.valueOf(scriptId) };
			int delete = db.delete(TABLE_NAME, getPrimaryKey() + " = ?", whereArgs);
			if (delete < 1) {
				throw new DBException("error to delete row on :" + TABLE_NAME);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		} finally {
			db.endTransaction();
			if (db.isOpen()) {
				db.close();
			}
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

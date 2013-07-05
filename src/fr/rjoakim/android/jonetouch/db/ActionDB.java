package fr.rjoakim.android.jonetouch.db;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;

import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.bean.ActionScript;
import fr.rjoakim.android.jonetouch.bean.Server;

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
public class ActionDB extends DBHelper {

	private static final String DEFAULT_ACTION_TITLE = "action de démonstration"; 
	private static final String DEFAULT_ACTION_DESC = "Cette action de démonstration exécute seulement sur votre serveur la commande hostname." +
			"\n\nCommencez par créer une nouvelle connexion à l''aide du menu de gauche sur l''un de vos serveurs puis testez le script !!!\n\n Enjoy :)";
	
	public static final String TABLE_NAME = "action";
	public static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_TITLE = "title";
	private static final String COLUMN_NAME_DESCRIPTION = "description";
	private static final String COLUMN_NAME_BACKGROUND_COLOR = "background_color_hex";

	public static String buildCreateTableQuery() {
		final StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE ").append(TABLE_NAME).append(" (");
		builder.append(COLUMN_NAME_ID).append(" INTEGER PRIMARY KEY, ");
		builder.append(COLUMN_NAME_TITLE).append(" TEXT NOT NULL, ");
		builder.append(COLUMN_NAME_DESCRIPTION).append(" TEXT NOT NULL ");
		builder.append(");");
		return builder.toString();
	}

	public static String buildUpdateToCodeRelease5TableQuery() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ALTER TABLE ").append(TABLE_NAME);
		builder.append(" ADD COLUMN ").append(COLUMN_NAME_BACKGROUND_COLOR);
		builder.append(" TEXT NOT NULL DEFAULT ").append("'#99CC00'");
		return builder.toString();
	}
	
	public static String buildInsertQueries() {
		final StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO ");
		builder.append(TABLE_NAME).append(" (").
				append(COLUMN_NAME_ID).append(",").
				append(COLUMN_NAME_TITLE).append(",").
				append(COLUMN_NAME_DESCRIPTION).append(") ");
		builder.append("VALUES ('1', '" + DEFAULT_ACTION_TITLE + "', '" + DEFAULT_ACTION_DESC + "');");
		return builder.toString();
	}
	
	public ActionDB(Context context) {
		super(new SQLiteHelper(context));
	}

	private String buildSelectQuery() {
		return "SELECT action." + COLUMN_NAME_ID + " as actionId, "
				+ "action." + COLUMN_NAME_TITLE + " as actionTitle, "
				+ "action." + COLUMN_NAME_DESCRIPTION + " as actionDescription, "
				+ "action." + COLUMN_NAME_BACKGROUND_COLOR + " as actionBackgroundColor, "
				+ "actionServer." + ActionServersDB.COLUMN_NAME_SERVER + " as serverId "
				+ "FROM " + TABLE_NAME + " action "
				+ "LEFT JOIN " + ActionServersDB.TABLE_NAME + " actionServer ON action.id=actionServer.action ";
	}
	
	public long insert(String title, String description, String color) throws DBException {

		SQLiteDatabase db = getSqliteHelper().getWritableDatabase();
		db.beginTransaction();
		try {
			long actionId = insert(title, description, color, db);
			db.setTransactionSuccessful();
			return actionId;
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		} finally {
			db.endTransaction();
			if (db.isOpen()) {
				db.close();
			}
		}
	}

	public Long insert(String title, String description, String color, Server server) throws DBException {
	
		SQLiteDatabase db = getSqliteHelper().getWritableDatabase();
		db.beginTransaction();
		try {
			long actionId = insert(title, description, color, db);
			insert(actionId, server.getId(), db);
			db.setTransactionSuccessful();
			return actionId;
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		} finally {
			db.endTransaction();
			if (db.isOpen()) {
				db.close();
			}
		}
	}
	
	private Action parseAction(Cursor cursor, SQLiteDatabase db) throws DBException {
		try {
			long id = cursor.getLong(cursor.getColumnIndex("actionId"));
			String title = cursor.getString(cursor.getColumnIndex("actionTitle"));
			String description = cursor.getString(cursor.getColumnIndex("actionDescription"));
			Long serverId = cursor.getLong(cursor.getColumnIndex("serverId"));
			String backgroundColor = cursor.getString(cursor.getColumnIndex("actionBackgroundColor"));
			List<ActionScript> actionScripts = findAllScriptFromId(id, db);
			return new Action(
					id, title, description, backgroundColor,
					serverId == 0 ? null : serverId, actionScripts);
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		}
	}
	
	public Action findById(long actionId) throws DBException {
		SQLiteDatabase db = getSqliteHelper().getReadableDatabase();
		Cursor cursor = null;
		try {
			String[] args = new String[]{ String.valueOf(actionId) };
			cursor = db.rawQuery(buildSelectQuery() + "WHERE actionId = ?", args);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				return parseAction(cursor, db);
			}
			throw new DBException("action id {" + actionId + "} not found.");
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}
	}
	
	public List<Action> findAll() throws DBException {
		SQLiteDatabase db = getSqliteHelper().getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(buildSelectQuery(), null);
			List<Action> actions = Lists.newArrayList();
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					actions.add(parseAction(cursor, db));
				} while (cursor.moveToNext());
			}
			return actions;
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}
	}

	public List<ActionScript> findAllScriptFromId(long actionId, SQLiteDatabase db) throws DBException {
		Cursor cursor = null;
		try {
			List<ActionScript> actionScripts = Lists.newArrayList();
			String query = "SELECT " + ActionScriptsDB.COLUMN_NAME_ID + ", "
					+ ActionScriptsDB.COLUMN_NAME_SCRIPT + " FROM "
					+ ActionScriptsDB.TABLE_NAME
					+ " WHERE " + ActionScriptsDB.COLUMN_NAME_ACTION + " = ?";
			
			String[] arguments = new String[] { String.valueOf(actionId) };
			cursor = db.rawQuery(query, arguments);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					long id = cursor.getLong(cursor.getColumnIndex(ActionScriptsDB.COLUMN_NAME_ID));
					String script = cursor.getString(cursor.getColumnIndex(ActionScriptsDB.COLUMN_NAME_SCRIPT));
					actionScripts.add(new ActionScript(id, script));
				} while (cursor.moveToNext());
			}
			return actionScripts;
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	private long insert(String title, String description, String color, SQLiteDatabase db) throws DBException {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_TITLE, title);
		values.put(COLUMN_NAME_DESCRIPTION, description);
		values.put(COLUMN_NAME_BACKGROUND_COLOR, color);
		long id = db.insert(getTableName(), null, values);
		if (id == -1) {
			throw new DBException("insert row error on :" + TABLE_NAME);
		}
		return id;
	}

	private void insert(long actionId, String script, SQLiteDatabase db) throws DBException {
		ContentValues values = new ContentValues();
		values.put(ActionScriptsDB.COLUMN_NAME_ACTION, actionId);
		values.put(ActionScriptsDB.COLUMN_NAME_SCRIPT, script.trim());
		long id = db.insert(ActionScriptsDB.TABLE_NAME, null, values);
		if (id == -1) {
			throw new DBException("insert row error on :" + ActionScriptsDB.TABLE_NAME);
		}
	}
	
	private void insert(long actionId, long serverId, SQLiteDatabase db) throws DBException {
		ContentValues values = new ContentValues();
		values.put(ActionServersDB.COLUMN_NAME_ACTION, actionId);
		values.put(ActionServersDB.COLUMN_NAME_SERVER, serverId);
		long id = db.insert(ActionServersDB.TABLE_NAME, null, values);
		if (id == -1) {
			throw new DBException("insert row error on :" + ActionServersDB.TABLE_NAME);
		}
	}


	public void update(long actionId, String title, String description, String color,
			Long serverOldId, Long serverNewId) throws DBException {

		SQLiteDatabase db = getSqliteHelper().getWritableDatabase();
		db.beginTransaction();
		try {
			updateTitle(actionId, title, db);
			updateDescription(actionId, description, db);
			updateColor(actionId, color, db);
			
			if (serverOldId != null) {
				delete(actionId, serverOldId, db);
			}
			
			if (serverNewId != null) {
				insert(actionId, serverNewId, db);
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
	
	private void updateTitle(long actionId, String title, SQLiteDatabase db) throws DBException {
		String[] whereArgs = new String[] { String.valueOf(actionId) };
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_TITLE, title);
		int id = db.update(TABLE_NAME, values, getPrimaryKey() + " = ?", whereArgs);
		if (id < 1) {
			throw new DBException("error updating row on :" + TABLE_NAME);
		}
	}
	
	private void updateDescription(long actionId, String description, SQLiteDatabase db) throws DBException {
		String[] whereArgs = new String[] { String.valueOf(actionId) };
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_DESCRIPTION, description);
		int id = db.update(TABLE_NAME, values, getPrimaryKey() + " = ?", whereArgs);
		if (id < 1) {
			throw new DBException("error updating row on :" + TABLE_NAME);
		}
	}
	
	private void updateColor(long actionId, String color, SQLiteDatabase db) throws DBException {
		String[] whereArgs = new String[] { String.valueOf(actionId) };
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_BACKGROUND_COLOR, color);
		int id = db.update(TABLE_NAME, values, getPrimaryKey() + " = ?", whereArgs);
		if (id < 1) {
			throw new DBException("error updating row on :" + TABLE_NAME);
		}
	}
	
	private void delete(long actionId, long serverId, SQLiteDatabase db) throws DBException {
		String[] whereArgs = new String[] { String.valueOf(actionId), String.valueOf(serverId) };
		String where = ActionServersDB.COLUMN_NAME_ACTION + " = ? AND " + ActionServersDB.COLUMN_NAME_SERVER + " = ?";
		long id = db.delete(ActionServersDB.TABLE_NAME, where, whereArgs);
		if (id == 0) {
			throw new DBException("insert row error on :" + ActionServersDB.TABLE_NAME);
		}
	}
	
	public void deleteScript(long actionScriptId) throws DBException {
		SQLiteDatabase db = getSqliteHelper().getWritableDatabase();
		db.beginTransaction();
		try {
			deleteScript(actionScriptId, db);
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
	
	private void deleteScript(long actionScriptId, SQLiteDatabase db) throws DBException {
		String[] whereArgs = new String[] { String.valueOf(actionScriptId) };
		String where = ActionScriptsDB.COLUMN_NAME_ID + " = ?";
		long id = db.delete(ActionScriptsDB.TABLE_NAME, where, whereArgs);
		if (id == 0) {
			throw new DBException("delete row error on :" + ActionScriptsDB.TABLE_NAME);
		}
	}

	public void updateActionScript(long actionId, List<ActionScript> actionScripts,
			List<String> newScripts) throws DBException {
		
		SQLiteDatabase db = getSqliteHelper().getWritableDatabase();
		db.beginTransaction();
		try {
			for (ActionScript actionScript: actionScripts) {
				deleteScript(actionScript.getId(), db);
			}
			
			for (String script: newScripts) {
				insert(actionId, script, db);
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
	
	public void delete(Action action) throws DBException {
		SQLiteDatabase db = getSqliteHelper().getWritableDatabase();
		db.beginTransaction();
		try {
			String[] whereArgs = new String[] { String.valueOf(action.getId()) };
			String where = ActionScriptsDB.COLUMN_NAME_ACTION + " = ?";
			db.delete(ActionScriptsDB.TABLE_NAME, where, whereArgs);
			
			whereArgs = new String[] { String.valueOf(action.getId()) };
			where = ActionServersDB.COLUMN_NAME_ACTION + " = ?";
			db.delete(ActionServersDB.TABLE_NAME, where, whereArgs);
			
			whereArgs = new String[] { String.valueOf(action.getId()) };
			where = COLUMN_NAME_ID + " = ?";
			long id = db.delete(TABLE_NAME, where, whereArgs);
			if (id == 0) {
				throw new DBException("delete row error on :" + TABLE_NAME);
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

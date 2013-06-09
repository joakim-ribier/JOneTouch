package fr.rjoakim.android.jonetouch.db;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;

import fr.rjoakim.android.jonetouch.bean.Authentication;
import fr.rjoakim.android.jonetouch.bean.AuthenticationType;
import fr.rjoakim.android.jonetouch.bean.AuthenticationTypeEnum;
import fr.rjoakim.android.jonetouch.bean.NoAuthentication;
import fr.rjoakim.android.jonetouch.bean.SSHAuthenticationPassword;
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
public class ServerDB extends DBHelper {

	public static final String TABLE_NAME = "server";
	public static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_TITLE = "title";
	private static final String COLUMN_NAME_HOST = "host";
	private static final String COLUMN_NAME_PORT = "port";
	private static final String COLUMN_NAME_DESCRIPTION = "description";
	private static final String COLUMN_NAME_AUTHENTICATION_TYPE = "authentication_type";

	public static String buildCreateTableQuery() {
		final StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE ").append(TABLE_NAME).append(" (");
		builder.append(COLUMN_NAME_ID).append(" INTEGER PRIMARY KEY, ");
		builder.append(COLUMN_NAME_TITLE).append(" TEXT NOT NULL, ");
		builder.append(COLUMN_NAME_HOST).append(" TEXT NOT NULL, ");
		builder.append(COLUMN_NAME_PORT).append(" INTEGER NOT NULL, ");
		builder.append(COLUMN_NAME_DESCRIPTION).append(" TEXT NOT NULL, ");
		builder.append(COLUMN_NAME_AUTHENTICATION_TYPE).append(" INTEGER NOT NULL, ");
		builder.append("UNIQUE(").append(COLUMN_NAME_HOST).append(", ").append(COLUMN_NAME_AUTHENTICATION_TYPE).append("), ");
		builder.append("FOREIGN KEY(");
		builder.append(COLUMN_NAME_AUTHENTICATION_TYPE).append(") REFERENCES ");
		builder.append(AuthenticationTypeDB.TABLE_NAME).append("(").append(AuthenticationTypeDB.COLUMN_NAME_ID).append(")");
		builder.append(");");
		return builder.toString();
	}

	public ServerDB(Context context) {
		super(new SQLiteHelper(context));
	}

	private String buildSelectQuery() {
		return "SELECT server." + COLUMN_NAME_ID + " as serverId, "
				+ COLUMN_NAME_TITLE
				+ ", " + COLUMN_NAME_HOST + ", " + COLUMN_NAME_PORT + ", "
				+ COLUMN_NAME_DESCRIPTION + ", "
				+ COLUMN_NAME_AUTHENTICATION_TYPE + ", "
				+ AuthenticationTypeDB.COLUMN_NAME_TYPE + ", "
				+ "ssh." + SSHAuthenticationPasswordDB.COLUMN_NAME_LOGIN + " as loginssh, "
				+ "ssh." + SSHAuthenticationPasswordDB.COLUMN_NAME_PWD + " as pwdssh, "
				+ "no." + NoAuthenticationDB.COLUMN_NAME_LOGIN + " as loginno "
				+ "FROM " + TABLE_NAME + " server "
				+ "LEFT JOIN " + AuthenticationTypeDB.TABLE_NAME + " authType ON server.authentication_type=authType.id "
				+ "LEFT JOIN " + SSHAuthenticationPasswordDB.TABLE_NAME + " ssh ON server.id=ssh.server "
				+ "LEFT JOIN " + NoAuthenticationDB.TABLE_NAME + " no ON server.id=no.server";
	}
	
	public Long insert(String title, String host, int port, String description, AuthenticationTypeEnum authenticationTypeEnum,
			String login, String password) throws DBException {

		SQLiteDatabase db = getSqliteHelper().getWritableDatabase();
		db.beginTransaction();
		try {
			final Long serverId = insertServer(title, host, port, description, authenticationTypeEnum, db);
			insertSSHAuthenticationPasswordType(serverId, login, password, db);
			db.setTransactionSuccessful();
			return serverId;
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		} finally {
			db.endTransaction();
			if (db.isOpen()) {
				db.close();
			}
		}
	}

	public Long insert(String title, String host, int port, String description, AuthenticationTypeEnum authenticationTypeEnum,
			String login) throws DBException {

		SQLiteDatabase db = getSqliteHelper().getWritableDatabase();
		db.beginTransaction();
		try {
			final Long serverId = insertServer(title, host, port, description, authenticationTypeEnum, db);
			insertNoAuthenticationType(serverId, login, db);
			db.setTransactionSuccessful();
			return serverId;
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		} finally {
			db.endTransaction();
			if (db.isOpen()) {
				db.close();
			}
		}
	}
	
	public List<Server> findAll() throws DBException {
		SQLiteDatabase db = getSqliteHelper().getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(buildSelectQuery(), null);
			List<Server> authenticationTypes = Lists.newArrayList();
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					authenticationTypes.add(buildServerFromCursor(cursor));
				} while (cursor.moveToNext());
			}
			return authenticationTypes;
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}
	}
	
	public Server findFromId(long id) throws DBException {
		SQLiteDatabase db = getSqliteHelper().getReadableDatabase();
		Cursor cursor = null;
		try {
			String query = buildSelectQuery() + " WHERE server.id = ?";
			String[] arguments = new String[] { String.valueOf(id) };
			cursor = db.rawQuery(query, arguments);
			Server server = null;
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				return buildServerFromCursor(cursor);
			}
			return server;
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			db.close();
		}
	}
	
	public Long findByHostAndAuthenticationType(String host,
			AuthenticationTypeEnum authenticationTypeEnum) throws DBException {
		
		SQLiteDatabase db = getSqliteHelper().getReadableDatabase();
		Cursor cursor = null;
		try {
			String where = COLUMN_NAME_HOST + " = ?" + " AND " + COLUMN_NAME_AUTHENTICATION_TYPE + " = ?";
			String[] arguments = new String[] { host, String.valueOf(authenticationTypeEnum.getId()) };
			cursor = db.query(getTableName(), null, where, arguments, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				return cursor.getLong(cursor.getColumnIndex(getPrimaryKey()));
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

	public void delete(long serverId, AuthenticationTypeEnum authenticationTypeEnum) throws DBException {
		SQLiteDatabase db = getSqliteHelper().getWritableDatabase();
		db.beginTransaction();
		try {
			deleteAuthentication(serverId, authenticationTypeEnum, db);
			
			String[] whereArgs = new String[] { String.valueOf(serverId) };
			db.delete(ActionServersDB.TABLE_NAME, ActionServersDB.COLUMN_NAME_SERVER + " = ?", whereArgs);
			
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
	
	public Long update(Server server, String title, String host, Integer port, String description,
			AuthenticationType type, String login, String password) throws DBException {
		
		SQLiteDatabase db = getSqliteHelper().getWritableDatabase();
		db.beginTransaction();
		try {
			long serverId = server.getId();
			String[] whereArgs = new String[] { String.valueOf(serverId) };
			final ContentValues values = new ContentValues();
			values.put(COLUMN_NAME_TITLE, title);
			values.put(COLUMN_NAME_HOST, host);
			values.put(COLUMN_NAME_PORT, port);
			values.put(COLUMN_NAME_DESCRIPTION, description);
			values.put(COLUMN_NAME_AUTHENTICATION_TYPE, type.getType().getId());
			
			int id = db.update(TABLE_NAME, values, getPrimaryKey() + " = ?", whereArgs);
			if (id < 1) {
				throw new DBException("error to update row on :" + TABLE_NAME);
			}
			
			deleteAuthentication(serverId,
					server.getAuthentication().getAuthenticationTypeEnum(), db);
			
			switch (type.getType()) {
			case NO_AUTHENTICATION:
				insertNoAuthenticationType(serverId, login, db);
				break;
			case SSH_AUTHENTICATION_PASSWORD:
				insertSSHAuthenticationPasswordType(serverId, login, password, db);
				break;
			default:
				throw new DBException("authentication type not supported");
			}
			db.setTransactionSuccessful();
			return serverId;
		} catch (Exception e) {
			throw new DBException(e.getMessage(), e);
		} finally {
			db.endTransaction();
			if (db.isOpen()) {
				db.close();
			}
		}
	}
	
	private void insertSSHAuthenticationPasswordType(final Long serverId, String login,
			String password, SQLiteDatabase db) throws DBException {
		
		ContentValues values = new ContentValues();
		values.put(SSHAuthenticationPasswordDB.COLUMN_NAME_LOGIN, login);
		values.put(SSHAuthenticationPasswordDB.COLUMN_NAME_PWD, password);
		values.put(SSHAuthenticationPasswordDB.COLUMN_NAME_SERVER, serverId);
		Long id = db.insert(SSHAuthenticationPasswordDB.TABLE_NAME, null, values);
		if (id < 1) {
			throw new DBException("error to insert row on :" + SSHAuthenticationPasswordDB.TABLE_NAME);
		}
	}

	private Long insertServer(String title, String host, int port, String description,
			AuthenticationTypeEnum authenticationTypeEnum, SQLiteDatabase db) {
		
		final ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_TITLE, title);
		values.put(COLUMN_NAME_HOST, host);
		values.put(COLUMN_NAME_PORT, port);
		values.put(COLUMN_NAME_DESCRIPTION, description);
		values.put(COLUMN_NAME_AUTHENTICATION_TYPE, authenticationTypeEnum.getId());
		
		return db.insert(getTableName(), null, values);
	}

	private void insertNoAuthenticationType(final Long serverId, String login, SQLiteDatabase db) throws DBException {
		ContentValues values = new ContentValues();
		values.put(NoAuthenticationDB.COLUMN_NAME_LOGIN, login);
		values.put(NoAuthenticationDB.COLUMN_NAME_SERVER, serverId);
		Long id = db.insert(NoAuthenticationDB.TABLE_NAME, null, values);
		if (id < 1) {
			throw new DBException("error to insert row on :" + NoAuthenticationDB.TABLE_NAME);
		}
	}
	
	private Server buildServerFromCursor(Cursor cursor) throws DBException {
		long id = cursor.getLong(cursor.getColumnIndex("serverId"));
		String title = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TITLE));
		String host = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HOST));
		int port = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PORT));
		String desc = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DESCRIPTION));
		long type = cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_AUTHENTICATION_TYPE));
		
		String labelType = cursor.getString(cursor.getColumnIndex(AuthenticationTypeDB.COLUMN_NAME_TYPE));
		
		Authentication authentication = null;
		switch (AuthenticationTypeEnum.fromId(type)) {
		case NO_AUTHENTICATION:
			authentication = new NoAuthentication(
					cursor.getString(cursor.getColumnIndex("loginno")), labelType);
			break;
		case SSH_AUTHENTICATION_PASSWORD:
			authentication = new SSHAuthenticationPassword(
					cursor.getString(cursor.getColumnIndex("loginssh")),
					cursor.getString(cursor.getColumnIndex("pwdssh")),
					labelType);
			break;
		default:
			throw new DBException("authentication type not found");
		}
		return new Server(id, title, host, port, desc, authentication);
	}

	private void deleteAuthentication(long serverId, AuthenticationTypeEnum authenticationTypeEnum, SQLiteDatabase db) throws DBException {
		int deleteAuthServer = -1;
		String[] whereArgs = new String[] { String.valueOf(serverId) };
		switch (authenticationTypeEnum) {
		case NO_AUTHENTICATION:
			deleteAuthServer = db.delete(NoAuthenticationDB.TABLE_NAME,
					NoAuthenticationDB.COLUMN_NAME_SERVER + " = ?", whereArgs);
			if (deleteAuthServer < 1) {
				throw new DBException("error to delete row on :" + NoAuthenticationDB.TABLE_NAME);
			}
			break;
		case SSH_AUTHENTICATION_PASSWORD:
			deleteAuthServer = db.delete(SSHAuthenticationPasswordDB.TABLE_NAME,
					SSHAuthenticationPasswordDB.COLUMN_NAME_SERVER + " = ?", whereArgs);
			if (deleteAuthServer < 1) {
				throw new DBException("error to delete row on :" + SSHAuthenticationPasswordDB.TABLE_NAME);
			}
			break;
		default:
			throw new DBException("authentication type not supported");
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

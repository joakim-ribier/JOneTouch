package fr.rjoakim.android.jonetouch.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.common.collect.Lists;

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
public class SQLiteHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "actionssvr";
    
	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("PRAGMA foreign_keys=ON;");
		db.execSQL(UserDB.buildCreateTableQuery());
		db.execSQL(ServerDB.buildCreateTableQuery());
		db.execSQL(AuthenticationTypeDB.buildCreateTableQuery());
		onCreate(db, AuthenticationTypeDB.buildInsertQueries());
		db.execSQL(SSHAuthenticationPasswordDB.buildCreateTableQuery());
		db.execSQL(NoAuthenticationDB.buildCreateTableQuery());
		db.execSQL(ActionDB.buildCreateTableQuery());
		onCreate(db, ActionDB.buildInsertQueries());
		db.execSQL(ScriptDB.buildCreateTableQuery());
		onCreate(db, ScriptDB.buildInsertQueries());
		db.execSQL(ActionScriptsDB.buildCreateTableQuery());
		onCreate(db, ActionScriptsDB.buildInsertQueries());
		db.execSQL(ActionServersDB.buildCreateTableQuery());
	}

	private void onCreate(SQLiteDatabase db, Iterable<String> queries) {
		for (String query: queries) {
			db.execSQL(query);
		}
	}
	
	private void onCreate(SQLiteDatabase db, String query) {
		onCreate(db, Lists.newArrayList(query));
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}

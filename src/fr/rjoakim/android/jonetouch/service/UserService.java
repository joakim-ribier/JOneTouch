package fr.rjoakim.android.jonetouch.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import android.content.Context;
import fr.rjoakim.android.jonetouch.bean.User;
import fr.rjoakim.android.jonetouch.db.DBException;
import fr.rjoakim.android.jonetouch.db.UserDB;

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
public class UserService {
	
	private final UserDB userDB;

	public UserService(Context context) {
		userDB = new UserDB(context);
	}
	
	public boolean isExists() {
		Long find = userDB.findUser();
		return find != null;
	}
	
	public Long create(String email, String password) throws ServiceException {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(email), "email field is required");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(password), "password field is required");
		String pwdEncode = encodePassword(email, password);
		try {
			return userDB.insert(email, pwdEncode);
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	public User get(long userId) throws ServiceException {
		try {
			return userDB.findUserById(userId);
		} catch (DBException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	public User get() throws ServiceException {
		Long id = userDB.findUser();
		if (id == null) {
			return null;
		}
		return get(id);
	}

	public String encodePassword(String email, String password) {
		try {
			String str = email + password;
			MessageDigest messageDigest = java.security.MessageDigest.getInstance("SHA-256");
			return new String(Hex.encodeHex(messageDigest.digest(str.getBytes())));
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
}

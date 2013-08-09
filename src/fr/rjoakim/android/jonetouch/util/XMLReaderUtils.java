package fr.rjoakim.android.jonetouch.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.bean.ActionScript;
import fr.rjoakim.android.jonetouch.bean.Authentication;
import fr.rjoakim.android.jonetouch.bean.AuthenticationTypeEnum;
import fr.rjoakim.android.jonetouch.bean.MyAuthentication;
import fr.rjoakim.android.jonetouch.bean.NoAuthentication;
import fr.rjoakim.android.jonetouch.bean.SSHAuthenticationPassword;
import fr.rjoakim.android.jonetouch.bean.Server;
import fr.rjoakim.android.jonetouch.dialog.bean.BackupXmlParse;


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
public class XMLReaderUtils extends XMLParserUtils {
	
	public static BackupXmlParse parse(String xml, MyAuthentication myAuthentication, String key,
			boolean isParseServers, boolean isParseActions) throws XMLParserUtilsException {

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
			document.getDocumentElement().normalize();
			
			checkBackupFormat(document);
			boolean isEncrypt =
					isEncryptBackupPassword(document.getDocumentElement());
			
			List<Server> servers = Lists.newArrayList();
			List<Action> actions = Lists.newArrayList();
			
			if (isParseServers) {
				parseServersElements(document,
						myAuthentication, key, servers, isEncrypt);
			}
			
			if (isParseActions) {
				parseActionsElements(document, actions);
			}

			return new BackupXmlParse(servers, actions);
		} catch (ParserConfigurationException e) {
			throw new XMLParserUtilsException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new XMLParserUtilsException(e.getMessage(), e);
		} catch (IOException e) {
			throw new XMLParserUtilsException(e.getMessage(), e);
		}
	}

	private static boolean isEncryptBackupPassword(Element parent) {
		String value = getRequiredAttribute(parent, AUTHENTICATION);
		return value.equals(ENCRYPT);
	}
	
	private static void parseServersElements(Document document,
			MyAuthentication myAuthentication, String key, List<Server> servers,
			boolean isEncrypt) throws XMLParserUtilsException {
		
		NodeList serversNodeList = document.getElementsByTagName(SERVER);
		for (int cpt = 0; cpt < serversNodeList.getLength(); cpt++) {
			
			Node serverNode = serversNodeList.item(cpt);
			if (serverNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element serverElement = (Element) serverNode;
				
				long id = getRequiredLongAttribute(serverElement, DATABASE_ID);
				int port = getRequiredIntAttribute(serverElement, PORT);
				
				String title = getRequiredElementValue(serverElement, TITLE);
				String hostname = getRequiredElementValue(serverElement, HOSTNAME);
				String description = getRequiredElementValue(serverElement, DESCRIPTION);
				Authentication authentication = parseAuthenticationElement(serverElement, myAuthentication, key, isEncrypt);

				servers.add(new Server(
						id, title, hostname, port, description, authentication));
			}
		}
	}

	private static Authentication parseAuthenticationElement(Element serverElement,
			MyAuthentication myAuthentication, String key, boolean isEncrypt) throws XMLParserUtilsException {
		
		Element serverAuthentication = getRequiredElement(serverElement, AUTHENTICATION);
		long type = getRequiredLongAttribute(serverAuthentication, TYPE);
		AuthenticationTypeEnum authenticationTypeEnum = AuthenticationTypeEnum.fromId(type);
		Authentication authentication = null;
		switch (authenticationTypeEnum) {
		case NO_AUTHENTICATION:
			authentication = new NoAuthentication(
					getRequiredElementValue(serverElement, LOGIN), null);
			break;
		case SSH_AUTHENTICATION_PASSWORD:
			authentication = parseSSHAuthenticationPasword(
					serverElement, myAuthentication, key, isEncrypt);
			
			break;
		default :
			throw new XMLParserUtilsException("authentication field is required.");
		}
		return authentication;
	}

	private static Authentication parseSSHAuthenticationPasword(Element serverElement,
			MyAuthentication myAuthentication, String key, boolean isEncrypt) throws XMLParserUtilsException {
		
		String passwordValue = getRequiredElementValue(serverElement, PASSWORD);
		String newPwd = getPassword(key, isEncrypt, passwordValue);
		String pwdEncoded = encryptPassword(myAuthentication, newPwd);
		String login = getRequiredElementValue(serverElement, LOGIN);
		return new SSHAuthenticationPassword(login, pwdEncoded, null);
	}

	private static String encryptPassword(MyAuthentication myAuthentication, String newPwd) {
		String pwdEncoded = "";
		try {
			pwdEncoded = CryptographyUtils.encrypt(newPwd, myAuthentication.getKey());
		} catch (CryptographyException e) {
			pwdEncoded = "error decrypt password";
		}
		return pwdEncoded;
	}

	private static String getPassword(String key,
			boolean isEncrypt, String password) {
		
		String newPwd = password;
		if (isEncrypt) {
			 try {
				 if (!Strings.isNullOrEmpty(key)) {
					 newPwd = CryptographyUtils.decrypt(password, key);
				 }
			} catch (CryptographyException e) {
				newPwd = "error decrypt password";
			}
		}
		return newPwd;
	}

	private static long getRequiredLongAttribute(Element parent, String name) throws XMLParserUtilsException {
		Long value =  getOptionalLongAttribute(parent, name);
		if (value != null) {
			return value.longValue();
		}
		throw new XMLParserUtilsException(name  + " field is required.");
	}
	
	private static Long getOptionalLongAttribute(Element parent, String name) {
		return Longs.tryParse(parent.getAttribute(name));
	}
	
	private static int getRequiredIntAttribute(Element parent, String name) throws XMLParserUtilsException {
		Integer value = Ints.tryParse(parent.getAttribute(name));
		if (value != null) {
			return value.intValue();
		}
		throw new XMLParserUtilsException(name  + " field is required.");
	}
	
	private static String getRequiredAttribute(Element parent, String name) {
		String value = parent.getAttribute(name);
		Preconditions.checkNotNull(value, name + " field is required");
		return value;
	}

	private static String getRequiredElementValue(Element parent, String name) throws XMLParserUtilsException {
		NodeList nodeList = parent.getElementsByTagName(name);
		Node node = nodeList.item(0);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			return element.getTextContent();
		}
		throw new XMLParserUtilsException(name + " field is required.");
	}

	private static Element getRequiredElement(Element parent, String name) throws XMLParserUtilsException {
		NodeList nodeList = parent.getElementsByTagName(name);
		Node node = nodeList.item(0);
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			return (Element) node;
		}
		throw new XMLParserUtilsException(name + " field is required.");
	}
	
	private static void parseActionsElements(Document document, List<Action> actions) throws XMLParserUtilsException {
		NodeList actionsNodeList = document.getElementsByTagName(ACTION);
		for (int cpt = 0; cpt < actionsNodeList.getLength(); cpt++) {
			
			Node actionNode = actionsNodeList.item(cpt);
			if (actionNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element actionElement = (Element) actionNode;
				
				String backgroundHexColor = getRequiredAttribute(actionElement, BACKGROUND_COLOR);
				Long serverId = getOptionalLongAttribute(actionElement, SERVER_ID);
				String title = getRequiredElementValue(actionElement, TITLE);
				String description = getRequiredElementValue(actionElement, DESCRIPTION);
				
				List<ActionScript> actionScripts = Lists.newArrayList();
				parseActionScriptsElements(actionElement, actionScripts);
				actions.add(new Action(
						-1, title, description,
						backgroundHexColor, serverId, actionScripts));
			}
		}
	}

	private static void parseActionScriptsElements(
			Element actionElement, List<ActionScript> actionScripts) {
		
		NodeList actionScriptsNodeList = actionElement.getElementsByTagName(SCRIPT);
		for (int cpt = 0; cpt < actionScriptsNodeList.getLength(); cpt++) {
			
			Node scriptNode = actionScriptsNodeList.item(cpt);
			if (scriptNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element scriptElement = (Element) scriptNode;
				String script = scriptElement.getTextContent();
				actionScripts.add(new ActionScript(-1, script));
			}
		}
	}

	private static void checkBackupFormat(Document document) throws XMLParserUtilsException {
		String name = document.getDocumentElement().getAttribute(NAME);
		String authentication = document.getDocumentElement().getAttribute(AUTHENTICATION);
		
		try {
			Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "name field is required");
			Preconditions.checkArgument(!Strings.isNullOrEmpty(authentication), "authentication field is required");
			Preconditions.checkArgument(name.equals(BACKUP), "name is not equals to " + BACKUP);
		} catch(IllegalArgumentException ex) {
			throw new XMLParserUtilsException(ex.getMessage(), ex);
		}
		
		if (!authentication.equals(ENCRYPT) && !authentication.equals(DECRYPT)) {
			throw new XMLParserUtilsException("authentication field wrong format.");
		}
	}
}

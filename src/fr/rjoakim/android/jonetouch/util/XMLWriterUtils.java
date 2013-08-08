package fr.rjoakim.android.jonetouch.util;

import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.joda.time.DateTime;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.rjoakim.android.jonetouch.bean.Action;
import fr.rjoakim.android.jonetouch.bean.ActionScript;
import fr.rjoakim.android.jonetouch.bean.Authentication;
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
public class XMLWriterUtils {
	
	private static final String JONETOUCH = "jonetouch";
	private static final String NAME = "name";
	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";
	private static final String DATETIME = "datetime";
	private static final String SERVERS = "servers";
	private static final String SERVER = "server";
	private static final String HOSTNAME = "hostname";
	private static final String PORT = "port";
	private static final String DATABASE_ID = "databaseId";
	private static final String AUTHENTICATION = "authentication";
	private static final String TYPE = "type";
	private static final String LOGIN = "login";
	private static final String PASSWORD = "password";
	private static final String SCRIPTS = "scripts";
	private static final String SCRIPT = "script";
	private static final String ACTIONS = "actions";
	private static final String ACTION = "action";
	private static final String TITLE = "title";
	private static final String SERVER_ID = "serverId";
	private static final String BACKGROUND_COLOR = "backgroundColor";
	private static final String DESCRIPTION = "description";

	public static String write(String key, List<Server> servers, List<Action> actions,
			int versionCode, String versionName) throws XMLWriterUtilsException {
		
		return writeXMLDocument(key, servers, actions, versionCode, versionName);
	}
	
	public static String write(List<Server> servers, List<Action> actions,
			int versionCode, String versionName) throws XMLWriterUtilsException {
		
		return writeXMLDocument(null, servers, actions, versionCode, versionName);
	}

	private static String writeXMLDocument(String key, List<Server> servers, List<Action> actions
			, int versionCode, String versionName) throws XMLWriterUtilsException {
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			Document document = builder.newDocument();
			Element rootElement = createRootElement(document, key, versionCode, versionName);
			document.appendChild(rootElement);
			
			appendServerListElement(key, rootElement, document, servers);
			appendActionListElement(rootElement, document, actions);
			
			return transform(document);
		} catch (ParserConfigurationException e) {
			throw new XMLWriterUtilsException(e.getMessage(), e);
		}
	}

	private static Element createRootElement(Document document, String key, int versionCode, String versionName) {
		Element element = document.createElement(JONETOUCH);
		
		element.setAttributeNode(
				createAttribute(document, NAME, "backup"));
		
		element.setAttributeNode(
				createAttribute(document, DATETIME, DateTime.now().toString()));
		
		if (key != null) {
			element.setAttributeNode(
					createAttribute(document, AUTHENTICATION, "decrypt"));
		} else {
			element.setAttributeNode(
					createAttribute(document, AUTHENTICATION, "encrypt"));
		}
		
		element.setAttributeNode(
				createAttribute(document, VERSION_CODE, String.valueOf(versionCode)));
		
		element.setAttributeNode(
				createAttribute(document, VERSION_NAME, versionName));
		
		return element;
	}

	private static void appendServerListElement(String key, Element rootElement,
			Document document, List<Server> servers) {
		
		Element serversElement = document.createElement(SERVERS);
		rootElement.appendChild(serversElement);
		for (Server server: servers) {
			try {
				Element serverElement = document.createElement(SERVER);
				serversElement.appendChild(serverElement);
				serverElement.setAttributeNode(
						createAttribute(document, DATABASE_ID, String.valueOf(server.getId())));
				
				serverElement.setAttributeNode(
						createAttribute(document, PORT, String.valueOf(server.getPort())));
				
				serverElement.appendChild(
						createElementWithCDATASection(document, TITLE, server.getTitle()));
				
				serverElement.appendChild(
						createElementWithTextNode(document, HOSTNAME, server.getHost()));
				
				serverElement.appendChild(
						createElementWithCDATASection(document, DESCRIPTION, server.getDescription()));
				appendAuthenticationElement(key, document, server, serverElement);
			} catch (CryptographyException e) {
				// next element
			}
		}
	}

	private static void appendAuthenticationElement(String key, Document document,
			Server server, Element serverElement) throws CryptographyException {
		
		Authentication authentication = server.getAuthentication();
		Element authenticationElement = document.createElement(AUTHENTICATION);
		authenticationElement.setAttributeNode(
				createAttribute(document, TYPE,
						String.valueOf(authentication.getAuthenticationTypeEnum().getId())));
		serverElement.appendChild(authenticationElement);
		
		switch (authentication.getAuthenticationTypeEnum()) {
		case NO_AUTHENTICATION:
			NoAuthentication noAuthentication = (NoAuthentication) authentication;
			authenticationElement.appendChild(
					createElementWithTextNode(document, LOGIN, noAuthentication.getLogin()));
			break;
		case SSH_AUTHENTICATION_PASSWORD:
			SSHAuthenticationPassword sshAuth = (SSHAuthenticationPassword) authentication;
			authenticationElement.appendChild(
					createElementWithTextNode(document, LOGIN, sshAuth.getLogin()));
			if (key != null) {
				authenticationElement.appendChild(
						createElementWithTextNode(document, PASSWORD, sshAuth.getDecryptPassword(key)));
			} else {
				authenticationElement.appendChild(
						createElementWithTextNode(document, PASSWORD, sshAuth.getPassword()));
			}
			break;
		}
	}
	
	private static void appendActionListElement(Element rootElement,
			Document document, List<Action> actions) {
		
		Element actionsElement = document.createElement(ACTIONS);
		rootElement.appendChild(actionsElement);
		for (Action action: actions) {
			
			Element actionElement = document.createElement(ACTION);
			actionsElement.appendChild(actionElement);
			
			actionElement.setAttributeNode(
					createAttribute(document, BACKGROUND_COLOR, action.getBackgroundHexColor()));
			
			if (action.getServerId() != null) {
				actionElement.setAttributeNode(
						createAttribute(document, SERVER_ID, String.valueOf(action.getServerId())));
			}
			
			actionElement.appendChild(
					createElementWithCDATASection(document, TITLE, action.getTitle()));
			
			actionElement.appendChild(
					createElementWithCDATASection(document, DESCRIPTION, action.getDescription()));
			
			Element actionScriptsElement = document.createElement(SCRIPTS);
			actionElement.appendChild(actionScriptsElement);
			for (ActionScript actionScript: action.getActionScripts()) {
				actionScriptsElement.appendChild(
						createElementWithCDATASection(document, SCRIPT, actionScript.getScript()));
			}
		}
	}
	
	private static String transform(Document document) throws XMLWriterUtilsException {
		try {
			TransformerFactory transformerFactory =
					TransformerFactory.newInstance();
			
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			
			DOMSource source = new DOMSource(document);
			StringWriter stringWriter = new StringWriter();
			StreamResult result = new StreamResult(stringWriter);

			transformer.transform(source, result);

			return stringWriter.getBuffer().toString();
		
		} catch (TransformerConfigurationException e) {
			throw new XMLWriterUtilsException(e.getMessage(), e);
		} catch (TransformerException e) {
			throw new XMLWriterUtilsException(e.getMessage(), e);
		}
	}

	private static Element createElementWithTextNode(
			Document document, String name, String value) {
		
		Element element = document.createElement(name);
		element.appendChild(document.createTextNode(value));
		return element;
	}
	
	private static Element createElementWithCDATASection(
			Document document, String name, String value) {
		
		Element element = document.createElement(name);
		element.appendChild(document.createCDATASection(value));
		return element;
	}
	
	private static Attr createAttribute(Document document, String name, String value) {
		Attr attr = document.createAttribute(name);
		attr.setValue(value);
		return attr;
	}
}

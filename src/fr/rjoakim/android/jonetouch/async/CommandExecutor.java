package fr.rjoakim.android.jonetouch.async;

import java.io.IOException;
import java.security.PublicKey;

import net.schmizz.sshj.AndroidConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.userauth.method.AuthPassword;
import net.schmizz.sshj.userauth.password.PasswordFinder;
import net.schmizz.sshj.userauth.password.PasswordUtils;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import fr.rjoakim.android.jonetouch.MyTerminal;
import fr.rjoakim.android.jonetouch.R;
import fr.rjoakim.android.jonetouch.bean.MyAuthentication;
import fr.rjoakim.android.jonetouch.bean.NoAuthentication;
import fr.rjoakim.android.jonetouch.bean.SSHAuthenticationPassword;
import fr.rjoakim.android.jonetouch.bean.Server;
import fr.rjoakim.android.jonetouch.util.CryptographyException;

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
public class CommandExecutor extends AsyncTask<String, String, Void>{

	private final MyAuthentication myAuthentication;

	private final Activity activity;
	private final MyTerminal myTerminal;
	
	private SSHClient ssh;
	private Server server;

	public CommandExecutor(Activity activity, MyTerminal myTerminal, MyAuthentication myAuthentication) {
		this.activity = activity;
		this.myTerminal = myTerminal;
		this.myAuthentication = myAuthentication;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Toast.makeText(activity, getString(R.string.command_executor_execute),
				Toast.LENGTH_LONG).show();
		
		myTerminal.show();
		
		ssh = new SSHClient(new AndroidConfig());
		ssh.addHostKeyVerifier(new HostKeyVerifier() {
			@Override
			public boolean verify(String arg0, int arg1, PublicKey arg2) {
				return true;
			}
		});
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		if (values[0].equals("success")) {
			myTerminal.addInfo(values[1]);
		} else {
			myTerminal.addError(values[1]);
		}
		myTerminal.smoothScrollTo();
	}
	
	@Override
	protected Void doInBackground(String... cmds) {
		try {
			publishProgress("success", getString(R.string.command_executor_try_connection, server.getHost()));
			sshAuth();
			publishProgress("success", formatDisplayMessage(getString(R.string.command_executor_connect)));
			for (String line: cmds) {
				final Session session = ssh.startSession();
				try {
					publishProgress("success", formatDisplayMessage(line.trim()));
					Command cmd = session.exec(line);
					String message = IOUtils.readFully(cmd.getInputStream()).toString();
					publishProgress("success", message);
				} finally {
					session.close();
				}
			}
			publishProgress("success", formatDisplayMessage(""));
			publishProgress("success", formatDisplayMessage(getString(R.string.command_executor_desconnect)));
		} catch (Exception e) {
			publishProgress("failed", getString(R.string.command_executor_failed, e.getMessage()));
		} finally {
		    try {
				if (ssh != null) {
					ssh.disconnect();
				}
			} catch (IOException e) {
				publishProgress("failed", getString(R.string.command_executor_failed, e.getMessage()));
			}
		}
		return null;
	}

	private String formatDisplayMessage(String message) {
		Iterable<String> multi = Splitter.on("\n").split(message);
		if (Iterables.size(multi) > 1) {
			return server.getAuthentication().getLogin()
					+ "@" + server.getHost() + ":~$ " + "./script.sh";
		} else {
			return server.getAuthentication().getLogin()
					+ "@" + server.getHost()+ ":~$ " + message;	
		}
	}
	
	private void sshAuth() throws IOException, UserAuthException, TransportException, CryptographyException {
		if (server != null) {
			ssh.connect(server.getHost(), server.getPort());
			switch (server.getAuthentication().getAuthenticationTypeEnum()) {
			case NO_AUTHENTICATION:
				NoAuthentication noAuthentication = (NoAuthentication) server.getAuthentication();
				ssh.auth(noAuthentication.getLogin());
				break;
			case SSH_AUTHENTICATION_PASSWORD:
				SSHAuthenticationPassword sshAuthenticationPassword = (SSHAuthenticationPassword) server.getAuthentication();
				String password = sshAuthenticationPassword.getDecryptPassword(myAuthentication.getKey());
				PasswordFinder createOneOff = PasswordUtils.createOneOff(password.toCharArray());
				ssh.auth(sshAuthenticationPassword.getLogin(), new AuthPassword(createOneOff));
				break;
			default:
				throw new RuntimeException("authentication type is not supported");
			}
		} else {
			throw new IllegalStateException("server is required");
		}
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		publishProgress("success", "\ndone");
	}

	public void connect(Server server) {
		this.server = server;
	}
	
	private String getString(int resId) {
		return activity.getString(resId);
	}
	
	private String getString(int resId, Object... object) {
		return activity.getString(resId, object);
	}
}

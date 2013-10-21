package com.example.pronus;

import java.util.Collection;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity{

	private static XMPPConnection connection;
	private Button start;
	private EditText userName;
	private EditText password;
	public static final String HOST = "talk.google.com";
	public static final int PORT = 5222;
	public static final String SERVICE = "gmail.com";
	private String USERNAME = null;
	private String PASSWORD = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    // Removes notification bar

		setContentView(R.layout.login);

		userName = (EditText)findViewById(R.id.userNameLogin);

		password = (EditText)findViewById(R.id.password);

		start = (Button)findViewById(R.id.start);

		start.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				USERNAME = userName.getText().toString();
				PASSWORD = password.getText().toString();
				connect();
				Intent intent = new Intent(Login.this, Main.class);
				startActivity(intent);
				Login.this.finish();
			}

		});
	}
	public void connect() {

		//final ProgressDialog dialog = ProgressDialog.show(this,
				//"Connecting...", "Please wait...", false);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				
				// Create a connection
				ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
				XMPPConnection connection = new XMPPConnection(connConfig);

				try {
					connection.connect();
					Log.i("XMPP",
							"Connected to " + connection.getHost());
				} catch (XMPPException ex) {
					Log.e("XMPP", "Failed to connect to "
							+ connection.getHost());
					Log.e("XMPP", ex.toString());
					setConnection(null);
				}
				try {
					// SASLAuthentication.supportSASLMechanism("PLAIN", 0);

					connection.login(USERNAME, PASSWORD, "0123456789101");
					Log.i("XMPP",
							"Logged in as " + connection.getUser());

					// Set the status to available
					Presence presence = new Presence(Presence.Type.available);
					connection.sendPacket(presence);
					setConnection(connection);

					Roster roster = connection.getRoster();

					// Try to add vincenzoarceri

					//roster.createEntry("vincenzoarceri.92@gmail.com/0123456789101", "Vincenzo Arceri", null);

					Collection<RosterEntry> entries = roster.getEntries();
					for (RosterEntry entry : entries) {
						Log.d("XMPPy",
								"--------------------------------------");
						Log.d("XMPP", "RosterEntry " + entry);
						Log.d("XMPP",
								"User: " + entry.getUser());
						Log.d("XMPP",
								"Name: " + entry.getName());
						Log.d("XMPP",
								"Status: " + entry.getStatus());
						Log.d("XMPP",
								"Type: " + entry.getType());
						Presence entryPresence = roster.getPresence(entry
								.getUser());

						Log.d("XMPP", "Presence Status: "
								+ entryPresence.getStatus());
						Log.d("XMPP", "Presence Type: "
								+ entryPresence.getType());
						Presence.Type type = entryPresence.getType();
						if (type == Presence.Type.available)
							Log.d("XMPP", "Presence AVIALABLE");
						Log.d("XMPP", "Presence : "
								+ entryPresence);

					}
				} catch (XMPPException ex) {
					Log.e("XMPPChatDemoActivity", "Failed to log in as "
							+ USERNAME);
					Log.e("XMPPChatDemoActivity", ex.toString());
					setConnection(null);
				}

				//dialog.dismiss();
			}
		});
		t.start();
		//dialog.show();
	}
	public void setConnection(XMPPConnection connection) {
//		Main.connection = connection;
		Login.connection = connection;
		if (Login.connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			Login.connection.addPacketListener(new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					if (message.getBody() != null) {
						String fromName = StringUtils.parseBareAddress(message
								.getFrom());
						Log.i("XMPP", "Text Recieved " + message.getBody()
								+ " from " + fromName );
						new UIUpdater().execute(fromName,message.getBody(),"");
					}
				}
			}, filter);
		}
	}
}

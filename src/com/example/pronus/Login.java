package com.example.pronus;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	private Button start;
	private EditText userName;
	private EditText password;
	private static final String HOST = "talk.google.com";
	private static final int PORT = 5222;
	private static final String SERVICE = "gmail.com";
	private String USERNAME = null;
	private String PASSWORD = null;
	public static XMPPConnection connection;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    // Removes notification bar

		setContentView(R.layout.login);

		userName = (EditText) findViewById(R.id.userNameLogin);
		password = (EditText) findViewById(R.id.password);
		start = (Button) findViewById(R.id.start);

		Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		start.startAnimation(fadeIn);

		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				USERNAME = userName.getText().toString();
				PASSWORD = password.getText().toString();

				connect();
					
				Intent intent = new Intent(Login.this, Main.class);

				// Salvo la mail utilizzata per il login nella classe Main
				intent.putExtra("mail", USERNAME);
				SMSService.mail = USERNAME;
				startActivity(intent);
				Login.this.finish();
			}
		});
	}

	public void connect() {

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				// Create a connection
				ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
				XMPPConnection connection = new XMPPConnection(connConfig);

				try {
					connection.connect();
					Log.i("XMPP", "Connected to " + connection.getHost());
				} catch (XMPPException ex) {
					Log.e("XMPP", "Failed to connect to " + connection.getHost());
					Log.e("XMPP", ex.toString());
					setConnection(null);
				}

				try {
					connection.login(USERNAME, PASSWORD, "0123456789101");
					Log.i("XMPP", "Logged in as " + connection.getUser());

					// Set the status to available
					Presence presence = new Presence(Presence.Type.available);
					connection.sendPacket(presence);
					setConnection(connection);

				} catch (XMPPException ex) {
					Log.e("XMPPChatDemoActivity", "Failed to log in as " + USERNAME);
					Log.e("XMPPChatDemoActivity", ex.toString());
					setConnection(null);
				}
			}
		});

		t.start();
	}

	public void setConnection(XMPPConnection connection) {
		Login.connection = connection;
		startService(new Intent(this, SMSService.class));
	}
}

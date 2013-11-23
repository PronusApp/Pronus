package com.example.pronus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class SMSService extends Service {

	public static XMPPConnection connection;
	public static Map<String,Conversation> smsList = new HashMap<String,Conversation>();
	public static String seed = null;
	private Database database;
	public static boolean alreadyLogged = false;
	public static String mail;

	@Override
	public void onCreate() {

		Log.i("SMSService","PasswordUpdater lanciato");
		this.connection = Login.connection;

		database = new Database(getBaseContext());
		database.sendPassword();

		Log.i("SMSService", "Servizio creato");
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "SMSService fermato", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startid) {

		Log.i("SMSService", "onStart");

		// Setto un ascoltatore sia per ricevere messaggi che per ricevere le chiavi pubbliche dei contatti

		this.connection = Login.connection;

		// Listener per i messaggi (chat)

		if (connection != null) {

			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			connection.addPacketListener(new PacketListener() {

				@Override
				public void processPacket(Packet packet) {
					Message message = (Message) packet;

					if (message.getBody() != null) {
						String fromName = StringUtils.parseBareAddress(message.getFrom());
						Log.i("SMSService", "Messaggio ricevuto " + message.getBody() + " da " + fromName );

						// Ho ricevo il messaggio criptato, devo decriptarlo con la chiave

						String clear = "";

						try {
							clear = Decoder.decrypt(new String(seed), message.getBody());
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (database.addMessage(fromName, clear, 1))
							Log.i("SMSService ","Messaggio aggiunto al database: " + clear);

						// Lancio la notifica alla ricezione del messaggio
						// se e solo se la mia applicazione non è in esecuzione.

						if(!isForeground("com.example.pronus")){
							createNotification(fromName, clear);

<<<<<<< HEAD
=======
						}else{
							Log.i("SMSService","Aggiungo messaggio");
							new UIUpdater().execute(fromName, clear ,"");
<<<<<<< HEAD
>>>>>>> 852558465517e28fca49103e2f313c442d536e72
=======
>>>>>>> 852558465517e28fca49103e2f313c442d536e72
						}
						Log.i("SMSService","Aggiungo messaggio");
						Log.i("SMSService","Testo del messaggio:" + clear);
						Log.i("SMSService","Da:" + fromName);
						String email = fromName.substring(0, fromName.indexOf('/'));
						new UIUpdater().execute(email, clear ,"false");

						database.sendPassword();

					}
				}
			}, filter);
		}

		// Listener per le chiavi pubbliche (normal)
		if (connection != null) {

			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.normal);
			connection.addPacketListener(new PacketListener() {

				@Override
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					if ((message.getBody() != null) && (!message.getBody().equals("IWannaYourKey"))) {
						String fromName = StringUtils.parseBareAddress(message.getFrom());

						Log.i("SMSService", "Password ricevuta " + message.getBody() + " da " + fromName);

						if (database.addPassword(message.getBody(), fromName))
							Log.i("SMSService","Password aggiunta al database");
						else
							Log.i("SMSService","Impossibile aggiungere la password al database");
<<<<<<< HEAD


=======
						
<<<<<<< HEAD
>>>>>>> 852558465517e28fca49103e2f313c442d536e72
=======
>>>>>>> 852558465517e28fca49103e2f313c442d536e72
					} else if (message.getBody().equals("IWannaYourKey")) {

						String from = StringUtils.parseBareAddress(message.getFrom());
						Log.i("SMSService", "Richiesta di password ricevuta da " + from);

						Message msg = new Message(from, Message.Type.normal);

						msg.setBody(seed);

						if (Login.connection != null) {	
							Login.connection.sendPacket(msg);
							Log.i("SMSService","Passoword inviata con successo");
						}
<<<<<<< HEAD
<<<<<<< HEAD

						database.sendPassword();
=======
						
						//database.sendPassword();
>>>>>>> 852558465517e28fca49103e2f313c442d536e72
=======
						
						//database.sendPassword();
>>>>>>> 852558465517e28fca49103e2f313c442d536e72
					}
				}
			}, filter);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Il metodo createNotification viene 
	 */
	public void createNotification(String name, String message) {
		//PendingIntent lanciato al click sulla notifica
		Intent intent = new Intent(this, Main.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		//Creo la notifica 
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)

		.setSmallIcon(R.drawable.ic_launcher)

		.setContentTitle(name)

		.setContentText(message)

		.setContentIntent(pIntent);

		NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		//Build della notifica e lancio

		mNotifyMgr.notify(0,  mBuilder.build());
	}

	/*
	 * Questo metodo mi permette di verificare se ci sono applicazioni in Foreground
	 * nel momento in cui devo lanciare la notifica.
	 * E' necessario in quanto una notifica deve essere lanciata solo se l'applicazione
	 * non � in esecuzione.
	 */

	public boolean isForeground(String myPackage){

		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		List< ActivityManager.RunningTaskInfo > runningTaskInfo = manager.getRunningTasks(1); 

		ComponentName componentInfo = runningTaskInfo.get(0).topActivity;

		if (componentInfo.getPackageName().equals(myPackage)) 
			return true;
		return false;
	}
}

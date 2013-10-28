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
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class SMSService extends Service {

	public static XMPPConnection connection;
	public static Map<String,Conversation> smsList = new HashMap<String,Conversation>();

	public static String seed;


	@Override
	public void onCreate() {
		
		seed = "ThisIsASecretKey";
		
		this.connection = Login.connection;
		
		Log.i("SMSService", "Servizio creato");

	}

	public static void sendPublicKey() {

		SQLiteDatabase database = ConversationList.mDatabaseHelper.getReadableDatabase();

		String[] columns = {"email"};

		Cursor cursor = database.query("contatti", columns, null, null, null,null,null);

		while (cursor.moveToNext()) {
			String to = cursor.getString(0);

			if (to != null) {
				
				// Canale dedicato allo scambio di chiavi pubbliche
				Message msg = new Message(to, Message.Type.normal);
				msg.setBody(seed);				

				if (Login.connection != null) 
					Login.connection.sendPacket(msg);

				Log.i("SMSService","Inviata password a " + to);
			}
		}

		Log.i("SMSService", "Inviata password a tutti i contatti");

	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Log.d("Service", "onStart");

		// Setto un ascoltatore sia per ricevere messaggi che per ricevere le chiavi pubbliche dei contatti

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
						Log.i("SMSService", "Text Received " + message.getBody() + " from " + fromName );
						// Ho ricevo il messaggio criptato, devo decriptarlo con la chiave

						String clear = "";
						
						try {
							clear = Decoder.decrypt( new String(seed), message.getBody());
						} catch (Exception e) {
							e.printStackTrace();
						}

						if(addMessage(fromName, clear, 1))
							Log.i("Login - ","Messaggio aggiunto al database:" + clear);

						//Lancio la notifica alla ricezione del messaggio
						//se e solo se la mia applicazione non è in esecuzione.
						
						if(!isForeground("com.example.pronus"))
							createNotification(fromName, clear);

						new UIUpdater().execute(fromName, clear ,"");
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
					if (message.getBody() != null) {
						String fromName = StringUtils.parseBareAddress(message.getFrom());

						Log.i("SMSService", "Public key received " + message.getBody() + " from " + fromName);

						if (addPublicKey(message.getBody(), fromName))
							Log.i("SMSService","Chiave pubblica aggiunta al database");
						else
							Log.i("SMSService","Impossibile aggiungere chiave pubblica al database");


						new UIUpdater().execute(fromName,message.getBody(),"");
					}
				}
			}, filter);
		}


	}

	public boolean addMessage(final String nome_conversazione, final String messaggio, int bool) {

		ContentValues values = new ContentValues();

		values.put("nome_conversazione", nome_conversazione);
		values.put("bool", bool);
		values.put("messaggio", messaggio);

		SQLiteDatabase database = ConversationList.mDatabaseHelperForConversation.getWritableDatabase();

		long id = database.insert("conversazioni", null, values);

		if (id == -1)
			return false;
		return true;
	}

	public boolean addPublicKey(String public_key, String email) {

		SQLiteDatabase database = ConversationList.mDatabaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put("password", public_key);
		String whereClause = "email = ?";
		String[] whereArgs = {email};

		int id = database.update("conversazioni", values, whereClause, whereArgs);

		if (id == -1)
			return false;
		return true;
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

		if(componentInfo.getPackageName().equals(myPackage)) return true;

		return false;
	}
}

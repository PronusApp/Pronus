package com.example.pronus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SMSService extends Service{
	public static XMPPConnection connection;
	public static Map<String,Conversation> smsList=new HashMap<String,Conversation>();


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		
		Log.d("Service", "onCreate");
		/*
		 * creo il database per i contatti
		 */
		
		//ConversationList.smsList = smsList;
		//ConversationList.update();
		
	}
	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		this.connection = Login.connection;

		/*
		 * creo il PacketListener per ascoltare i messaggi in entrata
		 */
		Log.d("Service", "onStart");
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			connection.addPacketListener(new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					if (message.getBody() != null) {
						String fromName = StringUtils.parseBareAddress(message
								.getFrom());
						Log.i("XMPP", "Text Recieved " + message.getBody()
								+ " from " + fromName );
						if(addMessage(fromName,message.getBody(), 1))
							Log.i("Login - ","messaggio ricevuto aggiunto");
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

}

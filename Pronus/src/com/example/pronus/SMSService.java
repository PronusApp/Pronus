package com.example.pronus;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Hex;
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
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SMSService extends Service {

	public static XMPPConnection connection;
	public static Map<String,Conversation> smsList=new HashMap<String,Conversation>();
	
	// Chiave pubblica e privata associata al contatto
	public static PrivateKey privata;
	public static PublicKey pubblica;

	@Override
	public void onCreate() {
		
		Log.d("Service", "onCreate");
		
		KeyPairGenerator kpg = null;
		// Inizializzazione delle chiavi
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			Log.i("SMSService","Impossibile creare un'istanza RSA");
		}
        kpg.initialize(1024);
        
        // Generazione delle chiavi
        KeyPair kp = kpg.generateKeyPair();
        
        // Questa sarà la chiave che dovrò passare ai miei coontatti per poter comunicare con me
		pubblica = kp.getPublic();
		
		// Questa sarà la chiave personale e segreta del contatto per decifrare i messaggi in entrata
		privata = kp.getPrivate();
		
		Log.i("SMSService", "Creazione chiave pubblica e chiave privata effettuata con successo");
	}
	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Log.d("Service", "onStart");
		
		this.connection = Login.connection;
		
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
						Log.i("SMSService", "Text Recieved " + message.getBody() + " from " + fromName );
						// Ho ricevo il messaggio criptato, devo decriptarlo con la chiave privata
						
						Cipher cc = null;
						byte[] plainFile = null;

						try {
						cc = Cipher.getInstance("RSA/ECB/PKCS1Padding");
						cc.init(Cipher.DECRYPT_MODE, privata);

						byte[] bytes = Hex.decodeHex(message.getBody().toCharArray());
						
						plainFile = cc.doFinal(bytes);		
						} catch (Exception e) {
							Log.i("SMSService", "Impossibile decodificare il messaggio");
						}
						
						if(addMessage(fromName,new String(plainFile), 1))
							Log.i("Login - ","Messaggio aggiunto al database");
						new UIUpdater().execute(fromName,message.getBody(),"");
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
						
						Log.i("SMSService", "Public key received " + message.getBody() + " from " + fromName );
						if (addPublicKey(message.getBody(), fromName))
							Log.i("Login - ","Chiave pubblica aggiunta al database");
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

		values.put("public_key", public_key);
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

}

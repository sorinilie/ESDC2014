package ro.ucv.ids.smarthotel.connections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import ro.ucv.ids.smarthotel.activities.RoomActivity;
import ro.ucv.ids.smarthotel.security.CreateHash;
import ro.ucv.ids.smarthotel.utils.GetTime;
import ro.ucv.ids.smarthotel.utils.IVocabulary;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class RoomDownloader extends AsyncTask<String, String,String > implements IVocabulary {
	private WeakReference<RoomActivity> activity;

	int command;

	private String user = null;
	private String password = null;

	private String timePasswordHash;

	public RoomDownloader(RoomActivity act, int command) {
		super();
	
		
		SharedPreferences settings = act.getSharedPreferences(PREFS_SMARTHOTEL,0);
		user = settings.getString(USER, null);
		password = settings.getString (PASSWORD,null);
		Log.e("User&Password after getting from Settings", user +" "+ password);
		
		activity=new WeakReference<RoomActivity>(act);
		this.command = command;
	}

	protected String doInBackground(String... urls) {
		Long time = GetTime.ReturnTime();
		try {
			Log.e("User is", user);
			Log.e("Password is", password);
			timePasswordHash = CreateHash.generateStorngPasswordHash(password, user);
			Log.e("Hash with the two is", timePasswordHash);
			timePasswordHash= CreateHash.generateSmallHash(timePasswordHash, time.toString());
			Log.e("Hash with time is", timePasswordHash);

		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (InvalidKeySpecException e1) {
			e1.printStackTrace();
		}
		
		String parameters= "";
		
		if (command == SET_DOOR) {
			if (checkIfUserPassNotNull(user,password)) {
				if (!urls[0].contains("?")) {
					parameters = "?";
				}
					
				parameters = parameters + "user=" + user + "&" + "password=" + timePasswordHash; 
			}
		}
		
		String result = "";
		HttpClient httpclient = new DefaultHttpClient();
		
	/*	if (!urls[0].contains("?"))
			parameters = "?" + parameters;
		
		HttpGet httpget = new HttpGet(urls[0] + parameters); */
		
		HttpGet httpget = new HttpGet(urls[0] + parameters);
		Log.e("URL ", urls[0]+parameters);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			Log.e("In try","");
			if (entity != null) {
				Log.e("entity!=null",entity.toString());
				InputStream instream = entity.getContent();
				result = convertStreamToString(instream);
				instream.close();
			}
		} catch (Exception e) {}
		return result; 
			}
	
	private boolean checkIfUserPassNotNull(String user2, String password2) {
		return (user2!=null && password2!=null);
	}

	protected void onPostExecute(String res) {
		
		Log.e("trying to send a command",""+ res);
		if (res.equals(error)){
			activity.get().onPostDownloadError(res);
			Log.e("Error", error);
		}
		else
		{
		switch (command){
		case SET_LIGHT: 	
			Log.e("finished downloading light",""+ res);
			activity.get().onPostDownloadLight(res); 
			break;
		
		case SET_SPON:
			activity.get().onPostDownloadOn(res);
			break;
			
		case SET_SPOFF:
			activity.get().onPostDownloadOff(res);
			break;
		
		case GET_SPMON:
			activity.get().onPostDownloadMon(res);
			break;
			
		case GET_SPSTS:
			activity.get().onPostDownloadSts(res);
			break;
		
		case SET_DOOR:
			Log.e("command is open door",""+ res);
			activity.get().onPostDownloadDoorOpn(res);
			break;
		}
	}
	}


	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}



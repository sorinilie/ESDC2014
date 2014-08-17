package ro.ucv.ids.smarthotel.accountmanager;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import ro.ucv.ids.smarthotel.connections.URLS;
import ro.ucv.ids.smarthotel.security.CreateHash;
import android.util.Log;

public class MyServerAuthenticator implements IServerAuthenticator, URLS {

	public static boolean final_response = false;
	public static String date;
	
	 
	@Override
	public String signUp(String email, String username, String password) {
		// TODO: register new user on the server and return its auth token
		// Note: It's done in RegisActivity
		return null;
	}

	
	public static void serverResponded(String response) {
		if (response.contains("OK")) {
			final_response = true;
		}
	}
	@Override
	public String signIn(String email, String password) {
		String authToken = null;
		sendCredentials (email,password,URL+"/Login");

		// If true, save account token as email + date (of registration)
		if (final_response == true) {
			final DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
			date = df.format(new Date());
			authToken = email + "-" + date;
		}
		else {
			Log.e("Authentication", "User/Password are wrong");
		}
		
		 
		return authToken;
	}
	
	// comunicate with server for logging in
	private void sendCredentials (String email, String password, String URL) {
		HttpPost post = new HttpPost(URL);
		Log.d("sendCredentials", "After creating a new HttpPost");
		DefaultHttpClient httpclient = new DefaultHttpClient();
		Log.d("sendCredentials", "After creating a new DefaultHttpClient");
		Log.d("sendCredentials", "Sending : " + "\nemail= "+email +"\npassword=" + password + "\nurl = "+URL );
		String hashedPass = null;
		try {
			hashedPass = CreateHash.generateStorngPasswordHash(password, email);
			Log.d("sendCredentials", "After creating the Hash");
		} catch (NoSuchAlgorithmException e1) {
			
			e1.printStackTrace();
		} catch (InvalidKeySpecException e1) {
			
			e1.printStackTrace();
		}
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("Email", email));
		nameValuePairs.add(new BasicNameValuePair("Password", hashedPass));
		
		Log.d("sendCredentials", "After creating the Array: " + nameValuePairs.toString());
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(post);
			HttpEntity httpEntity = response.getEntity();
            String output = EntityUtils.toString(httpEntity);
            serverResponded(output);
		} catch (UnsupportedEncodingException e) {
			Log.e("Servlet", "Could not send Array of username and Password");
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

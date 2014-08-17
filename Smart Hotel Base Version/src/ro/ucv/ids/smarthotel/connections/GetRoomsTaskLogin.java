package ro.ucv.ids.smarthotel.connections;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
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

import ro.ucv.ids.smarthotel.activities.LoginActivity;
import android.os.AsyncTask;
import android.util.Log;

public class GetRoomsTaskLogin extends AsyncTask<String, String, String> implements URLS {
	
	private WeakReference<LoginActivity> activity;
	
	public GetRoomsTaskLogin (LoginActivity activity) {
		super();
		this.activity = new WeakReference <LoginActivity>(activity);
	}
	
	@Override
	protected String doInBackground(String... params) {
		String roomListString = getRoomsFromServer(params[0], URLrooms);
        return roomListString;
	}
	
	@Override
	protected void onPostExecute(String res) {
		System.out.println("In onPost : " + res);
		if (res != null && res.equals(""))
			activity.get().setRoomsStringArray(res);
	}

	private String getRoomsFromServer (String email,String URL) {
		HttpPost post = new HttpPost(URL);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String serverResponse ="";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("Email", email));
		
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(post);
			HttpEntity httpEntity = response.getEntity();
            String output = EntityUtils.toString(httpEntity);
            serverResponse = output;
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
		
		return serverResponse;
	}
}
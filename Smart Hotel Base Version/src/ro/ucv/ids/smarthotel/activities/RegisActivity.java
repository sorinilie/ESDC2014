package ro.ucv.ids.smarthotel.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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

import ro.ucv.ids.smarthotel.R;
import ro.ucv.ids.smarthotel.activities.LoginActivity.UserLoginTask;
import ro.ucv.ids.smarthotel.connections.URLS;
import ro.ucv.ids.smarthotel.security.CreateHash;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class RegisActivity extends Activity implements URLS{
	
	// Values for email and password at the time of the login attempt.
		public String mEmail;
		public String mPassword;
		public String command = "Register";
		// UI references.
		private EditText mEmailView;
		private EditText mPasswordView;
		protected View mLoginFormView;
		protected View mLoginStatusView;
		protected TextView mLoginStatusMessageView;
		private UserLoginTask mAuthTask = null;

		public static boolean final_response = false;
		public static boolean email_already_exists = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regis);

		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptRegister();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegister();
					}
				});
		
		if (null != mEmail) {
			if (!mEmail.isEmpty()) {
				mPasswordView.requestFocus();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.regis, menu);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void attemptRegister() {
		if (mAuthTask != null) {
			return;
		}

		Log.d("attemptRegister", "We're in!");
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {

			Log.d("attemptRegister", "User and Pass are OK");
			//new Connection().execute();
			Connection connect = new Connection();
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			    connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[])null);
			else
			    connect.execute((Object[])null);
			Log.d("after AsyncTask", "final_response == " + final_response);
			
			if (final_response == true) {
				finish();
			}
			else
			{
				cancel = true;
				if (email_already_exists == true) {
					//mEmailView.setError("Email already registered");
					mEmailView.setError(getString(R.string.error_invalid_email));
					focusView = mEmailView;
					
				}
			}
		}
	}
	
	public static void serverResponded(String response) {
		if (response.contains("OK")) {
			final_response = true;
			Log.d("serverResponded", "output is OK");
		}
		else
			if (response.contains("EXISTS")) {
				final_response = false;
				email_already_exists = true;
				Log.d("After receving response", "Email already exists");
			}
	}
	
	@SuppressWarnings("rawtypes")
	private class Connection extends AsyncTask {
		 
        @Override
        protected Object doInBackground(Object... arg0) {
        //	showProgress(true);
            sendCredentials(mEmail,mPassword,URL+"/Register",command);
            return null;
        }
    }
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	private void sendCredentials (String email, String password, String URL, String command) {
		HttpPost post = new HttpPost(URL);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		Log.d("sendCredentials", "We're in!");
		
		String hashedPass = null;
		try {
			hashedPass = CreateHash.generateStorngPasswordHash(password, email);
		} catch (NoSuchAlgorithmException e1) {
			
			e1.printStackTrace();
		} catch (InvalidKeySpecException e1) {
			
			e1.printStackTrace();
		}
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("Command", command));
		nameValuePairs.add(new BasicNameValuePair("Email", email));
		nameValuePairs.add(new BasicNameValuePair("Password", hashedPass));

		Log.d("send Register", "Array is " + nameValuePairs.toString());
		try {
			//post.setEntity((HttpEntity) new UrlEncodedFormEntity(nameValuePairs));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(post);
			HttpEntity httpEntity = response.getEntity();
            String output = EntityUtils.toString(httpEntity);
            serverResponded(output);
			Log.d("send Register","Respose = " + output);
			if (final_response == true)
				finish();
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
	
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.activity_regis,
					container, false);
			return rootView;
		}
	}

}

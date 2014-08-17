package ro.ucv.ids.smarthotel.activities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


import ro.ucv.ids.smarthotel.R;
import ro.ucv.ids.smarthotel.accountmanager.AccountUtils;
import ro.ucv.ids.smarthotel.connections.GetRoomsTaskLogin;
import ro.ucv.ids.smarthotel.utils.IVocabulary;
import ro.ucv.ids.smarthotel.utils.Room;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends AccountAuthenticatorActivity implements IVocabulary{
	
	public static final String ARG_ACCOUNT_TYPE = "accountType";
	public static final String ARG_AUTH_TOKEN_TYPE = "authTokenType";
	public static final String ARG_IS_ADDING_NEW_ACCOUNT = "isAddingNewAccount";
	public static final String PARAM_USER_PASSWORD = "password";

	private AccountManager mAccountManager;
	
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private String roomsStringArray = "";
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    //Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);

	    //Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
		setContentView(R.layout.activity_login);
		setUpLoginForm ();
		
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
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
						attemptLogin();
					}
				});
		
		if (null != mEmail) {
			if (!mEmail.isEmpty()) {
				mPasswordView.requestFocus();
			}
		}
	}

	private void setUpLoginForm() {
		
		mAccountManager = AccountManager.get(this);

		mEmail = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);
		mPasswordView = (EditText) findViewById(R.id.password);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		Log.e("attemptLogin", "");
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
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			Log.e("After locally checking","");
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}
	

	/**
	 * Shows the progress UI and hides the login form.
	 */
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
	
	public void sendMessage(View view) 
	{
	    Intent intent = new Intent(LoginActivity.this, RegisActivity.class);
	    startActivity(intent);
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Intent> {
		
		@Override
		protected Intent doInBackground(Void... params) {
			
			// TODO: attempt authentication against a network service
			String authToken = AccountUtils.mServerAuthenticator.signIn(mEmail, mPassword);

			Log.e("after UserLoginTask.Do in background", "");
            final Intent res = new Intent();
            
            res.putExtra(AccountManager.KEY_ACCOUNT_NAME, mEmail);
            res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountUtils.ACCOUNT_TYPE);
            res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
            res.putExtra(PARAM_USER_PASSWORD, mPassword);
            
            return res;
		}

		@Override
		protected void onPostExecute(final Intent intent) {
			mAuthTask = null;
			showProgress(false);

			Log.e("After execute", "");
			if (null == intent.getStringExtra(AccountManager.KEY_AUTHTOKEN)) {
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			} else {
				finishLogin(intent);
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
		
		private void finishLogin(Intent intent) {
			//LoginActivity.this.setEmail(mEmail + "\n" + mPassword);
			SharedPreferences settings = getSharedPreferences(PREFS_SMARTHOTEL, 0);
			SharedPreferences.Editor editor = settings.edit();
			
			editor.putString(USER, mEmail);
			editor.putString(PASSWORD, mPassword);
			editor.commit();
			
			final String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
		    final String accountPassword = intent.getStringExtra(PARAM_USER_PASSWORD);
		    final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
		    String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

		    if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
		        // Creating the account on the device and setting the auth token we got
		        // (Not setting the auth token will cause another call to the server to authenticate the user)
		        mAccountManager.addAccountExplicitly(account, accountPassword, null);
		        mAccountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authToken);
		    } else {
		        mAccountManager.setPassword(account, accountPassword);
		    }
		    
	        setAccountAuthenticatorResult(intent.getExtras());
	    	setResult(AccountAuthenticatorActivity.RESULT_OK, intent);

	    	//TODO
		   // finish();

	    	GetRoomsTaskLogin roomsTask = new GetRoomsTaskLogin(LoginActivity.this);
	    	roomsTask.execute(mEmail);
	    	try {
				String getRoomsResponse = roomsTask.get(6000, TimeUnit.MILLISECONDS);
				setRoomsStringArray(getRoomsResponse);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	    	
	    	
	    	ArrayList <Room> rooms = new ArrayList<Room>();
	    	rooms = extractRooms (getRoomsStringArray());

			Intent newIntent = new Intent(LoginActivity.this,TabRoomsActivity.class);
			newIntent.putExtra("Rooms", rooms);
			startActivity(newIntent);
		}
	}
	
	private ArrayList <Room> extractRooms (String roomsStringArray) {
		String[] splitStrings;
		splitStrings = roomsStringArray.split(";");
		ArrayList<Room> roomList = new ArrayList <Room> ();
		
		String galileoIP;
		Integer galileoPort;
		boolean hasBathroom;
		boolean hasAC;
		Integer roomID;	
		
		for (int i=0; i< splitStrings.length ; i++) {
			roomID = Integer.parseInt(splitStrings[i]);
			galileoIP = splitStrings [i+1];
			galileoPort = Integer.parseInt(splitStrings[i+2]);
			hasBathroom = Boolean.parseBoolean(splitStrings[i+3]);
			hasAC = Boolean.parseBoolean(splitStrings[i+4]);
			
			roomList.add(new Room (roomID, galileoIP, galileoPort, hasBathroom, hasAC));
			i=i+4;
		}
		
		return roomList;
	}
	
	public String getRoomsStringArray () {
		return this.roomsStringArray;
	}
	
	public void setRoomsStringArray (String roomsString) {
		
		Toast.makeText(this.getApplicationContext(), "In metoda : " + roomsString, Toast.LENGTH_LONG) ;
		this.roomsStringArray = roomsString;
	}

	private void saveLocations(String toSave){
		try {


			File file = new File(this.getFilesDir(),"cache.csv");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(toSave);
			Log.e("Saving to file", toSave);
			bw.close();


		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	public void setEmail(String mEmail2) {
		saveLocations (mEmail2);
		
	}

	@Override
	public void onBackPressed() {
		setResult(AccountAuthenticatorActivity.RESULT_CANCELED);
		super.onBackPressed();
	}	
}

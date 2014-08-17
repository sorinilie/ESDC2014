package ro.ucv.ids.smarthotel.activities;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ro.ucv.ids.smarthotel.R;
import ro.ucv.ids.smarthotel.accountmanager.AccountUtils;
import ro.ucv.ids.smarthotel.accountmanager.AuthPreferences;
import ro.ucv.ids.smarthotel.connections.GetRoomsTaskMain;
import ro.ucv.ids.smarthotel.connections.URLS;
import ro.ucv.ids.smarthotel.utils.Room;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends Activity implements URLS{
	
	@SuppressWarnings("unused")
	private static final String TAG = "MainActivity";
	
	private static final int REQ_SIGNUP = 1;
	
	private AccountManager mAccountManager;
	private AuthPreferences mAuthPreferences;
	private String authToken;

	private String roomsStringArray;
	//private boolean final_response = false;
	//private static String saved_user;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		checkIfAccountExists();	

		//if it exists, then this activity will start
//		Intent myIntent = new Intent(MainActivity.this, RoomActivity.class);
//		MainActivity.this.startActivity(myIntent);
		
	}
	
	private void checkIfAccountExists() {
		authToken = null;
		mAuthPreferences = new AuthPreferences(this);
		mAccountManager = AccountManager.get(this);
		mAccountManager.getAuthTokenByFeatures(AccountUtils.ACCOUNT_TYPE, AccountUtils.AUTH_TOKEN_TYPE, null, this, null, null, new GetAuthTokenCallback(), null);
		
		
	}

	public class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {

		@Override
		public void run(AccountManagerFuture<Bundle> result) {
			Bundle bundle;
			
			try {
				bundle = result.getResult();

				final Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (null != intent) {
					startActivityForResult(intent, REQ_SIGNUP);
				} else {
					authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
					final String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
					
					// Save session username & auth token
					mAuthPreferences.setAuthToken(authToken);
					mAuthPreferences.setUsername(accountName);
					
					//text1.setText("Retrieved auth token: " + authToken);
					//text2.setText("Saved account name: " + mAuthPreferences.getAccountName());
					//text3.setText("Saved auth token: " + mAuthPreferences.getAuthToken());
					
					// If the logged account didn't exist, we need to create it on the device
					Account account = AccountUtils.getAccount(MainActivity.this, accountName);
					if (null == account) {
						account = new Account(accountName, AccountUtils.ACCOUNT_TYPE);
						mAccountManager.addAccountExplicitly(account, bundle.getString(LoginActivity.PARAM_USER_PASSWORD), null);
						mAccountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authToken);
					}
					/*
					ArrayList <Room> rooms = new ArrayList<Room>();
					rooms.add(new Room("192.168.7.1",true,false,321));
					rooms.add(new Room("192.168.6.2",false,true,231));
					rooms.add(new Room("192.168.8.1",true,false,351));

					Intent newIntent = new Intent(MainActivity.this,TabRoomsActivity.class);
					newIntent.putExtra("Rooms", rooms);
					startActivity(newIntent);
					*/
					
					/*Intent myIntent = new Intent(MainActivity.this, RoomActivity.class);
					MainActivity.this.startActivity(myIntent);
					*/
					
					GetRoomsTaskMain roomsTask = new GetRoomsTaskMain(MainActivity.this);
			    	roomsTask.execute(accountName);
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

					Intent newIntent = new Intent(MainActivity.this,TabRoomsActivity.class);
					newIntent.putExtra("Rooms", rooms);
					startActivity(newIntent);
					
				}
			} catch(OperationCanceledException e) {
				// If signup was cancelled, force activity termination
				finish();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}

	public String getRoomsStringArray () {
		return this.roomsStringArray;
	}
	
	public void setRoomsStringArray (String roomsString) {
		
		Toast.makeText(this.getApplicationContext(), "In metoda : " + roomsString, Toast.LENGTH_LONG) ;
		this.roomsStringArray = roomsString;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public String getToken() {
		mAccountManager.invalidateAuthToken(AccountUtils.ACCOUNT_TYPE, authToken);
		mAuthPreferences.setAuthToken(null);
		mAuthPreferences.setUsername(null);
		mAccountManager.getAuthTokenByFeatures(AccountUtils.ACCOUNT_TYPE, AccountUtils.AUTH_TOKEN_TYPE, null, this, null, null, new GetAuthTokenCallback(), null);
		Account[] abcd =mAccountManager.getAccounts();
		return abcd.toString();
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_close_session:
				
				// Clear session and ask for new auth token
				mAccountManager.invalidateAuthToken(AccountUtils.ACCOUNT_TYPE, authToken);
				mAuthPreferences.setAuthToken(null);
				mAuthPreferences.setUsername(null);
				mAccountManager.getAuthTokenByFeatures(AccountUtils.ACCOUNT_TYPE, AccountUtils.AUTH_TOKEN_TYPE, null, this, null, null, new GetAuthTokenCallback(), null);

				return true;
			default:
				return super.onOptionsItemSelected(item);
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
}

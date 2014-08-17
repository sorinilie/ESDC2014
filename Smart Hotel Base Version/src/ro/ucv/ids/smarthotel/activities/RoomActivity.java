package ro.ucv.ids.smarthotel.activities;

import ro.ucv.ids.smarthotel.connections.RoomDownloader;
import ro.ucv.ids.smarthotel.utils.IVocabulary;
import ro.ucv.ids.smarthotel.utils.Room;
import ro.ucv.ids.smarthotel.utils.URLList;
import ro.ucv.ids.smarthotel.R;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Button;

public class RoomActivity extends Activity implements
		OnSeekBarChangeListener, IVocabulary{
	int SPsts = 0 ;
	Button opndoor;
	ToggleButton toggleButton;
	URLList urls;
	private SeekBar bar_red;
	private SeekBar bar_green;
	private SeekBar bar_blue;
	private TextView textProgressRed, textProgressGreen, textProgressBlue;
	public int red_progress = 0, green_progress = 0, blue_progress = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room);

		//////////////////////////////////////////////////////////////////////
		// THE ROOM CLASS WHICH IS SET TO OUR TAB. THIS CONTAINS  .getRoomID//
		Room room = (Room) getIntent().getSerializableExtra("Room"); 		//
		/////////////////////////////////////////////////////////////////////
		
		// THIS IS MANUAL SETTING
		urls = new URLList("192.168.1.1:8080");
		
		// creeasem urls pentru a seta de fiecare data ip ul +/jsp , dar 
		// acum nu mai e nevoie, mereu o sa fie setat pe 192.168.1.1:8080 (brain)
		
		bar_red = (SeekBar) findViewById(R.id.redSeekbar);
		bar_red.setOnSeekBarChangeListener(this);
		bar_green = (SeekBar) findViewById(R.id.greenSeekbar);
		bar_green.setOnSeekBarChangeListener(this);
		bar_blue = (SeekBar) findViewById(R.id.blueSeekbar);
		bar_blue.setOnSeekBarChangeListener(this);

		textProgressRed = (TextView) findViewById(R.id.progress_display_red);
        textProgressGreen = (TextView) findViewById(R.id.progress_display_green);
        textProgressBlue = (TextView) findViewById(R.id.progress_display_blue);
		
		bar_red.getProgressDrawable().setColorFilter(Color.RED, Mode.SRC_IN);
		bar_red.getThumb().setColorFilter(Color.RED, Mode.SRC_IN);
		
		bar_green.getProgressDrawable().setColorFilter(Color.GREEN, Mode.SRC_IN);
		bar_green.getThumb().setColorFilter(Color.GREEN, Mode.SRC_IN);
		
		bar_blue.getProgressDrawable().setColorFilter(Color.BLUE, Mode.SRC_IN);
		bar_blue.getThumb().setColorFilter(Color.BLUE, Mode.SRC_IN);
		
		toggleButton= (ToggleButton)findViewById(R.id.toggleButton);
		opndoor = (Button)findViewById(R.id.opnDoor);
		
		new RoomDownloader(this, GET_SPSTS).execute(urls.getURL_spsts());

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

	@Override
    public void onProgressChanged(SeekBar seekBar, int progress,
        boolean fromUser) {
        
    	Integer convert = progress;
        // change action text label to changing
        switch (seekBar.getId()) {

        case R.id.redSeekbar:
            red_progress = progress;
            textProgressRed.setText(convert.toString());
            break;

        case R.id.greenSeekbar:
            green_progress = progress;
            textProgressGreen.setText(convert.toString());
            break;
        
        case R.id.blueSeekbar:
        	blue_progress = progress;
            textProgressBlue.setText(convert.toString());
            break;
        }

    }

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		seekBar.setSecondaryProgress(seekBar.getProgress()); 
		
		switch (seekBar.getId()) {

		case R.id.redSeekbar:
			(new RoomDownloader(this, SET_LIGHT)).execute(urls.getURL_Light() + "?red="
					+ seekBar.getProgress());
			Log.e("Light red", "" + seekBar.getProgress() + urls.getURL_Light());
			break;

		case R.id.greenSeekbar:
			(new RoomDownloader(this, SET_LIGHT)).execute(urls.getURL_Light() + "?green="
					+ seekBar.getProgress());
			Log.e("Light green", "" + seekBar.getProgress());
			break;

		case R.id.blueSeekbar:
			(new RoomDownloader(this, SET_LIGHT)).execute(urls.getURL_Light() + "?blue="
					+ seekBar.getProgress());
			Log.e("Light blue", "" + seekBar.getProgress());
			break;
		}

	}

	
	public void toggleStatus (View v){

		// instantiate the downloader class in onCreate in the SettingsActivity
		if (SPsts == 1){
			(new RoomDownloader(this, SET_SPOFF)).execute(urls.getURL_spoff());
		}
		else{
			(new RoomDownloader(this, SET_SPON)).execute(urls.getURL_spon());
		}
	}

	public void setButton(String res){
		// TODO Auto-generated method stub
		if (res!=null&&!res.equals("")) {
			SPsts	= Integer.parseInt(res.trim());
		
		
		Log.e("Neghina",res);
		toggleButton.setChecked(SPsts==1);
		}
	}
	public void onPostDownloadLight(String res) {

	}

	public void onPostDownloadOn(String res) {
		setButton( res);
	}

	public void onPostDownloadOff(String res) {
		setButton( res);
	}
	
	public void onPostDownloadMon(String res) {
		setButton( res);
	}
	
	public void onPostDownloadSts(String res) {
		setButton( res);
	}
	
	public void doorOpn(View v){
		Log.e("", "Incerc sa deschid usa");
		(new RoomDownloader(this, SET_DOOR)).execute(urls.getURL_opndoor());		
	}

	public void onPostDownloadDoorOpn(String res) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Door Opened", 2000);
	}

	public void onPostDownloadError(String res) {
		Toast.makeText(this, "Error", 3000);
		
	}

}

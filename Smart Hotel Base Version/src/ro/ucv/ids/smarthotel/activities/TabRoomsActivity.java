package ro.ucv.ids.smarthotel.activities;

import java.util.ArrayList;

import ro.ucv.ids.smarthotel.R;
import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import ro.ucv.ids.smarthotel.utils.Room;;

@SuppressWarnings("deprecation")
public class TabRoomsActivity extends ActivityGroup {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_roomtabs);

		final TabHost tabHost=(TabHost)findViewById(R.id.tabhost);
		tabHost.setup(this.getLocalActivityManager());
		
		@SuppressWarnings("unchecked")
		ArrayList <Room> rooms = (ArrayList<Room>) getIntent().getSerializableExtra("Rooms");
		Intent tabIntent;
		
		TabSpec tabSpec;
		for (Room i:rooms) {
			tabSpec = tabHost.newTabSpec("Tab");
			tabIntent = new Intent();
			tabIntent.setClass(this,RoomActivity.class);
			tabIntent.putExtra("Room", i);
			tabSpec.setContent(tabIntent);
			tabSpec.setIndicator("Room "+ i.getRoomID(),null);
			tabHost.addTab(tabSpec);
		}
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String arg0) {

				setTabColor(tabHost);
			}
		});
		setTabColor(tabHost);
	}

	public void setTabColor(TabHost tabhost) {

		for(int i=0;i<tabhost.getTabWidget().getChildCount();i++) {
			TextView tv = (TextView)  tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //unselected
			tv.setTextColor(Color.parseColor("#ffffff"));
		}

		TextView tv = (TextView)  tabhost.getCurrentTabView().findViewById(android.R.id.title); //unselected
		tv.setTextColor(Color.parseColor("#ffffff"));
	}
}

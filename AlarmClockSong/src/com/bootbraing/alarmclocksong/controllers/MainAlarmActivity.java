package com.bootbraing.alarmclocksong.controllers;

import java.util.ArrayList;
import java.util.List;

import android.R.drawable;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bootbraing.alarmclocksong.R;
import com.bootbraing.alarmclocksong.dao.AlarmDAO;
import com.bootbraing.alarmclocksong.models.Alarm;
import com.bootbraing.alarmclocksong.models.Alarm.AlarmFormat;

public class MainAlarmActivity extends Activity {

	//	private final int MAIN_ALARM_ACTIVITY = 1;
	private final int SET_ALARM_ACTIVITY = 2;
	private List<Alarm> alarms = new ArrayList<Alarm>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_alarm);
		listAllAlarms();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_alarm, menu);
		
		
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		default:
		case R.id.action_settings:
		Intent i = new Intent(this, AlarmPreferences.class);
		startActivityForResult(i, 10);	
		break;
	
		}
		
		return super.onOptionsItemSelected(item);
	}

	public void addNewAlarm(View v) {
		Intent i = new Intent(this, SetAlarmActivity.class);
		startActivityForResult(i, SET_ALARM_ACTIVITY);
	}
	
	public void editAlarm(Alarm alarm){
		Intent i = new Intent(this, SetAlarmActivity.class);
        i.putExtra("Alarm", alarm);
		startActivityForResult(i, SET_ALARM_ACTIVITY);
		
	}
	
	public void listAllAlarms() {
		alarms = new AlarmDAO(this).getAlarms();

		TableLayout table = (TableLayout) MainAlarmActivity.this
				.findViewById(R.id.tAlarms);
		for (Alarm alrm : alarms) {
			TableRow row = new TableRow(this);
			//TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
			TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
			row.setLayoutParams(lp);
			row.setId((int) alrm.getId());
			ToggleButton tbOnOff = new ToggleButton(this);
			tbOnOff.setChecked(alrm.isEnabled());
		    //tbOnOff.setBackgroundResource(R.drawable.alarm_clock_32);
			tbOnOff.setWidth(128);
			tbOnOff.setHeight(128);
			tbOnOff.setBackgroundColor(Color.TRANSPARENT);
			tbOnOff.setButtonDrawable(R.layout.alarm_on_off);
			tbOnOff.setTextOff("");
			tbOnOff.setTextOn("");
			tbOnOff.setText("");
			
			/*tbOnOff.setWidth(64\\);
			tbOnOff.setHeight(64);
			tbOnOff.setGravity(Gravity.CENTER);
			tbOnOff.buildDrawingCache(true);*/
			

			tbOnOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					buttonView.setBackgroundResource(drawable.list_selector_background);
					TableRow tr = (TableRow) buttonView.getParent();
					Alarm alarm = new AlarmDAO(getApplicationContext()).getAlarm(tr.getId());
					OnOffAlarm onOffAlarm = new OnOffAlarm();
	
					if (isChecked) {
						alarm.setEnabled(true);
						onOffAlarm.turnAlarmOffOn(alarm, true,getApplicationContext());
					} else {
						alarm.setEnabled(false);
						onOffAlarm.turnAlarmOffOn(alarm, false,getApplicationContext());
					}
/*					Toast.makeText(
							getApplicationContext(),
							"Updated Alarm : "
									+ new AlarmDAO(getApplicationContext())
											.update(alarm), Toast.LENGTH_LONG)
							.show();
					Toast.makeText(getApplicationContext(),
							"Alarm After Update : " + alarm, Toast.LENGTH_LONG)
							.show();*/
					new AlarmDAO(getApplicationContext()).update(alarm);
					
				}
			});
			
			/*Log.d("Alarm ID","Alarm ID : " + alrm.getId());
			Log.d("Alarm Label","Alarm Label : " + alrm.getLabel() );
			Log.d("Alarm Days","Alarm Days : " + alrm.getDaysOfWeek().toString(this, false));
			Log.d("Alarm Ringtone","Alarm Ringtone : " + alrm.getAlert().toString());
			Log.d("Alarm Random","Alarm Random : " + alrm.isRandomRingtone());*/
			alrm.setAlarmFormat(AlarmFormat.HOUR_24);
			Log.d("Alarm Details" , " Alarm : " + alrm.toString());
			Log.d("Alarm Repeating" , "Days : " + Integer.toBinaryString(alrm.getDaysOfWeek().getCoded()));
			Log.d("=====","======");
			//Log.d(" ","");

			TextView tvTimeNTitle = new TextView(this);
			tvTimeNTitle.setText("" + alrm.getLabel());
			row.addView(tbOnOff);
			row.addView(tvTimeNTitle);
			row.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					Toast.makeText(getApplicationContext(), "You Click the Alarm with ID : " + ((TableRow)v).getId(), Toast.LENGTH_SHORT).show();
					editAlarm(new AlarmDAO(getApplicationContext()).getAlarm(((TableRow)v).getId()));
					((TableRow)v).setBackgroundResource(drawable.list_selector_background);

				}
			});
			table.addView(row);

		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.onCreate(null);
	}

}

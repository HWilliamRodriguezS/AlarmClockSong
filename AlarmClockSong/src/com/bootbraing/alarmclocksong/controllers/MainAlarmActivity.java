package com.bootbraing.alarmclocksong.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bootbraing.alarmclocksong.R;
import com.bootbraing.alarmclocksong.dao.AlarmDAO;
import com.bootbraing.alarmclocksong.models.Alarm;

public class MainAlarmActivity extends Activity {

	//	private final int MAIN_ALARM_ACTIVITY = 1;
	private final int SET_ALARM_ACTIVITY = 2;
	private List<Alarm> alarms = new ArrayList<Alarm>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_alarm);
		
		// Create an offset from the current time in which the alarm will go
		// off.
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 5);

		// Create a new PendingIntent and add it to the AlarmManager
//		Intent intent = new Intent(this, AlarmReceiverActivity.class);
//		PendingIntent pendingIntent = PendingIntent.getActivity(this, 12345,
//				intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
//		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
		listAllAlarms();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_alarm, menu);
		return true;
	}

	public void addNewAlarm(View v) {
		Intent i = new Intent(this, SetAlarmActivity.class);
		startActivityForResult(i, SET_ALARM_ACTIVITY);
	}

	public void toogleAlarm(View v) {
		System.out.println("Im in");

	}

	public void listAllAlarms() {
		alarms = new AlarmDAO(this).getAlarms();

		TableLayout table = (TableLayout) MainAlarmActivity.this
				.findViewById(R.id.tAlarms);
		for (Alarm alrm : alarms) {
			TableRow row = new TableRow(this);
			TableRow.LayoutParams lp = new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT);
			row.setLayoutParams(lp);
			row.setId((int) alrm.getId());
			ToggleButton tbOnOff = new ToggleButton(this);
			tbOnOff.setChecked(alrm.isEnabled());
			
		/*	Toast.makeText(getApplicationContext(),
					"Alarm After Update : " + alarms, Toast.LENGTH_LONG)
					.show();*/
			
			// Creating the ToggleButton Even onchange/onChecked
			// ToggleButton tbOnOff = new ToggleButton(this);
			// tbOnOff.setText("On");
			tbOnOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {

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
				}
			});

			TextView tvTimeNTitle = new TextView(this);
			tvTimeNTitle.setText("" + alrm.getLabel());
			row.addView(tbOnOff);
			row.addView(tvTimeNTitle);
			table.addView(row);

		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.onCreate(null);
	}

}

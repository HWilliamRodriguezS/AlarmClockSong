package com.bootbraing.alarmclocksong.controllers;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bootbraing.alarmclocksong.R;
import com.bootbraing.alarmclocksong.dao.AlarmDAO;
import com.bootbraing.alarmclocksong.dao.AlarmSQLiteHelper;
import com.bootbraing.alarmclocksong.models.Alarm;
import com.bootbraing.alarmclocksong.models.Alarm.AlarmFormat;
import com.bootbraing.alarmclocksong.models.AlarmReaderContract.AlarmEntry;

public class SetAlarmActivity extends Activity {

	private final int MAIN_ALARM_ACTIVITY = 1;
	private final int SET_ALARM_ACTIVITY = 2;
	private AlarmManager alarmMgr;
	private TextView tvTime;
	private AlarmDAO alarmDAO ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_alarm);
		tvTime = (TextView) findViewById(R.id.time);
		alarmDAO = new AlarmDAO(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	public void setAlarm(Alarm alarm) {
		alarm.setAlarmFormat(AlarmFormat.HOUR_24);
		alarmMgr = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(this, AlarmReceiverActivity.class);
		PendingIntent alarmIntent = PendingIntent.getActivity(this, Integer.parseInt(alarm.getHour() + "" + alarm.getMinutes()), intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		// Set the alarm to start at
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
		calendar.set(Calendar.MINUTE, alarm.getMinutes());

		// setRepeating() lets you specify a precise 
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, alarmIntent);

	}

	public void setTime(View v) {

		final Alarm alarm = new Alarm();
		Calendar mcurrentTime = Calendar.getInstance();
		int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
		int minute = mcurrentTime.get(Calendar.MINUTE);
		TimePickerDialog mTimePicker;
		mTimePicker = new TimePickerDialog(SetAlarmActivity.this,
				new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker timePicker,
							int selectedHour, int selectedMinute) {
						alarm.setHour(selectedHour);
						alarm.setMinutes(selectedMinute);
						alarm.setEnabled(true);
						alarm.setSilent(false);
						alarm.setLabel("test");
						alarm.setAlarmFormat(AlarmFormat.HOUR_12);
						setAlarm(alarm);
						saveAlarm(alarm);
						tvTime.setText("" + alarm.getHour() + ":" + alarm.getMinutes() + " " + alarm.getAMPM());
					}

				}, hour, minute, false);// Yes 24 hour time
		
		mTimePicker.setTitle("Select Time");
		mTimePicker.show();
	}
	
	public void saveAlarm(Alarm alarm){
		alarmDAO.createAlarm(alarm);
	}
	
	@Override
	public void onBackPressed() {
		alarmDAO.close();
		super.onBackPressed();
	}

	public void setTime2(View v) {
		Toast.makeText(getApplicationContext(), "Testing", Toast.LENGTH_LONG)
				.show();
		final Alarm alarm = new Alarm();
		Calendar mcurrentTime = Calendar.getInstance();
		int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
		int minute = mcurrentTime.get(Calendar.MINUTE);
		TimePickerDialog mTimePicker;
		mTimePicker = new TimePickerDialog(SetAlarmActivity.this,
				new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker timePicker,
							int selectedHour, int selectedMinute) {
						tvTime.setText("" + selectedHour + ":" + selectedMinute);
						alarm.setHour(selectedHour);
						alarm.setMinutes(selectedMinute);
						alarm.setEnabled(true);
						alarm.setSilent(false);
						alarm.setLabel("test"); 
						
						setAlarm(alarm);
					
					}

				}, hour, minute, false);// Yes 24 hour time

		AlarmSQLiteHelper mDbHelper = AlarmSQLiteHelper.getInstance(
				getBaseContext());
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(AlarmEntry.COLUMN_NAME_ALARM_ID, 1);
		values.put(AlarmEntry.COLUMN_NAME_ENABLED, alarm.isEnabled() ? 1 : 0);
		values.put(AlarmEntry.COLUMN_NAME_SILENT, alarm.isSilent() ? 1 : 0);
		values.put(AlarmEntry.COLUMN_NAME_LABEL, alarm.getLabel());

		long newRowId;
		newRowId = db.insert(AlarmEntry.TABLE_NAME, null, values);

		SQLiteDatabase dbR = mDbHelper.getReadableDatabase();

		String[] projection = { AlarmEntry._ID,
				AlarmEntry.COLUMN_NAME_ALARM_ID,
				AlarmEntry.COLUMN_NAME_ENABLED, AlarmEntry.COLUMN_NAME_SILENT,
				AlarmEntry.COLUMN_NAME_LABEL };

		String sortOrder = AlarmEntry.COLUMN_NAME_ALARM_ID + " DESC";

		Cursor cursor = db.query(AlarmEntry.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
				);

		/*
		 * cursor.moveToFirst(); long itemId = cursor.getLong(
		 * cursor.getColumnIndexOrThrow(FeedEntry._ID) );
		 */
		cursor.getCount();
		cursor.moveToFirst();
		long itemId = cursor.getLong(cursor
				.getColumnIndexOrThrow(AlarmEntry._ID));

		Toast.makeText(getBaseContext(), "Total :  " + cursor.getCount(),
				Toast.LENGTH_SHORT).show();
		mTimePicker.setTitle("Select Time");
		mTimePicker.show();

	}

}

package com.bootbraing.alarmclocksong.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bootbraing.alarmclocksong.models.Alarm;
import com.bootbraing.alarmclocksong.models.AlarmReaderContract.AlarmEntry;

public class AlarmDAO {

	private SQLiteDatabase db;
	private AlarmSQLiteHelper dbHelper;

	public AlarmDAO(Context context) {
		dbHelper = AlarmSQLiteHelper.getInstance(context);
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		db.close();
	}

	public Alarm getAlarm(Integer alarmId) {
		String[] tableColumns = new String[] { AlarmEntry._ID,
				AlarmEntry.COLUMN_NAME_ALARM_ID,
				AlarmEntry.COLUMN_NAME_ENABLED, AlarmEntry.COLUMN_NAME_HOUR,
				AlarmEntry.COLUMN_NAME_MINUTES, AlarmEntry.COLUMN_NAME_TIME,
				AlarmEntry.COLUMN_NAME_VIBRATE, AlarmEntry.COLUMN_NAME_LABEL,
				AlarmEntry.COLUMN_NAME_ALERT, AlarmEntry.COLUMN_NAME_SILENT, };
		Cursor cursor = db.query(AlarmEntry.TABLE_NAME, tableColumns,
				AlarmEntry._ID + "=?",
				new String[] { String.valueOf(alarmId) }, null, null, null,
				null);
		
		Alarm alarm = new Alarm();
		if (cursor != null){ 
			cursor.moveToFirst();

		alarm.setId(cursor.getInt(0));
		alarm.setEnabled((cursor.getInt(2) == 1) ? true : false);
		alarm.setHour(cursor.getInt(3));
		alarm.setMinutes(cursor.getInt(4));
		alarm.setTime(cursor.getInt(5));
		alarm.setVibrate(cursor.getInt(6) == 1 ? true : false);
		alarm.setLabel(cursor.getString(7));
		alarm.setSilent((cursor.getInt(8) == 1) ? true : false);
		}
		
		return alarm;
	}

	/**
	 * Create new ALARM object
	 * 
	 * @param alarm
	 *            Label
	 */
	public void createAlarm(Alarm alarm) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(AlarmEntry.COLUMN_NAME_ENABLED, alarm.isEnabled() ? 1	: 0);
		contentValues.put(AlarmEntry.COLUMN_NAME_HOUR, alarm.getHour());
		contentValues.put(AlarmEntry.COLUMN_NAME_MINUTES, alarm.getMinutes());
		contentValues.put(AlarmEntry.COLUMN_NAME_TIME, alarm.getTime());
		contentValues.put(AlarmEntry.COLUMN_NAME_VIBRATE, alarm.isVibrate() ? 1	: 0);
		contentValues.put(AlarmEntry.COLUMN_NAME_LABEL, alarm.getLabel());
		contentValues.put(AlarmEntry.COLUMN_NAME_ALERT, alarm.getAlert().toString());
		contentValues.put(AlarmEntry.COLUMN_NAME_SILENT, alarm.isSilent() ? 1: 0);
		// Insert into DB
		db.insert("alarms", null, contentValues);
	}

	/**
	 * Delete ALARM object
	 * 
	 * @param alarmId
	 */
	public void deleteAlarm(int alarmId) {
		// Delete from DB where id match
		db.delete("alarms", AlarmEntry.COLUMN_NAME_ALARM_ID + " = " + alarmId,
				null);
	}

	/**
	 * Get all ALARMs.
	 * 
	 * @return
	 */
	public List<Alarm> getAlarms() {
		List<Alarm> alarmList = new ArrayList<Alarm>();

		// Name of the columns we want to select
		String[] tableColumns = new String[] {
				AlarmEntry._ID,
				AlarmEntry.COLUMN_NAME_ALARM_ID,
				AlarmEntry.COLUMN_NAME_ENABLED,
				// AlarmEntry.COLUMN_NAME_SUBTITLE,
				AlarmEntry.COLUMN_NAME_HOUR, AlarmEntry.COLUMN_NAME_MINUTES,
				AlarmEntry.COLUMN_NAME_TIME,
				// AlarmEntry.COLUMN_NAME_DAYSOFWEEK,
				AlarmEntry.COLUMN_NAME_VIBRATE, AlarmEntry.COLUMN_NAME_LABEL,
				AlarmEntry.COLUMN_NAME_ALERT, AlarmEntry.COLUMN_NAME_SILENT, };

		// Query the database
		Cursor cursor = db.query("alarms", tableColumns, null, null, null,
				null, null);
		cursor.moveToFirst();

		// Iterate the results
		Alarm alarm;
		while (!cursor.isAfterLast()) {
			alarm = new Alarm();

			alarm.setId(cursor.getInt(0));
			alarm.setEnabled((cursor.getInt(2) == 1) ? true : false);
			alarm.setHour(cursor.getInt(3));
			alarm.setMinutes(cursor.getInt(4));
			alarm.setTime(cursor.getInt(5));
			alarm.setVibrate(cursor.getInt(6) == 1 ? true : false);
			alarm.setLabel(cursor.getString(7));
			alarm.setSilent((cursor.getInt(8) == 1) ? true : false);

			alarmList.add(alarm);

			cursor.moveToNext();
		}
		return alarmList;
	}

	public int update(Alarm alarm) {
		// TODO Auto-generated method stub
		
		ContentValues contentValues = new ContentValues();
		contentValues.put(AlarmEntry.COLUMN_NAME_ENABLED, alarm.isEnabled() ? 1	: 0);
		contentValues.put(AlarmEntry.COLUMN_NAME_HOUR, alarm.getHour());
		contentValues.put(AlarmEntry.COLUMN_NAME_MINUTES, alarm.getMinutes());
		contentValues.put(AlarmEntry.COLUMN_NAME_TIME, alarm.getTime());
		contentValues.put(AlarmEntry.COLUMN_NAME_VIBRATE, alarm.isVibrate() ? 1	: 0);
		contentValues.put(AlarmEntry.COLUMN_NAME_LABEL, alarm.getLabel());
		contentValues.put(AlarmEntry.COLUMN_NAME_ALERT, alarm.getAlert().toString());
		contentValues.put(AlarmEntry.COLUMN_NAME_SILENT, alarm.isSilent() ? 1: 0);
		
		// updating row
		return db.update(AlarmEntry.TABLE_NAME, 
				         contentValues, 
				         AlarmEntry.COLUMN_NAME_ALARM_ID + " = ?",
				         new String[] { String.valueOf(alarm.getId()) });
	}

}

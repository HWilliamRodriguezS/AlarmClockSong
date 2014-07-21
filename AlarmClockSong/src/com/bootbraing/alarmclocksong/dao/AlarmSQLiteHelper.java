package com.bootbraing.alarmclocksong.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bootbraing.alarmclocksong.models.AlarmReaderContract.AlarmEntry;

public class AlarmSQLiteHelper extends SQLiteOpenHelper {

	private static AlarmSQLiteHelper alarmSQLiteHelper;

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "AlarmReader.db";
	/***********************************/
	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ AlarmEntry.TABLE_NAME + " (" 
			+ AlarmEntry._ID + " INTEGER PRIMARY KEY," 
			+ AlarmEntry.COLUMN_NAME_ALARM_ID + INTEGER_TYPE + COMMA_SEP 
			+ AlarmEntry.COLUMN_NAME_ENABLED + INTEGER_TYPE + COMMA_SEP 
			+ AlarmEntry.COLUMN_NAME_SUBTITLE + TEXT_TYPE + COMMA_SEP 
			+ AlarmEntry.COLUMN_NAME_HOUR + INTEGER_TYPE + COMMA_SEP 
			+ AlarmEntry.COLUMN_NAME_MINUTES + INTEGER_TYPE + COMMA_SEP 
			+ AlarmEntry.COLUMN_NAME_TIME + INTEGER_TYPE + COMMA_SEP 
			+ AlarmEntry.COLUMN_NAME_DAYSOFWEEK + INTEGER_TYPE + COMMA_SEP 
			+ AlarmEntry.COLUMN_NAME_VIBRATE + TEXT_TYPE + COMMA_SEP 
			+ AlarmEntry.COLUMN_NAME_LABEL + TEXT_TYPE + COMMA_SEP 
			+ AlarmEntry.COLUMN_NAME_ALERT + TEXT_TYPE + COMMA_SEP
			+ AlarmEntry.COLUMN_NAME_SILENT + TEXT_TYPE + COMMA_SEP
			+ AlarmEntry.COLUMN_NAME_RANDOM + INTEGER_TYPE +   
			// Any other options for the CREATE command
			" )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ AlarmEntry.TABLE_NAME;

	private AlarmSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade policy
		// is
		// to simply to discard the data and start over
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);

	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	public static AlarmSQLiteHelper getInstance(Context context) {

		if (alarmSQLiteHelper == null) {
			alarmSQLiteHelper = new AlarmSQLiteHelper(
					context.getApplicationContext());
		}
		return alarmSQLiteHelper;
	}

}

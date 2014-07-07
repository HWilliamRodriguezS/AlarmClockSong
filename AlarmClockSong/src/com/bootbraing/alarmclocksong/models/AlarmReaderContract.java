package com.bootbraing.alarmclocksong.models;

import android.provider.BaseColumns;

public class AlarmReaderContract {
	
	public AlarmReaderContract(){
		
	}
	
	public static abstract class AlarmEntry implements BaseColumns{
		
		public static final String TABLE_NAME = "alarms";
        public static final String COLUMN_NAME_ALARM_ID = "alarmid";
        public static final String COLUMN_NAME_ENABLED = "enabled";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
        public static final String COLUMN_NAME_HOUR = "hour";
        public static final String COLUMN_NAME_MINUTES = "minutes";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_DAYSOFWEEK = "daysofweek";
        public static final String COLUMN_NAME_VIBRATE = "vibrate";
        public static final String COLUMN_NAME_LABEL = "label";
        public static final String COLUMN_NAME_ALERT = "alert";
        public static final String COLUMN_NAME_SILENT = "silent";
        
	}

}

package com.bootbraing.alarmclocksong.models;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.Context;
import android.net.Uri;

import com.bootbraing.alarmclocksong.R;

public class Alarm {

	private int id = 0;
	private boolean enabled = false;
	private int hour = 0;
	private int minutes = 0;
	private DaysOfWeek daysOfWeek = getDaysOfWeek();
	private long time = 0;
	private boolean vibrate = false;
	private String label = "";
	private Uri alert = Uri.parse("");
	private boolean silent = false;
	private AlarmFormat alarmFormat = AlarmFormat.HOUR_24;
	
	public Alarm(){
		
	}
	
	public Alarm(int id, boolean enabled, int hour, int minutes,
			DaysOfWeek daysOfWeek, long time, boolean vibrate, String label,
			Uri alert, boolean silent) {
		super();
		this.id = id;
		this.enabled = enabled;
		this.hour = hour;
		this.minutes = minutes;
		this.daysOfWeek = daysOfWeek;
		this.time = time;
		this.vibrate = vibrate;
		this.label = label;
		this.alert = alert;
		this.silent = silent;
		this.alarmFormat = AlarmFormat.HOUR_24;
	}

	static final class DaysOfWeek {

        private static int[] DAY_MAP = new int[] {
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY,
        };

        // Bitmask of all repeating days
        private int mDays;

        DaysOfWeek(int days) {
            mDays = days;
        }

        public String toString(Context context, boolean showNever) {
            StringBuilder ret = new StringBuilder();

            // no days
            if (mDays == 0) {
               return showNever ? context.getText(R.string.never).toString() : "";
            }

            // every day
            if (mDays == 0x7f) {
               return context.getText(R.string.every_day).toString();
            }

            // count selected days
            int dayCount = 0, days = mDays;
            while (days > 0) {
                if ((days & 1) == 1) dayCount++;
                days >>= 1;
            }

            // short or long form?
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] dayList = (dayCount > 1) ?
                    dfs.getShortWeekdays() :
                    dfs.getWeekdays();

            // selected days
            for (int i = 0; i < 7; i++) {
                if ((mDays & (1 << i)) != 0) {
                    ret.append(dayList[DAY_MAP[i]]);
                    dayCount -= 1;
                  if (dayCount > 0) 
                	 ret.append(context.getText(R.string.select_days));
                	 // ret.append("Select Days");
                }
            }
            return ret.toString();
        }
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getHour() {
		
		if(this.alarmFormat == AlarmFormat.HOUR_12){
		   return (Integer) ((this.hour == 0)?"12":hour - 12);			
		}
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public DaysOfWeek getDaysOfWeek() {
		return daysOfWeek;
	}

	public void setDaysOfWeek(DaysOfWeek daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isVibrate() {
		return vibrate;
	}

	public void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Uri getAlert() {
		return alert;
	}

	public void setAlert(Uri alert) {
		this.alert = alert;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}
	
	public String getTimeStr(){
		
		return "" + hour + ":" + minutes ;
	}
	
	@Override
	public String toString() {
		return "Alarm [id=" + id + ", enabled=" + enabled + ", hour=" + hour
				+ ", minutes=" + minutes + ", daysOfWeek=" + daysOfWeek
				+ ", time=" + time + ", vibrate=" + vibrate + ", label="
				+ label + ", alert=" + alert + ", silent=" + silent + "]";
	}
	
	
	public AlarmFormat getAlarmFormat() {
		return alarmFormat;
	}

	public void setAlarmFormat(AlarmFormat alarmFormat) {
		this.alarmFormat = alarmFormat;
	}
	
	public String getAMPM(){
		
		String fmt = "";
		
		if(this.alarmFormat == AlarmFormat.HOUR_12){
			fmt=(this.hour > 12)?"PM":"AM";
			
		}
		
		return fmt;
		
	}

	public enum AlarmFormat{
		
		HOUR_12,HOUR_24;
		
	}
	
	/*
	public static Alarm getAlarm(int id,Cursor cursor){
		long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(AlarmEntry._ID));
		long enabled = cursor.getLong(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_NAME_ENABLED));
		long hour = cursor.getLong(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_NAME_HOUR));
		long minutes = cursor.getLong(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_NAME_MINUTES));
		long minutes = cursor.getLong(cursor.getColumnIndexOrThrow(AlarmEntry.COLUMN_NAME_MINUTES));
         
		
		this.daysOfWeek = daysOfWeek;
		this.time = time;
		this.vibrate = vibrate;
		this.label = label;
		this.alert = alert;
		this.silent = silent;
		this.alarmFormat = AlarmFormat.HOUR_24;
		
		
		return null;
	}
	*/
	
}

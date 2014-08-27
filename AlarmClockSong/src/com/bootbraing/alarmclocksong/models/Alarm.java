package com.bootbraing.alarmclocksong.models;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.bootbraing.alarmclocksong.R;


public class Alarm implements Parcelable,Cloneable {

	private int id = 0;
	private boolean enabled = false;
	private int hour = 7;
	private int minutes = 0;
	private DaysOfWeek daysOfWeek = new DaysOfWeek(0);
	private long time = 0;
	private boolean vibrate = false;
	private String label = "";
	private Uri alert = Uri.parse("");
	private boolean silent = false;
	private AlarmFormat alarmFormat = AlarmFormat.HOUR_24;
	private boolean randomRingtone ;
	
	/*  
	 none=0,sun=1,mon=2...sat=7,all=8
	  */
	private int selectedDay = 0;
	
	
	public Alarm(){
		
	}
	
	public Alarm(int id, boolean enabled, int hour, int minutes,
			DaysOfWeek daysOfWeek, long time, boolean vibrate, String label,
			Uri alert, boolean silent ,boolean randomRingtone) {
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
		this.randomRingtone = randomRingtone;
	}
	
	public Alarm(Parcel in) {
		
		id = in.readInt();
		enabled =(in.readInt() == 1)?true:false;
		hour = in.readInt();
		minutes = in.readInt();
		daysOfWeek = new DaysOfWeek(in.readInt());
		time = in.readInt();
		vibrate = (in.readInt() == 1)?true:false;
		label = in.readString();
	    alert = Uri.parse(in.readString());
		silent = (in.readInt() == 1)?true:false;
		randomRingtone = (in.readInt() == 1)?true:false;
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
		
		if(this.alarmFormat == AlarmFormat.HOUR_12 && hour > 12){
		   return (Integer) ((this.hour == 0)?"12":hour - 12);			
		}
		else if(this.alarmFormat == AlarmFormat.HOUR_12 && hour == 0){
			return 12;
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
		this.alarmFormat = AlarmFormat.HOUR_12;
		return String.format(Locale.getDefault(),"%2d:%02d %s",getHour(),getMinutes(),getAMPM()) ;
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
	
	
	
	public static final class DaysOfWeek {

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

        public DaysOfWeek(int days) {
            mDays = days;
        }

        public String toString(Context context, boolean showNever) {
            StringBuilder ret = new StringBuilder();

            // no days
            if (mDays == 0) {
                return showNever ?
                        context.getText(R.string.never).toString() : "";
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
                    if (dayCount > 0) ret.append(
                            context.getText(R.string.day_concat));
                }
            }
            return ret.toString();
        }

        private boolean isSet(int day) {
            return ((mDays & (1 << day)) > 0);
        }

        public void set(int day, boolean set) {
            if (set) {
                mDays |= (1 << day);
            } else {
                mDays &= ~(1 << day);
            }
        }

        public void set(DaysOfWeek dow) {
            mDays = dow.mDays;
        }

        public int getCoded() {
            return mDays;
        }
        
        public String getBinCoded(){
        	String days = Integer.toBinaryString(getCoded());
    		char[] daysC = days.toCharArray();
    		char[] daysBin = new char[daysC.length];
    		String code ="";
    		for(int i = (daysC.length -1) ,iday=0 ; i >= 0 ; i--,iday++){
    			daysBin[iday]=daysC[i];
    			code +=daysC[i];
    		}
    		
        	return code;
        }
        
        // Returns days of week encoded in an array of booleans.
        public boolean[] getBooleanArray() {
            boolean[] ret = new boolean[7];
            for (int i = 0; i < 7; i++) {
                ret[i] = isSet(i);
            }
            return ret;
        }

        public boolean isRepeatSet() {
            return mDays != 0;
        }

        /**
         * returns number of days from today until next alarm
         * @param c must be set to today
         */
        public int getNextAlarm(Calendar c) {
            if (mDays == 0) {
                return -1;
            }

            int today = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7;
            int day = 0;
            int dayCount = 0;
            for (; dayCount < 7; dayCount++) {
                day = (today + dayCount) % 7;
                if (isSet(day)) {
                    break;
                }
            }
            return dayCount;
        }
    }
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(enabled?1:0);
		dest.writeInt(hour);
		dest.writeInt(minutes);
		dest.writeInt(daysOfWeek.getCoded());
		dest.writeInt((int)time);
		dest.writeInt(vibrate?1:0);
		dest.writeString(label);
		dest.writeString(alert.toString());
		dest.writeInt(silent?1:0);
		dest.writeInt(randomRingtone?1:0);
	}
	
	public boolean isRandomRingtone() {
		return randomRingtone;
	}

	public void setRandomRingtone(boolean randomRingtone) {
		this.randomRingtone = randomRingtone;
	}

	public int getSelectedDay() {
		return selectedDay;
	}

	public void setSelectedDay(int selectedDay) {
		this.selectedDay = selectedDay;
	}

	public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() {
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };


    @Override
	public Object clone() throws CloneNotSupportedException {
		/*Alarm clonedAlarm = new Alarm();
		
		clonedAlarm.id = id;
		clonedAlarm.enabled = enabled;
		clonedAlarm.hour = hour;
		clonedAlarm.minutes = minutes;
		clonedAlarm.daysOfWeek = daysOfWeek;
		clonedAlarm.time = time;
		clonedAlarm.vibrate = vibrate;
		clonedAlarm.label = label;
		clonedAlarm.alert = alert;
		clonedAlarm.silent = silent;
		clonedAlarm.alarmFormat = AlarmFormat.HOUR_24;
		clonedAlarm.randomRingtone = randomRingtone;
		
		return clonedAlarm;*/
		return super.clone();
	}
	
}

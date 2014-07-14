package com.bootbraing.alarmclocksong.controllers;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.util.Log;
import android.view.Menu;
import android.widget.TimePicker;

import com.bootbraing.alarmclocksong.R;
import com.bootbraing.alarmclocksong.dao.AlarmDAO;
import com.bootbraing.alarmclocksong.models.Alarm;
import com.bootbraing.alarmclocksong.models.Alarm.AlarmFormat;
import com.bootbraing.alarmclocksong.models.AlarmReaderContract.AlarmEntry;
import com.bootbraing.alarmclocksong.utils.RepeatPreference;

public class SetAlarmActivity extends PreferenceActivity /*implements OnSharedPreferenceChangeListener*/ {

//	private final int MAIN_ALARM_ACTIVITY = 1;
//	private final int SET_ALARM_ACTIVITY = 2;
	private AlarmManager alarmMgr;
	private AlarmDAO alarmDAO ;
	
	private Preference menuTimePref;
	private EditTextPreference menuLabelPref;
	private RepeatPreference menuRepeat;
	private RingtonePreference menuRingtone;
	private CheckBoxPreference menuVibrate;
	
	private Alarm alarm = new Alarm();
	//private SharedPreferences prefs;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.set_alarm_prefs);
	    PreferenceManager.setDefaultValues(SetAlarmActivity.this, R.xml.set_alarm_prefs,false);
	    
	   /* prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    // prefs.registerOnSharedPreferenceChangeListener(this);
*/		
		menuTimePref = findPreference("time");
		
		menuLabelPref = (EditTextPreference)findPreference("label");
		menuLabelPref.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    public boolean onPreferenceChange(Preference p,
                            Object newValue) {
                        p.setSummary((String) newValue);
                        return true;
                    }
                });
		
		menuRepeat = (RepeatPreference) findPreference("setRepeat");
		
		menuRingtone = (RingtonePreference) findPreference("ringtone");
		menuRingtone.setOnPreferenceChangeListener(new RingtonePreference.OnPreferenceChangeListener() {
                    public boolean onPreferenceChange(Preference p,
                            Object newValue) {
						String name = RingtoneManager.getRingtone(
								getApplicationContext(),
								Uri.parse((String) newValue)).getTitle(
								getApplicationContext());
                        p.setSummary((String) name);
                        return true;
                    }
                });
		
		menuVibrate = (CheckBoxPreference) findPreference("vibrate");
		alarmDAO = new AlarmDAO(this);
	}
	
	/*@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
	    super.onResume();
	    // Set up a listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
	    super.onPause();
	    // Unregister the listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	public void setAlarm(Alarm alarm) {
		alarm.setAlarmFormat(AlarmFormat.HOUR_24);
		alarmMgr = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(this, AlarmReceiverActivity.class);
		intent.putExtra(AlarmEntry.COLUMN_NAME_ALERT,alarm.getAlert().toString());
		intent.putExtra("Alarm",alarm);
		//intent.putExtra(name, value)
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

	public void setTime() {

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
						alarm.setAlarmFormat(AlarmFormat.HOUR_12);
						Log.d("Selected Time : ", selectedHour + " , " +selectedMinute);
						menuTimePref.setSummary(alarm.getTimeStr());
					}

				}, hour, minute, false);// Yes 24 hour time
		
		mTimePicker.setTitle("Select Time");
		mTimePicker.show();
	}
	
	@SuppressWarnings("deprecation")
	@Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == menuTimePref) {
            setTime();
        }
        
        //menuLabelPref.setSummary(menuLabelPref.getText());

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
		
	public void saveAlarm(Alarm alarm){
		alarmDAO.createAlarm(alarm);
	}
	
	@Override
	public void onBackPressed() {
		acceptSettedAlarm();
		alarmDAO.close();
		super.onBackPressed();
	}
	
	public void acceptSettedAlarm(){
		alarm.setEnabled(true);
		alarm.setSilent(false);
		alarm.setLabel((String)menuLabelPref.getSummary());
		alarm.setDaysOfWeek(menuRepeat.getDaysOfWeek());
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String strRingtonePreference = prefs.getString("ringtone", "DEFAULT_RINGTONE_URI");
		Uri ringtoneUri = Uri.parse(strRingtonePreference);
		alarm.setAlert(ringtoneUri);
		alarm.setVibrate(menuVibrate.isChecked());
		setAlarm(alarm);
		saveAlarm(alarm);
	}

	/*@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	    menuLabelPref.setSummary(menuLabelPref.getText());
	}*/
	
}

package com.bootbraing.alarmclocksong.controllers;

import java.util.Calendar;
import java.util.List;

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
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow.LayoutParams;
import android.widget.TimePicker;

import com.bootbraing.alarmclocksong.R;
import com.bootbraing.alarmclocksong.dao.AlarmDAO;
import com.bootbraing.alarmclocksong.models.Alarm;
import com.bootbraing.alarmclocksong.models.Alarm.AlarmFormat;
import com.bootbraing.alarmclocksong.utils.RepeatPreference;

public class SetAlarmActivity extends PreferenceActivity{

	private AlarmManager alarmMgr;
	private AlarmDAO alarmDAO;

	private Preference menuTimePref;
	private EditTextPreference menuLabelPref;
	private RepeatPreference menuRepeat;
	private RingtonePreference menuRingtone;
	private CheckBoxPreference menuVibrate;
	private CheckBoxPreference menuRandomRington;

	private Button buttonCancel;
	private Button buttonAcept;
	private Button buttonDelete;

	private Alarm alarm = new Alarm();
	private Alarm editAlarm;
	
	private Alarms alarms;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.set_alarm_prefs);
		PreferenceManager.setDefaultValues(SetAlarmActivity.this,
				R.xml.set_alarm_prefs, false);
		alarms = new Alarms(getApplicationContext());
		
		Intent intent = getIntent();
		editAlarm = (Alarm) intent.getParcelableExtra("Alarm");
		
		if (editAlarm != null) {
			try {
				alarm = (Alarm) editAlarm.clone();
			} catch (CloneNotSupportedException e) {
				Log.e("SetAlarmActivity","Exception : " + e.getMessage());
			}
		} else {
			alarm.setAlert(Settings.System.DEFAULT_ALARM_ALERT_URI);
		}

		alarm.setAlarmFormat(AlarmFormat.HOUR_12);
		menuTimePref = findPreference("time");
		menuTimePref.setSummary(alarm.getTimeStr());

		menuLabelPref = (EditTextPreference) findPreference("label");
		menuLabelPref.setSummary(alarm.getLabel());
		menuLabelPref.setText(alarm.getLabel());
		menuLabelPref.setOnPreferenceChangeListener(
				new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference p,
							Object newValue) {
						p.setSummary((String) newValue);
						return true;
					}
				});

		menuRepeat = (RepeatPreference) findPreference("setRepeat");
		menuRepeat.setSummary(alarm.getDaysOfWeek().toString(
				getApplicationContext(), true));
		menuRepeat.setDaysOfWeek(alarm.getDaysOfWeek());

		menuRingtone = (RingtonePreference) findPreference("ringtone");
		menuRingtone.setSummary(RingtoneManager.getRingtone(
				getApplicationContext(), alarm.getAlert()).getTitle(
				getApplicationContext()));

		menuRingtone.setOnPreferenceChangeListener(
				new RingtonePreference.OnPreferenceChangeListener() {
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
		menuVibrate.setChecked(alarm.isVibrate());

		menuRandomRington = (CheckBoxPreference) findPreference("random");
		menuRandomRington.setChecked(alarm.isRandomRingtone());

		alarmDAO = new AlarmDAO(this);

		getListView().setItemsCanFocus(true);

		// Grab the content view so we can modify it.
		FrameLayout content = (FrameLayout) getWindow().getDecorView()
				.findViewById(android.R.id.content);

		// Get the main ListView and remove it from the content view.
		ListView lv = getListView();
		content.removeView(lv);

		// Create the new LinearLayout that will become the content view and
		// make it vertical.
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		// Have the ListView expand to fill the screen minus the save/cancel
		// buttons.
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		ll.addView(lv, lp);

		// Inflate the buttons onto the LinearLayout.
		View v = LayoutInflater.from(this).inflate(R.layout.alarm_save_cancel,ll);

		// Attach actions to each button.
		buttonAcept = (Button) v.findViewById(R.id.alarm_save);
		buttonAcept.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (editAlarm != null) {
					editAlarm.setAlarmFormat(AlarmFormat.HOUR_24);
					removeAlarmFromManager(editAlarm);
				}
				acceptSettedAlarm();
				finish();
			}
		});

		buttonCancel = (Button) v.findViewById(R.id.alarm_cancel);
		buttonCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		buttonCancel.setEnabled(false);

		buttonDelete = (Button) v.findViewById(R.id.alarm_delete);
		buttonDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				alarm.setAlarmFormat(AlarmFormat.HOUR_24);
				deleteAlarm(alarm);
				finish();
			}
		});
		
		setContentView(ll);
	}

	protected void deleteAlarm(Alarm alarm) {
		alarmDAO.deleteAlarm(alarm.getId());
		removeAlarmFromManager(alarm);
	}

	private void removeAlarmFromManager(Alarm alarm) {
		alarm.setAlarmFormat(AlarmFormat.HOUR_24);
		List<PendingIntent> pIntents = alarms.alarmPendingItents(alarm);//alarmPendingItents(alarm);

		try {
			for (PendingIntent pIntent : pIntents) {
				alarmMgr.cancel(pIntent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
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
		buttonCancel.setEnabled(true);
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	public void saveAlarm(Alarm alarm) {
		if (alarm.getId() == 0) {
			this.alarm.setId(alarmDAO.createAlarm(alarm));
		} else {
			alarmDAO.update(alarm);
		}
	}

	@Override
	public void onBackPressed() {
		alarmDAO.close();
		super.onBackPressed();
	}

	public void acceptSettedAlarm() {

		alarm.setEnabled(true);
		alarm.setSilent(false);
		alarm.setLabel((String) menuLabelPref.getSummary());
		alarm.setDaysOfWeek(menuRepeat.getDaysOfWeek());
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		String strRingtonePreference = prefs.getString("ringtone",
				"DEFAULT_RINGTONE_URI");
		Uri ringtoneUri = Uri.parse(strRingtonePreference);
		if (ringtoneUri.toString() != "DEFAULT_RINGTONE_URI") {
			alarm.setAlert(ringtoneUri);
		}
		alarm.setVibrate(menuVibrate.isChecked());
		alarm.setRandomRingtone(menuRandomRington.isChecked());
		saveAlarm(alarm);
		alarms.setAlarm(getApplicationContext(), alarm);
		alarmDAO.close();	
	}

}

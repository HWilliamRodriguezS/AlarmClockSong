package com.bootbraing.alarmclocksong.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import com.bootbraing.alarmclocksong.models.AlarmReaderContract.AlarmEntry;
import com.bootbraing.alarmclocksong.utils.RepeatPreference;

public class SetAlarmActivity extends PreferenceActivity /*
														 * implements
														 * OnSharedPreferenceChangeListener
														 */{

	// private final int MAIN_ALARM_ACTIVITY = 1;
	// private final int SET_ALARM_ACTIVITY = 2;
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

	// private SharedPreferences prefs;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.set_alarm_prefs);
		PreferenceManager.setDefaultValues(SetAlarmActivity.this,
				R.xml.set_alarm_prefs, false);

		Intent intent = getIntent();
		Alarm editAlarm = (Alarm) intent.getParcelableExtra("Alarm");
		if (editAlarm != null) {
			this.alarm = editAlarm;
		} else {
			alarm.setAlert(Settings.System.DEFAULT_ALARM_ALERT_URI);
		}

		alarm.setAlarmFormat(AlarmFormat.HOUR_12);
		menuTimePref = findPreference("time");
		menuTimePref.setSummary(alarm.getTimeStr());

		menuLabelPref = (EditTextPreference) findPreference("label");
		menuLabelPref.setSummary(alarm.getLabel());
		menuLabelPref
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
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

		menuRingtone
				.setOnPreferenceChangeListener(new RingtonePreference.OnPreferenceChangeListener() {
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

		// We have to do this to get the save/cancel buttons to highlight on
		// their own.
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
		View v = LayoutInflater.from(this).inflate(R.layout.alarm_save_cancel,
				ll);

		// Attach actions to each button.
		buttonAcept = (Button) v.findViewById(R.id.alarm_save);
		buttonAcept.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Enable the alarm when clicking "Done"
				// mEnabled = true;
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
				deleteAlarm(alarm);
				finish();
			}
		});
		// Replace the old content view with our new one.
		setContentView(ll);

	}

	protected void deleteAlarm(Alarm alarm) {
		alarmDAO.deleteAlarm(alarm.getId());
		removeAlarmFromManager(alarm);
	}

	private void removeAlarmFromManager(Alarm alarm) {
		alarm.setAlarmFormat(AlarmFormat.HOUR_24);
		List<PendingIntent> pIntents = alarmPendingItents(alarm);

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

	public PendingIntent composePendingAlarmIntent(Alarm alarm) {
		alarm.setAlarmFormat(AlarmFormat.HOUR_24);
		alarmMgr = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(this, AlarmReceiverActivity.class);
		intent.putExtra(AlarmEntry.COLUMN_NAME_ALERT, alarm.getAlert()
				.toString());
		intent.putExtra("Alarm", alarm);
		/*Log.e("Alarm Details : ",
				""
						+ Integer.parseInt("1" + alarm.getId() + ""
								+ alarm.getSelectedDay() + "00"
								+ alarm.getHour() + "" + alarm.getMinutes()));*/
		PendingIntent alarmIntent = PendingIntent.getActivity(
				this,
				Integer.parseInt("1" + alarm.getId() + ""
						+ alarm.getSelectedDay() + "00" + alarm.getHour() + ""
						+ alarm.getMinutes()), intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		return alarmIntent;
	}

	public void setAlarm(Alarm alarm) {

		alarm.setAlarmFormat(AlarmFormat.HOUR_24);
		PendingIntent alarmIntent;

		String daysS = alarm.getDaysOfWeek().getBinCoded();
		char[] daysC = daysS.toCharArray();

		if (daysS.equals("1111111")) {

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
			calendar.set(Calendar.MINUTE, alarm.getMinutes());
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			alarm.setSelectedDay(8);
			alarmIntent = composePendingAlarmIntent(alarm);

			if (Calendar.getInstance().getTimeInMillis() > calendar
					.getTimeInMillis()) {
				calendar.setTimeInMillis(calendar.getTimeInMillis()
						+ (24 * 60 * 60 * 1000));
			}

			alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), 7 * 24 * 60 * 60 * 1000,
					alarmIntent);
			/*Log.e("alarm weedly etted ",
					" Setted : " + calendar.getTimeInMillis());*/

		} else if (daysS.equals("0")) {

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
			calendar.set(Calendar.MINUTE, alarm.getMinutes());
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			alarm.setSelectedDay(0);
			alarmIntent = composePendingAlarmIntent(alarm);

			if (Calendar.getInstance().getTimeInMillis() > calendar
					.getTimeInMillis()) {
				calendar.setTimeInMillis(calendar.getTimeInMillis()
						+ (24 * 60 * 60 * 1000));
			}
			/*Log.e("alarm setted once",
					" Setted : " + calendar.getTimeInMillis());*/
			alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
					alarmIntent);

		} else {

			for (int i = 0; i < daysC.length; i++) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
				calendar.set(Calendar.MINUTE, alarm.getMinutes());
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				if (i == 6) {
					calendar.set(Calendar.DAY_OF_WEEK, (1));

				} else {
					calendar.set(Calendar.DAY_OF_WEEK, ((i + 1) + 1));
				}

				if (Calendar.getInstance().getTimeInMillis() > calendar
						.getTimeInMillis()) {
					calendar.setTimeInMillis(calendar.getTimeInMillis()
							+ (7 * 24 * 60 * 60 * 1000));
				}

				if (daysC[i] == '1') {
					alarm.setSelectedDay(i + 1);
					alarmIntent = composePendingAlarmIntent(alarm);

					alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
							calendar.getTimeInMillis(),
							7 * 24 * 60 * 60 * 1000, alarmIntent);
				}

			}
		}

	}

	public List<PendingIntent> alarmPendingItents(Alarm alarm){
		
		List<PendingIntent> pendingIntents = new ArrayList<PendingIntent>();
		String daysS = alarm.getDaysOfWeek().getBinCoded();
		char[] days = alarm.getDaysOfWeek().getBinCoded().toCharArray();
		
		if(daysS.equals("0")){
			alarm.setSelectedDay(0);
			PendingIntent alarmIntent = composePendingAlarmIntent(alarm);
			pendingIntents.add(alarmIntent);
		}else if(daysS.equals("1111111")){
			alarm.setSelectedDay(8);
			PendingIntent alarmIntent = composePendingAlarmIntent(alarm);
			pendingIntents.add(alarmIntent);
		}else{
		
		for(int i = 0 ; i < days.length ; i++){	
			
			if (days[i] == '1') {
				alarm.setSelectedDay(i+1);
				PendingIntent alarmIntent = composePendingAlarmIntent(alarm);
				pendingIntents.add(alarmIntent);
			}
			
		}
		}
		
		return pendingIntents;
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
		setAlarm(alarm);
		alarmDAO.close();
	}

}

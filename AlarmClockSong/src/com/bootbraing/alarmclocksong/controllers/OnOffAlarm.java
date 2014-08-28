package com.bootbraing.alarmclocksong.controllers;

import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

import com.bootbraing.alarmclocksong.models.Alarm;

public class OnOffAlarm {

	public void changeAlarmState() {
	}

	public void turnAlarmOffOn(Alarm alarm, boolean offOn, Context context) {
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		/*
		 * Intent intent = new Intent(context, AlarmReceiverActivity.class);
		 * PendingIntent alarmIntent = PendingIntent.getActivity(context,
		 * Integer.parseInt(alarm.getHour() + "" + alarm.getMinutes()), intent,
		 * PendingIntent.FLAG_CANCEL_CURRENT);
		 * 
		 * alarmManager.cancel(alarmIntent);
		 */

		Alarms alarms = new Alarms(context);

		if (offOn) {
			alarms.setAlarm(context, alarm);
		} else {
			List<PendingIntent> pIntents = alarms.alarmPendingItents(alarm);
			try {
				for (PendingIntent pIntent : pIntents) {
					alarmManager.cancel(pIntent);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.d("", "REMOVE ALARM :  " + alarm.toString());
		}
	}

	public void setOnStateChangeListener() {
	}

}

package com.bootbraing.alarmclocksong.controllers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bootbraing.alarmclocksong.models.Alarm;

public class OnOffAlarm  {
	



	public void changeAlarmState(){
		
		
		
		
		
	}
	
	
	public void turnAlarmOffOn(Alarm alarm, boolean offOn,Context context){
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, AlarmReceiverActivity.class);
		PendingIntent alarmIntent = PendingIntent.getActivity(context, 12345, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		//intent.putExtra("ALERT_TIME", alert.date);
		//intent.putExtra("ID_ALERT", alert.idAlert);
		//intent.putExtra("TITLE", alert.title);
		//intent.putExtra("GEO_LOC", alert.isGeoLoc);
//		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
//		        alarm.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

		alarmManager.cancel(alarmIntent);
		Log.e("","REMOVE ALARM :  " + alarm.toString());
		
	}
	
	
	

}

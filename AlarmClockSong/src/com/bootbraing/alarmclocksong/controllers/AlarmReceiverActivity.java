package com.bootbraing.alarmclocksong.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bootbraing.alarmclocksong.R;
import com.bootbraing.alarmclocksong.models.Alarm;
import com.bootbraing.alarmclocksong.models.Alarm.AlarmFormat;
import com.bootbraing.alarmclocksong.models.AlarmReaderContract.AlarmEntry;

public class AlarmReceiverActivity extends Activity {

	private MediaPlayer mMediaPlayer; 
	//private Uri alert;
	private Alarm alarm;
	private Vibrator vibratorPlayer;
	
	private Button stopAlarm;
	private Button snoozeAlarm; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        alarm = (Alarm) intent.getParcelableExtra("Alarm");
        vibratorPlayer = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //Log.d("Reicibing Parceable Object" ,"Pareable Alarm : " + alarm);
        //alert =Uri.parse(intent.getStringExtra(AlarmEntry.COLUMN_NAME_ALERT));
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.alarm_receiver);
 
        stopAlarm = (Button) findViewById(R.id.stopAlarm);
        stopAlarm.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
            	stopAlarm();
                return false;
            }
        });
        
        snoozeAlarm = (Button)findViewById(R.id.snoozeAlarm);
        snoozeAlarm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(getApplicationContext(),"Got clicked", Toast.LENGTH_SHORT).show();
				snoozeAlarm();
				stopAlarm();
				
			}
		});
        
        
        playSound(this, getAlarmUri());
        turnVibratorOn(true);
        
//        SharedPreferences prefs = getApplicationContext().getSharedPreferences("AlarmClockSong", 0);
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        float volumen = prefs.getFloat("volumen", 0.125f);
//        Toast.makeText(getApplicationContext(), "Is this the setted volumen ?" + volumen, Toast.LENGTH_LONG).show();
        
    }
    
    /*public Uri getRandomRingtone(){
    	
		RingtoneManager ringtoneMgr = new RingtoneManager(this);
		Uri[] allRingtones ;
		List<Uri> listRingtones = new ArrayList<Uri>();
		int[] params = { 0, 1, 2 };
		int ringTypes[] = {RingtoneManager.TYPE_ALARM,RingtoneManager.TYPE_RINGTONE,RingtoneManager.TYPE_NOTIFICATION};
		//int var = RingtoneManager.TYPE_ALARM;
		for (int param : params) {

			switch (param) {
			
			case 0:

				break;
			case 1:
				ringtoneMgr.setType(ringTypes[param]);
				Cursor alarmsCursor = ringtoneMgr.getCursor();
				int alarmsCount = alarmsCursor.getCount();
				if (alarmsCount == 0 && !alarmsCursor.moveToFirst()) {
					return null;
				}
				
				Uri[] alarms = new Uri[alarmsCount];
				while (!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {
					int currentPosition = alarmsCursor.getPosition();
					//alarms[currentPosition] = ringtoneMgr.getRingtoneUri(currentPosition);
					//listRingtones.add(ringtoneMgr.getRingtonePosition(currentPosition));
					listRingtones.add(ringtoneMgr.getRingtoneUri(currentPosition));
				}
				
				alarmsCursor.close();
				break;
				
			case 2:
				break;
			default:
				
			}

		}
		allRingtones = new Uri[listRingtones.size()];
		listRingtones.toArray(allRingtones);
		Toast.makeText(getApplicationContext(), "Size : " + listRingtones.size() + " Data"  + listRingtones, Toast.LENGTH_LONG ).show();
		return allRingtones[new Random(new Date().getTime()).nextInt(allRingtones.length)];
	}*/
    
public Uri getRandomRingtone(){
    	
		//RingtoneManager ringtoneMgr = new RingtoneManager(this);
		Uri[] allRingtones ;
		List<Uri> listRingtones = new ArrayList<Uri>();
		int ringTypes[] = {RingtoneManager.TYPE_ALARM,RingtoneManager.TYPE_RINGTONE,RingtoneManager.TYPE_NOTIFICATION};
		
		for (int ringType : ringTypes) {
			RingtoneManager ringtoneMgr = new RingtoneManager(this);
			ringtoneMgr.setType(ringType);
			Cursor alarmsCursor = ringtoneMgr.getCursor();
			if (alarmsCursor.getCount() == 0 && !alarmsCursor.moveToFirst()) {
				return null;
			}
			
			while (!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {
				listRingtones.add(ringtoneMgr.getRingtoneUri(alarmsCursor.getPosition()));
			}
			
			alarmsCursor.close();
			

		}
		allRingtones = new Uri[listRingtones.size()];
		listRingtones.toArray(allRingtones);
		return allRingtones[new Random(new Date().getTime()).nextInt(allRingtones.length)];
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}
    
    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }
    
    private void turnVibratorOn(final boolean state){
		if (state) {
			long[] pattern = { 0, 300, 1000 };
			vibratorPlayer.vibrate(pattern, 0);
		}else{
			vibratorPlayer.cancel();
		}
    }
 
    private Uri getAlarmUri() {
      
        //Log.d("Before getting in " ,alarm.getAlert() + "");
        if (alarm.getAlert() == null  || alarm.getAlert().toString() == "" ) {
            alarm.setAlert(Settings.System.DEFAULT_ALARM_ALERT_URI);
            //Log.d("Default Alarm :" ,Settings.System.DEFAULT_ALARM_ALERT_URI + "'");
            if (alarm.getAlert() == null  || alarm.getAlert().toString() == "") {
            	alarm.setAlert(Settings.System.DEFAULT_ALARM_ALERT_URI);
            }
        }
        
        if(alarm.isRandomRingtone()){
        	Uri tmpRing = getRandomRingtone();
        	if(tmpRing != null){
        	  alarm.setAlert(tmpRing);
        	}
        }
        
        return alarm.getAlert();
    }
    
    private void stopAlarm(){
    	mMediaPlayer.stop();
        turnVibratorOn(false);
        finish();    	
    }

	private void snoozeAlarm(){
		alarm.setAlarmFormat(AlarmFormat.HOUR_24);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		Intent intent = new Intent(this, AlarmReceiverActivity.class);
		intent.putExtra(AlarmEntry.COLUMN_NAME_ALERT,alarm.getAlert().toString());
		intent.putExtra("Alarm",alarm);
		//intent.putExtra(name, value)
		PendingIntent alarmIntent = PendingIntent.getActivity(this, Integer.parseInt(alarm.getHour() + "" + alarm.getMinutes()), intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		// Set the alarm to start at
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		//calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
		//calendar.set(Calendar.MINUTE, alarm.getMinutes());

		// setRepeating() lets you specify a precise 
//	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    String snooze = prefs.getString("snooze", "5");
//	    Integer snooze =(Integer) prefs.getInt("snooze", 5);
	//    Toast.makeText(getApplicationContext(),"znoose value : " + snooze, Toast.LENGTH_SHORT).show();
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis() + ((Integer.parseInt(snooze)) * 60 * 1000), 0, alarmIntent);
		
	}
	
	
}

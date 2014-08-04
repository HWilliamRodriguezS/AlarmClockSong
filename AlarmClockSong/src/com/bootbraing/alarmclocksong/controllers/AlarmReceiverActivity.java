package com.bootbraing.alarmclocksong.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
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
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

import com.bootbraing.alarmclocksong.R;
import com.bootbraing.alarmclocksong.models.Alarm;
import com.bootbraing.alarmclocksong.models.Alarm.AlarmFormat;
import com.bootbraing.alarmclocksong.models.AlarmReaderContract.AlarmEntry;

public class AlarmReceiverActivity extends Activity {

	private final int ALARM_RECEIVER_ACTIVITY = 1206070001;
	
	private MediaPlayer mMediaPlayer; 
	private Alarm alarm;
	private Vibrator vibratorPlayer;
	
	private Button stopAlarm;
	private Button snoozeAlarm; 
	
	private NotificationManager notifMgr;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        alarm = (Alarm) intent.getParcelableExtra("Alarm");
        vibratorPlayer = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.alarm_receiver);
 
        stopAlarm = (Button) findViewById(R.id.stopAlarm);
        stopAlarm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopAlarm();
				notifMgr.cancel(ALARM_RECEIVER_ACTIVITY);
			}
		});
        /*stopAlarm.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
            	stopAlarm();
                return false;
            }
        });
        */
        snoozeAlarm = (Button)findViewById(R.id.snoozeAlarm);
        snoozeAlarm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//Toast.makeText(getApplicationContext(),"Got clicked", Toast.LENGTH_SHORT).show();
				snoozeAlarm();
				stopAlarm();
				
			}
		});
        
        playSound(this, getAlarmUri());
        
        if(alarm.isVibrate()){
            turnVibratorOn(true);
        }
        
        notifMgr=  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //Intent newIntent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(this, AlarmReceiverActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        
       
        
		String body = "The body of the notification";
		String title = "Title Of Notification";
		Notification n = new Notification(R.drawable.acs_ic_default,body,System.currentTimeMillis());
		n.setLatestEventInfo(this, title, body, pi);
		n.defaults = Notification.DEFAULT_ALL;
		notifMgr.notify(ALARM_RECEIVER_ACTIVITY,n);
        
        //Notification n = new Notification(R.drawable.alarm_clock_24,body,System.currentTimeMillis());
        
//        Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
//        notificationIntent.setClass(getApplicationContext(),AlarmReceiverActivity.class);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        Notification n = new Notification(R.drawable.alarm_clock_24,body,System.currentTimeMillis());
//	;
//        
//        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
//       		 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        
//    	n.setLatestEventInfo(this, title, body, notificationPendingIntent);
//		n.defaults = Notification.DEFAULT_ALL;
//		notifMgr.notify(ALARM_RECEIVER_ACTIVITY,n);
//        // notifMgr.

        /*Unlock the Screen to show the Activity*/
		KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		if (keyguardManager.inKeyguardRestrictedInputMode()) 
		{
		    Window window = this.getWindow();//Activity.getWindow();
		    window.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
		    window.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		    window.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
		} 
        
    }
    
    @Override
	public void onBackPressed() {
    	moveTaskToBack (true);
	}

	public Uri getRandomRingtone(int[] ringTypes){
    
		Uri[] allRingtones ;
		List<Uri> listRingtones = new ArrayList<Uri>();
		//int ringTypes[] = {RingtoneManager.TYPE_ALARM,RingtoneManager.TYPE_RINGTONE,RingtoneManager.TYPE_NOTIFICATION,1024};
		
		for (int ringType : ringTypes) {
			
			if(ringType == 1024){continue;}
			
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
		
		/* Ending */
		//Some audio may be explicitly marked as not being music
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

		String[] projection = {
		        MediaStore.Audio.Media._ID,
		        MediaStore.Audio.Media.ARTIST,
		        MediaStore.Audio.Media.TITLE,
		        MediaStore.Audio.Media.DATA,
		        MediaStore.Audio.Media.DISPLAY_NAME,
		        MediaStore.Audio.Media.DURATION
		};

		@SuppressWarnings("deprecation")
		Cursor cursor = this.managedQuery(
		        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		        projection,
		        selection,
		        null,
		        null);
		//listRingtones = new ArrayList<Uri>();
		List<String> songs = new ArrayList<String>();
		while(cursor.moveToNext()){
		        songs.add(cursor.getString(3) );
		        //Uri uriSong= Uri.parse("");
		        listRingtones.add(Uri.parse(cursor.getString(3)));
		}
		
		
		/* Ending */
		//Toast.makeText(getApplicationContext(),"Music : " + songs,Toast.LENGTH_LONG).show();
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
        	
        	
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        	Map<String,?> soundList = prefs.getAll();
        	String str ;//= soundList.get("soundList").toString();
        	int[] values = null;
			if (soundList.get("soundList") != null && soundList.get("soundList") != "") {
				str = soundList.get("soundList").toString();
				String[] splited = null;
				splited = str.split("\\s+");
				values = new int[splited.length];
				
				for (int i = 0; i < splited.length; i++) {
					// {"Ringtones","Alerts","Notifications","MusicLibrary"};
					// {RingtoneManager.TYPE_ALARM,RingtoneManager.TYPE_RINGTONE,RingtoneManager.TYPE_NOTIFICATION,1024};
					values[i] = (splited[i].equals("Ringtones")) ? RingtoneManager.TYPE_ALARM
							: 256;
					values[i] = (splited[i].equals("Alerts")) ? RingtoneManager.TYPE_RINGTONE
							: values[i];
					values[i] = (splited[i].equals("Notifications")) ? RingtoneManager.TYPE_NOTIFICATION
							: values[i];
					values[i] = (splited[i].equals("MusicLibrary")) ? 1024
							: values[i];
					Toast.makeText(getApplicationContext()," Value :  " + splited[i],Toast.LENGTH_LONG).show();
				}
				//Toast.makeText(getApplicationContext(),	" lenght : " + values.length + " , and value : " + values[0], Toast.LENGTH_LONG).show();

			}
        	//Object eo = soundList.get("soundList");
        	//Toast.makeText(getApplicationContext()," Prefs " + soundList,Toast.LENGTH_LONG).show();
        	//Log.d("Prefs " , "Prefs :" + soundList.get("soundList"));
        	
        	if(values != null){
        		//Toast.makeText(getApplicationContext()," Inside ",Toast.LENGTH_LONG).show();
        		Uri tmpRing = getRandomRingtone(values);
        	  alarm.setAlert(tmpRing);
        	}
        }
        
        return alarm.getAlert();
    }
    
    private void stopAlarm(){
    	mMediaPlayer.stop();
        turnVibratorOn(false);
        finish();    
        moveTaskToBack(true);
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

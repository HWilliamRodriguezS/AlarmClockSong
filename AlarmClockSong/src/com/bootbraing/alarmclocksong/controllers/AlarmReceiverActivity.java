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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bootbraing.alarmclocksong.R;
import com.bootbraing.alarmclocksong.models.Alarm;
import com.bootbraing.alarmclocksong.models.Alarm.AlarmFormat;

public class AlarmReceiverActivity extends Activity {

	private final int ALARM_RECEIVER_ACTIVITY = 1206070001;
	
	private MediaPlayer mMediaPlayer; 
	private Alarm alarm;
	private Vibrator vibratorPlayer;
	
	private Button stopAlarm;
	private Button snoozeAlarm; 
	private TextView tvAlarmLabel;
	private TextView tvAlarmTime;
	
	private NotificationManager notifMgr;
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        alarm = (Alarm) intent.getParcelableExtra("Alarm");
        vibratorPlayer = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
//        LinearLayout  linearLayout = (LinearLayout) findViewById(R.id.linearLayoutid);
//        linearLayout.setBackgroundResource(R.drawable.background_fingerboard);
        setContentView(R.layout.alarm_receiver);
 
        stopAlarm = (Button) findViewById(R.id.stopAlarm);
        stopAlarm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopAlarm();
				notifMgr.cancel(ALARM_RECEIVER_ACTIVITY);
				
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				if(prefs.contains("snoozedAlamr")){
					//long intentID = Long.parseLong(prefs.getString("snoozedAlarm", "0"));
					String snooze = prefs.getString("snooze", "5");
					
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis() + (  (Integer.parseInt(snooze)) * 60 * 1000) );
				    
				    alarm.setMinutes(calendar.get(Calendar.MINUTE));
				    alarm.setHour(calendar.get(Calendar.HOUR_OF_DAY));
				    
				    PendingIntent pIntent = new Alarms(getApplicationContext()).composePendingAlarmIntent(getApplicationContext(), alarm);
				  
					AlarmManager alrmgr = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
					alrmgr.cancel(pIntent);
					Log.d("Intent ID " , "Intent ID : " + Integer.parseInt(alarm.getHour() + "" + alarm.getMinutes()));
					prefs.edit().remove("snoozedAlamr").commit();
				}
				
				
			}
		});

        snoozeAlarm = (Button)findViewById(R.id.snoozeAlarm);
        snoozeAlarm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				if(prefs.contains("snoozedAlamr")){
					//long intentID = Long.parseLong(prefs.getString("snoozedAlarm", "0"));
					String snooze = prefs.getString("snooze", "5");
					
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis() + (  (Integer.parseInt(snooze)) * 60 * 1000) );
				    
				    alarm.setMinutes(calendar.get(Calendar.MINUTE));
				    alarm.setHour(calendar.get(Calendar.HOUR_OF_DAY));
				    
				    PendingIntent pIntent = new Alarms(getApplicationContext()).composePendingAlarmIntent(getApplicationContext(), alarm);
				  
					AlarmManager alrmgr = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
					alrmgr.cancel(pIntent);
					Log.d("Intent ID " , "Intent ID : " + Integer.parseInt(alarm.getHour() + "" + alarm.getMinutes()));
					prefs.edit().remove("snoozedAlamr").commit();
				}
				snoozeAlarm();
				stopAlarm();
			}
		});
         
        playSound(getApplicationContext(), getAlarmUri());
        if(alarm.isVibrate()){
            turnVibratorOn(true);
        }
        
        notifMgr =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        intent.addCategory(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(this, AlarmReceiverActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        
        tvAlarmLabel = (TextView) findViewById(R.id.alarm_label);
        tvAlarmLabel.setText(alarm.getLabel());
       
        tvAlarmTime = (TextView) findViewById(R.id.alarm_time);
        tvAlarmTime.setText(alarm.getTimeStr());
        
		String body = "Alarm : " + alarm.getTimeStr() + " , " + alarm.getLabel();
		String title = "Alarm Alart!";
		Notification n = new Notification(R.drawable.ic_stat_notify_alarm,body,System.currentTimeMillis());
		n.setLatestEventInfo(this, title, body, pi);
		//n.defaults = Notification.DEFAULT_ALL;
		n.defaults = 0;
		n.defaults |= Notification.DEFAULT_LIGHTS;
		n.defaults |= Notification.DEFAULT_VIBRATE;
		//n.audioStreamType = AudioManager.RINGER_MODE_SILENT;
		notifMgr.notify(ALARM_RECEIVER_ACTIVITY,n);
        
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

		List<String> songs = new ArrayList<String>();
		while(cursor.moveToNext()){
			songs.add(cursor.getString(3) );
		    listRingtones.add(Uri.parse(cursor.getString(3)));
		}
		
		/* Ending */
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
            Log.e("AlarmReceiver : ",e.getMessage());
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
        if (alarm.getAlert() == null  || alarm.getAlert().toString() == "" ) {
            alarm.setAlert(Settings.System.DEFAULT_ALARM_ALERT_URI);
            if (alarm.getAlert() == null  || alarm.getAlert().toString() == "") {
            	alarm.setAlert(Settings.System.DEFAULT_ALARM_ALERT_URI);
            }
        }
        
        if(alarm.isRandomRingtone()){
        	
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        	Map<String,?> soundList = prefs.getAll();
        	String str ;
        	int[] values = null;
			if (soundList.get("soundList") != null && soundList.get("soundList") != "") {
				str = soundList.get("soundList").toString();
				String[] splited = null;
				splited = str.split("\\s+");
				values = new int[splited.length];
				
				for (int i = 0; i < splited.length; i++) {
					values[i] = (splited[i].equals("Ringtones")) ? RingtoneManager.TYPE_ALARM : 256;
					values[i] = (splited[i].equals("Alerts")) ? RingtoneManager.TYPE_RINGTONE : values[i];
					values[i] = (splited[i].equals("Notifications")) ? RingtoneManager.TYPE_NOTIFICATION : values[i];
					values[i] = (splited[i].equals("MusicLibrary")) ? 1024	: values[i];
				}

			}
        	
        	if(values != null){
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

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String snooze = prefs.getString("snooze", "5");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis() + (  (Integer.parseInt(snooze)) * 60 * 1000) );
	    
	    alarm.setMinutes(calendar.get(Calendar.MINUTE));
	    alarm.setHour(calendar.get(Calendar.HOUR_OF_DAY));
	    alarm.setDaysOfWeek(new Alarm.DaysOfWeek(0));
	    new Alarms(getApplicationContext()).setAlarm(getApplicationContext(), alarm);
		
		prefs.edit().putString("snoozedAlamr", "" + snooze).commit();
		Toast.makeText(getApplicationContext(), getString(R.string.alarm_snoozed) + " " + snooze + " minutes" , Toast.LENGTH_LONG).show();
		
		
		
	}

}

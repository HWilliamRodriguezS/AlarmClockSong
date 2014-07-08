package com.bootbraing.alarmclocksong.controllers;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.bootbraing.alarmclocksong.R;
import com.bootbraing.alarmclocksong.models.Alarm;

public class AlarmReceiverActivity extends Activity {

	private MediaPlayer mMediaPlayer; 
	//private Uri alert;
	private Alarm alarm;
	private Vibrator vibratorPlayer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        alarm = (Alarm) intent.getParcelableExtra("Alarm");
        vibratorPlayer = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Log.d("Reicibing Parceable Object" ,"Pareable Alarm : " + alarm);
        //alert =Uri.parse(intent.getStringExtra(AlarmEntry.COLUMN_NAME_ALERT));
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.alarm);
 
        Button stopAlarm = (Button) findViewById(R.id.stopAlarm);
        stopAlarm.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                mMediaPlayer.stop();
                turnVibratorOn(false);
                finish();
                return false;
            }
        });
        
        playSound(this, getAlarmUri());
        turnVibratorOn(true);
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
      
        if (alarm.getAlert() == null  || alarm.getAlert().toString() == "" ) {
            alarm.setAlert(Settings.System.DEFAULT_ALARM_ALERT_URI);
            if (alarm.getAlert() == null  || alarm.getAlert().toString() == "") {
            	alarm.setAlert(Settings.System.DEFAULT_ALARM_ALERT_URI);
            }
        }
        return alarm.getAlert();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}
	
	
}

package com.bootbraing.alarmclocksong.controllers;

import java.util.ArrayList;
import java.util.List;

import android.R.drawable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bootbraing.alarmclocksong.R;
import com.bootbraing.alarmclocksong.dao.AlarmDAO;
import com.bootbraing.alarmclocksong.models.Alarm;
import com.bootbraing.alarmclocksong.models.Alarm.AlarmFormat;

@SuppressLint("ClickableViewAccessibility")
public class MainAlarmActivity extends Activity {

	//	private final int MAIN_ALARM_ACTIVITY = 1;
	private final int SET_ALARM_ACTIVITY = 2;
	private List<Alarm> alarms = new ArrayList<Alarm>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_alarm);
		findViewById(R.id.mainAlarmLayout).setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.setBackgroundResource(drawable.list_selector_background);
				return false;
			}
		});
		
	}
	
	@Override
	 public void onWindowFocusChanged(boolean hasFocus) {
	  super.onWindowFocusChanged(hasFocus);
	  listAllAlarms();
	 }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_alarm, menu);
		
		
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		default:
		case R.id.action_settings:
		Intent i = new Intent(this, AlarmPreferences.class);
		startActivityForResult(i, 10);	
		break;
	
		}
		
		return super.onOptionsItemSelected(item);
	}

	public void addNewAlarm(View v) {
		Intent i = new Intent(this, SetAlarmActivity.class);
		startActivityForResult(i, SET_ALARM_ACTIVITY);
	}
	
	public void editAlarm(Alarm alarm){
		alarm.setAlarmFormat(AlarmFormat.HOUR_24);
//		Log.e("Before PutExtra","Alarm : " + alarm);
		Intent i = new Intent(this, SetAlarmActivity.class);
        i.putExtra("Alarm", alarm);
		startActivityForResult(i, SET_ALARM_ACTIVITY);
		
	}
	
	@SuppressLint("RtlHardcoded")
	public void listAllAlarms() {
		alarms = new AlarmDAO(this).getAlarms();

		TableLayout table = (TableLayout) MainAlarmActivity.this.findViewById(R.id.tAlarms);
		table.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
		for (Alarm alrm : alarms) {
			LinearLayout vLine = new LinearLayout(this);
			vLine.setLayoutParams(new LayoutParams(1, LayoutParams.MATCH_PARENT));
			vLine.setBackgroundResource(R.drawable.vertical_gradle);
			
			LinearLayout hLine = new LinearLayout(this);
			hLine.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,1));
			hLine.setBackgroundResource(R.drawable.horizontal_gradle);
			TableRow row = new TableRow(this);
			//TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
			TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
			row.setLayoutParams(lp);
			row.setId((int) alrm.getId());
			
			ToggleButton tbOnOff = new ToggleButton(this);
			tbOnOff.setChecked(alrm.isEnabled());
			tbOnOff.setBackgroundColor(Color.TRANSPARENT);
			//tbOnOff.setBackgroundResource(R.layout.alarm_on_off);
			tbOnOff.setButtonDrawable(R.layout.alarm_on_off);
			tbOnOff.setTextOff("");
			tbOnOff.setTextOn("");
			tbOnOff.setText("");
			
			tbOnOff.setWidth(table.getWidth()/4);
			tbOnOff.setHeight(table.getWidth()/4);
			//tbOnOff.setPadding(4, 4, 4, 4);
			tbOnOff.setGravity(Gravity.CENTER);
			//tbOnOff.setG
            //tbOnOff.
			tbOnOff.buildDrawingCache(true);
			
		    //LayoutParams tbParams = new LayoutParams();
			//tbParams.setMargins(24,24, 24, 24);
			
			//tbParams.
			//tbOnOff.setLayoutParams(tbParams);

			tbOnOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					buttonView.setBackgroundResource(drawable.list_selector_background);
					TableRow tr = (TableRow) buttonView.getParent();
					Alarm alarm = new AlarmDAO(getApplicationContext()).getAlarm(tr.getId());
					OnOffAlarm onOffAlarm = new OnOffAlarm();
	
					if (isChecked) {
						alarm.setEnabled(true);
						onOffAlarm.turnAlarmOffOn(alarm, true,getApplicationContext());
					} else {
						alarm.setEnabled(false);
						onOffAlarm.turnAlarmOffOn(alarm, false,getApplicationContext());
					}
					new AlarmDAO(getApplicationContext()).update(alarm);
					
				}
			});
			
			alrm.setAlarmFormat(AlarmFormat.HOUR_24);
			Log.d("Alarm Details" , " Alarm : " + alrm.toString());
			Log.d("Alarm Repeating" , "Days : " + Integer.toBinaryString(alrm.getDaysOfWeek().getCoded()));
			Log.d("=====","======");
		
			
			TableLayout tbr = new TableLayout(this);
			LayoutParams tableParam = new LayoutParams((table.getWidth()/4)*3,LayoutParams.MATCH_PARENT);

			tbr.setWeightSum(1);
			tbr.setLayoutParams(tableParam);
			tbr.setGravity(Gravity.CENTER);

			TableRow row1_1 = new TableRow(this);
			//Log.e("TableLayout " , "Width : " + tbr.getWidth());
			TextView tvAlarmTitle = new TextView(this);
			tvAlarmTitle.setText("" + alrm.getLabel());
			tvAlarmTitle.setGravity(Gravity.CENTER);
			tvAlarmTitle.setTypeface(Typeface.DEFAULT_BOLD);
			//InputFilter[] filterArray = new InputFilter[1];
			//filterArray[0] = new InputFilter.LengthFilter(36);
			//tvAlarmTitle.setFilters(filterArray);
			tvAlarmTitle.setEllipsize(TruncateAt.END);
			
			LayoutParams attParam = new LayoutParams(table.getWidth()/2,LayoutParams.MATCH_PARENT,0.7f);
			attParam.gravity = Gravity.LEFT;
			//tvAlarmTitle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,(0.7f)));
			tvAlarmTitle.setLayoutParams(new LayoutParams(table.getWidth()/4,LayoutParams.MATCH_PARENT,(0.7f)));
			//.setLayoutParams(new LayoutParams())
			
			TextView tvAlarmTime = new TextView(this);
			//tvAlarmTime.setTextSize(22f);
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			
//			Log.e("Font Metrics : " , "Font Size :" + metrics.scaledDensity);
//			Log.e("Font Metrics : " , "Font Size :" + metrics.DENSITY_MEDIUM);
			
			tvAlarmTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
			//tvAlarmTime.setTextSize(metrics.scaledDensity);
			tvAlarmTime.setTypeface(Typeface.DEFAULT_BOLD);
			tvAlarmTime.setGravity(Gravity.CENTER);
			//Log.e("Alarm Data : "," Alarm : " + alrm.toString());
			tvAlarmTime.setText("" + alrm.getTimeStr());
		    LayoutParams atParam = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,0.3f);
		    atParam.gravity = Gravity.RIGHT;
			//tvAlarmTime.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,0.3f));
			tvAlarmTime.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,0.3f));
			
			row1_1.addView(tvAlarmTitle);
			row1_1.addView(tvAlarmTime);
			
			TableRow row1_2 = new TableRow(this);
			
			TextView tvAlarmDays = new TextView(this);
			tvAlarmDays.setText(alrm.getDaysOfWeek().toString(getApplicationContext(), false));
			
			LayoutParams rowParam = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT,1f);
			rowParam.span = 2;
			tvAlarmDays.setLayoutParams(rowParam);
			
			row1_2.addView(tvAlarmDays);
			
			tbr.addView(row1_1);
			tbr.addView(row1_2);
						
			row.addView(tbr);
			row.addView(vLine);
			row.addView(tbOnOff);
			row.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					((TableRow)v).setBackgroundResource(drawable.list_selector_background);
					return false;
				}
				
			});
			
			row.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					editAlarm(new AlarmDAO(getApplicationContext()).getAlarm(((TableRow)v).getId()));
				}
			});
			table.addView(row);
			table.addView(hLine);

		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.onCreate(null);
	}

}

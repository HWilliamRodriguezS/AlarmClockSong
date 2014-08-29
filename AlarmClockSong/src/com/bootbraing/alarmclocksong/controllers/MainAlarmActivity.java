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

	private final int SET_ALARM_ACTIVITY = 2;
	private List<Alarm> alarms = new ArrayList<Alarm>();
	private boolean loaded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_alarm);
		findViewById(R.id.mainAlarmLayout).setOnTouchListener(
				new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						v.setBackgroundResource(drawable.list_selector_background);
						return false;
					}
				});
		loaded = false;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!loaded) {
			listAllAlarms();
			loaded = true;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
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

		switch (item.getItemId()) {
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

	public void editAlarm(Alarm alarm) {
		alarm.setAlarmFormat(AlarmFormat.HOUR_24);
		Intent i = new Intent(this, SetAlarmActivity.class);
		i.putExtra("Alarm", alarm);
		startActivityForResult(i, SET_ALARM_ACTIVITY);
	}

	@SuppressLint("RtlHardcoded")
	public void listAllAlarms() {
		alarms = new AlarmDAO(this).getAlarms();

		TableLayout table = (TableLayout) MainAlarmActivity.this
				.findViewById(R.id.tAlarms);
		table.removeAllViews();

		// Determine density
		DisplayMetrics screenMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(screenMetrics);
		int density = screenMetrics.densityDpi;

		for (Alarm alrm : alarms) {
			LinearLayout vLine = new LinearLayout(this);
			vLine.setLayoutParams(new LayoutParams(1, LayoutParams.MATCH_PARENT));
			vLine.setBackgroundResource(R.drawable.vertical_gradle);

			LinearLayout hLine = new LinearLayout(this);
			hLine.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
			hLine.setBackgroundResource(R.drawable.horizontal_gradle);
			
			TableRow row = new TableRow(this);
			TableRow.LayoutParams lp = new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,
					TableRow.LayoutParams.MATCH_PARENT);
			row.setLayoutParams(lp);
			row.setId((int) alrm.getId());

			ToggleButton tbOnOff = new ToggleButton(this);
			tbOnOff.setChecked(alrm.isEnabled());
			tbOnOff.setBackgroundColor(Color.TRANSPARENT);
			tbOnOff.setButtonDrawable(R.layout.alarm_on_off);
			tbOnOff.setTextOff("");
			tbOnOff.setTextOn("");
			tbOnOff.setText("");
			tbOnOff.setWidth(density / 2);
			tbOnOff.setHeight(density / 2);
			tbOnOff.setGravity(Gravity.CENTER);
			tbOnOff.buildDrawingCache(true);

			tbOnOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					buttonView.setBackgroundResource(drawable.list_selector_background);
					TableRow tr = (TableRow) buttonView.getParent();
					Alarm alarm = new AlarmDAO(getApplicationContext()).getAlarm(tr.getId());
					OnOffAlarm onOffAlarm = new OnOffAlarm();

					if (isChecked) {
						alarm.setEnabled(true);
						onOffAlarm.turnAlarmOffOn(alarm, true,
								getApplicationContext());
					} else {
						alarm.setEnabled(false);
						onOffAlarm.turnAlarmOffOn(alarm, false,
								getApplicationContext());
					}
					new AlarmDAO(getApplicationContext()).update(alarm);
				}
			});

			alrm.setAlarmFormat(AlarmFormat.HOUR_24);
			TableLayout tbl = new TableLayout(this);
			LayoutParams tableParam = new LayoutParams(
					(table.getWidth() / 4) * 3, LayoutParams.MATCH_PARENT);

			tbl.setWeightSum(1);
			tbl.setLayoutParams(tableParam);
			tbl.setGravity(Gravity.CENTER);

			TableRow row1_1 = new TableRow(this);
			
			TextView tvAlarmTitle = new TextView(this);
			tvAlarmTitle.setText("" + alrm.getLabel());
			tvAlarmTitle.setGravity(Gravity.CENTER);
			tvAlarmTitle.setTypeface(Typeface.DEFAULT_BOLD);
			tvAlarmTitle.setEllipsize(TruncateAt.END);
			tvAlarmTitle.setLayoutParams(new LayoutParams(table.getWidth() / 4,
					LayoutParams.MATCH_PARENT, (0.7f)));

			TextView tvAlarmTime = new TextView(this);
			tvAlarmTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
			tvAlarmTime.setTypeface(Typeface.DEFAULT_BOLD);
			tvAlarmTime.setGravity(Gravity.CENTER);
			tvAlarmTime.setText("" + alrm.getTimeStr());
			LayoutParams atParam = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, 0.3f);
			atParam.gravity = Gravity.RIGHT;
			tvAlarmTime.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT, 0.3f));

			row1_1.addView(tvAlarmTitle);
			row1_1.addView(tvAlarmTime);

			TableRow row1_2 = new TableRow(this);

			TextView tvAlarmDays = new TextView(this);
			tvAlarmDays.setText(alrm.getDaysOfWeek().toString(
					getApplicationContext(), false));

			LayoutParams rowParam = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.MATCH_PARENT, 1f);
			rowParam.span = 2;
			
			tvAlarmDays.setLayoutParams(rowParam);
			
			row1_2.addView(tvAlarmDays);

			tbl.addView(row1_1);
			tbl.addView(row1_2);

			row.addView(tbl);
			row.addView(vLine);
			row.addView(tbOnOff);
			row.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					((TableRow) v).setBackgroundResource(drawable.list_selector_background);
					return false;
				}
			});

			row.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					editAlarm(new AlarmDAO(getApplicationContext()).getAlarm(((TableRow) v).getId()));
				}
			});
			table.addView(row);
			table.addView(hLine);
		}

	}

}

package com.dianxin.tby.stopwatch.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dianxin.tby.stopwatch.R;
import com.dianxin.tby.stopwatch.adapter.StopwatchRecordAdapter;
import com.dianxin.tby.stopwatch.units.RecordDao;
import com.dianxin.tby.stopwatch.units.Stopwatch;

public class StopwatchActivity extends Activity {
	private TextView watchTxt;
	private Button watchStateBtn;
	private Button watchOpearBtn;
	private ListView resultList;
	private Handler watchHandle;
	private Stopwatch stopwatch;
	private StopwatchRecordAdapter adapter;

	private RecordDao recordDao;
	private ImageView secondArrow;
	private int frontSecond = 0; // 上一次刷新的秒数
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stopwatch);
		init();
	}

	private void init() {
		watchTxt = (TextView) findViewById(R.id.watchTxt);
		watchStateBtn = (Button) findViewById(R.id.stateBtn);
		watchStateBtn.setTag(true);
		watchOpearBtn = (Button) findViewById(R.id.operatBtn);
		watchOpearBtn.setTag(false);
		resultList = (ListView) findViewById(R.id.resultList);
		secondArrow = (ImageView) findViewById(R.id.second_arrow);

		watchHandle = new WatchHandle();
		stopwatch = new Stopwatch(watchHandle);
		adapter = new StopwatchRecordAdapter(this);
		resultList.setAdapter(adapter);
		recordDao = RecordDao.getInstance(this);
	}

	public void handleClick(View v) {
		switch (v.getId()) {
		case R.id.stateBtn:
			stateBtnClick((Button)v);
			break;
		case R.id.operatBtn:
			operatBtnClick((Button)v);
			break;
		default:
			break;
		}
	}

	private void stateBtnClick(Button v) {
		// true 为未开始 false为已开始
		if (v.getTag() == null)
			return;
		if ((Boolean) v.getTag()) {
			startWatch(v);
		} else {
			stopWatch(v);
		}
	}

	private void startWatch(Button v) {
		v.setTag(false);
		((Button) v).setText(R.string.pause_watch);
		stopwatch.startStopwatch();
		watchOpearBtn.setTag(true);
		watchOpearBtn.setText(R.string.record_watch);
	}

	private void stopWatch(Button v) {
		stopwatch.stopStopwatch();
		watchOpearBtn.setTag(false);
		watchOpearBtn.setText(R.string.reset_watch);
		((Button) v).setText(R.string.begin_watch);
		v.setTag(true);
	}

	private void operatBtnClick(Button v) {
		if (v.getTag() == null)
			return;
		if ((Boolean) v.getTag()) {
			// 计数操作
			adapter.addRecord(stopwatch.makeSnapshot());
			resultList.setSelection(0);
		} else { 
			// 重置
			watchTxt.setText(R.string.init_watch);
			stopwatch.resetStopwatch();
			frontSecond = 0;
			secondArrow.clearAnimation();
			if (adapter.getCount() > 0) {
				showSaveAskDialog();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stopwatch, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getOrder()) {
		case 100:
			viewHistory();
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void viewHistory() {
		Intent intent = new Intent();
		intent.setClass(this, HistorySectionActivity.class);
		startActivity(intent);
	}

	private class WatchHandle extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Stopwatch.GET_CURTIME:
				long curTime = stopwatch.makeSnapshot();
				watchTxt.setText(Stopwatch.formatTime(curTime));
				
				int curSecond = Stopwatch.getSecond(curTime);
				float from = (float) (frontSecond * 0.360);
				float to = (float) (curSecond * 0.360);
				long duration = Math.abs((curSecond - frontSecond) * 1000);
				turnSecondArrow(makeTurnArrowAnim(from, to, duration));
				frontSecond = curSecond;
				break;

			default:
				break;
			}
			
			super.handleMessage(msg);
		}

	}
	
	private Animation makeTurnArrowAnim(float fromDegrees, float toDegrees,
			long duration) {
		RotateAnimation rotateAnim = new RotateAnimation(fromDegrees,
				toDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.65f);
		rotateAnim.setFillAfter(true);
		rotateAnim.setInterpolator(new LinearInterpolator());
		rotateAnim.setDuration(duration);
		rotateAnim.setRepeatCount(-1);
		return rotateAnim;
	}
	
	private void turnSecondArrow(Animation animation) {
		secondArrow.startAnimation(animation);
	}

	private void showSaveAskDialog() {
		new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.reset_ask))
				.setMessage(getResources().getString(R.string.save_ask))
				.setPositiveButton(getResources().getString(R.string.save),
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 保存结果
								saveRecords(null);
								adapter.clearData();
								dialog.dismiss();
							}
						})
				.setNegativeButton(getResources().getString(R.string.not_save),
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								adapter.clearData();
								dialog.dismiss();
							}
						}).show();
	}

	private void saveRecords(final String name) {
		new AsyncTask<Object, Integer, Integer>() {
			
			
			@Override
			protected void onPreExecute() {
				if(pd == null) {
					pd = new ProgressDialog(StopwatchActivity.this);
					pd.setCancelable(false);
					pd.setMessage(StopwatchActivity.this.getResources().getString(R.string.wait));
				}
				pd.show();
				super.onPreExecute();
			}

			@SuppressWarnings("unchecked")
			@Override
			protected Integer doInBackground(Object... params) {
				recordDao.save((List<long[]>)params[0], (String)params[1]);
				return null;
			}

			@Override
			protected void onPostExecute(Integer result) {
				pd.dismiss();
				super.onPostExecute(result);
			}
			
			
		}.execute(adapter.makeSnapshotData(), name);
	}

}

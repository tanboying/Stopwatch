package com.dianxin.tby.stopwatch.ui;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.dianxin.tby.stopwatch.R;
import com.dianxin.tby.stopwatch.adapter.StopwatchRecordAdapter;
import com.dianxin.tby.stopwatch.units.RecordDao;

public class RecordDetailsActivity extends Activity {
	private ListView list;
	private StopwatchRecordAdapter adapter;
	private RecordDao rd;
	private int sectionId;
	private String paramKey = "sectionId";
	private ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_details);
		if(savedInstanceState == null) {
			Bundle b = this.getIntent().getExtras();
			sectionId = b.getInt(paramKey);
		} else {
			sectionId = savedInstanceState.getInt(paramKey);
		}
		init(sectionId);
		
	}
	
	private void init(int sectionId) {
		list = (ListView) findViewById(R.id.detailList);
		rd = RecordDao.getInstance(this);
		new AsyncTask<Integer, Integer, List<long[]>>() {

			
			@Override
			protected void onPreExecute() {
				if(pd == null) {
					pd = new ProgressDialog(RecordDetailsActivity.this);
					pd.setCancelable(false);
					pd.setMessage(RecordDetailsActivity.this.getResources().getString(R.string.wait));
				}
				pd.show();
				super.onPreExecute();
			}

			@Override
			protected List<long[]> doInBackground(Integer... params) {
				return rd.findRecordsBySectionId(params[0]);
			}

			@Override
			protected void onPostExecute(List<long[]> result) {
				adapter = new StopwatchRecordAdapter(RecordDetailsActivity.this, result);
				list.setAdapter(adapter);
				pd.dismiss();
				adapter.notifyDataSetChanged();
				super.onPostExecute(result);
			}
			
		}.execute(sectionId);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(paramKey, sectionId);
		super.onSaveInstanceState(outState);
	}
	
	
}

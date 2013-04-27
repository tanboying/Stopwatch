package com.dianxin.tby.stopwatch.ui;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dianxin.tby.stopwatch.R;
import com.dianxin.tby.stopwatch.adapter.HistoryAdapter;
import com.dianxin.tby.stopwatch.units.RecordDao;

public class HistorySectionActivity extends Activity implements OnItemClickListener{
	private ListView listView;
	private RecordDao recordDao;
	private HistoryAdapter adapter;
	private ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_section);
		init();
	}
	
	private void init() {
		listView = (ListView) findViewById(R.id.section_list);
		recordDao = RecordDao.getInstance(this);
		new AsyncTask<String, Integer, List<List<Object>>>() {
			
			
			@Override
			protected void onPreExecute() {
				if(pd == null) {
					pd = new ProgressDialog(HistorySectionActivity.this);
					pd.setCancelable(false);
					pd.setMessage(HistorySectionActivity.this.getResources().getString(R.string.wait));
				}
				pd.show();
				super.onPreExecute();
			}

			@Override
			protected List<List<Object>> doInBackground(String... params) {
				return recordDao.findSectionsList();
			}

			@Override
			protected void onPostExecute(List<List<Object>> result) {
				adapter = new HistoryAdapter(HistorySectionActivity.this, result);
				listView.setAdapter(adapter);
				pd.dismiss();
				adapter.notifyDataSetChanged();
				super.onPostExecute(result);
			}
			
		}.execute();
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		@SuppressWarnings("unchecked")
		List<Object> item = (List<Object>) adapter.getItem(position);
		int sectionId = (Integer)item.get(HistoryAdapter.HISTORY_ID);
		Intent intent = new Intent();
		Bundle b = new Bundle();
		b.putInt("sectionId", sectionId);
		intent.putExtras(b);
		intent.setClass(this, RecordDetailsActivity.class);
		startActivity(intent);
	}
	
}

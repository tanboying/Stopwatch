package com.dianxin.tby.stopwatch.adapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dianxin.tby.stopwatch.R;
import com.dianxin.tby.stopwatch.units.Stopwatch;

public class StopwatchRecordAdapter extends BaseAdapter {
	static public int RECORD_SEQUENCE = 0;
	static public int RECORD_CURTIME = 1;
	static public int RECORD_GAP = 2;
	
	final private int[] itemIds = {R.id.leftTxt, R.id.middleTxt, R.id.rightTxt};
	//0:sequence 1:curTime 2:gap
	private List<long[]> datas;
	private LayoutInflater inflater;
	private Context context;
	public StopwatchRecordAdapter(Context context) {
		datas = new LinkedList<long[]>();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}
	public StopwatchRecordAdapter(Context context, List<long[]> datas) {
		this.datas = datas;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.listitem_3txt, null);
			viewHolder = new ViewHolder();
			viewHolder.leftView = (TextView)convertView.findViewById(itemIds[RECORD_SEQUENCE]);
			viewHolder.middleView = (TextView)convertView.findViewById(itemIds[RECORD_CURTIME]);
			viewHolder.rightView = (TextView)convertView.findViewById(itemIds[RECORD_GAP]);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		bindData(viewHolder, position);
		return convertView;
	}
	
	private void bindData(ViewHolder viewHolder, int position) {
		viewHolder.leftView.setText(context.getResources().getString(R.string.record_sequence, datas.get(position)[0]));
		viewHolder.middleView.setText(Stopwatch.formatTime(datas.get(position)[1]));
		viewHolder.rightView.setText("+" + Stopwatch.formatTime(datas.get(position)[2]));
		
	}
	
	public void addRecord(long curTime) {
		long [] item = new long[3];
		item[0] = (long) (datas.size() + 1);
		item[1] = curTime;
		item[2] = datas.size() > 0? curTime - (datas.get(0)[RECORD_CURTIME]) : curTime;
		datas.add(0, item);
		this.notifyDataSetChanged();
	}
	
	public void clearData() {
		datas.clear();
		notifyDataSetChanged();
	}
	
	public List<long[]> makeSnapshotData() {
		List<long[]> newDatas = new ArrayList<long[]>(datas);
		return newDatas;
	}
}

package com.dianxin.tby.stopwatch.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dianxin.tby.stopwatch.R;

public class HistoryAdapter extends BaseAdapter {
	final private int[] itemIds = {R.id.sectionName, R.id.createDate};
	final static public int HISTORY_ID = 0;
	final static public int HISTORY_RECORDNAME = 1;
	final static public int HISTORY_CREATEDATE = 2;
	private List<List<Object>> datas;
	private LayoutInflater inflater;
	
	public HistoryAdapter(Context context, List<List<Object>> datas) {
		this.datas = datas;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
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
			convertView = inflater.inflate(R.layout.listitem_section, null);
			viewHolder = new ViewHolder();
			viewHolder.leftView = (TextView)convertView.findViewById(itemIds[0]);
			viewHolder.middleView = (TextView)convertView.findViewById(itemIds[1]);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		bindData(viewHolder, position);
		return convertView;
	}
	
	private void bindData(ViewHolder viewHolder, int position) {
		viewHolder.leftView.setText((String)datas.get(position).get(HISTORY_RECORDNAME));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date = sdf.format(new Date((Long)datas.get(position).get(HISTORY_CREATEDATE)));
		viewHolder.middleView.setText(date);
	}

}

package com.dianxin.tby.stopwatch.units;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.dianxin.tby.stopwatch.R;
import com.dianxin.tby.stopwatch.adapter.StopwatchRecordAdapter;
import com.dianxin.tby.stopwatch.database.DataBaseHelper;

public class RecordDao {
	
	static private RecordDao rd;
	private DataBaseHelper db = null;
	private String DatabaseName = "StopwatchDB";
	private String RecordListTableName = "Record_Section";
	private String RecordTableName = "Record";
	private int dbVersion = 1;
	private Context context;
	
	static public RecordDao getInstance(Context context) {
		synchronized (RecordDao.class) {
			if(rd == null) {
				rd = new RecordDao(context);
			}
		}
		return rd;
	}
	private RecordDao(Context context) {
		db = new DataBaseHelper(context, DatabaseName, null, dbVersion);
		this.context = context;
	}
	/**
	 * 存储一组时间计数
	 * @param datas	该组的数据
	 * @param name 该组计数的名字
	 */
	public void save(List<long[]> datas, String name) {
		if(datas == null || datas.size() == 0) return;
		int id = saveSection(name);
		if( id == -1) return;
		SQLiteDatabase  writeDB = db.getWritableDatabase();
		for (long[] data : datas) {
			ContentValues cv = new ContentValues(datas.size());
			cv.put("sectionId", id);
			cv.put("sequence", data[StopwatchRecordAdapter.RECORD_SEQUENCE]);
			cv.put("curtime", data[StopwatchRecordAdapter.RECORD_CURTIME]);
			cv.put("gap", data[StopwatchRecordAdapter.RECORD_GAP]);
			writeDB.insert(RecordTableName, null, cv);
		}
		closeResource(writeDB, null);
	}
	
	/**
	 * 保存到recordlist 表中，并且返回id
	 * @param name 记录的名称 不允许为空
	 * @return 如果插入失败则返回 -1 否则返回自动生成的id
	 */
	private int saveSection(String name) {
		int id = -1;
		if(TextUtils.isEmpty(name)) {
			name = context.getResources().getString(R.string.section_sequence, amountSectionCount()+1);
		}
		SQLiteDatabase  writeDB = db.getWritableDatabase();
		ContentValues rlcv = new ContentValues();
		rlcv.put("sectionName", name);
		rlcv.put("createDate", System.currentTimeMillis());
		long rowid = writeDB.insert(RecordListTableName, null, rlcv);
		if (rowid != -1) {
			id = findSectionIdByRowId(rowid);
		}
		closeResource(writeDB, null);
		return id;
	}
	
	private long amountSectionCount() {
		int result = 0;
		SQLiteDatabase  readDB = db.getReadableDatabase();
		Cursor cur = readDB.rawQuery("select count(*) from " + RecordListTableName, null);
		cur.moveToNext();
		result = cur.getInt(0);
		closeResource(readDB, null);
		return result;
	}
	/**
	 * 通过rowid 查找Record_List的ID
	 * @param rowid
	 * @return id
	 */
	private int findSectionIdByRowId(long rowid) {
		SQLiteDatabase  readDB = db.getReadableDatabase();
		Cursor cur = null;
		int id = 0;
		cur = readDB.rawQuery("select id from " + RecordListTableName + " where rowid=?", new String[]{rowid + ""});
		cur.moveToNext();
		id = cur.getInt(0);
		closeResource(readDB, cur);
		return id;
	}
	/**
	 * 通过id 查Rcords
	 * @param id
	 * @return
	 */
	public List<long[]> findRecordsBySectionId(int id) {
		SQLiteDatabase  readDB = db.getReadableDatabase();
		Cursor cur = null;
		List<long[]> datas = new ArrayList<long[]>();
		cur = readDB.rawQuery("select * from " + RecordTableName + " where sectionId=? order by sequence desc", new String[]{id + ""});
		while(cur.moveToNext()) {
			long[] data = new long[3];
			data[StopwatchRecordAdapter.RECORD_SEQUENCE] = cur.getLong(1);
			data[StopwatchRecordAdapter.RECORD_CURTIME] = cur.getLong(2);
			data[StopwatchRecordAdapter.RECORD_GAP] = cur.getLong(3);
			datas.add(data);
		}
		closeResource(readDB, cur);
		return datas;
	}
	/**
	 * 查找所有的计数组
	 * @return
	 */
	public List<List<Object>> findSectionsList() {
		SQLiteDatabase  readDB = db.getReadableDatabase();
		Cursor cur = null;
		List<List<Object>> datas = new ArrayList<List<Object>>();
		cur = readDB.rawQuery("select * from " + RecordListTableName, null);
		while(cur.moveToNext()) {
			List<Object> data = new ArrayList<Object>(3);
			data.add(cur.getInt(0));
			data.add(cur.getString(1));
			data.add(cur.getLong(2));
			datas.add(data);
		}
		closeResource(readDB, cur);
		return datas;
	}
	
	private void closeResource(SQLiteDatabase db, Cursor cur) {
		if(cur != null) {
			cur.close();
		}
		if(db != null) {
			db.close();
		}
	}
	
	public void closeDB() {
		db.close();
	}
	
}

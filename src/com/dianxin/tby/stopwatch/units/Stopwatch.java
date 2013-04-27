package com.dianxin.tby.stopwatch.units;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;


public class Stopwatch {
	final static public int GET_CURTIME = 1;
	private Timer timer;
	private Handler targetHandler;
	
	private volatile long initTime; //第一次按开始的时候记录系统时间 当重置时设为0
	private volatile long nowTime;	//秒表已经走的时间，当重置时设为0

	public Stopwatch(Handler handler) {
		if(handler == null) throw new NullPointerException("Initialization Stopwatch failed. Because Handler can not Null!");
		targetHandler = handler;
	}
	
	public void startStopwatch() {
		initTime = System.currentTimeMillis() - nowTime;
		if(timer == null) timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				long curTime = System.currentTimeMillis() - initTime;
				nowTime = curTime;
				Message msg = new Message();
				msg.what = GET_CURTIME;
				targetHandler.sendMessage(msg);
			}
		}, 0, 10);
	}
	
	public void stopStopwatch() {
		timer.cancel();
		timer = null;
	}
	
	public void resetStopwatch() {
		nowTime = 0;
	}
	
	public long makeSnapshot() {
		//丢失掉最小精度，避免影响计算时间差
		return nowTime - nowTime % 10;
	}
	
	static public String formatTime(long curTime) {
		//mm:SS.ss
		int ss = (int) (curTime % 1000) / 10;
		int SS = (int) (curTime % 60000) / 1000;
		int mm = (int) (curTime % 3600000) / 60000;
		
		StringBuilder showTime = new StringBuilder(7);
		if(mm < 10) showTime.append("0");
		showTime.append(mm).append(":");
		if(SS < 10) showTime.append("0");
		showTime.append(SS).append(".");
		if(ss < 10) showTime.append("0");
		showTime.append(ss);
		return showTime.toString();
	}
	
	static public int getSecond(long curTime) {
		return (int)curTime % 60000;
	}
}

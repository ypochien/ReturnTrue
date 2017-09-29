/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.returntrue.util;

import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Propagate another {@link #Thread} as {@link #Timer} to record a second of
 * time in the regular speed.</br> Therefore,you can not use this Timer to
 * control any kind of UI component directively,</br> based on the reason of Android
 * internal policy. </br> Such as
 * "Only the original thread that created a view hierarchy can touch its views"
 * .</br> Be attention to use the callBack of {@link #PreciseTimerListener}.
 * Whatever situation you want to control UI component through
 * {@link #PreciseTimer}</br> ,and have to create this {@link #PreciseTimer}
 * through UI Thread. Such as
 * {@link #Activity.onCreate(Bundle savedInstanceState)} in Activity.</br></br>
 * 
 * 利用PreciseTimer 去控制UI，一定要藉由與要控制的UI,用同一個UI Thread 建制。避免 Exception 發生。
 * 
 * @author JosephWang
 * 
 */
public class PreciseTimer {
	/**
	 * The status of countDown type.</br>
	 * 
	 * 倒數計時的狀態
	 * 
	 * @author JosephWang
	 * 
	 */
	public static enum CountDownTimeMode {
		Begin, ReStart, OnTick, Pause, Resume, TimeOut, Stop;
		public static CountDownTimeMode getStatus(int index) {
			return CountDownTimeMode.values()[index];
		}
	}

	private long countDownSecond = 61;
	private Timer timer;
	private PreciseTimerListener listener;
	private long beginTime = 0;
	private long tempTickSecond = 0;
	private CountDownTimeMode countDownTimeMode = CountDownTimeMode.Begin;

	public CountDownTimeMode getCountDownTimeMode() {
		return countDownTimeMode;
	}

	public long getCurrentTickSecond() {
		return tempTickSecond;
	}

	public PreciseTimer(PreciseTimerListener listener, long countDownSecond) {
		beginTime = countDownSecond;
		tempTickSecond = countDownSecond;
		this.listener = listener;
		this.countDownSecond = countDownSecond;
	}

	/***** 預設60秒 *******/
	public PreciseTimer(PreciseTimerListener listener) {
		this.listener = listener;
	}

	public void start(long countDownSeconds) {
		beginTime = countDownSeconds;
		tempTickSecond = countDownSeconds;
		countDownSecond = countDownSeconds;
		startTimerInner();
	}

	public void start() {
		startTimerInner();
	}

	private void startTimerInner() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {// 倒數計時器
					public void run() {
						if (countDownSecond > 0) {
							if (countDownSecond == beginTime) {
								countDownTimeMode = CountDownTimeMode.Begin;
								listener.onStart(countDownSecond);
							} else {
								countDownTimeMode = CountDownTimeMode.OnTick;
								listener.onTick(countDownSecond);
							}
							countDownSecond--;
						} else if (countDownSecond == 0) {
							listener.timeout();
							timer.cancel();
							timer.purge();
						}
						tempTickSecond = countDownSecond;
					}
				}, 0, 1000);
	}

	/**
	 * Stop timer.</br> 停止Timer.
	 */
	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			countDownTimeMode = CountDownTimeMode.Stop;
			tempTickSecond = countDownSecond;
			listener.onStop();
		}
	}

	/**
	 * Pause timer,and record the second on tick.</br> 暫停Timer 並記錄時間.
	 */
	public void pause() {
		if (timer != null) {
			countDownTimeMode = CountDownTimeMode.Pause;
			timer.cancel();
			timer.purge();
			tempTickSecond = countDownSecond;
		}
	}

	/**
	 * Resume timer with the record time paused before.</br> 以上次暫停記錄時間,恢復Timer.
	 */
	public void resume() {
		if (timer != null) {
			countDownTimeMode = CountDownTimeMode.Resume;
			timer.cancel();
			timer.purge();
			countDownSecond = tempTickSecond;
			start();
		}
	}

	/**
	 * ReStart timer with the begin time.</br> 重啟Timer.
	 */
	public void reStart() {
		if (timer != null) {
			countDownTimeMode = CountDownTimeMode.ReStart;
			timer.cancel();
			timer.purge();
			countDownSecond = beginTime;
			start();
		}
	}

	public interface PreciseTimerListener
	{
		void onStart(long tickSecon);

		void timeout();

		void onTick(long tickSecon);

		void onStop();
	}
}
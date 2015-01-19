package com.example.strictmodeapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Looper;

public class EventManager {

	public static class Message {
		public int what;
		public Object obj;
	}

	public static interface OnEventReceiver {
		public void onEvent(Message message);
	}

	private static EventManager manager;
	private Map<Class, List<OnEventReceiver>> map = new HashMap<Class, List<OnEventReceiver>>();
	private Handler handler = new Handler(Looper.getMainLooper());

	public static EventManager getDefault() {

		if (null == manager) {
			synchronized (EventManager.class) {
				if (null == manager) {
					manager = new EventManager();
				}
			}
		}

		return manager;
	}

	private EventManager() {
	}

	public synchronized void register(Class clazz, OnEventReceiver receiver) {

		List<OnEventReceiver> receivers = map.get(clazz);

		if (null == receivers) {
			receivers = new ArrayList<EventManager.OnEventReceiver>();
			map.put(clazz, receivers);
		}

		receivers.add(receiver);
	}

	public synchronized void unRegister(Class clazz) {
		map.remove(clazz);
	}

	public synchronized void postMessage(Class clazz, final Message message) {

		List<OnEventReceiver> receivers = map.get(clazz);

		if (null != receivers) {
			
			boolean isUiThread = Looper.getMainLooper() == Looper.myLooper();
			
			for (final OnEventReceiver receiver : receivers) {

				if (!isUiThread) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							receiver.onEvent(message);
						}
					});
				} else {
					receiver.onEvent(message);
				}

			}
		}
	}

}

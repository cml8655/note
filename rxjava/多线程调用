package com.cml.rx;

import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * 多线程调用一个subject对象时，需要使用SerializedSubject进行封装，防止并发问题,SerializedSubject会将所有的值转换到一个统一的线程进行调用
 * 
 * @author cml 2015年11月16日
 *
 */
public class MultiThread {
	public static void main(String[] args) {

		final PublishSubject<Integer> subject = PublishSubject.create();
		subject.publish();

		subject.asObservable().subscribe(new Action1<Integer>() {

			@Override
			public void call(Integer t) {
				System.out.println("======asObservable.onnext===>" + t+","+Thread.currentThread().getId());
			}

		});
		subject.subscribe(new Action1<Integer>() {

			@Override
			public void call(Integer t) {
				System.out.println("======onnext===>" + t+","+Thread.currentThread().getId());
			}

		});

		final SerializedSubject<Integer, Integer> ser = new SerializedSubject<Integer, Integer>(subject);

		for (int i = 0; i < 10; i++) {
			final int value = i;
			new Thread() {
				public void run() {
					ser.onNext(value);
				};
			}.start();
		}

	}
}

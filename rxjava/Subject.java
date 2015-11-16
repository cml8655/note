package com.cml.rx;

import rx.Observable;
import rx.Observer;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subjects.AsyncSubject;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;

/**
 * subject与obserable效果相同，只是subject可以动态写入值，而obserable值是固定写死的！！！！！
 * <p>
 * 1、PublishSubject 必须要publish后才可以，subscribe会接收到后面的值
 * </p>
 * <p>
 * 2、BehaviorSubject 会接收到subscribe之前的一个值和后面的所有值
 * <p>
 * 3、AsyncSubject 只会接收到最后一个值
 * <p>
 * 4、ReplaySubject 无论何时订阅，都能收到全部的消息
 * 
 * @author cml 2015年11月16日
 *
 */
public class RxSubject {
	public static void main(String[] args) throws Exception {
		// 后面订阅的对象也会接收到全部值
		ReplaySubject<Integer> subject = ReplaySubject.create();
		subject.subscribe(new Action1<Integer>() {

			@Override
			public void call(Integer t) {
				System.out.println("===ACtion1=====>" + t);
			}
		});
		subject.onNext(1);
		subject.onNext(2);
		subject.onNext(3);
		subject.onNext(4);
		subject.subscribe(new Action1<Integer>() {

			@Override
			public void call(Integer t) {
				System.out.println("===ACtion2=====>" + t);
			}
		});

	}

	private static void PublishSubject() {
		// 要执行 先得publish，在subscribe，只有subscribe后才有效
		PublishSubject<String> subject = PublishSubject.create();

		subject.onNext("1");
		subject.subscribe(new Action1<String>() {

			@Override
			public void call(String t) {
				System.out.println("call->" + t);
			}
		});
		subject.publish();
		subject.onNext("2");
		subject.onNext("3");
		// subject.publish();// 只有调用此方法后，后面加入的值才会调用
		subject.asObservable().subscribe(new Observer<String>() {

			@Override
			public void onCompleted() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(Throwable e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNext(String t) {
				System.out.println("dddddddddd===>" + t);
			}
		});

		subject.onNext("4");
		subject.onNext("5");
		subject.onNext("6");

		System.out.println("=-------------end-------------");
	}

	private static void BehaviorSubject() {
		// BehaviorSubject 在调用subscribe方法之前。只会获取subscribe之前的一个next对象，之后的对象都会调用
		// 1->2->3->subscribe->4->5 输出：3,4,5
		final BehaviorSubject<Integer> subject = BehaviorSubject.create(-1);
		subject.onNext(1);
		subject.onNext(2);
		subject.onNext(3);
		subject.subscribe(new Observer<Integer>() {

			@Override
			public void onCompleted() {
				System.out.println("completed11");
			}

			@Override
			public void onError(Throwable e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNext(Integer t) {
				System.out.println(t);
			}
		});
		subject.onNext(111);
		subject.onNext(222);
		subject.onNext(333);
		subject.onNext(4444);// 调用completed后，不再接收任何消息

		new Thread() {
			public void run() {
				try {
					Thread.sleep(3000);
					subject.onNext(999);
					subject.onCompleted();
				} catch (InterruptedException e) {
					subject.onError(e);
				}

			};
		}.start();

		System.out.println("---completed---" + subject.hasCompleted());
	}

	private static void AsyncSubject() throws InterruptedException {
		System.out.println("---------------------");
		// 只会执行最后一个任务的subject
		AsyncSubject<Integer> ayAsyncSubject = AsyncSubject.create();

		ayAsyncSubject.subscribe(new Observer<Integer>() {

			@Override
			public void onCompleted() {
				System.out.println("-doOnCompleted===" + Thread.currentThread().getId());
			}

			@Override
			public void onError(Throwable e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNext(Integer t) {
				System.out.println("---" + t + "===》threadID:" + Thread.currentThread().getId());
			}
		});

		ayAsyncSubject.onNext(1);
		ayAsyncSubject.onNext(2);
		// 只有调用此方法后，才会执行订阅的方法
		ayAsyncSubject.onCompleted();
		Thread.sleep(5000);
	}
}

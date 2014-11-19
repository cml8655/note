package cn.com.lawson.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import cn.com.lawson.R;
import cn.com.lawson.accounts.AccountUtil;
import cn.com.lawson.service.ServiceManager;
import cn.com.lawson.utils.PrefUtil;

/**
 * 欢迎界面
 * 
 * 2014-9-29
 */
public class WelcomeActivity extends FragmentActivity {

	private List<View> welcomeImgs;
	private ViewPager welcomeContainer;
	private String version;
	private static final int SCHEDULE = 2000;

	// 初始化时默认为需要自动翻页，用户点击后则由用户控制
	private boolean shouldAuto = true;

	private Handler flipHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (!shouldAuto) {
				return;
			}
			int position = msg.arg1;
			if (isCurrentPage(position)) {
				flipPage(position + 1);
			}
		}
	};

	private boolean isCurrentPage(int position) {
		return welcomeContainer.getCurrentItem() == position;
	}

	/**
	 * 翻到下一页中
	 * 
	 * @param position
	 */
	private void flipPage(int position) {

		if (position < welcomeImgs.size()) {
			welcomeContainer.setCurrentItem(position);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);

		version = getIntent().getStringExtra("version");

		welcomeImgs = new ArrayList<View>();

		// welcomeImgs.add(initView());
		welcomeImgs.add(initFirstView());
		welcomeImgs.add(initSecondView());

		welcomeContainer = (ViewPager) findViewById(R.id.welcomeContainer);

		welcomeContainer.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				sendFlipMsg(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		welcomeContainer.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				shouldAuto = false;
				return false;
			}
		});

		welcomeContainer.setAdapter(new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView((View) object);
			}

			@Override
			public int getCount() {
				return welcomeImgs.size();
			}

		

		});

		sendFlipMsg(0);
	};

	private void sendFlipMsg(int position) {
		Message msg = flipHandler.obtainMessage();
		msg.arg1 = position;
		msg.what = 1;
		flipHandler.sendMessageDelayed(msg, SCHEDULE);
	}

	private View initView() {
		return LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.activity_loading, null);
	}

	private View initFirstView() {
		return LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.welcome_item, null);
	}

	private View initSecondView() {
		View view = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.welcome_item2, null);

		Button startNow = (Button) view.findViewById(R.id.startNow);
		startNow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 设置页面已经显示过 了
				PrefUtil.setShowWelcomePage(getApplicationContext(), version);

				Intent intent = new Intent();

				if (StringUtils.isBlank(AccountUtil
						.getUserToken(getApplicationContext()))) {
					intent.setClassName("cn.com.lawson",
							"cn.com.lawson.activity.LoginActivity");
				} else {
					intent.setClassName("cn.com.lawson",
							"cn.com.lawson.activity.SlidingMainActivity");
					ServiceManager
							.startLocationMonitorService(WelcomeActivity.this);
				}
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_from_right,
						R.anim.slide_out_to_left);

				WelcomeActivity.this.finish();
			}
		});

		return view;
	}

}

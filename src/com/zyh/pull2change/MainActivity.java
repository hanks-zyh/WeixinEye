package com.zyh.pull2change;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bindViews();

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenHeight = metrics.heightPixels;

		mDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ValueAnimator va = ValueAnimator.ofFloat(0, time);
				va.setDuration((long) time);
				va.start();
				final float sc = mRl2.getScaleX();
				final float oldY = mEye.getY();
				va.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator arg0) {
						float value = (float) arg0.getAnimatedValue() / time;
						Log.i("", "Value:" + value);
						mRl2.setY((1 - value) * screenHeight);
						mDel.setAlpha(1 - value);
						mTv.setAlpha(value);
						mAdd.setAlpha(value);
						mSearch.setAlpha(value);
						mEye.setScaleX(sc * (1 - value) + 1);
						mEye.setScaleY(sc * (1 - value) + 1);
						mEye.setY(oldY * (1 - value));
						if (value >= 1) {
							isMoving = false;
							mDel.setEnabled(false);
						}
					}
				});
			}
		});
		mRl2.setOnTouchListener(new MyTouchEvent());
	}

	private boolean isMoving = false;

	class MyTouchEvent implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (isMoving)
				return true;
			final int[] location = new int[2];
			mEye.getLocationOnScreen(location);
			int bound = (int) (location[1] + mEye.getHeight());

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downY = (int) event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				int dy = (int) event.getRawY() - downY;
				if (dy < 0)
					break;
				Log.i("ACTION_MOVE", "move>>>>>>>>>>" + dy);
				ViewHelper.setTranslationY(mRl2, dy * factory);
				mEye.setImageBitmap(getCutEye(dy));
				break;
			case MotionEvent.ACTION_UP:
				dy = (int) event.getRawY() - downY;
				Log.i("ACTION_MOVE", "bound" + bound + ",up>>>>>>>>>>" + event.getRawY() + "  "
						+ screenHeight);
				if (dy > bound) {
					showVedioView(event);
				} else {
					showChatView(event);
				}
				break;
			}
			return true;
		}
	}

	private float factory = 0.6f;
	private int screenHeight;
	private int downY;
	private RelativeLayout mRl2;
	private ImageView mEye;
	private float time = 500;
	private TextView mTv;
	private Button mDel;
	private Button mSearch;
	private Button mAdd;

	private void bindViews() {
		mRl2 = (RelativeLayout) findViewById(R.id.rl2);
		mEye = (ImageView) findViewById(R.id.iv_eye);
		mDel = (Button) findViewById(R.id.bt_del);
		mSearch = (Button) findViewById(R.id.bt_search);
		mAdd = (Button) findViewById(R.id.bt_add);
		mTv = (TextView) findViewById(R.id.tv);
	}

	public Bitmap getCutEye(int dy) {
		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.a_a);
		Bitmap bitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Config.RGB_565);
		if (dy > mEye.getHeight()*1.5f) {
			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE); // »æÖÆ¿ÕÐÄÔ²
			canvas.drawBitmap(bm, 0, 0, paint);

			Log.i("", "bmH:" + bm.getHeight() + ",dy:" + dy);
			paint.setStrokeWidth(bm.getHeight()*1.5f - dy * 0.5f);
			RectF oval = new RectF(0, 0, bm.getWidth(), bm.getHeight());
			canvas.drawArc(oval, 0, 360, false, paint);
		}
		return bitmap;
	}

	public void showChatView(MotionEvent event) {
		isMoving = true;
		final int oldY = (int) mRl2.getY();
		ValueAnimator va = ValueAnimator.ofFloat(0, time);
		va.setDuration((long) time);
		va.start();
		va.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				float value = (float) arg0.getAnimatedValue() / time;
				Log.i("", "Value:" + value);
				mRl2.setY(oldY * (1 - value));
				if (value >= 1)
					isMoving = false;
			}
		});
	}

	public void showVedioView(MotionEvent event) {
		isMoving = true;

		final int[] location = new int[2];
		mRl2.getLocationOnScreen(location);

		final int oldY = (int) mRl2.getY();
		ValueAnimator va = ValueAnimator.ofFloat(0, time);
		va.setDuration((long) time);
		va.start();
		va.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				float value = (float) arg0.getAnimatedValue() / time;
				Log.i("", "Value:" + value);
				mRl2.setY(oldY + value * (screenHeight - location[1]));
				mEye.setScaleX(value + 1);
				mEye.setScaleY(value + 1);
				mEye.setY(value * 200);
				mDel.setAlpha(value);
				mTv.setAlpha(1 - value);
				mAdd.setAlpha(1 - value);
				mSearch.setAlpha(1 - value);
				if (value >= 1) {
					mDel.setEnabled(true);
				}
			}
		});
	}

}

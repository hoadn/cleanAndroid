package com.cssweb.android.main;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cssweb.android.base.BaseActivity;
import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.service.AutoLoadAllStock;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.user.track.ServiceControl;
import com.cssweb.android.util.ActivityUtil;
import com.cssweb.android.util.CssSystem;

public class MainActivity extends BaseActivity implements SensorEventListener {
	private Context mContext = MainActivity.this;

	private Animation anim = null,animArrow = null;
    private SensorManager mSensorManager;
    private int rotate = 0, screenWeight, screenHeight;
    private float eDegress = 0f;
    private Button btn1=null,btn2=null,btn3=null,btn4=null;
    
    private ImageView middle = null;
    private boolean touchMiddleFlag = false;
    private boolean touchFlag = false;
    
    private double accelerationX = 0;
    private double accelerationY = 0;
	
    private boolean firstTouchFlag = true;
//    private PowerManager mPowerManager; //电源控制，比如防锁屏
//    private WakeLock mWakeLock;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        setContentView(R.layout.main);
        getScreenXY();
        initTitle(0, 0, "", false);
        
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        LinearInterpolator lir = new LinearInterpolator();  
        animation.setInterpolator(lir);
        ImageView lightView = (ImageView)findViewById(R.id.njzq_light);
        lightView.startAnimation(animation);
        
        middle = (ImageView)findViewById(R.id.njzq_yykh);

        btn1 = (Button) findViewById(R.id.zr_lluser);
        btn1.setTag(Global.NJZQ_JLP_LLYH);
//        btn1.setOnClickListener(toolbarClick);
        btn2 = (Button) findViewById(R.id.zr_tyuser);
        btn2.setTag(Global.NJZQ_JLP_TYYH);
        btn2.setOnClickListener(toolbarClick);
        btn3 = (Button) findViewById(R.id.zr_jyuser);
        btn3.setTag(Global.NJZQ_JLP_JYYH);
        btn3.setOnClickListener(toolbarClick);
        btn4 = (Button) findViewById(R.id.zr_system);
        btn4.setTag(Global.NJZQ_JLP_SZ);
        btn4.setOnClickListener(toolbarClick);
//        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
//        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass()
//                .getName()); //处理屏幕防止锁屏
        
		initPopupWindow();
    }
    
    private void initBottomBtn() {
        int loginType = TradeUser.getInstance().getLoginType();
        if(loginType==1||loginType==2) {//服务密码登录或者交易密码登录
        	btn1.setBackgroundResource(R.drawable.njzq_llyh);
        	btn2.setBackgroundResource(R.drawable.njzq_tiyh);
        	btn3.setBackgroundResource(R.drawable.njzq_jyyh_select);
        	btn4.setBackgroundResource(R.drawable.njzq_sz);
        	initTitle(0, 0, "", true);
        	middle.setImageResource(R.drawable.njzq_jlp_wdzq);
        }
        else if(loginType==3) {//体验卡登录
        	btn1.setBackgroundResource(R.drawable.njzq_llyh);
        	btn2.setBackgroundResource(R.drawable.njzq_tiyh_select);
        	btn3.setBackgroundResource(R.drawable.njzq_jyyh);
        	btn4.setBackgroundResource(R.drawable.njzq_sz);
        	initTitle(0, 0, "", true);
        	middle.setImageResource(R.drawable.njzq_jlp_wdzq);
        }
        else {//浏览用户
        	btn1.setBackgroundResource(R.drawable.njzq_llyh_select);
        	btn2.setBackgroundResource(R.drawable.njzq_tiyh);
        	btn3.setBackgroundResource(R.drawable.njzq_jyyh);
        	btn4.setBackgroundResource(R.drawable.njzq_sz);
        	initTitle(0, 0, "", false);
        	middle.setImageResource(R.drawable.njzq_jlp_yykh);
        }
    }

	protected void toolBarClick(int tag, View v) {
		FairyUI.switchToWnd(tag, MainActivity.this);
//		FairyUI.switchToWndWithSingle(tag, MainActivity.this, true);
	}
	
	

	protected void initTitle(int resid1, int resid2, String str, boolean flag) {
		super.initTitle(resid1, resid2, str);
		midText.setBackgroundResource(R.drawable.njzq_title_content);
		LinearLayout l = (LinearLayout) findViewById(R.id.zr_topleft);
		if (!flag)
			l.setVisibility(View.INVISIBLE);
		else
			l.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(firstTouchFlag){
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;

			case MotionEvent.ACTION_MOVE:
				break;

			case MotionEvent.ACTION_UP:
				touch(event);
				break;

			case MotionEvent.ACTION_CANCEL:
				break;
			}
		}
		return super.onTouchEvent(event);
	}
	
	private void touch(MotionEvent event){
		
		float x = event.getX();
		float y = event.getY();

		int startY = (screenHeight - 50) / 2;
		double j = Math.atan2(y - startY, x - screenWeight / 2);

		double r = Math.sqrt((y - startY) * (y - startY)
				+ (x - screenWeight / 2) * (x - screenWeight / 2));
		// 240-100
		if (screenHeight == 480) {
			if (r < 160 && r > 40) {
				this.touchFlag = true;
				getAngle(j);
				touchMiddleFlag = false;
				firstTouchFlag = false;
			} else if (r < 40) {
				touchMiddleFlag = true;
				firstTouchFlag = false;
			}
		} else {
			if (r < 240 && r > 100) {
				this.touchFlag = true;
				getAngle(j);
				touchMiddleFlag = false;
				firstTouchFlag = false;
			} else if (r < 100) {
				touchMiddleFlag = true;
				firstTouchFlag = false;
			}
		}
		
		if (touchMiddleFlag) {
			int loginType = TradeUser.getInstance().getLoginType();
			if (loginType == 1 || loginType == 2) {// 服务密码登录或者交易密码登录
				FairyUI.switchToWnd(Global.NJZQ_JLP_WDZQTAG, MainActivity.this);
			} else if (loginType == 3) {// 体验卡登录
				FairyUI.switchToWnd(Global.NJZQ_JLP_TYKTAG, MainActivity.this);
			} else {// 浏览用户
				FairyUI.switchToWnd(Global.NJZQ_JLP_YYKHTAG, MainActivity.this);
			}
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		// mWakeLock.release();
		System.gc();
	}

	@Override
	protected void onResume() {
		super.onResume();
		changeBG();
		initBottomBtn();
		firstTouchFlag = true;
		touchMiddleFlag = false;
		this.touchFlag = false;
		mSensorManager.registerListener(this, mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mSensorManager
				.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_UI);
		// mWakeLock.acquire(); //恢复时解除锁屏
	}

	@Override
	protected void onStop() {
		super.onStop();
		mSensorManager.unregisterListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 或者下面这种方式
		// android.os.Process.killProcess(android.os.Process.myPid());
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下的如果是BACK，同时没有重复
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			onExit();
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onExit() {
		String fundid = TradeUser.getInstance().getFundid();
		if(fundid == null || fundid.equals("")) 
			showDialog(2);
		else 
			showDialog(1);
	}

	private void getAngle(double j) {
		if (j > -2.74 && j <= -1.96) {
			eDegress = 45;
			rotate = 7;
		} else if (j > -1.96 && j <= -1.18) {
			eDegress = 0;
			rotate = 0;
		} else if (j > -1.18 && j <= -0.39) {
			eDegress = 315;
			rotate = 1;
		} else if (j > -0.39 && j <= 0.38) {
			eDegress = 270;
			rotate = 2;
		} else if (j > 0.38 && j <= 1.18) {
			eDegress = 225;
			rotate = 3;
		} else if (j > 1.18 && j <= 1.96) {
			eDegress = 180;
			rotate = 4;
		} else if (j > 1.96 && j <= 2.74) {
			eDegress = 135;
			rotate = 5;
		} else if (j > 2.74 || j <= -2.74) {
			eDegress = 90;
			rotate = 6;
		}

		mSensorManager.unregisterListener(this);

		aniRotateAuto(-eDegress);
		aniRotateImage(-eDegress);
	}

	private void getScreenXY() {
		DisplayMetrics localDisplayMetrics2 = MainActivity.this
				.getApplicationContext().getResources().getDisplayMetrics();
		screenWeight = localDisplayMetrics2.widthPixels;
		screenHeight = localDisplayMetrics2.heightPixels;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public void onSensorChanged(SensorEvent event) {
		float x, y, j = 0;
		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				x = event.values[SensorManager.DATA_X];
				y = event.values[SensorManager.DATA_Y];
				if (touchFlag) {
					return;
				}
				
				double kFilteringFactor = 0.09f;
				accelerationX = x * kFilteringFactor + accelerationX * (1.0 - kFilteringFactor);
				accelerationY = y * kFilteringFactor + accelerationY * (1.0 - kFilteringFactor);
				
				float degree = (float) (180*Math.atan2(-accelerationY , accelerationX)/Math.PI) + 90;
//				 Log.i("tag", "x : " + accelerationX +" y : " +accelerationY +" : mb_degree : "
//						 +mb_degree);
				
//				Log.i("tag", "x  :   "+x +"  y  :   "+y + "z  :   "+z);
				
				
				if(degree > -22.5 && degree <= 22.5){
					j = 0;
				}else if(degree > 22.5 && degree <= 67.5){
					j = 315;
				}else if(degree > 67.5 && degree <= 112.5){
					j = 270;
				}else if(degree > 112.5 && degree <= 157.5){
					j = 225;
				}else if(degree > 157.5 && degree <= 202.5){
					j = 180;
				}else if(degree > 202.5 && degree <= 247.5){
					j = 135;
				}else if(degree > -67.5 && degree <= -22.5){
					j = 45;
				}else if(degree > 247.5 || degree < -67.5){
					j = 90;
				}
				aniRotateImage(-j);
				aniRotateAuto(degree );
				
			}
		}
	}

	private float DegressQuondam = 0.0f, SegressQuondam = 0.0f;

	private void aniRotateImage(float fDegress) {
		roate(fDegress, findViewById(R.id.njzq_select), anim, DegressQuondam,
				true);
		DegressQuondam = fDegress;
	}

	private void aniRotateAuto(float fDegress) {
		roate(fDegress, findViewById(R.id.njzq_arrow), animArrow,
				SegressQuondam, false);
		SegressQuondam = fDegress;
	}

	private void roate(float fDegress, View view, Animation animaton,
			float quondam, final boolean fromflag) {

		if (quondam == fDegress) {
			if (touchFlag) {
				touchFlag = false;
				FairyUI.switchToWnd(rotate, mContext);
			}
			return;
		}
		if (quondam >= -90 && fDegress <= -225) {
			animaton = new RotateAnimation(quondam, fDegress + 360,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
		} else if (quondam <= -225 && fDegress <= 0 && fDegress >= -90) {
			animaton = new RotateAnimation(quondam, fDegress - 360,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
		} else
			animaton = new RotateAnimation(quondam, fDegress,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);

		animaton.setInterpolator(new DecelerateInterpolator());
		if (fromflag) {
			animaton.setDuration(1100);
		} else {
			animaton.setDuration(400);
		}

		animaton.setFillAfter(true);
		animaton.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				if (touchFlag && fromflag) {
					touchFlag = false;
					FairyUI.switchToWnd(rotate, mContext);
				}
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});

		view.startAnimation(animaton);
	}
	
    public void isExitSystem() {
    	stopService(new Intent(this, AutoLoadAllStock.class));
    	int version = CssSystem.getSDKVersionNumber();
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);   
    	if(version<8) {//2.2以下版本
    		
    		(new  ServiceControl(this)).stopTrackService();
    		manager.restartPackage(getPackageName());
    	}
    	else {
    		(new  ServiceControl(this)).stopTrackService();
    		//manager.killBackgroundProcesses(getPackageName());
    		System.exit(0);
    	}
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
	    	case 1:
	    		return  new AlertDialog.Builder(mContext).setTitle(R.string.alert_dialog_about)
	    				// 设置标题
	    						.setMessage(R.string.acc_login_out)
	    						// 设置内容
	    						.setPositiveButton(R.string.alert_dialog_ok,// 设置确定按钮
	    								new DialogInterface.OnClickListener() {
	    									public void onClick(DialogInterface dialog,
	    											int whichButton) {
    											ActivityUtil.clearTradeRecord();
    											changeBG();
    											initBottomBtn();
    											ServiceControl.destoryLogin();
	    									}
	    								}).setNeutralButton(R.string.alert_dialog_cancel,
	    								new DialogInterface.OnClickListener() {
	    									public void onClick(DialogInterface dialog,
	    											int whichButton) {
	    										// 点击"取消"按钮之后暂不处理
	    									}
	    								}).create();// 创建按钮
	    	case 2:
	    		return  new AlertDialog.Builder(mContext).setTitle(
	    				R.string.alert_dialog_about)
	    				// 设置标题
	    						.setMessage(R.string.sure_login_out)
	    						// 设置内容
	    						.setPositiveButton(R.string.alert_dialog_ok,// 设置确定按钮
	    								new DialogInterface.OnClickListener() {
	    									public void onClick(DialogInterface dialog,
	    											int whichButton) {
	    										SharedPreferences sharedPreferences = getSharedPreferences(
    													"mobile", Context.MODE_PRIVATE);
    											Editor editor = sharedPreferences.edit();// 获取编辑器
    											editor.putBoolean("exitFlag", true);
    											editor.commit();// 提交修改
    											
    											// 点击“确定”转向登陆框
    											isExitSystem();
	    									}
	    								}).setNeutralButton(R.string.alert_dialog_cancel,
	    								new DialogInterface.OnClickListener() {
	    									public void onClick(DialogInterface dialog,
	    											int whichButton) {
	    										// 点击"取消"按钮之后暂不处理
	    									}
	    								}).create();// 创建按钮
    	}
    	return null;
    }
}

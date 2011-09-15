package com.cssweb.android.video;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.HandlerThread;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;

import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.main.R;
/**
 * 视频播放类
 * @author hoho
 *
 */
public class VideoPlayer extends CssBaseActivity {
	private android.widget.VideoView videoView;
	private String url = null;
	private Dialog dialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        setContentView(R.layout.zr_mediaplayer2);
        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("url");
        
        HandlerThread mHandlerThread  = new HandlerThread("CSSWEB_THREAD");
		mHandlerThread.start();
		mHandler = new MessageHandler(mHandlerThread.getLooper());
        
        //创建进度条
        dialog=ProgressDialog.show(this, "进度提示", "视频正在加载中...");
        videoView = (android.widget.VideoView) findViewById(R.id.videoView);
        init(1);
       //videoView.setVideoPath("/sdcard/media/test1.mp4");
       //videoView.setVideoURI(Uri.parse("http://wangjun.easymorse.com/wp-content/video/mp4/tuzi.mp4"));
       
        
       /*videoView.setVideoURI(Uri.parse(url));
       MediaController mediaController = new MediaController(this);
       videoView.setMediaController(mediaController);
       videoView.setOnPreparedListener(new OnPreparedListener() {
    	   //开始播放
			public void onPrepared(MediaPlayer mp) {
				//videoView.setBackgroundColor(Color.argb(0, 0, 255, 0));
				dialog.dismiss();
			}
		});
     //播放完毕
       videoView.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				dialog.dismiss();
				Toast.makeText(VideoPlayer.this, "播放完毕，谢谢观看!", Toast.LENGTH_LONG).show();
			}
		});
       videoView.start();*/
    }
    
    
   @Override
	protected void init(int type) {
		mHandler.removeCallbacks(r);
		r = new Runnable(){
			public void run() {
				mHandler.sendEmptyMessage(0);
			}
		};
		mHandler.post(r);
	
	}
    
    
   @Override
	protected void handlerData() {
		Runnable r = new Runnable(){
			public void run() {
				try{
					   //videoView.setVideoURI(Uri.parse("http://wangjun.easymorse.com/wp-content/video/mp4/tuzi.mp4"));
					   //videoView.setVideoPath("/sdcard/media/tuzi.mp4");
					   videoView.setVideoURI(Uri.parse(url));
				       MediaController mediaController = new MediaController(VideoPlayer.this);
				       videoView.setMediaController(mediaController);
				       videoView.setOnPreparedListener(new OnPreparedListener() {
				    	   //开始播放
							public void onPrepared(MediaPlayer mp) {
								//videoView.setBackgroundColor(Color.argb(0, 0, 255, 0));
								dialog.dismiss();
							}
						});
				     //播放完毕
				       videoView.setOnCompletionListener(new OnCompletionListener() {
							public void onCompletion(MediaPlayer mp) {
								dialog.dismiss();
								Toast.makeText(VideoPlayer.this, "播放完毕，谢谢观看!", Toast.LENGTH_LONG).show();
							}
						});
				       videoView.start();
				}catch(Exception e){
					e.printStackTrace();
				}finally {
					//进度条消失
					//dialog.dismiss();
				}
			}
		};
		runOnUiThread(r);
	}
   
   
	@Override
	protected void onPause() {
		//防止泄露窗口（activity 关闭,可能dialog没有关闭）
		if(dialog!=null)
			dialog.dismiss();
		super.onPause();
	}
    
    
}
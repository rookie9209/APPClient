package com.mlegeb.remote.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.mlegeb.remote.common.Constants;
import com.mlegeb.remote.common.LogUtil;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

/**
 * 名称: SocketService.java
 * 描述: Service监听Socket是否有Pc传来的信息
 *
 * @author a_xiang
 * @version v1.0
 * @created 2015年2月4日
 */
public class SocketService extends Service {
	

	/** UDP传输协议 */
	private DatagramSocket inSocket;
	
	/** 跨线程信息传递 */
	private MyHandler myHandler;

	/** 初始化变量 */
	@Override
	public void onCreate() {
		super.onCreate();
		myHandler = new MyHandler();
		try {
			this.inSocket = new DatagramSocket(Constants.LISTENER_PORT);
		} catch (SocketException e) {
			Toast.makeText(this, "无法创建Socket",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	/** 开启监听线程 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new SocketListener()).start();
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	
	/**
	 * 名称: SocketService.java
	 * 描述: 线程Run函数实现
	 *
	 * @author a_xiang
	 * @version v1.0
	 * @created 2015年2月4日
	 */
	class SocketListener implements Runnable{

		@Override
		public void run() {
			Message message = Message.obtain();
			try{
				byte[] buf = new byte[1024];
				DatagramPacket op = new DatagramPacket(buf, buf.length);
				//设置超时时间为2秒
				SocketService.this.inSocket.setSoTimeout(2000);
				SocketService.this.inSocket.receive(op);
				String receiveStr = new String(buf).trim();
				LogUtil.d(getClass(),  "反馈数据：" + receiveStr);
				//判断接收到的信息是否连接正确
				if(receiveStr.equals("Successful")){
					LogUtil.d(getClass(),  "Android客户端连接成功");
					//连接成功发送1通知handler
					message.what = 1;
					myHandler.sendMessage(message);
					return;
				}
				
			}catch(Exception e){
				//连接成功发送0通知handler
				message.what = 0;
				myHandler.sendMessage(message);
				LogUtil.d(getClass(),  "IP地址请求超时，连接失败！");
			}
		}
	}
	
	/**
	 * 名称: SocketService.java
	 * 描述: handler实现处理传过来的信息
	 *
	 * @author a_xiang
	 * @version v1.0
	 * @created 2015年2月4日
	 */
	@SuppressLint("HandlerLeak")
	class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			//根据接收到信息通过广播通知主线程UI
			Intent intent = new Intent(Constants.RETURN_ACTION);
			if(msg.what == 1){
				intent.putExtra("result", true);
			}
			else {
				intent.putExtra("result", false);
			}
			sendBroadcast(intent);
		}
	}

	/**
	 * service销毁炒作
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.d(getClass(),  "Destroy");
		this.inSocket.close();
	}
}

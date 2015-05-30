package com.example.sharingbook;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.sharingbook.Chat.MyThread;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class MessageListenner extends Service {
	boolean allowRun = false;

	@Override
	public void onCreate() {
		allowRun = true;
		MyThread thread = new MyThread();
		thread.start();
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	void remind(String ustuid, String uname, String message) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		// 定义通知栏展现的内容信息
		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = uname + "给您发来消息";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		// 定义下拉通知栏时要展现的内容信息
		Context context = getApplicationContext();
		CharSequence contentTitle = tickerText;
		CharSequence contentText = message;
		
		
		Intent notificationIntent = new Intent(this, Chat.class);
		notificationIntent.putExtra("ustuid", ustuid);
		notificationIntent.putExtra("uname", uname);
		
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
		mNotificationManager.notify(1, notification);
	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				try {
					JSONObject json = new JSONObject((String) msg.obj);
					JSONArray arr = json.getJSONArray("dataList");

					if (arr.length() == 2) {
						JSONObject obj = arr.getJSONObject(1);
						remind(obj.getString("ustuid"), obj.getString("uname"), obj.getString("message"));						
					}
				} catch (Exception e) {
				
				}

			}
		}
	};

	class MyThread extends Thread {
		public void run() {
			while (allowRun) {
				String webServer = getResources().getString(R.string.webServer);
				String umd5 = tool.getString(MessageListenner.this, "umd5");
				String ustuid = tool.getString(MessageListenner.this, "ustuid");

				String httpUrl = webServer + "/messageListenner.php?umd5="
						+ umd5 + "&ustuid=" + ustuid;
				try {
					URL url = new URL(httpUrl);
					HttpURLConnection urlConn = (HttpURLConnection) url
							.openConnection();
					urlConn.connect();
					InputStream input = urlConn.getInputStream();
					InputStreamReader inputreader = new InputStreamReader(input);
					BufferedReader reader = new BufferedReader(inputreader);

					String inputLine = "", resultData = "";
					while ((inputLine = reader.readLine()) != null) {
						resultData += inputLine + "\n";
					}

					if (resultData.length() > 0) {
						Message msg = new Message();
						msg.what = 1;
						msg.obj = resultData;

						handler.sendMessage(msg);
					}
					reader.close();
					inputreader.close();
					input.close();
					reader = null;
					inputreader = null;
					input = null;
				} catch (Exception e) {

				}

				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
}

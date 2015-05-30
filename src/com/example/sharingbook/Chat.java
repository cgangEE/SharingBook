package com.example.sharingbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Chat extends Activity {
	String ustuidS = null;
	String unameS = null;
	String messageS = null;

	String ustuidMe = null;
	String unameMe = null;

	Bitmap picS = null;
	Bitmap picMe = null;

	ListView listview = null;
	ListViewAdapter ad = null;

	Button button = null;
	EditText edittext = null;
	JSONArray jsonArray = null;
	ProgressBar progressbar = null;
	RequestQueue mQueue = null;
	RelativeLayout layout = null;
	MyThread thread = null;
	boolean mQueueEmpty = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		Intent intent = getIntent();
		ustuidS = intent.getStringExtra("ustuid");
		unameS = intent.getStringExtra("uname");
		messageS = intent.getStringExtra("message");

		ustuidMe = tool.getString(this, "ustuid");
		unameMe = tool.getString(this, "uname");

		listview = (ListView) findViewById(R.id.cListview);
		button = (Button) findViewById(R.id.cButton);
		edittext = (EditText) findViewById(R.id.cEditText);
		setTitle(unameS);

		mQueue = Volley.newRequestQueue(this);
		progressbar = (ProgressBar) findViewById(R.id.cSpinner);
		layout = (RelativeLayout) findViewById(R.id.clayout);

		layout.setVisibility(View.GONE);
		progressbar.setVisibility(View.VISIBLE);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		showUpic(ustuidMe);
		showUpic(ustuidS);
		
	}

	void buyMessageSend() {
		if (messageS != null && messageS.length() != 0) {
			edittext.setText(messageS);
			send(null);
		}
	}

	public void send(View view) {
		String msg = edittext.getText().toString();
		if (msg.length() == 0) {
			show(getResources().getString(R.string.messageEmpty));
			return;
		}
		try {
			edittext.setText("");
			String msgUTF8 = URLEncoder.encode(msg, "utf-8");
			JSONObject message = new JSONObject();
			message.put("msender", ustuidMe);
			message.put("mreceiver", ustuidS);
			message.put("message", msg);
			jsonArray.put(message);
			ad.notifyDataSetChanged();
			listview.setSelection(listview.getCount() - 1);

			String webServer = getResources().getString(R.string.webServer);
			String umd5 = tool.getString(this, "umd5");

			StringRequest stringRequest = new StringRequest(webServer
					+ "/sendMessage.php?msender=" + ustuidMe + "&umd5=" + umd5
					+ "&mreceiver=" + ustuidS + "&message=" + msgUTF8,
					new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							char c = response.charAt(0);
							// show(String.valueOf(c));
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {

						}
					});
			mQueue.add(stringRequest);

		} catch (Exception e) {

		}

	}

	void loadData() {

		String webServer = getResources().getString(R.string.webServer);
		String umd5 = tool.getString(this, "umd5");

		JsonObjectRequest jsonReq = new JsonObjectRequest(webServer
				+ "/message.php?umd5=" + umd5 + "&ustuid=" + ustuidMe + "&B="
				+ ustuidS, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray arr = response.getJSONArray("dataList");

					jsonArray = new JSONArray();

					jsonArray.put(arr.getJSONObject(0));

					for (int i = arr.length() - 1; i >= 1; --i)
						jsonArray.put(arr.getJSONObject(i));

					setListView();

				} catch (Exception e) {
					if (tool.DEBUG)
						show(e.toString());
				}
			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}

		});
		mQueue.add(jsonReq);

	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				try {
					JSONObject json = new JSONObject((String) msg.obj);
					JSONArray arr = json.getJSONArray("dataList");

					if (arr.length() == 2) {
						jsonArray.put(arr.getJSONObject(1));
						ad.notifyDataSetChanged();
						listview.setSelection(listview.getCount() - 1);
					}
				} catch (Exception e) {
					if (tool.DEBUG)
						show(e.toString());
				}

			}
		}
	};

	boolean allowRun = true;

	class MyThread extends Thread {
		public void run() {
			while (allowRun) {
				String webServer = getResources().getString(R.string.webServer);
				String umd5 = tool.getString(Chat.this, "umd5");

				String httpUrl = webServer + "/messageCheck.php?umd5=" + umd5
						+ "&msender=" + ustuidS + "&mreceiver=" + ustuidMe;
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
					if (tool.DEBUG)
						show(e.toString());
				}

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

	@Override
	public void finish() {
		allowRun = false;

		super.finish();
	}

	void setListView() {

		ad = new ListViewAdapter(this);
		listview.setAdapter(ad);
		listview.setSelection(listview.getCount() - 1);
		crossfade();
		thread = new MyThread();
		thread.start();

		buyMessageSend();
	}

	class ListViewAdapter extends BaseAdapter {
		Context context;
		LayoutInflater mLayoutInflater;

		public ListViewAdapter(Context context) {
			this.context = context;
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return jsonArray.length() - 1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;

			try {
				JSONObject message = jsonArray.getJSONObject(position + 1);

				if (message.getString("msender").compareTo(ustuidMe) == 0)
					view = mLayoutInflater.inflate(R.layout.chat_item_right,
							null);
				else
					view = mLayoutInflater.inflate(R.layout.chat_item, null);

				TextView cmesssge = (TextView) view.findViewById(R.id.cmessage);
				ImageView cpic = (ImageView) view.findViewById(R.id.cpic);

				if (message.getString("msender").compareTo(ustuidMe) == 0) {
					cpic.setImageBitmap(picMe);
				} else {
					cpic.setImageBitmap(picS);
				}
				cmesssge.setText(message.getString("message"));
			} catch (Exception e) {
			}
			return view;
		}
	}

	public void showUpic(final String ustuid) {

		if (mQueue == null)
			mQueue = Volley.newRequestQueue(this);

		final File file = new File(this.getFilesDir(), ustuid + "upic");
		try {
			if (file.exists()) {
				FileInputStream in = new FileInputStream(file);
				if (ustuid == ustuidMe)
					picMe = BitmapFactory.decodeStream(in);

				else {
					picS = BitmapFactory.decodeStream(in);
					loadData();
				}
				return;
			}
		} catch (Exception e) {
		}

		String webServer = getResources().getString(R.string.webServer);

		JsonObjectRequest jsonReq = new JsonObjectRequest(webServer
				+ "/userinfo.php?&ustuid=" + ustuid, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {

							String upic = response.getString("upic");

							ImageRequest imgReq = new ImageRequest(
									upic,
									new Response.Listener<Bitmap>() {
										@Override
										public void onResponse(Bitmap response) {

											if (ustuid == ustuidMe)
												picMe = response;
											else {
												picS = response;
												loadData();
											}

											try {
												file.createNewFile();
												FileOutputStream out = new FileOutputStream(
														file);
												out.write(tool
														.bitmap2Bytes(response));
												out.flush();
												out.close();
											} catch (IOException e) {
												new AlertDialog.Builder(
														Chat.this)
														.setMessage(
																e.toString())
														.setPositiveButton(
																R.string.confirm,
																null).show();
											}

										}
									}, 0, 0, Config.RGB_565,
									new Response.ErrorListener() {
										@Override
										public void onErrorResponse(
												VolleyError error) {
										}
									});
							mQueue.add(imgReq);

						} catch (Exception e) {

						}
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}

				});

		mQueue.add(jsonReq);
	}

	private void crossfade() {
		int mShortAnimationDuration = getResources().getInteger(
				android.R.integer.config_shortAnimTime);
		layout.setAlpha(0f);
		layout.setVisibility(View.VISIBLE);

		layout.animate().alpha(1f).setDuration(mShortAnimationDuration)
				.setListener(null);

		progressbar.animate().alpha(0f).setDuration(mShortAnimationDuration)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						progressbar.setVisibility(View.GONE);
					}
				});
	}

	public void show(String s) {
		new AlertDialog.Builder(this).setMessage(s)
				.setPositiveButton(R.string.confirm, null).show();
	}

}

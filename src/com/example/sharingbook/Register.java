package com.example.sharingbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class Register extends Activity {

	Spinner ugrade;
	EditText ustuid, upwd;
	String ustuidS, upwdS, ugradeS;
	ArrayAdapter mAdapter;
	int begin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		ugrade = (Spinner) findViewById(R.id.ugrade);
		ustuid = (EditText) findViewById(R.id.ustuid);
		upwd = (EditText) findViewById(R.id.upwd);

		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMM");
		String date = sDateFormat.format(new java.util.Date());

		begin = Integer.parseInt(date.substring(0, 4));
		if (date.charAt(5) < '9')
			begin--;

		String m[] = new String[begin - 1996 + 1];
		m[0] = "年级";
		for (int i = begin; i >= 1997; --i)
			m[begin - i + 1] = Integer.toString(i);

		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, m);
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ugrade.setAdapter(mAdapter);
		ugrade.setVisibility(View.VISIBLE);

	}

	public void register(View view) {

		/* verify user's input */
		ugradeS = ugrade.getSelectedItem().toString();
		if (ugradeS.compareTo("年级") == 0) {
			new AlertDialog.Builder(this).setMessage(R.string.ugradeRemind)
					.setPositiveButton(R.string.confirm, null).show();
			return;
		}

		ustuidS = ustuid.getText().toString();
		if (ustuidS.length() == 0) {
			new AlertDialog.Builder(this).setMessage(R.string.ustuidRemind)
					.setPositiveButton(R.string.confirm, null).show();
			return;
		}
		upwdS = upwd.getText().toString();
		if (upwdS.length() == 0) {
			new AlertDialog.Builder(this).setMessage(R.string.upwdRemind)
					.setPositiveButton(R.string.confirm, null).show();
			return;
		}

		/* get response from server according user's input */

		if (Network.ok(this)) {
			new HttpTask()
					.execute("http://www.sharingbook.cn/register.php?ustuid="
							+ ustuidS + "&upwd=" + upwdS + "&ugrade=" + ugradeS);
		} else
			new AlertDialog.Builder(this).setMessage(R.string.networkRemind)
					.setPositiveButton(R.string.confirm, null).show();

	}

	public void show(String s) {
		new AlertDialog.Builder(this).setMessage(s)
				.setPositiveButton(R.string.confirm, null).show();
	}

	public void newActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		
		startActivity(intent);
	}

	public void login(String s) {
		new AlertDialog.Builder(this)
				.setMessage(s)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								newActivity();
							}
						}).show();
	}

	public class HttpTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			return downloadUrl(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			char c = result.charAt(0);
			if (c == '0' || c == '1') {
				if (c == '0')
					result = "您的学号曾经注册过，\n点确认直接登录";
				else
					result = "恭喜您，注册成功！";
				login(result);
			} else if (c == '2') {
				result = "年级、学号或密码错误，\n请你重新输入";
				show(result);
			}
		}

		private String downloadUrl(String xurl) {
			InputStream is = null;
			try {
				URL url = new URL(xurl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000 /* 10s */);
				conn.setConnectTimeout(15000 /* 15s */);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);

				conn.connect();
				int response = conn.getResponseCode();
				if (response == 200) {
					is = conn.getInputStream();
					return readIt(is, 30);
				} else
					return getResources().getString(R.string.networkFailed);
			} catch (IOException e) {
				return getResources().getString(R.string.networkFailed);
			}
		}

		public String readIt(InputStream stream, int len) throws IOException,
				UnsupportedEncodingException {
			Reader reader = null;
			reader = new InputStreamReader(stream, "UTF-8");
			char[] buffer = new char[len];
			reader.read(buffer);
			return new String(buffer);
		}
	}

}

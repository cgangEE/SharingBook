package com.example.sharingbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class Login extends Activity {
	EditText ustuid, upwd;
	String ustuidS, upwdS, umd5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		if (tool.getString(this, "umd5") != null) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		ustuid = (EditText) findViewById(R.id.ustuid);
		upwd = (EditText) findViewById(R.id.upwd);
		ustuid.setText(tool.getString(this, "ustuid"));
		upwd.setText(tool.getString(this, "upwd"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.resiger) {
			Intent intent = new Intent(this, Register.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void login(View view) {

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

		umd5 = tool.md5(ustuidS + upwdS);
		if (Network.ok(this)) {
			String webServer = getResources().getString(R.string.webServer);
			new HttpTask().execute(webServer + "/login.php?umd5="
								+ umd5+"&ustuid="+ustuidS);
			
		//	new HttpTask().execute("http://www.sharingbook.cn/login.php?umd5="
		//			+ umd5+"&ustuid="+ustuidS);
		} else
			new AlertDialog.Builder(this).setMessage(R.string.networkRemind)
					.setPositiveButton(R.string.confirm, null).show();

	}

	public void show(String s) {
		new AlertDialog.Builder(this).setMessage(s)
				.setPositiveButton(R.string.confirm, null).show();
	}

	public void loginSuccess() {
		tool.putString(this, "ustuid", ustuidS);
		tool.putString(this, "upwd", upwdS);
		tool.putString(this, "umd5", umd5);
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
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
			if (c == '1') {
				loginSuccess();
			} else {
				if (c == '0')
					result = getResources().getString(R.string.loginError);
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
					return readIt(is, 10);
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

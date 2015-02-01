package com.example.sharingbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	EditText ustuid, upwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		ustuid = (EditText) findViewById(R.id.ustuid);
		upwd = (EditText) findViewById(R.id.upwd);
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
		
		String ustuidS = ustuid.getText().toString();
		if (ustuidS.length() == 0) {
			new AlertDialog.Builder(this).setMessage(R.string.ustuidRemind).setPositiveButton(R.string.confirm, null).show();
			return;
		}
		String upwdS = upwd.getText().toString();
		if (upwdS.length() == 0) {
			new AlertDialog.Builder(this).setMessage(R.string.upwdRemind).setPositiveButton(R.string.confirm, null).show();
			return;
		}

	}
}

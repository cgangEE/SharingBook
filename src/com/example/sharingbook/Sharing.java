package com.example.sharingbook;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class Sharing extends Activity {
	Spinner slocation;
	EditText sname, sauthor, sprice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharing);
		
		sname = (EditText) findViewById(R.id.sname);
		sauthor = (EditText) findViewById(R.id.sauthor);
		sprice = (EditText) findViewById(R.id.sprice);
		slocation = (Spinner) findViewById(R.id.slocation);
		
		String location[] = {"Ωª“◊µÿµ„", "¡¯‘∞", "∫…‘∞", "æ’‘∞", "À…‘∞"};
		ArrayAdapter mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, location);
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		slocation.setAdapter(mAdapter);
		slocation.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sharing, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.posting) {
			/*
			Intent intent = new Intent(this, Register.class);
			startActivity(intent);
			*/
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

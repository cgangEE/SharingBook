package com.example.sharingbook;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainFragment extends Fragment {
	private int pos;
	private Activity act;
	final static int a[] = new int[] { R.string.setting, R.string.logout };

	public MainFragment(int position, Activity activity) {
		pos = position;
		act = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView;

		switch (pos) {
		case 3:
			rootView = (ViewGroup) inflater.inflate(R.layout.me, container,
					false);
			ListView listview = (ListView) rootView.findViewById(R.id.listview);
			ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

			for (int i = 0; i < a.length; ++i) {
				HashMap<String, String> item = new HashMap<String, String>();
				item.put("item", getResources().getString(a[i]));
				list.add(item);
			}

			SimpleAdapter ad = new SimpleAdapter(act, list, R.layout.me_item,
					new String[] { "item" }, new int[] { R.id.item });
			listview.setAdapter(ad);
			listview.setOnItemClickListener(new LogoutListenre());
			break;
		default:
			rootView = (ViewGroup) inflater.inflate(R.layout.frag, container,
					false);
		}
		return rootView;
	}

	public void logout() {
		tool.putString(act, "umd5", null);
		Intent intent = new Intent(act, Login.class);
		startActivity(intent);
		act.finish();
	}

	public class LogoutListenre implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (a[position]) {
			case R.string.setting:
				break;
			case R.string.logout:
				new AlertDialog.Builder(act)
						.setMessage(R.string.logoutRemind)
						.setPositiveButton(R.string.cancel, null)
						.setNegativeButton(R.string.logout,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										logout();
									}
								}).show();
				break;
			}

		}

	}
}
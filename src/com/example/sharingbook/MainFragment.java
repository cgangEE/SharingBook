package com.example.sharingbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class MainFragment extends Fragment {
	private int pos;
	private Activity act;
	final static int a[] = new int[] { 0, R.string.setting, R.string.logout };

	Bitmap upicBP = null;
	ListView listview = null;
	ProgressBar progressbar = null;

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
			listview = (ListView) rootView.findViewById(R.id.listview);
			progressbar = (ProgressBar) rootView
					.findViewById(R.id.loading_spinner);
			listview.setVisibility(View.GONE);
			upateUserInfo(act);

			break;
		default:
			rootView = (ViewGroup) inflater.inflate(R.layout.frag, container,
					false);
		}
		return rootView;
	}

	public void upateUserInfo(final Activity act) {
		if (tool.getString(act, "uname") != null && upicBP != null) {
			setListView();
			listview.setVisibility(View.VISIBLE);
			progressbar.setVisibility(View.GONE);
			return;
		}

		String umd5 = tool.getString(act, "umd5");
		String ustuid = tool.getString(act, "ustuid");
		final RequestQueue mQueue = Volley.newRequestQueue(act);

		JsonObjectRequest jsonReq = new JsonObjectRequest(
				"http://www.sharingbook.cn/userinfo.php?umd5=" + umd5
						+ "&ustuid=" + ustuid, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							tool.putString(act, "uname",
									response.getString("uname"));
							tool.putString(act, "upic",
									response.getString("upic"));
							tool.putString(act, "usex",
									response.getString("usex"));
							tool.putString(act, "uphone",
									response.getString("uphone"));
							tool.putString(act, "ugrade",
									response.getString("ugrade"));

							String upic = tool.getString(act, "upic");

							final File file = new File(act.getFilesDir(),
									response.getString("uname")+"upic");

							if (!file.exists()) {
								ImageRequest imgReq = new ImageRequest(
										upic,
										new Response.Listener<Bitmap>() {
											@Override
											public void onResponse(
													Bitmap response) {
												upicBP = response;

												try {
													file.createNewFile();
													FileOutputStream out = new FileOutputStream(
															file);
													out.write(bitmap2Bytes(upicBP));
													out.flush();
													out.close();
												} catch (IOException e) {
													new AlertDialog.Builder(act)
															.setMessage(
																	e.toString())
															.setPositiveButton(
																	R.string.confirm,
																	null)
															.show();
												}

												setListView();
												crossfade();
											}
										}, 0, 0, Config.RGB_565,
										new Response.ErrorListener() {
											@Override
											public void onErrorResponse(
													VolleyError error) {
											}
										});
								mQueue.add(imgReq);
							} else {
								FileInputStream in = new FileInputStream(file);
								upicBP = BitmapFactory.decodeStream(in);
								setListView();
								crossfade();
							}

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

	public byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	private void crossfade() {
		int mShortAnimationDuration = getResources().getInteger(
				android.R.integer.config_shortAnimTime);

		listview.setAlpha(0f);
		listview.setVisibility(View.VISIBLE);

		listview.animate().alpha(1f).setDuration(mShortAnimationDuration)
				.setListener(null);

		progressbar.animate().alpha(0f).setDuration(mShortAnimationDuration)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						progressbar.setVisibility(View.GONE);
					}
				});
	}

	public void setListView() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		for (int i = 1; i < a.length; ++i) {
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("item", getResources().getString(a[i]));
			list.add(item);
		}

		ListViewAdapter ad = new ListViewAdapter(act, list, R.layout.frag,
				new String[] { "item" }, new int[] { R.id.item });
		listview.setAdapter(ad);

		listview.setOnItemClickListener(new LogoutListener());
	}

	public class ListViewAdapter extends BaseAdapter {
		final int TYPE_COUNT = 2, FIRST_TYPE = 0, OTHERS_TYPE = 1;
		int resource;
		List<? extends Map<String, ?>> mArrayList;
		LayoutInflater mLayoutInflater;

		public ListViewAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			this.mArrayList = data;
			this.resource = resource;
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			if (mArrayList == null)
				return 0;
			else
				return mArrayList.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			if (mArrayList == null)
				return null;
			else {
				if (position > 0)
					return mArrayList.get(position - 1);
				else
					return mArrayList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0)
				return FIRST_TYPE;
			else
				return OTHERS_TYPE;
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_COUNT;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int currentType = getItemViewType(position);
			View view = convertView;
			if (currentType == FIRST_TYPE) {
				FirstItemViewHolder holder = null;
				if (view == null) {
					view = mLayoutInflater
							.inflate(R.layout.me_item_image, null);
					holder = new FirstItemViewHolder();
					holder.imageView = (ImageView) view.findViewById(R.id.upic);
					holder.textView = (TextView) view.findViewById(R.id.uname);
					view.setTag(holder);
				} else {
					holder = (FirstItemViewHolder) view.getTag();
				}
				if (holder.imageView != null) {
					holder.imageView.setImageBitmap(upicBP);
				}
				if (holder.textView != null) {
					holder.textView.setText(tool.getString(act, "uname"));
				}
			} else {
				OthersViewHolder holder = null;
				if (view == null) {
					view = mLayoutInflater.inflate(R.layout.me_item, null);
					holder = new OthersViewHolder();
					holder.textView = (TextView) view.findViewById(R.id.item);
					view.setTag(holder);
				} else {
					holder = (OthersViewHolder) view.getTag();
				}
				if (mArrayList != null && holder.textView != null)
					holder.textView.setText((String) (mArrayList
							.get(position - 1).get("item")));
			}
			convertView = view;
			return convertView;
		}

		private class FirstItemViewHolder {
			ImageView imageView;
			TextView textView;
		}

		private class OthersViewHolder {
			TextView textView;
		}

	}

	public class LogoutListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (a[position]) {
			case R.string.setting:
				break;
			case R.string.logout:
				new AlertDialog.Builder(act)
						.setMessage(R.string.logoutRemind)
						.setPositiveButton(R.string.logout,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										logout();
									}
								}).setNegativeButton(R.string.cancel, null)
						.show();
				break;
			default:
			}

		}

		public void logout() {
			tool.putString(act, "umd5", null);
			Intent intent = new Intent(act, Login.class);
			startActivity(intent);
			act.finish();
		}

	}
}
package com.example.sharingbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class EditBookInfo extends Activity {

	Button observingButton = null;

	TextView uname = null;
	ImageView upic = null;

	String ustuidS = null;
	String sidS = null;
	String unameS = null;
	String umd5 = null;
	ImageView image = null;
	RequestQueue mQueue = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editbookinfo);

		uname = (TextView) findViewById(R.id.eUname);
		upic = (ImageView) findViewById(R.id.eUpic);

		Intent intent = getIntent();
		ustuidS = intent.getStringExtra("ustuid");
		sidS = intent.getStringExtra("sid");
		unameS = intent.getStringExtra("uname");

		uname.setText(unameS);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		
		showUpic();
		refresh();

	}

	public void delete(View view) {
		new AlertDialog.Builder(this)
				.setMessage(R.string.deleteRemind)
				.setPositiveButton(R.string.delete,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (mQueue == null)
									mQueue = Volley
											.newRequestQueue(EditBookInfo.this);
								String webServer = getResources().getString(
										R.string.webServer);
								String umd5 = tool.getString(EditBookInfo.this,
										"umd5");
								String ustuid = tool.getString(
										EditBookInfo.this, "ustuid");

								StringRequest stringRequest = new StringRequest(
										webServer
												+ "/sharingDelete.php?ustuid="
												+ ustuid + "&umd5=" + umd5
												+ "&sid=" + sidS,
										new Response.Listener<String>() {
											@Override
											public void onResponse(
													String response) {
												char c = response.charAt(0);
												if (c == '1') {
													finish();
												} else
													show(getResources()
															.getString(
																	R.string.networkFailed));

											}
										}, new Response.ErrorListener() {
											@Override
											public void onErrorResponse(
													VolleyError error) {
												show(getResources().getString(
														R.string.networkFailed));
											}
										});
								mQueue.add(stringRequest);
							}
						}).setNegativeButton(R.string.cancel, null).show();
	}


	public void refresh() {
		final String webServer = getResources().getString(R.string.webServer);
		if (mQueue == null)
			mQueue = Volley.newRequestQueue(EditBookInfo.this);

		JsonObjectRequest jsonReq = new JsonObjectRequest(webServer
				+ "/onebookinfo.php?sid=" + sidS, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							TextView sname = (TextView) findViewById(R.id.eSname);
							TextView sauthor = (TextView) findViewById(R.id.eSauthor);
							TextView sprice = (TextView) findViewById(R.id.eSprice);
							TextView slocation = (TextView) findViewById(R.id.eSlocation);
							TextView uname = (TextView) findViewById(R.id.eSuname);
							TextView stime = (TextView) findViewById(R.id.eStime);
							image = (ImageView) findViewById(R.id.eSpic);

							sname.setText(getResources().getString(
									R.string.sname)
									+ ": " + response.getString("sname"));
							sauthor.setText(getResources().getString(
									R.string.sauthor)
									+ ": " + response.getString("sauthor"));
							sprice.setText(getResources().getString(
									R.string.sprice)
									+ ": "
									+ getSprice(response.getString("sprice")));
							slocation.setText(getResources().getString(
									R.string.slocation)
									+ "： " + response.getString("slocation"));
							uname.setText("由" + response.getString("uname")
									+ "分享");
							stime.setText(response.getString("stime")
									.split(" ")[0]);

							String url = webServer + "/upload/"
									+ response.getString("spic") + ".jpg";

							ImageRequest imgReq = new ImageRequest(
									url,
									new Response.Listener<Bitmap>() {
										@Override
										public void onResponse(Bitmap response) {
											image.setImageBitmap(response);
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

	public String getSprice(String sprice) {
		if (sprice.compareTo(new String("0元，分享一本书，交一个朋友")) == 0)
			return new String("0元");
		return sprice;
	}

	void setUpic(Bitmap bp) {
		upic.setImageBitmap(bp);
	}

	public void show(String s) {
		new AlertDialog.Builder(this).setMessage(s)
				.setPositiveButton(R.string.confirm, null).show();
	}

	public void showUpic() {

		if (mQueue == null)
			mQueue = Volley.newRequestQueue(EditBookInfo.this);

		String webServer = getResources().getString(R.string.webServer);

		JsonObjectRequest jsonReq = new JsonObjectRequest(webServer
				+ "/userinfo.php?&ustuid=" + ustuidS, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {

							String upic = response.getString("upic");

							final File file = new File(
									EditBookInfo.this.getFilesDir(),
									response.getString("ustuid") + "upic");

							if (!file.exists()) {
								ImageRequest imgReq = new ImageRequest(
										upic,
										new Response.Listener<Bitmap>() {
											@Override
											public void onResponse(
													Bitmap response) {
												setUpic(response);
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
															EditBookInfo.this)
															.setMessage(
																	e.toString())
															.setPositiveButton(
																	R.string.confirm,
																	null)
															.show();
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
							} else {
								FileInputStream in = new FileInputStream(file);
								Bitmap upicBP = BitmapFactory.decodeStream(in);
								setUpic(upicBP);
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

}

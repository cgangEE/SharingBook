package com.example.sharingbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

public class MainActivity extends FragmentActivity {

	private static final int NUM_PAGES = 4;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	SearchView searchView = null;
	String spicS = null;

	String ustuidS = null;
	String umd5S = null;
	int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new SlideAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setOffscreenPageLimit(1);
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				getActionBar().setSelectedNavigationItem(position);
				super.onPageSelected(position);
			}
		});

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#dddddd")));

		ActionBar.TabListener tabListener = new MTabListener();

		for (int i = 0; i < NUM_PAGES; ++i)
			actionBar.addTab(actionBar.newTab().setText(getTabText(i))
					.setTabListener(tabListener));

		Random r = new Random();
		spicS = (new Integer(r.nextInt(100000000))).toString();

		ustuidS = tool.getString(this, "ustuid");
		umd5S = tool.getString(this, "umd5");
		
		startService(new Intent("newservice.action"));
	}

	public String getTabText(int i) {
		switch (i) {
		case 0:
			return getResources().getString(R.string.home);
		case 1:
			return getResources().getString(R.string.message);
		case 2:
			return getResources().getString(R.string.find);
		default:
			return getResources().getString(R.string.me);
		}
	}

	public void show(String s) {
		new AlertDialog.Builder(this).setMessage(s)
				.setPositiveButton(R.string.confirm, null).show();
	}

	public class MTabListener implements TabListener {

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			mPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

	}

	private class SlideAdapter extends FragmentPagerAdapter {
		public SlideAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Fragment getItem(int position) {
			// Important, need to modify....
			Fragment ret = null;
			switch (position) {
			case 0:
				ret = new FragmentHome(MainActivity.this);
				break;
			case 1:
				ret = new FragmentChat(MainActivity.this);
				break;
			case 2:
				ret = new FragmentFind(MainActivity.this);
				break;
			case 3:
				ret = new FragmentMe(MainActivity.this);
			}
			return ret;
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}

	}

	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0)
			super.onBackPressed();
		else
			mPager.setCurrentItem(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setOnQueryTextListener(new SearchListener());
		searchView.setSubmitButtonEnabled(false);
		return true;
	}

	class SearchListener implements SearchView.OnQueryTextListener {
		@Override
		public boolean onQueryTextSubmit(String query) {
			Intent intent = new Intent(MainActivity.this, Search.class);
			intent.putExtra("query", query);
			startActivity(intent);
			return false;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			return false;
		}
	}

	final int IMAGE_SELECT = 0;
	final int IMAGE_CAMERA = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			ContentResolver cr = this.getContentResolver();
			File file = null;

			Display currentDisplay = getWindowManager().getDefaultDisplay();
			int dw = currentDisplay.getWidth();
			int dh = currentDisplay.getHeight();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			InputStream in = null;
			Uri uri = null;
			try {
				if (type == IMAGE_SELECT) {
					uri = data.getData();
					in = cr.openInputStream(uri);
				} else {
					file = new File(Environment.getExternalStorageDirectory(),
							"spic.jpg");
					in = new FileInputStream(file);
				}

				Bitmap bp = BitmapFactory.decodeStream(in, null, options);

				int heightRatio = (int) Math.ceil(options.outHeight * 7.
						/ (float) dh);
				int widthRatio = (int) Math.ceil(options.outWidth * 7.
						/ (float) dw);

				if (heightRatio > 1 || widthRatio > 1)
					options.inSampleSize = heightRatio > widthRatio ? heightRatio
							: widthRatio;

				options.inPurgeable = true;
				options.inInputShareable = true;

				options.inJustDecodeBounds = false;
				if (type == IMAGE_SELECT)
					in = cr.openInputStream(uri);
				else
					in = new FileInputStream(file);
				bp = BitmapFactory.decodeStream(in, null, options);

				File file2 = new File(this.getFilesDir(), spicS);

				file2.createNewFile();
				FileOutputStream out = new FileOutputStream(file2);
				out.write(tool.bitmap2Bytes(bp));
				out.flush();
				out.close();

				if (Network.ok(this)) {
					String webServer = getResources().getString(
							R.string.webServer);

					new HttpTask().execute(webServer
							+ "/headPicUpdate.php?spic=" + spicS + "&ustuid="
							+ ustuidS + "&umd5=" + umd5S);

				} else
					new AlertDialog.Builder(this)
							.setMessage(R.string.networkRemind)
							.setPositiveButton(R.string.confirm, null).show();

			} catch (Exception e) {
				if (tool.DEBUG)
					show("fuck me " + e.toString());
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	void sharingSuccess() {
		new AlertDialog.Builder(this)
				.setMessage(R.string.headPicUpdateSuccess)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								final File file = new File(MainActivity.this
										.getFilesDir(), ustuidS + "upic");

								if (file.exists()) { // 判断文件是否存在
									file.getAbsoluteFile().delete(); // delete()方法
								}
							}
						}).show();
	}

	public class HttpTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			return uploadFile(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// show(result);
			char c = result.charAt(0);
			if (c == '1') {
				sharingSuccess();
			} else {
				result = getResources().getString(R.string.sharingFailed);
				show(result);
			}

		}

		private String uploadFile(String uploadUrl) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				httpclient.getParams().setParameter(
						CoreProtocolPNames.PROTOCOL_VERSION,
						HttpVersion.HTTP_1_1);
				HttpPost httppost = new HttpPost(tool.process(uploadUrl));

				MultipartEntity entity = new MultipartEntity();
				File file = new File(MainActivity.this.getFilesDir(), spicS);
				ContentBody fileBody = new FileBody(file);

				entity.addPart("uploadedfile", fileBody);
				httppost.setEntity(entity);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity resEntity = response.getEntity();

				// Log.e("sharingErr", "T");
				String ret = null;
				if (resEntity != null)
					ret = EntityUtils.toString(resEntity);
				else {
					// Log.e("sharingErr", "FT");
					ret = getResources().getString(R.string.networkFailed);
				}
				httpclient.getConnectionManager().shutdown();

				return ret;
			} catch (Exception e) {
				// Log.e("sharingErr", e.toString());
				return getResources().getString(R.string.networkFailed);
			}
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		int id = item.getItemId();
		switch (id) {
		case R.id.sharing:
			intent = new Intent(this, Sharing.class);
			startActivity(intent);
			return true;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

}

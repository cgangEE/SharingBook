package com.example.sharingbook;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

	private static final int NUM_PAGES = 4;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new SlideAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				getActionBar().setSelectedNavigationItem(position);
				super.onPageSelected(position);
			}

		});

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#dddddd")));

		ActionBar.TabListener tabListener = new MTabListener();

		for (int i = 0; i < NUM_PAGES; ++i)
			actionBar.addTab(actionBar.newTab().setText(getTabText(i))
					.setTabListener(tabListener));

		// setupActionBar();

	}

	/*
	 * private void setupActionBar() { ActionBar actionBar = getActionBar();
	 * actionBar.setDisplayShowTitleEnabled(false);
	 * actionBar.setDisplayUseLogoEnabled(false);
	 * actionBar.setDisplayHomeAsUpEnabled(false);
	 * actionBar.setDisplayShowCustomEnabled(true);
	 * actionBar.setDisplayShowHomeEnabled(false);
	 * 
	 * LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT,
	 * LayoutParams.MATCH_PARENT); View customNav =
	 * LayoutInflater.from(this).inflate(R.layout.actionbar, null); // layout
	 * which contains your button.
	 * 
	 * actionBar.setCustomView(customNav, lp1); }
	 */

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
		public Fragment getItem(int position) {
			return new MainFragment(position, MainActivity.this);
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
		return true;
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
			/*
			String IMAGE_TYPE="image/*";
			int IMAGE_CODE=0;
			Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
			getAlbum.setType(IMAGE_TYPE);
			startActivityForResult(getAlbum, IMAGE_CODE);
			*/
		case R.id.search:
			intent = new Intent(this, Search.class);
			startActivity(intent);
			break;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

}

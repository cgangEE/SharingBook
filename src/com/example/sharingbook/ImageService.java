package com.example.sharingbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class ImageService {
	//get a bitmap image by url if can, otherwise return null
	
	public static byte[] getImage(String path) {
		try{
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000 /* 10s */);
		conn.setConnectTimeout(15000 /* 15s */);
		conn.setRequestMethod("GET");
		InputStream inputStream = conn.getInputStream();
		byte[] data = readIt(inputStream);
		return data;
		}
		catch (Exception e){
			return null;
		}
	}
	
	public static byte[] readIt(InputStream inputStream) throws IOException{
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream ret = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1)
			ret.write(buffer, 0, len);
		ret.close();
		return ret.toByteArray();
	}
	
	public class BitmapCache implements ImageCache {
		private LruCache<String, Bitmap> mCache;

		public BitmapCache() {
			int maxSize = 10 * 1024 * 1024;
			mCache = new LruCache<String, Bitmap>(maxSize) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					return bitmap.getRowBytes() * bitmap.getHeight();
				}
			};
		}

		@Override
		public Bitmap getBitmap(String url) {
			return mCache.get(url);
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			mCache.put(url, bitmap);
		}

	}
}

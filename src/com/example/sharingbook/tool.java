package com.example.sharingbook;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class tool {
	static boolean DEBUG = false;
	
	public static String process(String s){
		String ret = "";
		for (int i=0; i<s.length(); ++i)
			if (s.charAt(i) == ' '){
				if (i==0 || (i!=0 && s.charAt(i-1)==' '))
					continue;
				ret += '+';
			}
			else 
				ret += s.charAt(i);
		return ret;
	}
	
	public static String md5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	public static void putString(Context context, String key, String value) {
		SharedPreferences sharePref = context.getSharedPreferences(context
				.getResources().getString(R.string.preference_key), 0);
		SharedPreferences.Editor editor = sharePref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(Context context, String key) {
		SharedPreferences sharePref = context.getSharedPreferences(context
				.getResources().getString(R.string.preference_key), 0);
		return sharePref.getString(key, null);
	}

	public static Bitmap bitmapChange(Bitmap bitmap, float x) {
		Matrix matrix = new Matrix();
		matrix.postScale(x, x); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}
	
	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
}

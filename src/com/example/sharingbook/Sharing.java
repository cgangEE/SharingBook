package com.example.sharingbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class Sharing extends Activity {
	Spinner slocation, sprice;
	EditText sname, sauthor;
	ImageButton spic;
	Bitmap bp = null;
	String location[] = { "分享地点", "柳园", "荷园", "菊园", "松园" };
	String priceStr[] = new String[102];
	String sauthorS, snameS, spriceS, slocationS;
	int spriceId, slocationId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharing);

		sname = (EditText) findViewById(R.id.sname);
		sauthor = (EditText) findViewById(R.id.sauthor);
		sprice = (Spinner) findViewById(R.id.sprice);
		slocation = (Spinner) findViewById(R.id.slocation);
		spic = (ImageButton) findViewById(R.id.spic);

		ArrayAdapter mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, location);
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		slocation.setAdapter(mAdapter);
		slocation.setVisibility(View.VISIBLE);
		priceStr[0] = "价格";
		priceStr[1] = "0元，分享一本书，交一个朋友";

		for (Integer i = 1; i <= 100; ++i) {
			priceStr[i + 1] = i.toString() + "元";
		}

		ArrayAdapter price = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, priceStr);
		price.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sprice.setAdapter(price);
		sprice.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.posting) {
			if (bp == null) {
				new AlertDialog.Builder(this).setMessage(R.string.spicRemind)
						.setPositiveButton(R.string.confirm, null).show();
				return true;
			}
			snameS = sname.getText().toString();
			if (snameS.length() == 0) {
				new AlertDialog.Builder(this).setMessage(R.string.snameRemind)
						.setPositiveButton(R.string.confirm, null).show();
				return true;
			}

			sauthorS = sauthor.getText().toString();
			if (sauthorS.length() == 0) {
				new AlertDialog.Builder(this)
						.setMessage(R.string.sauthorRemind)
						.setPositiveButton(R.string.confirm, null).show();
				return true;
			}

			spriceS = sprice.getSelectedItem().toString();
			spriceId = (int) sprice.getSelectedItemId();
			if (spriceId == 0) {
				new AlertDialog.Builder(this).setMessage(R.string.spriceRemind)
						.setPositiveButton(R.string.confirm, null).show();
				return true;
			}

			slocationS = slocation.getSelectedItem().toString();
			slocationId = (int) slocation.getSelectedItemId();
			if (slocationId == 0) {
				new AlertDialog.Builder(this)
						.setMessage(R.string.slocationRemind)
						.setPositiveButton(R.string.confirm, null).show();
				return true;
			}

			if (Network.ok(this)) {
				
			} else
				new AlertDialog.Builder(this)
						.setMessage(R.string.networkRemind)
						.setPositiveButton(R.string.confirm, null).show();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	final int IMAGE_SELECT = 0;
	final int IMAGE_CAMERA = 1;

	public class HttpTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
		private String uploadFile(String uploadUrl)  
		{  
			try{
		        HttpClient httpclient = new DefaultHttpClient();  
		        httpclient.getParams().setParameter(  
		                CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);  
		        HttpPost httppost = new HttpPost(uploadUrl);  
		          
		        MultipartEntity entity = new MultipartEntity();  
		          
		        File file = new File("");  
		        FileBody fileBody = new FileBody(file);  
		        entity.addPart("uploadedfile", fileBody);  
		              
		        httppost.setEntity(entity);  
		        HttpResponse response = httpclient.execute(httppost);  
		          
		        HttpEntity resEntity = response.getEntity();  
		        if (resEntity != null) {              
		            Toast.makeText(this, EntityUtils.toString(resEntity), Toast.LENGTH_LONG).show();  
		        }  
		  
		        httpclient.getConnectionManager().shutdown();  
			} catch (Exception e){
				return getResources().getString(R.string.networkFailed);
			}
		}  
		
	}
	public void getPhoto(View view) {
		final String[] items = { "从手机相册选择", "拍照" };
		new AlertDialog.Builder(this).setTitle("图书封面照片")
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (which == IMAGE_SELECT) {
							Intent intent = new Intent();
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intent, IMAGE_SELECT);
						} else {
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							File file = new File(Environment
									.getExternalStorageDirectory(), "spic.jpg");
							Uri uri = Uri.fromFile(file);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							startActivityForResult(intent, IMAGE_CAMERA);
						}
					}
				}).show();
	}

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
				if (requestCode == IMAGE_SELECT) {
					uri = data.getData();
					in = cr.openInputStream(uri);
				} else {
					file = new File(Environment.getExternalStorageDirectory(),
							"spic.jpg");
					in = new FileInputStream(file);
				}

				bp = BitmapFactory.decodeStream(in, null, options);

				int heightRatio = (int) Math.ceil(options.outHeight * 3.
						/ (float) dh);
				int widthRatio = (int) Math.ceil(options.outWidth * 3.
						/ (float) dw);

				if (heightRatio > 1 || widthRatio > 1)
					options.inSampleSize = heightRatio > widthRatio ? heightRatio
							: widthRatio;

				options.inPreferredConfig = Bitmap.Config.ARGB_4444;
				options.inPurgeable = true;
				options.inInputShareable = true;

				options.inJustDecodeBounds = false;
				if (requestCode == IMAGE_SELECT)
					in = cr.openInputStream(uri);
				else
					in = new FileInputStream(file);
				bp = BitmapFactory.decodeStream(in, null, options);

				Bitmap bpSmall = tool.bitmapChange(bp, (float) 0.6);
				spic.setImageBitmap(bpSmall);

			} catch (Exception e) {

			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sharing, menu);
		return true;
	}

}

package com.example.face_touch;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.example.face_touch.FaceDetect.CallBack;
import com.facepp.error.FaceppParseException;


public class NetDetectActivity extends Activity implements OnClickListener{


	protected static final int MSG_SUCCESS = 0;
	protected static final int MSG_ERROR = 1;
	private ImageView mPhoto;
	private Button mGetImage,mDetect,mTip;
	private View mWatting;
	private String mCurrentPhotoDir;

	private static int PICK_CODE =  1;

	private Bitmap photoImage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		mPaint = new Paint();
	}
	private void initView() {
		mPhoto = (ImageView) findViewById(R.id.get_photo);
		mGetImage = (Button) findViewById(R.id.get_image);
		mDetect = (Button) findViewById(R.id.get_detect);
		mTip = (Button) findViewById(R.id.get_tips);
		mWatting = findViewById(R.id.waitting);

		mGetImage.setOnClickListener(this);
		mDetect.setOnClickListener(this);

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.get_image:
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, PICK_CODE);
			break;
		case R.id.get_detect:
			mWatting.setVisibility(View.VISIBLE);
			FaceDetect.detect(photoImage, new CallBack() {

				@Override
				public void success(JSONObject result) {
					Message msg = Message.obtain();
					msg.what = MSG_SUCCESS;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}

				@Override
				public void error(FaceppParseException exception) {
					Message msg = Message.obtain();
					msg.what = MSG_ERROR;
					msg.obj = exception.getMessage();
					mHandler.sendMessage(msg);
				}
			});
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PICK_CODE){
			if(data!=null){
				Uri uri = data.getData();
				Cursor cursor = getContentResolver().query(uri, null, null, null, null);
				cursor.moveToFirst();
				int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
				mCurrentPhotoDir = cursor.getString(idx);
				cursor.close();
				resizePhoto();
				mPhoto.setImageBitmap(photoImage);
				mTip.setText("click Detect");
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	private void resizePhoto() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoDir,options);
		double radio = Math.max(options.outWidth*1.0d/1024f, options.outHeight*1.0d/1024f);
		options.inSampleSize = (int) Math.ceil(radio);
		options.inJustDecodeBounds = false;
		photoImage = BitmapFactory.decodeFile(mCurrentPhotoDir,options);
	}


	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_SUCCESS:
				mWatting.setVisibility(View.GONE);
				JSONObject js = (JSONObject) msg.obj;
				prepareBitmap(js);
				mPhoto.setImageBitmap(photoImage);
				break;
			case MSG_ERROR:
				mWatting.setVisibility(View.GONE);
				String errmsg = (String) msg.obj;
				mTip.setText(errmsg);
				break;

			default:
				break;
			}

			super.handleMessage(msg);
		}
	};
	private Paint mPaint;
	private void prepareBitmap(JSONObject js) {
		try {
			Bitmap bitmap = Bitmap.createBitmap(photoImage.getWidth(),photoImage.getHeight(),photoImage.getConfig());
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(photoImage, 0, 0, null);
			
			JSONArray  faces = js.getJSONArray("face");
			
			int faceCount = faces.length();
			mTip.setText("found --"+faceCount);
			for (int i = 0; i < faceCount; i++) {
				JSONObject face = faces.getJSONObject(i);
				
				JSONObject posObj = face.getJSONObject("position");
				float x = (float) posObj.getJSONObject("center").getDouble("x");
				float y = (float) posObj.getJSONObject("center").getDouble("y");
				
				float w = (float) posObj.getDouble("width");
				float h = (float) posObj.getDouble("height");
				
				x = x*bitmap.getWidth()/100;
				y = y*bitmap.getHeight()/100;
				w = w*bitmap.getWidth()/100;
				h = h*bitmap.getHeight()/100;
				mPaint.setColor(0xffffffff);
				mPaint.setStrokeWidth(3);
				canvas.drawLine(x-w/2, y-h/2, x-w/2, y+h/2, mPaint);
				canvas.drawLine(x-w/2, y-h/2, x+w/2, y-h/2, mPaint);
				canvas.drawLine(x-w/2, y+h/2, x+w/2, y+h/2, mPaint);
				canvas.drawLine(x+w/2, y-h/2, x+w/2, y+h/2, mPaint);
				int age = face.getJSONObject("attribute").getJSONObject("age").getInt("value");
				
				String gender = face.getJSONObject("attribute").getJSONObject("gender").getString("value");
				
				canvas.drawText(gender+"--"+age, x-10, y-h/2-10, mPaint);
				
				photoImage = bitmap;
				
			}
			
		} catch (Exception e) {
		}
	}
}

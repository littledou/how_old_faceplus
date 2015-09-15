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
import com.faceplusplus.api.FaceDetecter;
import com.faceplusplus.api.FaceDetecter.Face;
import com.facepp.error.FaceppParseException;


public class LocalDetectActivity extends Activity implements OnClickListener{


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
		setContentView(R.layout.local_main);
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
			final FaceDetecter detecter = new FaceDetecter();
			detecter.init(this, "b7dd142453f407ef0d22f36dec047510");
			new Thread(new Runnable() {

				@Override
				public void run() {
					Face[] findFaces = detecter.findFaces(photoImage);
					Message msg = new Message();
					if(findFaces.length>0){
						msg.what = MSG_SUCCESS;
						msg.obj = findFaces;
					}else {
						msg.what = MSG_ERROR;
					}
					mHandler.sendMessage(msg);
				}
			}).start();
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
				Face[] findFaces =  (Face[]) msg.obj;
				prepareBitmap(findFaces);
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
	private void prepareBitmap(Face[] findFaces) {
		try {
			Bitmap bitmap = Bitmap.createBitmap(photoImage.getWidth(),photoImage.getHeight(),photoImage.getConfig());
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(photoImage, 0, 0, null);

			int faceCount =findFaces.length;
			mTip.setText("found --"+faceCount);
			for (int i = 0; i < faceCount; i++) {
				Face face = findFaces[i];
				System.out.println(face.top+":"+face.bottom+":"+face.left+":"+face.right);
				photoImage = bitmap;

			}

		} catch (Exception e) {
		}
	}
}

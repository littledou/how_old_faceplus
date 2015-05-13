package com.example.face_touch;

import java.io.ByteArrayOutputStream;

import org.json.JSONObject;

import android.graphics.Bitmap;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

public class FaceDetect {


	public interface CallBack{
		void success(JSONObject result);
		void error(FaceppParseException Exception);
	}

	public static void detect(final Bitmap bm , final CallBack callBack){

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpRequests requests = new HttpRequests(Constant.KEY, Constant.SECRET, true, true);

					Bitmap bmSmall = Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight());

					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bmSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);

					byte[] byteArray = stream.toByteArray();
					PostParameters parameters = new PostParameters();
					parameters.setImg(byteArray);

					JSONObject detectionDetect = requests.detectionDetect(parameters);
					System.out.println(detectionDetect.toString());
					if(callBack!=null ){
						callBack.success(detectionDetect);
					}
				} catch (FaceppParseException e) {
					e.printStackTrace();
					if(callBack!=null ){
						callBack.error(e);
					}
				}
			}
		}).start();;

	}
}

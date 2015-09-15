package com.example.face_touch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class StartActivity extends Activity{

	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		LinearLayout parent = new LinearLayout(mContext);
		parent.setOrientation(LinearLayout.VERTICAL);
		parent.setGravity(Gravity.CENTER);
		Button bt1 = new Button(mContext);
		bt1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		bt1.setText("CameraDetector");
		parent.addView(bt1);
		
		Button bt2 = new Button(mContext);
		bt2.setText("ImageDetector");
		bt2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		parent.addView(bt2);
		Button bt3 = new Button(mContext);
		bt3.setText("FrameDetector");
		bt3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		parent.addView(bt3);
		
		
		setContentView(parent);

		bt1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext,LocalDetectActivity.class));
			}
		});
		bt2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext,NetDetectActivity.class));
			}
		});
		bt3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
	}
}

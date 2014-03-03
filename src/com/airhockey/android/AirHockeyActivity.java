package com.airhockey.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class AirHockeyActivity extends Activity {

	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		glSurfaceView = new GLSurfaceView(this);
		
		final ActivityManager activityManager =
				(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo =
				activityManager.getDeviceConfigurationInfo();
		final boolean supportEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
		
		if (supportEs2) {
			// Request an OpenGL ES 2.0 compatible context
			glSurfaceView.setEGLContextClientVersion(2);
			
			// Assign out renderer
			glSurfaceView.setRenderer(new AirHockeyRenderer(this));
			rendererSet = true;
		} else {
			Toast.makeText(this,
					"This device does not support OpenGL ES 2.0.0.",
					Toast.LENGTH_LONG).show();
			return;
		}
		
		setContentView(R.layout.activity_first_open_glproject);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.first_open_glproject, menu);
		return true;
	}
	
	public void startGame(View view) {
		if (rendererSet) {
			setContentView(glSurfaceView);
		} else {
			Toast.makeText(this,
					"The renderer is not set, cannot open OpenGL surface.",
					Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		if (rendererSet)
			glSurfaceView.onPause();
	}
	
	@Override
	protected void onResume()
	{
		super.onPause();
		if (rendererSet)
			glSurfaceView.onResume();
	}

}

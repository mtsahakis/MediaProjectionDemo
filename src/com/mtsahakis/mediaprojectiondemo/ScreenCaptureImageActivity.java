package com.mtsahakis.mediaprojectiondemo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class ScreenCaptureImageActivity extends Activity {
	
	private static final String TAG = ScreenCaptureImageActivity.class.getName();
	private static final int REQUEST_CODE= 100;
	
	private MediaProjectionManager mProjectionManager;
	private MediaProjection mProjection;
	private ImageReader mImageReader;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private int imagesProduced;
	private long startTimeInMillis;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	    
	    // call for the projection manager
	    mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
	    
	    // start projection
	    final Button startButton = (Button)findViewById(R.id.startButton);
	    startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startProjection();
			}
		});
	    
	    // stop projection
	    final Button stopButton = (Button)findViewById(R.id.stopButton);
	    stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopProjection();
			}
		});
	    
	    // start capture handling thread
	    new Thread() {
	    	@Override
	    	public void run() {
	    		Looper.prepare();
	    		mHandler = new Handler();
	    		Looper.loop();
	    	}
	    }.start();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == REQUEST_CODE) {
    		// for statistics -- init
    		imagesProduced = 0;
    		startTimeInMillis = System.currentTimeMillis();
    		
    		mProjection = mProjectionManager.getMediaProjection(resultCode, data);
    		
			if (mProjection != null) {
				final DisplayMetrics metrics = getResources().getDisplayMetrics();
				final int density = metrics.densityDpi;
				final int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
				final Display display = getWindowManager().getDefaultDisplay();
				final Point size = new Point();
				display.getSize(size);
				final int width = size.x;
				final int height = size.y;
				
				mImageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 2);
				mProjection.createVirtualDisplay("screencap", width, height, density, flags, mImageReader.getSurface(), new VirtualDisplayCallback(), mHandler);
				mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
    				
    				@Override
					public void onImageAvailable(ImageReader reader) {
    					Image image = null;
						FileOutputStream fos = null;
						Bitmap bitmap = null;
						
						try {
							image = mImageReader.acquireLatestImage();
							if (image != null) {
								final Image.Plane[] planes = image.getPlanes();
							    final Buffer imageBuffer = planes[0].getBuffer().rewind();
							    
							    // create bitmap
							    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
							    bitmap.copyPixelsFromBuffer(imageBuffer);
							    // write bitmap to a file
			        	        fos = new FileOutputStream(getFilesDir() + "/myscreen.png");
			        	        bitmap.compress(CompressFormat.JPEG, 100, fos);
							    
			        	        // for statistics
			        	        imagesProduced++;
			        	        final long now = System.currentTimeMillis();
			                    final long sampleTime = now - startTimeInMillis;
			                    Log.e(TAG, "produced images at rate: " + (imagesProduced/(sampleTime/1000.0f)) + " per sec");
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
	        	        	if (fos!=null) {
	        	        		try {
	        	        			fos.close();
	        	        		} catch (IOException ioe) { 
	        	        			ioe.printStackTrace();
	        	            	}
	        	            }
	        	        	
	        	        	if (bitmap!=null)
	        	        		bitmap.recycle();
	        	        	
	        	        	if (image!=null)
								image.close();
	        	        		
	        	        }
					}
    				
    			}, mHandler);
    		}
    	}
    	
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void startProjection() {
    	startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }
    
    private void stopProjection() {
    	mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProjection.stop();   
            }
    	});
    }  
     
    private class VirtualDisplayCallback extends VirtualDisplay.Callback {

		@Override
		public void onPaused() {
			super.onPaused();
			Log.e(TAG, "VirtualDisplayCallback: onPaused");
		}

		@Override
		public void onResumed() {
			super.onResumed();
			Log.e(TAG, "VirtualDisplayCallback: onResumed");
		}

		@Override
		public void onStopped() {
			super.onStopped();
			Log.e(TAG, "VirtualDisplayCallback: onStopped");
		}
    	
    }
    
}

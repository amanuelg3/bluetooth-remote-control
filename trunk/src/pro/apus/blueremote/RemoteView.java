/* Copyright 2007, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
**
** Author: Lars Kristian Roland
** License: Apache (standard Android license)
*/

package pro.apus.blueremote;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class RemoteView extends View {
    private static final String TAG = "CustomView";

	private ShapeDrawable mThrottleDrawable;
	private ShapeDrawable mSteeringDrawable;
	
    private int[] x = {-1,-1};
    private int[] y = {-1,-1};
    
    public int throttle = 0;
    public int aileron = 50;
    public int elevator = 50;
    public int rudder = 0;
    
    RectF throttleRect;
	Paint paint;
	Paint borderPaint;
	Paint bgPaint;
	
	private BluetoothSerialService mSerialService;

	private String status = "Not transmitting";
	
    public RemoteView(Context context) {
        super(context);     
        init();
   }
    
    public RemoteView (Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init();
    }  
  
    public RemoteView (Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        init();
    }  

    private void init()
    {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        
        borderPaint = new Paint();
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(5);
        borderPaint.setTextSize((float) 20.0);
        
        bgPaint = new Paint();
        bgPaint.setColor(Color.GRAY);
        bgPaint.setStrokeWidth(5);
        bgPaint.setTextSize((float) 20.0);
        
        mThrottleDrawable = new ShapeDrawable(new OvalShape());
        mThrottleDrawable.getPaint().setColor(Color.GREEN);
        
        mSteeringDrawable = new ShapeDrawable(new OvalShape());
        mSteeringDrawable.getPaint().setColor(Color.YELLOW);
    }
    
    private void calculateValues()
    {
    	float w = getWidth();
    	float wd2 = w/2;
    	float h = getHeight();
    	
    	if (w == 0 || h == 0) {
    		Log.e(TAG, "getWidth(); or getHeight(); returned zero");
    		throttle = 0;
    	}
    	else {
    		throttle = (int) (100 * (h - (float) y[0]) / h); 
    		rudder = (int) (100 * ((float) x[0]) / wd2);   		
    		elevator = (int) (100 * (h - (float) y[1]) / h); 
    		aileron = (int) (100 * (float)( x[1] - wd2) / wd2);
    	}
    	
    }
    

    protected void onDraw(Canvas canvas) 
    {    	
    	calculateValues();
    	
    	if (canvas.getWidth() > 0 && canvas.getHeight() > 0) {
    		
    		// Reset the values the first time
    		if (x[0] < 0 && y[0] < 0) {
            	x[0] = (int) getWidth()/4;
            	y[0] = (int) getHeight();
            	x[1] = (int) 3*getWidth()/4;
            	y[1] = (int) getHeight()/2;    
    		}
    	
	    	canvas.drawLine(getWidth()/4, 0, getWidth()/4, getHeight(), bgPaint);
	    	canvas.drawLine(0, getHeight()/2, getWidth(), getHeight()/2, bgPaint);
	    	canvas.drawLine(3*getWidth()/4, 0, 3*getWidth()/4, getHeight(), bgPaint);
	
			canvas.drawLine(0, y[0], canvas.getWidth()/2, y[0], paint);
			mThrottleDrawable.setBounds(x[0]-20, y[0]-20, x[0] + 20, y[0] + 20);
			mThrottleDrawable.draw(canvas);
			
			canvas.drawLine(canvas.getWidth()/2, y[1], canvas.getWidth(), y[1], paint);
			canvas.drawLine(x[1], 0, x[1], canvas.getHeight(), paint);
			mSteeringDrawable.setBounds(x[1]-20, y[1]-20, x[1] + 20, y[1] + 20);
			mSteeringDrawable.draw(canvas);
			
			//canvas.drawRect(throttleRect, paint);
			canvas.drawLine(canvas.getWidth()/2, 0, canvas.getWidth()/2, canvas.getHeight(), borderPaint);
			canvas.drawText("throttle: "+throttle, 10+getWidth()/4, 20, borderPaint);
			canvas.drawText("rudder: "+rudder, 10+getWidth()/4, 40, borderPaint);
			canvas.drawText("aileron: "+aileron, 10+getWidth()/2, 20, borderPaint);
			canvas.drawText("elevator: "+elevator, 10+getWidth()/2, 40, borderPaint);
			canvas.drawText(status, 10+getWidth()/4, getHeight()-20, borderPaint);
    	}	
    }

    public boolean onTouchEvent(MotionEvent mEvent)
    {
    	if (mEvent.getPointerCount() > 1) {
    		Log.d(TAG, "2 pointer events");
    	}
    	
    	for (int i = 0; i < mEvent.getPointerCount(); i++) {
    		if (((int) mEvent.getX(i)) < getWidth()/2) {
	        	x[0] = (int) mEvent.getX(i);
	        	y[0] = (int) mEvent.getY(i);
    		}
    		else {
    			x[1] = (int) mEvent.getX(i);
	        	y[1] = (int) mEvent.getY(i);
    		}
    	}

    	int action = mEvent.getAction();
 	   	int actionCode = action & MotionEvent.ACTION_MASK;
    	if (actionCode == MotionEvent.ACTION_UP && mEvent.getPointerCount() == 1) {		
    		mEvent.getActionIndex();
    		int id = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
    		Log.d(TAG, "LAST UP EVENT:"+id);
        	x[0] = (int) getWidth()/4;
        	y[0] = (int) getHeight();
        	x[1] = (int) 3*getWidth()/4;
        	y[1] = (int) getHeight()/2;        	
    	}
    	
    	invalidate();
		return true;    	
    } 
    
    public void updateStatus(String status)
    {
    	this.status  = status;
    	invalidate();
    }

}
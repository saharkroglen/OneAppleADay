package com.shoogisoft.oneappleaday.controls;

import com.shoogisoft.oneappleaday.MyApplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class RectangleRelativeLayout extends RelativeLayout {

	public RectangleRelativeLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public RectangleRelativeLayout(Context context, AttributeSet attrs) { 
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public RectangleRelativeLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, widthMeasureSpec + 150);
	}

}

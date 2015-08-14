package com.shoogisoft.oneappleaday.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SquareRelativeLayout extends RelativeLayout {

	public SquareRelativeLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public SquareRelativeLayout(Context context, AttributeSet attrs) { 
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public SquareRelativeLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}

}

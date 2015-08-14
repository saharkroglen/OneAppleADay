package com.shoogisoft.oneappleaday.Adapters;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shoogisoft.oneappleaday.R;


public class CategoryAdapter extends BaseAdapter {
	  private final Context mContext;	
	  private List<String> mCategories;

	  
	  public CategoryAdapter(Context context,ArrayList<String> categories) {
	    super();
	    this.mContext = context;
	    mCategories = categories;
	  }

	  @Override
	  public synchronized View getView(int position, View convertView, ViewGroup parent) { 
	    LayoutInflater inflater = (LayoutInflater) mContext .getSystemService(Context.LAYOUT_INFLATER_SERVICE);    
	    ViewGroup mTile1,mTile2 = null;
	    int firstItemInRow = 2 * position;
	    if (convertView == null)
		{
	    	convertView = inflater.inflate(R.layout.row_category, parent, false);
	    	mTile1 = (ViewGroup)convertView.findViewById(R.id.tile1);
	    	mTile1.addView(inflater.inflate(R.layout.item_onceaday_field, parent, false));
	    	
	    	mTile2 = (ViewGroup)convertView.findViewById(R.id.tile2);
//	    	mTile2.addView(inflater.inflate(R.layout.item_onceaday_category, parent, false));
	    	
	    }
	    else
	    {
	    	mTile1 = (ViewGroup)convertView.findViewById(R.id.tile1);
	    	mTile2 = (ViewGroup)convertView.findViewById(R.id.tile2);
	    }
	    
	    if (hasSecondItemInThisRow(firstItemInRow)){
	    	if (mTile2.getChildCount()==0)
	    		mTile2.addView(inflater.inflate(R.layout.item_onceaday_field, parent, false));	    	
	    }	    
    	else
    	{
    		if (mTile2.getChildCount()>0)
	    		mTile2.removeViewAt(0);//(inflater.inflate(R.layout.item_onceaday_category, parent, false));
    	}
    		
	    ImageView categoryImage1 = (ImageView) mTile1.findViewById(R.id.img_category);    
	    TextView categoryName1 = (TextView)mTile1.findViewById(R.id.txtCategoryName);
	    
	    ImageView categoryImage2 = (ImageView) mTile2.findViewById(R.id.img_category);    
	    TextView categoryName2 = (TextView)mTile2.findViewById(R.id.txtCategoryName);
	    	    
	    categoryName1.setText(getItem(firstItemInRow));
	    
	    if (categoryName2 != null)
	    	categoryName2.setText(getItem(firstItemInRow+1));
	    
	    return convertView;
	    
	  }

	private boolean hasSecondItemInThisRow(int firstItemInRow) {
		return mCategories.size() > firstItemInRow + 1;
	}
	
   

	@Override
	public int getCount() {
		return (int) Math.ceil(mCategories.size()/2d);
	}


	@Override
	public String getItem(int position) {		
		return mCategories.get(position);			
	}


	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	
}



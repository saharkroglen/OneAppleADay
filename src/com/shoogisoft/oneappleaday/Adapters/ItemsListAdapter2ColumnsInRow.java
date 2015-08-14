package com.shoogisoft.oneappleaday.Adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import android.R.raw;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.readystatesoftware.viewbadger.BadgeView;
import com.shoogisoft.oneappleaday.R;
import com.shoogisoft.oneappleaday.common.Constants;
import com.shoogisoft.oneappleaday.common.Utils;
import com.shoogisoft.oneappleaday.parse.Fields;
import com.shoogisoft.oneappleaday.pojo.PersistentItem;

public class ItemsListAdapter2ColumnsInRow extends BaseAdapter implements
		OnClickListener {
	private final Context mContext;
	private ArrayList<PersistentItem> mItemList;
	private BadgeView mInvitationBadge;
	ViewGroup mTile1, mTile2;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private OnItemSelectedListener callback;
		
	public interface OnItemSelectedListener {
		void onItemSelected(String position);
	}

	public void setOnItemClickListener(OnItemSelectedListener listener) {
		callback = listener;
	}

	public ItemsListAdapter2ColumnsInRow(Context c,
			ArrayList<PersistentItem> itemsFromPersistency) {
		this.mContext = c;
//		mFieldList = itemsFromPersistency;
//		filterNewItems(itemsFromPersistency);
		mItemList = Utils.filterNewItems(itemsFromPersistency);
		Utils.sortList(mItemList, false) ;
		handleFirst4DaysPlaceholders();
	}
	
//	private void filterNewItems(ArrayList<PersistentItem> items) {	
//		if (mItemList == null)
//			mItemList = new ArrayList<PersistentItem>();
//		for(PersistentItem item: items)
//		{
//			if (!item.IsNew){ 
//				mItemList.add(item);
//			}			
//		}
//		Utils.sortList(mItemList, false) ;
//		
//		handleFirst4DaysPlaceholders();
//	}

	private void handleFirst4DaysPlaceholders() {
		if (mItemList.size()<4)
		{
			if (mItemList.size()==1)
			{
				mItemList.add(new PersistentItem(mContext.getResources().getString(R.string.txt_tomorrow), "", 0, "", false,null,0,0,null));
				mItemList.add(new PersistentItem(mContext.getResources().getString(R.string.txt_day_after_tomorrow), "", 0, "", false,null,0,0,null));
				mItemList.add(new PersistentItem(mContext.getResources().getString(R.string.txt_2days_after_tomorrow), "", 0, "", false,null,0,0,null));
			}
			if (mItemList.size()==2)
			{
				mItemList.add(new PersistentItem(mContext.getResources().getString(R.string.txt_tomorrow), "", 0, "", false,null,0,0,null));
				mItemList.add(new PersistentItem(mContext.getResources().getString(R.string.txt_day_after_tomorrow), "", 0, "", false,null,0,0,null));		
			}
			if (mItemList.size()==3)
			{
				mItemList.add(new PersistentItem(mContext.getResources().getString(R.string.txt_tomorrow), "", 0, "", false,null,0,0,null));
											
			}
		}
	}

	static class ViewHolderItem {
		int itemIndex1;
		TextView itemTitleTile1;
//		TextView itemBodyTile1;
		ViewGroup layoutTile1;
		ImageView imgBackgroundTile1;
		ViewGroup tile1;
		int itemIndex2;
		TextView itemTitleTile2;
//		TextView itemBodyTile2;
		ViewGroup layoutTile2;
		ViewGroup tile2;
		ImageView imgBackgroundTile2;
	}

	@Override
	public synchronized View getView(int position, View convertView,
			ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ViewHolderItem viewHolder;
		int firstItemInRow = 2 * position;
		Log.v(Constants.TAG,"firstItemInRow: " + firstItemInRow);
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.row_category, parent, false);

			viewHolder = new ViewHolderItem();

			initTile1(convertView, parent, inflater, viewHolder,firstItemInRow);
			initTile2(convertView, parent, inflater, viewHolder,firstItemInRow);

			// store the holder with the view.
			convertView.setTag(viewHolder);

		} else {
			// we've just avoided calling findViewById() on resource everytime
			// just use the viewHolder
			viewHolder = (ViewHolderItem) convertView.getTag();
		}
		
		PersistentItem firstObjectInRow = getItem(firstItemInRow);
		viewHolder.itemTitleTile1.setText(firstObjectInRow.Title.replace("\n", " "));
		
		if (firstObjectInRow.ImageUrl != null)
			imageLoader.displayImage(firstObjectInRow.ImageUrl, viewHolder.imgBackgroundTile1,Utils.getImageLoaderOptions());
		else
			viewHolder.imgBackgroundTile1.setImageResource(R.drawable.no_media);
		

		Log.v(Constants.TAG,"set first tile tag: " + firstItemInRow  + " previous: " + mTile1.getTag());
		viewHolder.imgBackgroundTile1.setTag(firstItemInRow);

		if (hasSecondItemInThisRow(firstItemInRow)) {
			int secondItemIndex = firstItemInRow+1;
			PersistentItem secondObjectInRow = getItem(secondItemIndex);
			viewHolder.itemTitleTile2.setVisibility(View.VISIBLE);
			viewHolder.itemTitleTile2.setText(secondObjectInRow.Title.replace("\n", " "));

			if (secondObjectInRow.ImageUrl != null)
				imageLoader.displayImage(secondObjectInRow.ImageUrl, viewHolder.imgBackgroundTile2,Utils.getImageLoaderOptions());
			else
				viewHolder.imgBackgroundTile2.setImageResource(R.drawable.no_media);
			
			viewHolder.layoutTile2.setBackgroundResource(R.drawable.tile_white_selector);
			Log.v(Constants.TAG,"set second tile tag: " + secondItemIndex + " previous: " + mTile2.getTag());
			viewHolder.imgBackgroundTile2.setTag(secondItemIndex);
			
		}
		else
		{
			viewHolder.itemTitleTile2.setText("");
			viewHolder.itemTitleTile2.setVisibility(View.INVISIBLE);
//			viewHolder.itemBodyTile2.setText("");
			viewHolder.layoutTile2.setBackgroundResource(0);
			viewHolder.imgBackgroundTile2.setImageDrawable(null);
			
		}		

		return convertView;
	}
	
	

	private void initTile2(View convertView, ViewGroup parent,
			LayoutInflater inflater, ViewHolderItem viewHolder, int firstItemInRow) {
		
		mTile2 = (ViewGroup) convertView.findViewById(R.id.tile2);
		
		mTile2.addView(inflater.inflate(R.layout.item_multi_items_view,
				parent, false));	
		viewHolder.tile2 = mTile2;
		viewHolder.imgBackgroundTile2 = (ImageView)mTile2.findViewById(R.id.imgBackground);
		viewHolder.imgBackgroundTile2.setOnClickListener(this);
		viewHolder.layoutTile2 = (ViewGroup)mTile2
				.findViewById(R.id.tileShare);
		viewHolder.itemTitleTile2 = (TextView) mTile2
				.findViewById(R.id.txtItemTitle);
//		viewHolder.itemBodyTile2 = (TextView) mTile2
//				.findViewById(R.id.txtItemBody);
	}

	private void initTile1(View convertView, ViewGroup parent,
			LayoutInflater inflater, ViewHolderItem viewHolder, int firstItemInRow) {
		
		mTile1 = (ViewGroup) convertView.findViewById(R.id.tile1);
		
		mTile1.addView(inflater.inflate(R.layout.item_multi_items_view,
				parent, false));		
		viewHolder.tile1 = mTile1;
		viewHolder.imgBackgroundTile1 = (ImageView)mTile1.findViewById(R.id.imgBackground);
		viewHolder.imgBackgroundTile1.setOnClickListener(this);
		viewHolder.layoutTile1 = (ViewGroup)mTile1
				.findViewById(R.id.tileShare);
		viewHolder.itemTitleTile1 = (TextView) mTile1
				.findViewById(R.id.txtItemTitle);			
//		viewHolder.itemBodyTile1 = (TextView) mTile1
//				.findViewById(R.id.txtItemBody);
	}

	private boolean hasSecondItemInThisRow(int firstItemInRow) {
		return mItemList.size() > firstItemInRow + 1;
	}

	@Override
	public int getCount() {
		return (int) Math.ceil(mItemList.size() / 2d);
	}

	@Override
	public PersistentItem getItem(int position) {
		return mItemList.get(position);
	}

	@Override
	public long getItemId(int position) { 
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onClick(View tileView) {
		if (tileView.getTag() != null)
			callback.onItemSelected(tileView.getTag().toString());		
	}	
}

package com.shoogisoft.oneappleaday.Adapters;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.readystatesoftware.viewbadger.BadgeView;
import com.shoogisoft.oneappleaday.R;
import com.shoogisoft.oneappleaday.common.Constants;
import com.shoogisoft.oneappleaday.common.Utils;
import com.shoogisoft.oneappleaday.pojo.PersistentItem;

public class ItemsListAdapter extends BaseAdapter {
	private final Context mContext;
	private List<PersistentItem> mItemList;
	private BadgeView mInvitationBadge;
	private TextView mItemBody;
	private TextView mItemTitle;
	

	public ItemsListAdapter(Context context, List<PersistentItem> items) {
		super();
		this.mContext = context;
//		mItemList = items;
		
		mItemList = Utils.filterNewItems(items);
	}

//	public int getVisibleItems()
//	{
//		return mItemList.size();
//	}
	

	static class ViewHolderItem {
	    TextView itemTitle;
	    TextView itemBody;
	}
	@Override
	public synchronized View getView(int position, View convertView,
			ViewGroup parent) {
		
		 ViewHolderItem viewHolder;
	 
	    if(convertView==null){
	         
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_multi_items_view, parent,false);
	         
	        // set up the ViewHolder
	        viewHolder = new ViewHolderItem();
	        viewHolder.itemTitle = (TextView) convertView.findViewById(R.id.txtItemTitle);
	        viewHolder.itemBody = (TextView) convertView.findViewById(R.id.txtItemBody);
	         
	        // store the holder with the view.
	        convertView.setTag(viewHolder);
	         
	    }else{
	        // we've just avoided calling findViewById() on resource everytime
	        // just use the viewHolder
	        viewHolder = (ViewHolderItem) convertView.getTag();
	    }
	     
	    // object item based on the position
	    PersistentItem itemInPosition = getItem(position);
	   
	    // assign values if the object is not null
	    if(itemInPosition != null) {
	        // get the TextView from the ViewHolder and then set the text (item name) and tag (item ID) values
	        viewHolder.itemTitle.setText(itemInPosition.Title);
	        viewHolder.itemBody.setText(itemInPosition.Body);
	    }
	    convertView.refreshDrawableState();
	    return convertView;

	}

	@Override
	public int getCount() {
		return mItemList.size();
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

}

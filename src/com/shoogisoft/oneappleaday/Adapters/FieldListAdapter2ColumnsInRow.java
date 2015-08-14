package com.shoogisoft.oneappleaday.Adapters;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.readystatesoftware.viewbadger.BadgeView;
import com.shoogisoft.oneappleaday.R;
import com.shoogisoft.oneappleaday.parse.Fields;

public class FieldListAdapter2ColumnsInRow extends BaseAdapter implements OnClickListener {
	private final Context mContext;
	private List<Fields> mFieldList;
	private BadgeView mInvitationBadge;

	private OnFieldSelectedListener callback;
	public interface OnFieldSelectedListener {
	    void onFieldSelected(Fields field);
	}
	public void setOnFieldSelectedListener(OnFieldSelectedListener listener)
	{
		callback = listener;
	}
	
	
	
	public FieldListAdapter2ColumnsInRow(Context context, List<Fields> fieldList) {
		super();
		this.mContext = context;
		mFieldList = fieldList;
	}

	@Override
	public synchronized View getView(int position, View convertView,
			ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup mTile1 = null, mTile2 = null;
		int firstItemInRow = 2 * position;
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.row_category, parent, false);
			mTile1 = (ViewGroup) convertView.findViewById(R.id.tile1);
			mTile1.setOnClickListener(this);
			mTile1.addView(inflater.inflate(R.layout.item_onceaday_field,
					parent, false));

			mTile2 = (ViewGroup) convertView.findViewById(R.id.tile2);
			mTile2.setOnClickListener(this);
			

		} 


		if (hasSecondItemInThisRow(firstItemInRow)) {
			if (mTile2.getChildCount() == 0)
				mTile2.addView(inflater.inflate(
						R.layout.item_onceaday_field, parent, false));
		} else {
			if (mTile2.getChildCount() > 0)
				mTile2.removeViewAt(0);// (inflater.inflate(R.layout.item_onceaday_field,
										// parent, false));
		}

		ImageView fieldImage1 = (ImageView) mTile1
				.findViewById(R.id.img_category);
		TextView fieldName1 = (TextView) mTile1
				.findViewById(R.id.txtCategoryName);

		ImageView fieldImage2 = (ImageView) mTile2
				.findViewById(R.id.img_category);
		TextView fieldName2 = (TextView) mTile2
				.findViewById(R.id.txtCategoryName);

		mTile1.setTag(getItem(firstItemInRow));
		fieldName1.setText(getItem(firstItemInRow).getName());
		mInvitationBadge = new BadgeView(mContext, fieldName1);
		mInvitationBadge.setBackgroundResource(R.drawable.badge_orange);
		mInvitationBadge.setBadgeMargin(10, 10);
		mInvitationBadge.setText("2");
		mInvitationBadge.show();

		if (fieldName2 != null) {
			fieldName2.setText(getItem(firstItemInRow + 1).getName());
//			mTile2.setTag(getItem(firstItemInRow + 1).getObjectId());
			mTile2.setTag(getItem(firstItemInRow + 1));
		}

		return convertView;

	}

	private boolean hasSecondItemInThisRow(int firstItemInRow) {
		return mFieldList.size() > firstItemInRow + 1;
	}

	@Override
	public int getCount() {
		return (int) Math.ceil(mFieldList.size() / 2d);
	}

	@Override
	public Fields getItem(int position) {
		return mFieldList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void onClick(View tileView) {
		callback.onFieldSelected((Fields) tileView.getTag());		
	}

}

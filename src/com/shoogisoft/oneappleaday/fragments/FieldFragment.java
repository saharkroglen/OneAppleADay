package com.shoogisoft.oneappleaday.fragments;

import java.util.ArrayList;
import java.util.List;
import com.achep.header2actionbar.HeaderFragment;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.shoogisoft.oneappleaday.MainActivity;
import com.shoogisoft.oneappleaday.MyApplication;
import com.shoogisoft.oneappleaday.R;
import com.shoogisoft.oneappleaday.Adapters.ItemsListAdapter;
import com.shoogisoft.oneappleaday.Adapters.ItemsListAdapter2ColumnsInRow;
import com.shoogisoft.oneappleaday.Adapters.ItemsListAdapter2ColumnsInRow.OnItemSelectedListener;
import com.shoogisoft.oneappleaday.common.Constants;
import com.shoogisoft.oneappleaday.common.InternalMessage;
import com.shoogisoft.oneappleaday.common.ItemsPrefs;
import com.shoogisoft.oneappleaday.common.Utils;
import com.shoogisoft.oneappleaday.common.Utils.spinnerType;
import com.shoogisoft.oneappleaday.parse.Fields;
import com.shoogisoft.oneappleaday.pojo.PersistentItem;
import com.shoogisoft.oneappleaday.receivers.SyncReceiver;

public class FieldFragment extends HeaderFragment implements OnClickListener {

	/**
	 * The fragment argument representing the section number for this fragment.
	 */

	FrameLayout mContentOverlay;
	private ListView mItemsGrid;
	private Fields mField;
	private TextView mHeaderTitle;
	private ArrayList<PersistentItem> mFilteredItemList;
	private String mName;
	private String mFieldID;
	private ItemsListAdapter2ColumnsInRow mItemsListAdapter;
	
//	public static FieldFragment newInstance(int index) {
//		FieldFragment f = new FieldFragment();
//        Bundle args = new Bundle();
//        args.putInt("index", index);
//        f.setArguments(args);
//        return f;
//    }


	public FieldFragment() {

	}

	@Override
	public void onDetach() {
		super.onDetach();

	}

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);

		Bundle args = getArguments();
		if (args == null) {
			Log.e(Constants.TAG,
					"Field fragment opened without field arguments - required fieldID");
		} else {
//			mField = getArguments().getParcelable(Constants.ARG_FIELD);
			mFieldID = getArguments().getString(Constants.ARG_FIELD_ID);
			mName = getArguments().getString(Constants.ARG_FIELD_NAME);
			
			mFilteredItemList = getArguments().getParcelableArrayList(Constants.ARG_FILTERED_ITEM_LIST);
		}

		setHeaderBackgroundScrollMode(HEADER_BACKGROUND_SCROLL_PARALLAX);
		setOnHeaderScrollChangedListener(new OnHeaderScrollChangedListener() {
			@Override
			public void onHeaderScrollChanged(float progress, int height,
					int scroll) {
				if (getActivity() == null)
					return;
				height -= getActivity().getActionBar().getHeight();

				progress = (float) scroll / height;
				if (progress > 1f)
					progress = 1f;

				progress = (1 - (float) Math.cos(progress * Math.PI)) * 0.5f;

				((MainActivity) getActivity()).getFadingActionBarHelper()
						.setActionBarAlpha((int) (255 * progress));
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tileShare:
			// ((MainActivity) mContext).openPost();
			break;
		}
	}

	@Override
	public View onCreateContentOverlayView(LayoutInflater inflater,
			ViewGroup container) {
		ProgressBar progressBar = new ProgressBar(getActivity());
		mContentOverlay = new FrameLayout(getActivity());
		mContentOverlay.addView(progressBar, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		// if (mLoaded) mContentOverlay.setVisibility(View.GONE);
		mContentOverlay.setVisibility(View.GONE);
		return mContentOverlay;
	}

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);

		new SyncReceiver().setAlarm(getActivity());
		mItemsGrid = (ListView) rootView.findViewById(R.id.itemsGrid);
		

		// getItemsFromServer();

//		List<PersistentItem> itemsFromPersistency = new ItemsPrefs().getItemList();
		if (mFilteredItemList != null) {
			mItemsListAdapter = new ItemsListAdapter2ColumnsInRow(getActivity(), mFilteredItemList);
			mItemsListAdapter.setOnItemClickListener(new OnItemSelectedListener() {
				
				@Override
				public void onItemSelected(String itemPosition) {
					if (mFilteredItemList.size() > Integer.valueOf(itemPosition))
						Utils.sendMessage(getActivity(), new InternalMessage(
							InternalMessage.MESSAGE_OPEN_ITEM_PAGER_FRAGMENT, itemPosition));
				}
			});
			setWelcomeMessageVisibility(mItemsListAdapter.getCount() > 0);
			
		}
		else
		{
			setWelcomeMessageVisibility(false);
		}
		
		// itemPrefs.setIsNewState(false);

//		Utils.setCustomFontToViewGroup(rootView, Utils.FONT_OSWALD_REGULAR);
		return rootView;

	}

	private void setWelcomeMessageVisibility(boolean hide) {
		if (hide)
		{
			Utils.sendMessage(getActivity(),new InternalMessage(InternalMessage.MESSAGE_SET_WELCOME_MESSAGE_VISIBILITY,String.valueOf(false)));
			Utils.showSpinner(MyApplication.getContext(), false,spinnerType.largeWithoutBackground);
		}
		else
		{
			Utils.sendMessage(getActivity(),new InternalMessage(InternalMessage.MESSAGE_SET_WELCOME_MESSAGE_VISIBILITY,String.valueOf(true)));
			
		}
	}

	
	@Override
	public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container) {
		View headerRoot = inflater.inflate(R.layout.fragment_header, container,
				false);
//		mHeaderTitle = (TextView) headerRoot.findViewById(R.id.txtHeaderTitle);
//		mHeaderTitle.setText(mName);
		return headerRoot;

	}

	@Override
	public void onSetAdapter() {
		mItemsGrid.setAdapter(mItemsListAdapter);		
	}

}

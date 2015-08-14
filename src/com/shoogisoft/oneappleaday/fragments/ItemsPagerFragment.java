package com.shoogisoft.oneappleaday.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shoogisoft.oneappleaday.MyApplication;
import com.shoogisoft.oneappleaday.R;
import com.shoogisoft.oneappleaday.Adapters.ItemsPagerAdapter;
import com.shoogisoft.oneappleaday.Adapters.ItemsPagerAdapter.OnVoteListener;
import com.shoogisoft.oneappleaday.common.Constants;
import com.shoogisoft.oneappleaday.common.ItemViewPrefs;
import com.shoogisoft.oneappleaday.common.ItemsPrefs;
import com.shoogisoft.oneappleaday.common.Utils;
import com.shoogisoft.oneappleaday.common.Utils.spinnerType;
import com.shoogisoft.oneappleaday.parse.Items;
import com.shoogisoft.oneappleaday.parse.Items.OnQueryDone;
import com.shoogisoft.oneappleaday.pojo.PersistentItem;
import com.shoogisoft.oneappleaday.receivers.SyncReceiver;

public class ItemsPagerFragment extends Fragment {

	private ViewPager mViewPager;
	private int mSelectedPosition;
	private ItemsPagerAdapter mItemsPagerAdapter;
	private List<PersistentItem> itemsFromPersistency;
	private static boolean ignoreItemViewEvents = false; //used to ignore view event triggers when re-binding the pager adapter for the charts to appear once vote buttons are clicks

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);

		Bundle args = getArguments();
		if (args == null) {
			Log.e(Constants.TAG,
					"ItemsPagerFragment opened without selected item position, default to 0");
			mSelectedPosition = 0;
		} else {
			mSelectedPosition = getArguments().getInt(
					Constants.ARG_ITEM_POSITION);

		}
	}

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_items_pager, container, false);

		mViewPager = (ViewPager) rootView.findViewById(R.id.itemsPager);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@SuppressLint("NewApi")
			@Override
			public void onPageSelected(int arg0) {
				if (!ignoreItemViewEvents) {
					PersistentItem item = mItemsPagerAdapter
							.getItemAtPosition(arg0);

					ItemViewPrefs.setItemView(item.ObjectID, item.ItemID);
				}
				ignoreItemViewEvents = false;
			}

			@SuppressLint("NewApi")
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				int bitmapColorAverage = mItemsPagerAdapter
						.getItemAverageColor(arg0);
				Utils.setNotificationBarColor(bitmapColorAverage, getActivity());
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		itemsFromPersistency = (List<PersistentItem>) new ItemsPrefs()
				.getItemList().clone();
		// Collections.reverse(itemsFromPersistency);// reverse order to allow
		// right to left swiping
		if (itemsFromPersistency != null) {
			mItemsPagerAdapter = new ItemsPagerAdapter(getActivity(),
					itemsFromPersistency);
			mViewPager.setAdapter(mItemsPagerAdapter);
			mItemsPagerAdapter.setOnVoteListener(new OnVoteListener() {

				@Override
				public void onVote(final boolean knewIt) {
					Utils.showSpinner(MyApplication.getContext(), true,spinnerType.largeWithoutBackground);
					int selectedPagerPosition = mViewPager.getCurrentItem();
					PersistentItem item = mItemsPagerAdapter
							.getItemAtPosition(selectedPagerPosition);
					Items.getItems(new OnQueryDone() {
						
						@Override
						public void onItemsQueryResult(List<Items> itemList) {
							updateVotingDataFromServer(itemList);
							int selectedPagerPosition = updateMyVote(knewIt);
							mViewPager.setAdapter(mItemsPagerAdapter); // re-bind adapter to toggle votePanel visibility
							ignoreItemViewEvents = true;
							mViewPager.setCurrentItem(selectedPagerPosition);
							Utils.showSpinner(MyApplication.getContext(), false,spinnerType.largeWithoutBackground);
							
						}

						private void updateVotingDataFromServer(
								List<Items> itemList) {
							if (itemList.size()>0)
							{
								new SyncReceiver().UpdateSingleItemFromServer(itemList.get(0));
							}
						}
					}, item.ObjectID);
					
					

				}

				private int updateMyVote(boolean knewIt) {
					int selectedPagerPosition = mViewPager.getCurrentItem();
					PersistentItem item = mItemsPagerAdapter
							.getItemAtPosition(selectedPagerPosition);
					ItemsPrefs itemPrefs = new ItemsPrefs();
					HashMap<Integer, PersistentItem> itemsMap = itemPrefs
							.getItemMap();
					if (knewIt)
						itemsMap.get(item.ItemID).knew++;
					else
						itemsMap.get(item.ItemID).didntKnow++;

					itemPrefs.setItemList(new ArrayList<PersistentItem>(
							itemsMap.values()));
					return selectedPagerPosition;
				}
			});
		}

		mViewPager.setCurrentItem(Integer.valueOf(mSelectedPosition));

		Utils.showSpinner(MyApplication.getContext(), false,
				spinnerType.largeWithoutBackground);
		return rootView;
	}

	public int getCurrentPageIndex() {
		// return itemsFromPersistency.get(mViewPager.getCurrentItem());
		return mViewPager.getCurrentItem();
	}
	//
	// /**
	// * Detects left and right swipes across a view.
	// */
	// public class OnSwipeTouchListener implements OnTouchListener {
	//
	// private final GestureDetector gestureDetector;
	//
	// public OnSwipeTouchListener(Context context) {
	// gestureDetector = new GestureDetector(context,
	// new GestureListener());
	// }
	//
	// public void onSwipeLeft() {
	// }
	//
	// public void onSwipeRight() {
	// }
	//
	// public boolean onTouch(View v, MotionEvent event) {
	// return gestureDetector.onTouchEvent(event);
	// }

	// private final class GestureListener extends SimpleOnGestureListener {
	//
	// private static final int SWIPE_DISTANCE_THRESHOLD = 100;
	// private static final int SWIPE_VELOCITY_THRESHOLD = 100;
	//
	// // @Override
	// // public boolean onFling(MotionEvent e1, MotionEvent e2, float
	// // velocityX, float velocityY) {
	// // float distanceX = e2.getX() - e1.getX();
	// // float distanceY = e2.getY() - e1.getY();
	// // if (Math.abs(distanceX) > Math.abs(distanceY) &&
	// // Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD &&
	// // Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
	// // if (distanceX > 0)
	// // onSwipeRight();
	// // else
	// // onSwipeLeft();
	// // return true;
	// // }
	// // return false;
	// // }
	// private final int GESTURE_THRESHOULD = 50;
	// private final int GESTURE_VELOCITY_THRESHOULD = 50;
	//
	// protected MotionEvent mLastOnDownEvent = null;
	//
	// @Override
	// public boolean onDown(MotionEvent e) {
	// mLastOnDownEvent = e;
	// return super.onDown(e);
	// }
	//
	//
	// @Override
	// public boolean onFling(MotionEvent event1, MotionEvent event2,
	// float velocityx, float velocityy) {
	//
	// if (event1==null)
	// event1 = mLastOnDownEvent;
	// if (event1==null || event2==null)
	// return false;
	// float dX = event2.getX()-event1.getX();
	// float dY = event2.getY()-event1.getY();
	// try {
	// float diffx = event2.getX() - event1.getX();
	// float diffy = event2.getY() - event1.getY();
	//
	// if (Math.abs(diffx) > Math.abs(diffy)) {
	// if (Math.abs(diffx) > GESTURE_THRESHOULD
	// && Math.abs(velocityx) > GESTURE_VELOCITY_THRESHOULD) {
	// if (diffx > 0) {
	// onSwipeRight();
	// } else {
	// onSwipeLeft();
	// }
	// }
	// } else {
	// if (Math.abs(diffy) > GESTURE_THRESHOULD
	// && Math.abs(velocityy) > GESTURE_VELOCITY_THRESHOULD) {
	// if (diffy > 0) {
	// onSwipeBottom();
	// } else {
	// onSwipeTop();
	// }
	// }
	// }
	// } catch (Exception e) {
	// Log.d(Constants.TAG, "" + e.getMessage());
	// }
	// return false;
	// }
	//
	// public void onSwipeRight() {
	// // Toast.makeText(this.getClass().get, "top",
	// // Toast.LENGTH_SHORT).show();
	// Log.i(Constants.TAG, "Right");
	// }
	//
	// public void onSwipeLeft() {
	// Log.i(Constants.TAG, "Left");
	// // Toast.makeText(MyActivity.this, "top",
	// // Toast.LENGTH_SHORT).show();
	// Utils.sendMessage(getActivity(), new InternalMessage(
	// InternalMessage.MESSAGE_OPEN_FIELD_FRAGMENT, null));
	// }
	//
	// public void onSwipeTop() {
	// Log.i(Constants.TAG, "Top");
	// // Toast.makeText(MyActivity.this, "top",
	// // Toast.LENGTH_SHORT).show();
	// }
	//
	// public void onSwipeBottom() {
	// Log.i(Constants.TAG, "Bottom");
	// // Toast.makeText(MyActivity.this, "top",
	// // Toast.LENGTH_SHORT).show();
	// }
	//
	// }
	// }
}

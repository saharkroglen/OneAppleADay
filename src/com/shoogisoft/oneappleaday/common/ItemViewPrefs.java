package com.shoogisoft.oneappleaday.common;

import com.shoogisoft.oneappleaday.MyApplication;
import com.shoogisoft.oneappleaday.common.GoogleAnalyticsAdapter.ItemView;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ItemViewPrefs {

	public static int numberOfItemViews(String itemObjectID) {
		SharedPreferences itemViewPrefs = Utils
				.getSharedPrefs(Constants.SHARED_PREFS_ITEM_VIEW);
		return itemViewPrefs.getInt(itemObjectID, 0);
	}

	public static void setItemView(String itemObjectID, int itemID) {
		SharedPreferences itemViewPrefs = Utils
				.getSharedPrefs(Constants.SHARED_PREFS_ITEM_VIEW);
		Editor editor = itemViewPrefs.edit();
		int viewCount = itemViewPrefs.getInt(itemObjectID, 0) + 1;
		editor.putInt(itemObjectID, viewCount);
		editor.commit();

		if (viewCount <= 1)
			GoogleAnalyticsAdapter.sendAnalyticsEvent(
					MyApplication.getContext(), ItemView.CATEGORY,
					ItemView.FIRST_VIEW,String.valueOf(itemID));
		else
			GoogleAnalyticsAdapter.sendAnalyticsEvent(
					MyApplication.getContext(), ItemView.CATEGORY,
					ItemView.SECOND_OR_MORE_VIEWS,String.valueOf(itemID));
	}

}

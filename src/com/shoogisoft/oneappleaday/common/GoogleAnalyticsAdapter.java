package com.shoogisoft.oneappleaday.common;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
//import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class GoogleAnalyticsAdapter {

	public static void startActivity(Activity activity) {
		EasyTracker.getInstance(activity).activityStart(activity);
		// Log.d(Constants.TAG + ":GoogleAnalyticsAdapter",
		// activity.getLocalClassName() + " started");
	}

	public static void stopActivity(Activity activity) {
		EasyTracker.getInstance(activity).activityStop(activity);
		// Log.d(Constants.TAG + ":GoogleAnalyticsAdapter",
		// activity.getLocalClassName() + " stopped");
	}

	public static void sendAnalyticsEvent(Context ctx, String category,
			String event,String label) {
		EasyTracker.getInstance(ctx).send(
				MapBuilder.createEvent(category, event, label, null).build());
		 Log.d(Constants.TAG , "GoogleAnalyticsAdapter: " + event + ", Label: " + label);
	}

	public static class General {
		public static final String CATEGORY = "general_events";

		public static final String FIRST_LAUNCH = "first_launch";
		public static final String REFERRER_INSTALLATION = "referrer_installation";
	}
	
	public static class Vote {
		public static final String CATEGORY = "voting_events";

		public static final String VOTE_KNEW = "vote_knew";
		public static final String VOTE_DIDNT_KNOW = "vote_didnt_know";	
	}
	
	public static class Share {
		public static final String CATEGORY = "share_events";

		public static final String SHARE_TOP_BUTTON = "share_top_button";
		public static final String SHARE_SIDE_BUTTON = "share_side_button";
		
		public static final String SHARE_TOP_BUTTON_FACEBOOK = "share_top_button_facebook";		
		public static final String SHARE_TOP_BUTTON_WHATSAPP = "share_top_button_whatsapp";		
		public static final String SHARE_TOP_BUTTON_MORE_APPS = "share_top_button_more_apps";	
		public static final String SHARE_SIDE_BUTTON_FACEBOOK = "share_side_button_facebook";		
		public static final String SHARE_SIDE_BUTTON_WHATSAPP = "share_side_button_whatsapp";		
		public static final String SHARE_SIDE_BUTTON_MORE_APPS = "share_side_button_more_apps";

	}
	
	public static class ItemView {
		public static final String CATEGORY = "items_events";

		public static final String FIRST_VIEW = "first_view";
		public static final String SECOND_OR_MORE_VIEWS = "second_or_more_views";		
		public static final String PUSH_NOTIFICATION = "push_notification";		
		public static final String OPEN_ITEM_FROM_PUSH_NOTIFICATION = "open_item_from_push_notification";
	}
}

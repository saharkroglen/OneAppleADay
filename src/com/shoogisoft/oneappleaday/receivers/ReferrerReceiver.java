package com.shoogisoft.oneappleaday.receivers;

import java.net.URLDecoder;

import com.shoogisoft.oneappleaday.common.Constants;
import com.shoogisoft.oneappleaday.common.GoogleAnalyticsAdapter;
import com.shoogisoft.oneappleaday.common.Utils;
import com.shoogisoft.oneappleaday.common.GoogleAnalyticsAdapter.General;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ReferrerReceiver extends BroadcastReceiver {
	String referrer;
	
	@Override
	public void onReceive(Context context, Intent intent) {
//		Utils.initiateParse(context, Prefs.isProd());		
		Log.d(Constants.TAG, "on receive referrer"); 
		if (intent != null)
			Log.d("REFERRER", "action= " + intent.getAction());

		try {
			// Make sure this is the intent we expect
			if ((null != intent)
					&& (intent.getAction()
							.equals("com.android.vending.INSTALL_REFERRER"))) {
				// This intent should have a referrer string attached to it. 
				String rawReferrer = intent.getStringExtra("referrer");
				if (null != rawReferrer) {
					// The string is usually URL Encoded, so we need to decode it.
					String referrer = URLDecoder.decode(rawReferrer, "UTF-8");

					// Log the referrer string.
					Log.v(Constants.TAG,
							"ReferrerReceiver.onReceive: Raw referrer: " + rawReferrer + ", Decoded Referrer: " + referrer);
					
					GoogleAnalyticsAdapter.sendAnalyticsEvent(context, General.CATEGORY, General.REFERRER_INSTALLATION,referrer);
				}
			}
		} catch (Exception e) {
			Log.e(Constants.TAG, e.toString());
		}
	}
}
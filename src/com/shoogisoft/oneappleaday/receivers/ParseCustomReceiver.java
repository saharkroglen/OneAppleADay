package com.shoogisoft.oneappleaday.receivers;

import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ParseCustomReceiver extends BroadcastReceiver {
private static final String TAG = "ParseCustomReceiver";
public static String ACTION_INVITE_PROMOTER = "action_invite_promoter";
public static String EXTRA_JSON = "extra_json";
public static String FIELD_CAMPAIGN_ID = "campaignid";
public static String FIELD_MERCHANT_NAME = "merchentName";

	@Override
	public void onReceive(Context context, Intent intent) {

//	      String action = intent.getAction();
//	      String channel = intent.getExtras().getString("com.parse.Channel");
//
//	      
//	      String json = intent.getExtras().getString("com.parse.Data");
//	      Log.d(TAG, "received push notification:\n " + json );
//	      if (action.equals(ACTION_INVITE_PROMOTER))
//	      {
//	    	  InvitationBroadcastHandler ih = new InvitationBroadcastHandler(context, json);
//	      }
	}
}
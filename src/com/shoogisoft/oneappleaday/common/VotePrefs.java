package com.shoogisoft.oneappleaday.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
//import com.google.gson.Gson;
import com.shoogisoft.oneappleaday.MyApplication;
import com.shoogisoft.oneappleaday.parse.Fields;
import com.shoogisoft.oneappleaday.pojo.PersistentItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class VotePrefs {

	public static String ITEM_VOTE_PREFIX = "item_vote_";
	public static boolean didVoteAlready(String itemObjectID) {
		SharedPreferences settings = Utils.getSharedPrefs(Constants.SHARED_PREFS_VOTE);
		return settings.contains(ITEM_VOTE_PREFIX + itemObjectID);
	}
	

	public static void vote(String itemObjectID,boolean knewIt)
	{
		SharedPreferences settings = Utils.getSharedPrefs(Constants.SHARED_PREFS_VOTE);
		Editor editor = settings.edit();		
		editor.putBoolean(ITEM_VOTE_PREFIX + itemObjectID, knewIt);
		editor.commit();
	}
}

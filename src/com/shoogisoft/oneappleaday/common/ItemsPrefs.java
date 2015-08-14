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

public class ItemsPrefs {

	private static ArrayList<PersistentItem> mPersistentItemList;
	private static HashMap<Integer, PersistentItem> mPersistentItemMap;
	
	public ArrayList<PersistentItem> getItemList()
	{
		loadPrefs();
		return mPersistentItemList ;
	}
	
	/**
	 * change the isNew State of the whole persistent list.
	 * saves to prefs afterwards
	 * @param isNew
	 * @param daysCountSinceFeedStart = enter -1 to affect all items, otherwise to affect up to specific index 
	 */
	public void updateItemsStateAccordingToFeedStartDate(long daysCountSinceFeedStart , boolean enforceSyncingAllDevicesByStartDate)
	{
		boolean isDirty = false;
		for(PersistentItem item : mPersistentItemList)
		{
			if (item.ItemID <= daysCountSinceFeedStart || daysCountSinceFeedStart == -1)
			{
				if (item.IsNew)
				{
					isDirty = true;
					item.IsNew = false; 
				}
				
			}
			else if (enforceSyncingAllDevicesByStartDate)
			{
				if (!item.IsNew)
				{
					isDirty = true;
					item.IsNew = true;
				}
			}
		}
		if (isDirty)
			saveToPrefs();
	}
	public HashMap<Integer,PersistentItem> getItemMap() 
	{
		loadPrefs();
		if(mPersistentItemMap == null)
		{
			mPersistentItemMap = new HashMap<Integer,PersistentItem>();
			List<PersistentItem> list = getItemList();
			if (list == null)
				return null;
			for(PersistentItem item : list)
			{
				mPersistentItemMap.put(item.ItemID, item);
			}
		}
		
		return mPersistentItemMap;
	}
	
	public void setItemList(ArrayList<PersistentItem> list)
	{
		if (list.size()==0)
			return;
		mPersistentItemList = list;
		Utils.sortList(mPersistentItemList, false);
		saveToPrefs();
	}
	
	public void saveToPrefs()
	{
		if (mPersistentItemList == null)
			return;
		SharedPreferences settings;
		Editor editor;

		Context context = MyApplication.getContext();
		settings = context.getSharedPreferences(Constants.SHARED_PREFS_GENERAL,Context.MODE_PRIVATE);
		editor = settings.edit();

		Gson gson = new Gson();		
		String itemList = gson.toJson(mPersistentItemList);
		editor.putString(Constants.PREFS_ITEM_LIST, itemList);
		editor.commit();
		
		mPersistentItemMap = null;
	}
	
	private void loadPrefs()
	{
		if (mPersistentItemList == null)
		{
			SharedPreferences settings;
			List<PersistentItem> itemList;
			Context context = MyApplication.getContext();
			settings = context.getSharedPreferences(Constants.SHARED_PREFS_GENERAL,
					Context.MODE_PRIVATE);
	
			if (settings.contains(Constants.PREFS_ITEM_LIST)) {
				String json = settings.getString(Constants.PREFS_ITEM_LIST, null);
			
				Gson gson = new Gson();
				PersistentItem[] itemArray = gson.fromJson(json,PersistentItem[].class);
	
				itemList = Arrays.asList(itemArray);
				mPersistentItemList = new ArrayList<PersistentItem>(itemList);
//				Utils.sortList(mPersistentItemList, false);
			} 
		}
	}
}

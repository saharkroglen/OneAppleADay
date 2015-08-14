package com.shoogisoft.oneappleaday.parse;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.shoogisoft.oneappleaday.common.Constants;
import com.shoogisoft.oneappleaday.common.VotePrefs;

/**
 * this is an parse object class representation called "Items"
 * 
 * @author Sahar
 * 
 */
@ParseClassName("Items")
public class Items extends ParseObject {

	private static final String COLUMN_ITEM_TITLE = "itemTitle";
	private static final String COLUMN_ITEM_BODY = "itemBody";
	private static final String COLUMN_ITEM_ID = "itemID";
	private static final String COLUMN_PRODUCTION_READY = "productionReady";
	private static final String COLUMN_FIELD = "field";
	private static final String COLUMN_ID = "objectId";
	private static final String COLUMN_IMAGE = "backgroundImage";
	private static final String COLUMN_KNEW = "knew";
	private static final String COLUMN_ITEM_COLOR = "itemColor";
	private static final String COLUMN_DIDNT_KNOW = "didntKnow";
	OnQueryDone resultsCallback;

	public interface OnQueryDone {
		void onItemsQueryResult(List<Items> itemList);
	}

	public static void iKnew(final String itemObjectID) {
		ParseQuery<Items> mainQuery = ParseQuery.getQuery(Items.class);

		// Retrieve the object by id
		mainQuery.getInBackground(itemObjectID, new GetCallback<Items>() {

			@Override
			public void done(Items item, ParseException arg1) {
				if (item != null)
				{
					item.increment(COLUMN_KNEW);
					item.saveInBackground();
					
				}

			}
		});
		VotePrefs.vote(itemObjectID, true);
	}

	public static void iDidntKnow(final String itemObjectID) {
		ParseQuery<Items> mainQuery = ParseQuery.getQuery(Items.class);

		// Retrieve the object by id
		mainQuery.getInBackground(itemObjectID, new GetCallback<Items>() {

			@Override
			public void done(Items item, ParseException arg1) {
				if (item != null)
				{
					item.increment(COLUMN_DIDNT_KNOW);
					item.saveInBackground();
					
				}

			}
		});
		VotePrefs.vote(itemObjectID, false);
	}
	

	public String getTitle() {
		return getString(COLUMN_ITEM_TITLE);
	}

	public String getBody() {
		return getString(COLUMN_ITEM_BODY);
	}

	public int getItemID() {
		return getInt(COLUMN_ITEM_ID);
	}
	public int getKnewCount() {
		return getInt(COLUMN_KNEW);
	}
	public String getItemColor() {
		return getString(COLUMN_ITEM_COLOR);
	}
	public int getDidntKnowCount() {
		return getInt(COLUMN_DIDNT_KNOW);
	}

	public ParseFile getImage() {
		ParseFile image = getParseFile(COLUMN_IMAGE);
		return image;
	}

	public String getObjectID() {
		return getObjectId();
	}

	void fireItemsResult(List<Items> items) {
		resultsCallback.onItemsQueryResult(items);
	}

	public static void getServerItemsForField(String fieldID,
			final OnQueryDone doneCallback) {
		ParseQuery<Fields> innerQuery = ParseQuery.getQuery(Fields.class);
		innerQuery.whereEqualTo(Fields.COLUMN_OBJECT_ID, fieldID);

		ParseQuery<Items> query = ParseQuery.getQuery(Items.class);
		query.setLimit(500);
		query.whereEqualTo(COLUMN_PRODUCTION_READY, true);	
		query.orderByDescending(COLUMN_ITEM_ID);			
		
		query.include("Fields"/*.class.toString()*/); //join query with Fields class
		// query.whereExists("merchantID");
		query.whereMatchesQuery(COLUMN_FIELD, innerQuery);
		query.findInBackground(new FindCallback<Items>() {
			public void done(List<Items> itemList, ParseException e) {
				doneCallback.onItemsQueryResult(itemList);
			}
		});
	}

	public static void getItems(final OnQueryDone doneCallback,String itemObjectID) {
		ParseQuery<Items> mainQuery = ParseQuery.getQuery(Items.class);
		if (itemObjectID != null)
		{
			mainQuery.whereEqualTo(Items.COLUMN_ID, itemObjectID);
		}
		mainQuery.findInBackground(new FindCallback<Items>() {

			@Override
			public void done(List<Items> list, ParseException e) {
				if (e == null) {
					doneCallback.onItemsQueryResult(list);
				} else {
					Log.d(Constants.TAG, "Error: " + e.getMessage());
				}
			}
		});
	}

}

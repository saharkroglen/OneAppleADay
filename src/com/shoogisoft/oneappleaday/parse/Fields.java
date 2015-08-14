package com.shoogisoft.oneappleaday.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.shoogisoft.oneappleaday.MyApplication;
import com.shoogisoft.oneappleaday.common.Constants;
import com.shoogisoft.oneappleaday.pojo.PersistentItem;

@ParseClassName("Fields")
public class Fields extends ParseObject implements Parcelable {

	private static final String COLUMN_FIELD_NAME = "fieldName";
	private static final String COLUMN_FIELD_ID = "fieldID";
	public static final String COLUMN_OBJECT_ID = "objectId";
	public static final String COLUMN_START_DATE = "startDate";
	private OnQueryDone resultsCallback;

	public interface OnQueryDone {
		void onFieldsQueryResult(List<Fields> fieldList);
	}

	public Fields()
	{
		
	}
	
	public Date getStartDate() {
		return getDate(COLUMN_START_DATE);
	}
	public void setStartDate(Date startDate) {
		this.put(COLUMN_START_DATE, startDate);
	}
	
	public String getName() {
		return getString(COLUMN_FIELD_NAME);
	}

	public void setName(String name) {
		this.put(COLUMN_FIELD_NAME, name);
	}

	public void setFieldID(int id) {
		this.put(COLUMN_FIELD_ID, id);
	}

	public int getFieldID() {
		return getInt(COLUMN_FIELD_ID);
	}

	public void setObjectID(String objectID) {
		this.put(COLUMN_OBJECT_ID, objectID);
	}

	void fireFieldsIDsResult(List<Fields> Fieldss) {
		resultsCallback.onFieldsQueryResult(Fieldss);
	}

	public static void getFieldsForMerchant(String merchantID,
			final OnQueryDone doneCallback) {

		ParseQuery<Fields> mainQuery = ParseQuery.getQuery(Fields.class);
		mainQuery.whereEqualTo(COLUMN_FIELD_ID, merchantID);
		mainQuery.findInBackground(new FindCallback<Fields>() {

			@Override
			public void done(List<Fields> list, ParseException e) {
				if (e == null) {
					doneCallback.onFieldsQueryResult(list);
				} else {
					Log.d(Constants.TAG, "Error: " + e.getMessage());
				}
			}
		});
	}

	public static void getFields(final OnQueryDone doneCallback) {
		ParseQuery<Fields> mainQuery = ParseQuery.getQuery(Fields.class);
		mainQuery.findInBackground(new FindCallback<Fields>() {

			@Override
			public void done(List<Fields> list, ParseException e) {
				if (e == null) {
					doneCallback.onFieldsQueryResult(list);
				} else {
					Log.d(Constants.TAG, "Error: " + e.getMessage());
				}
			}
		});
	}

	public static Fields getFieldByID(String fieldID) {
		Fields field = null;
//		field = getFieldFromPrefs(fieldID);
//		if (field == null)
//		{
			ParseQuery<Fields> mainQuery = ParseQuery.getQuery(Fields.class);
			mainQuery.whereEqualTo(COLUMN_OBJECT_ID, fieldID);
			List<Fields> fieldList = null;
			try {
				fieldList = mainQuery.find();
	
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (fieldList.size() > 0)
			{
				field = fieldList.get(0);
//				saveToPrefs(field.getObjectId(), field);
				return field;
			}
			else
				return null;
//		}
//		else
//			return field;
	}

//	public static void saveToPrefs(String fieldID, Fields field) {
//
//		SharedPreferences settings;
//		Editor editor;
//
//		Context context = MyApplication.getContext();
//		settings = context.getSharedPreferences(Constants.SHARED_PREFS_NAME,
//				Context.MODE_PRIVATE);
//		editor = settings.edit();
//
//		Gson gson = new Gson();
//		String fieldJson = gson.toJson(field);
//		editor.putString(fieldID, fieldJson);
//		editor.commit();
//	}
//
//	public static Fields getFieldFromPrefs(String fieldID) {
//
//		SharedPreferences settings;
//		List<PersistentItem> itemList;
//		Context context = MyApplication.getContext();
//		settings = context.getSharedPreferences(Constants.SHARED_PREFS_NAME,
//				Context.MODE_PRIVATE);
//
//		String json = settings.getString(fieldID, null);
//		if (json == null)
//			return null;
//
//		Gson gson = new Gson();
//		Fields field = null;
//		try
//		{
//			field = gson.fromJson(json, Fields.class);
//		}
//		catch (Exception ex)
//		{
//			//TODO print this exception to bug sense
//			return null;
//		}
//
//		return field;
//	}

	public static void getFieldss(List<String> FieldsIDs,
			final OnQueryDone doneCallback) {
		final ArrayList<Fields> Fieldss = new ArrayList<Fields>();

		if (FieldsIDs.size() == 0) {
			doneCallback.onFieldsQueryResult(null);
			return;
		}

		List<ParseQuery<Fields>> queries = new ArrayList<ParseQuery<Fields>>();
		for (String id : FieldsIDs) {
			ParseQuery<Fields> query = ParseQuery.getQuery(Fields.class);
			query.whereEqualTo(COLUMN_OBJECT_ID, id);
			queries.add(query);
		}
		ParseQuery<Fields> mainQuery = ParseQuery.or(queries);
		mainQuery.findInBackground(new FindCallback<Fields>() {

			@Override
			public void done(List<Fields> list, ParseException e) {
				if (e == null) {
					doneCallback.onFieldsQueryResult(list);
				} else {
					Log.d(Constants.TAG, "Error: " + e.getMessage());
				}
			}
		});
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getName());
		dest.writeInt(getFieldID());
		dest.writeString(getObjectId());
	}

	private void readFromParcel(Parcel in) {
		setName(in.readString());
		setFieldID(in.readInt());
		setObjectID(in.readString());
	}

}

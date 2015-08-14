package com.shoogisoft.oneappleaday.common;

import android.os.Parcel;
import android.os.Parcelable;

public class PersistantItem implements Parcelable {
	/**
	 * 
	 */
	public String Title;
	public String Body;
	public String ItemID;
	public String ObjectID;
	public boolean IsNew;

	public PersistantItem(String title, String body, String itemID,
			String objectID, boolean isNew) {
		Title = title;
		Body = body;
		itemID = itemID;
		ObjectID = objectID;
		IsNew = isNew;
	}

	public PersistantItem(Parcel in) {
		readFromParcel(in) ;
	}

	public static final Parcelable.Creator<PersistantItem> CREATOR = new Parcelable.Creator<PersistantItem>() {
		public PersistantItem createFromParcel(Parcel in) {
			return new PersistantItem(in);
		}

		public PersistantItem[] newArray(int size) {
			return new PersistantItem[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(Title);
		dest.writeString(Body);
		dest.writeString(ItemID);
		dest.writeString(ObjectID);
	}

	private void readFromParcel(Parcel in) {
		Title = in.readString();
		Body = in.readString();
		ItemID = in.readString();
		ObjectID = in.readString();
	}
}
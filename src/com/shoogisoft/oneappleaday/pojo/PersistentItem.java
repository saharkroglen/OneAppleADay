package com.shoogisoft.oneappleaday.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class PersistentItem implements Parcelable
{
	/**
	 * 
	 */
	public String Title;
	public String Body;
	public int ItemID;
	public String ObjectID;
	public boolean IsNew;
	public String ImageUrl;
	public long knew;
	public long didntKnow;
	public String ItemColor;
	
	public PersistentItem(String title,String body,int itemID,String objectID,boolean isNew, String imageUrl,long knew, long didntKnow, String itemColor)
	{		
		this.Title = title;
		this.Body = body;
		this.ItemID = itemID;
		this.ObjectID = objectID;
		this.IsNew = isNew;
		this.ImageUrl = imageUrl;
		this.knew = knew;
		this.didntKnow = didntKnow;
		this.ItemColor = itemColor;
	}
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {		
		dest.writeString(Title);		
		dest.writeString(Body);
		dest.writeInt(ItemID);
		dest.writeString(ObjectID);
		dest.writeString(ImageUrl);
		dest.writeLong(knew);
		dest.writeLong(didntKnow);
		dest.writeString(ItemColor);
	}
	private void readFromParcel(Parcel in) {  
		Title = in.readString();
		Body = in.readString();
		ItemID = in.readInt();
		ObjectID = in.readString();
		ImageUrl = in.readString();
		knew = in.readLong();
		didntKnow = in.readLong();
		ItemColor = in.readString();
	}
}
package com.shoogisoft.oneappleaday.common;

import com.shoogisoft.oneappleaday.parse.Fields;

import android.os.Parcel;
import android.os.Parcelable;

public class InternalMessage implements Parcelable{
	
	public final static int MESSAGE_LOGIN_STATE_CHANGE = 1;
	public final static int MESSAGE_PROMOTER_ID_SAVED = 2;
	public final static int MESSAGE_LOGIN_FAILURE = 3;
	public final static int MESSAGE_FACEBOOK_LOGIN_FAILURE = 4;
	public final static int MESSAGE_SIGNUP_FAILURE = 5;
	public final static int MESSAGE_SETUP_DONE = 6;
	public final static int MESSAGE_LOGOUT = 7;
	public final static int MESSAGE_SELECT_MENU_ITEM = 8;
	public final static int MESSAGE_SHOW_SPINNER_WITH_TEXT = 9;
	public final static int MESSAGE_SHOW_SPINNER_NO_BACKGROUND = 13;
	public final static int MESSAGE_SHOW_SETUP_WIZARD_SPINNER = 10;
	public final static int MESSAGE_FACEBOOK_EMAIL_RESOLVED = 11;
	public final static int MESSAGE_LOGIN_SUCCESSFUL = 12;
	public final static int MESSAGE_OPEN_FIELD_FRAGMENT = 14;
	public final static int MESSAGE_SET_WELCOME_MESSAGE_VISIBILITY = 15;
	public final static int MESSAGE_OPEN_ITEM_PAGER_FRAGMENT = 16;
	public final static int MESSAGE_QUIT = 17;
	
	
	public int MessageID;
	public String MessageText;
	public String AdditionalContent;
	public Fields Field;
	
	public InternalMessage(int messageID, String messageContent)
	{
		this(messageID,messageContent,null,null);
	}
	public InternalMessage(int messageID, String messageContent,Fields field)
	{
		this(messageID,messageContent,null,field);
	}
	public InternalMessage(int messageID, String messageContent,String additionalContent)
	{
		this(messageID,messageContent,additionalContent,null);
	}
	public InternalMessage(int messageID, String messageContent,String additionalContent,Fields field)
	{
		this.MessageID = messageID;
		this.MessageText = messageContent; 
		this.AdditionalContent = additionalContent;
		this.Field = field;
		
	}
//	public InternalMessage(int messageID, Integer messageContent)
//	{
//		this.messageID = messageID;
//		this.messageText = messageContent.toString(); 
//	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {		
		dest.writeInt(MessageID);		
		dest.writeString(MessageText);
		dest.writeString(AdditionalContent);
		dest.writeParcelable(Field, flags);
	}
	private void readFromParcel(Parcel in) {  
		MessageID = in.readInt();
		MessageText = in.readString();
		AdditionalContent = in.readString();
		Field = in.readParcelable(Fields.class.getClassLoader());
	}
}

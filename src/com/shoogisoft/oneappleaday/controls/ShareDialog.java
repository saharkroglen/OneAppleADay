package com.shoogisoft.oneappleaday.controls;

import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.shoogisoft.oneappleaday.MainActivity;
import com.shoogisoft.oneappleaday.R;
import com.shoogisoft.oneappleaday.common.Constants;
import com.shoogisoft.oneappleaday.common.GoogleAnalyticsAdapter;
import com.shoogisoft.oneappleaday.common.Utils;
import com.shoogisoft.oneappleaday.common.GoogleAnalyticsAdapter.Share;
import com.shoogisoft.oneappleaday.common.Utils.enShareButton;
import com.shoogisoft.oneappleaday.pojo.PersistentItem;
public class ShareDialog extends Dialog implements
		android.view.View.OnClickListener {

	public interface OnShareSelectedListener {
		void onShareMethodSelected(int color);
	}

	private Activity mContext;
	private ViewGroup btnShareFacebook;
	private ViewGroup btnShareTwitter;
	private ViewGroup btnShareInstagram;
	private ViewGroup btnShareWhatsapp;
	private String mItemObjectID;
	private ViewGroup btnShareMore;
	private String ShareDialog;
	private String mItemTitle;
	private String mMessage;
	private String mItemPageUrl;
	private int mItemID;

	public ShareDialog(Activity context, OnShareSelectedListener listener,
			int theme, PersistentItem item, String itemTitle) {
		super(context, theme);
		mContext = context;
		mItemObjectID = item.ObjectID; 
		mItemID = item.ItemID;
		mItemTitle = itemTitle;
		mItemPageUrl = String.format("http://onceaday.parseapp.com/item?iid=%s&cid=%s#/",mItemID,mItemObjectID);
		mMessage = "\n" + mItemTitle + mContext.getResources().getString(R.string.txt_sharing_message) + mItemPageUrl;
		
	}

	public ShareDialog(Activity context,int theme, PersistentItem item, String itemTitle) {		
		this(context,null,theme, item, itemTitle);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_share);
		
		btnShareFacebook = (ViewGroup) findViewById(R.id.tileShareFacebook);
		btnShareFacebook.setOnClickListener(this);

//		btnShareTwitter = (ViewGroup) findViewById(R.id.tileShareTwitter);
//		btnShareTwitter.setOnClickListener(this);

//		btnShareInstagram = (ViewGroup) findViewById(R.id.tileShareInstagram);
//		btnShareInstagram.setOnClickListener(this);

		btnShareWhatsapp = (ViewGroup) findViewById(R.id.tileShareWhatsapp);
		btnShareWhatsapp.setOnClickListener(this);

		btnShareMore = (ViewGroup) findViewById(R.id.tileShareMore);
		btnShareMore.setOnClickListener(this);

		ViewGroup btnFacebookPost = (ViewGroup) findViewById(R.id.tileShareFacebook);		
		btnFacebookPost.setOnClickListener(this);

//		ViewGroup btnTwitterPost = (ViewGroup) findViewById(R.id.tileShareTwitter);		
//		btnTwitterPost.setOnClickListener(this);
//
//		ViewGroup btnInstagramPost = (ViewGroup) findViewById(R.id.tileShareInstagram);
//		btnInstagramPost.setOnClickListener(this);

		ViewGroup btnWhatsappPost = (ViewGroup) findViewById(R.id.tileShareWhatsapp);
		btnWhatsappPost.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.tileShareFacebook:			
//			Intent shareToFacebook = new Intent(mContext.getActivity(),
//					ImagePickActivity.class);
//			shareToFacebook.putExtra("campaignLink", campaignLandingPageLink);
//			shareToFacebook.putExtra("merchantName", campaignLandingPageLink);
//			mContext.startActivityForResult(shareToFacebook, Constants.REQUEST_CODE_CAPTURE_IMAGE_FOR_FACEBOOK);
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			// intent.putExtra(Intent.EXTRA_SUBJECT, "Foo bar"); // NB: has no effect!
			intent.putExtra(Intent.EXTRA_SUBJECT, "Subject 123");
			intent.putExtra(Intent.EXTRA_TEXT, mItemPageUrl);

			// See if official Facebook app is found
			boolean facebookAppFound = false;
			List<ResolveInfo> matches = mContext.getPackageManager().queryIntentActivities(intent, 0);
			for (ResolveInfo info : matches) {
			    if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
			        intent.setPackage(info.activityInfo.packageName);
			        facebookAppFound = true;
			        break;
			    }
			}

			// As fallback, launch sharer.php in a browser
			if (!facebookAppFound) {
			    String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + mItemPageUrl;
			    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
			}

			mContext.startActivity(intent);
			if (MainActivity.LastShareButtonClickType == enShareButton.top)
				GoogleAnalyticsAdapter.sendAnalyticsEvent(mContext, Share.CATEGORY,Share.SHARE_TOP_BUTTON_FACEBOOK, String.valueOf(mItemID));
			else
				GoogleAnalyticsAdapter.sendAnalyticsEvent(mContext, Share.CATEGORY,Share.SHARE_SIDE_BUTTON_FACEBOOK, String.valueOf(mItemID));
			
			break;

		case R.id.tileShareMore:

			openOtherAppsChooser();
			if (MainActivity.LastShareButtonClickType == enShareButton.top)
				GoogleAnalyticsAdapter.sendAnalyticsEvent(mContext, Share.CATEGORY,Share.SHARE_TOP_BUTTON_MORE_APPS, String.valueOf(mItemID));
			else
				GoogleAnalyticsAdapter.sendAnalyticsEvent(mContext, Share.CATEGORY,Share.SHARE_SIDE_BUTTON_MORE_APPS, String.valueOf(mItemID));
			
			break;

		case R.id.tileShareWhatsapp:

			PackageManager pm = mContext.getPackageManager();
			try {

				Intent waIntent = new Intent(Intent.ACTION_SEND);
				waIntent.setType("text/plain");
				

				PackageInfo info = pm.getPackageInfo("com.whatsapp",PackageManager.GET_META_DATA);
				// Check if package exists or not. If not then code
				// in catch block will be called
				waIntent.setPackage("com.whatsapp");

				waIntent.putExtra(Intent.EXTRA_TEXT, mMessage);
				String txtShareUsingTitle = mContext.getResources().getString(R.string.txt_share_using_title);
				mContext.startActivity(
						Intent.createChooser(waIntent, txtShareUsingTitle));
				
				

			} catch (NameNotFoundException e) {
				Utils.showToast(mContext, mContext.getResources().getString(R.string.toast_whatsapp_not_installed));
				Log.e(Constants.TAG,"Whatsapp not installed\n" + e.getMessage());
			}
			if (MainActivity.LastShareButtonClickType == enShareButton.top)
				GoogleAnalyticsAdapter.sendAnalyticsEvent(mContext, Share.CATEGORY,Share.SHARE_TOP_BUTTON_WHATSAPP, String.valueOf(mItemID));
			else
				GoogleAnalyticsAdapter.sendAnalyticsEvent(mContext, Share.CATEGORY,Share.SHARE_SIDE_BUTTON_WHATSAPP, String.valueOf(mItemID));
			
			break;
		}
		ShareDialog.this.dismiss();
	}

	private void openOtherAppsChooser() {
	
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//		Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		
//		sharingIntent.setType("text/plain;image/*");
//		sharingIntent.setType("image/*");
		sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		sharingIntent.setType("text/plain");
		
//		Uri screenshotUri = Uri.parse(path);
//		sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
//		File saveFile = new File(path);
//		ArrayList<Uri> SavedImages = new ArrayList<Uri>();
//		SavedImages.add(Uri.fromFile(saveFile));
//		sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, SavedImages);
		
//		sharingIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "saharkroglen@gmail.com" });
		String appName = mContext.getResources().getString(R.string.app_name);
		sharingIntent.putExtra(Intent.EXTRA_SUBJECT,appName );
		
		sharingIntent.putExtra(Intent.EXTRA_TEXT, mMessage);
		sharingIntent.putExtra(Intent.EXTRA_TITLE, appName);
		mContext.startActivity(Intent.createChooser(sharingIntent,mContext.getResources().getString(R.string.txt_share_using_title)));
	}

}

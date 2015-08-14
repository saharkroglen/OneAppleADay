package com.shoogisoft.oneappleaday;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils.Permissions.User;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;
import com.shoogisoft.oneappleaday.common.Constants;
import com.shoogisoft.oneappleaday.parse.Fields;
import com.shoogisoft.oneappleaday.parse.Items;
import com.shoogisoft.oneappleaday.parse.PushNotificationActivity;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class MyApplication extends android.app.Application {

	private static final String FIELD_PROMOTER_ID = "promoterID";
	private static Context sContext;
	private static User mUser;
	public static Point mScreenDimensions; 
	public static int ScreenWidth,ScreenHeight;
	
	@Override
	public void onCreate() {
		super.onCreate();

		sContext = getApplicationContext();

		parseInit();
//		facebookIdentityInit();
		initUser();
		
		initDisplayMetrics();
		
		initImageLoader(sContext);
			
	}

	private void initDisplayMetrics() {
		WindowManager wm = (WindowManager) sContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		ScreenWidth = outMetrics.widthPixels;
		ScreenWidth = outMetrics.heightPixels;
	}
	
	public static Context getContext() {
		return sContext;
	}
	
	
	public static void setScreenDimensions(Point p)
	{
		mScreenDimensions = p;
	}
	public static Point getScreenDimensions()
	{
		return mScreenDimensions;
	}
	private void parseInit() {
		// Add your initialization code here
		Parse.initialize(this, "gFWZRPLmGg1MRH8TeCNAg4erKgcWrk5OO4uqyJ67","Y8y3WoqfW4pEjSCpYqKBLVnYERvcqmL2GpDQEVf5");
		
		registerParseObjects(); 

		PushService.setDefaultPushCallback(this, PushNotificationActivity.class);
//		setInstallationParams(null, false);
		Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

		// ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();

		// defaultACL.setPublicReadAccess(true);// If you would like all objects
		// to be private by default, remove this line.
		ParseACL.setDefaultACL(defaultACL, true); // data is private by default
													// unless said otherwise
	}

	private void registerParseObjects() {
		ParseObject.registerSubclass(Fields.class);
		ParseObject.registerSubclass(Items.class);
//		ParseObject.registerSubclass(TrustRequest.class);
//		ParseObject.registerSubclass(UserInbox.class);
//		ParseObject.registerSubclass(Campaign.class);
//		ParseObject.registerSubclass(CampaignPromoter.class);
	}
	
	public static void setInstallationParams(String promoterID,
			boolean isFacebookUser) {

		ParseInstallation installation = ParseInstallation
				.getCurrentInstallation();
		
		if (promoterID == null) {
			Log.e(Constants.TAG,
					"Can't assign installation. promoterID is null"); 
			return;
		} else {
			installation.put(FIELD_PROMOTER_ID, promoterID);			
		}
		Log.v(Constants.TAG, String.format(
				"Installation set promoterID: %s, facebook user: %s",
				installation.get(FIELD_PROMOTER_ID), isFacebookUser));
		installation.saveInBackground();
	}
	public static void clearInstallationParams() {

		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.remove(FIELD_PROMOTER_ID);					
		Log.d(Constants.TAG, String.format("clear installation params"));
		installation.saveInBackground();
	}
	
	private void facebookIdentityInit() {
		ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));
	}

	private void initUser() {
//		if (mUser == null)
//			mUser = new User();
	}
	
	public static User getUser() {
		return mUser;
	}
	
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		File cacheDir = StorageUtils.getCacheDirectory(context);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory() 
//				.discCache(new UnlimitedDiscCache(cacheDir)) // default
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app				
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

}

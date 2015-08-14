package com.shoogisoft.oneappleaday;

import java.util.ArrayList;

import com.achep.header2actionbar.FadingActionBarHelper; 
import com.shoogisoft.oneappleaday.common.Constants;
import com.shoogisoft.oneappleaday.common.GoogleAnalyticsAdapter;
import com.shoogisoft.oneappleaday.common.GoogleAnalyticsAdapter.General;
import com.shoogisoft.oneappleaday.common.GoogleAnalyticsAdapter.ItemView;
import com.shoogisoft.oneappleaday.common.GoogleAnalyticsAdapter.Share;
import com.shoogisoft.oneappleaday.common.InternalMessage;
import com.shoogisoft.oneappleaday.common.ItemsPrefs;
import com.shoogisoft.oneappleaday.common.Utils;
import com.shoogisoft.oneappleaday.controls.ShareDialog;
import com.shoogisoft.oneappleaday.fragments.FieldFragment;
import com.shoogisoft.oneappleaday.fragments.HomeFragment;
import com.shoogisoft.oneappleaday.fragments.ItemsPagerFragment;
import com.shoogisoft.oneappleaday.parse.Fields;
import com.shoogisoft.oneappleaday.pojo.PersistentItem;
import com.shoogisoft.oneappleaday.receivers.SyncReceiver;
import com.splunk.mint.Mint;

import android.R.menu;
import android.app.ActionBar;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity  implements OnBackStackChangedListener
		{

	private FadingActionBarHelper mFadingActionBarHelper; 
	public static final int MENU_ITEM_HOME = 0;
	public static final int MENU_ITEM_INVITATIONS = 1;
	public static final int MENU_ITEM_LOGOUT = 2;
	public static final int MENU_ITEM_PREFS = 3;
	
	private Menu mMenu;

	
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private ViewGroup mProgressLayout;
	private ProgressBar mProgressbarNoBackground;
	private TextView mSpinnerText;
	private TextView mTxtNoItemsYetMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.initBugSense(this);
		if (!isFirstLaunch()){
			setFirstLaunch();			
		}
		
		setContentView(R.layout.activity_main);
		mFadingActionBarHelper = new FadingActionBarHelper(getActionBar(),
                getResources().getDrawable(R.drawable.actionbar_bg));
		getSupportFragmentManager().addOnBackStackChangedListener(this);
	    //Handle when activity is recreated like on orientation Change
	    shouldDisplayHomeUp(false);
		
		mFragmentManager = getSupportFragmentManager();
		refreshTitle();

		mProgressLayout = (ViewGroup) findViewById(R.id.progressLayout);	
		mTxtNoItemsYetMessage = (TextView) findViewById(R.id.txtNoItemsFoundYet);
		mSpinnerText = (TextView) findViewById(R.id.txtProgressTitle);
		mProgressbarNoBackground = (ProgressBar)findViewById(R.id.largeProgressBarNoBackground);

		showSpinner(true);
			
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mMessageReceiver,
				new IntentFilter(Utils.INTERNAL_MESSAGE_INTENT));

		reloadFilteredItemList();
		Intent intent = this.getIntent();
		
		if (intent.hasExtra(Constants.EXTRA_NOTIFICATION_ITEM_ID))
		{
			int selectedItemID = intent.getIntExtra(Constants.EXTRA_NOTIFICATION_ITEM_ID,0);
			int adjustedItemID = mItemsFromPersistency.size() - selectedItemID;//since the list is sorted backwards
//			openSelectedItem(adjustedItemID);
			Utils.sendMessage(MyApplication.getContext(), new InternalMessage(
					InternalMessage.MESSAGE_OPEN_ITEM_PAGER_FRAGMENT, String.valueOf(adjustedItemID)));
			GoogleAnalyticsAdapter.sendAnalyticsEvent(this, ItemView.CATEGORY, ItemView.OPEN_ITEM_FROM_PUSH_NOTIFICATION ,String.valueOf(selectedItemID));
		}
		else
		{
			openSelectedField();
		}
	
		initScreenSize();
	}
	
	private boolean isFirstLaunch() {
		SharedPreferences prefs = Utils.getSharedPrefs();
		return prefs.contains(Constants.PREFS_FIRST_LAUNCH);
	}
	

	private void setFirstLaunch()
	{
		SharedPreferences settings = Utils.getSharedPrefs();
		Editor editor = settings.edit();		
		editor.putBoolean(Constants.PREFS_FIRST_LAUNCH,true);
		editor.commit();
		GoogleAnalyticsAdapter.sendAnalyticsEvent(this, General.CATEGORY, General.FIRST_LAUNCH,null);
	}

	
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (intent != null)
		{
			if (intent.getAction().equals(Constants.ACTION_OPEN_FIELD))
			{
				Bundle data = intent.getExtras();
				if (data != null)
				{
					String fieldID = data.getString(Constants.EXTRA_FIELD_ID);
					if (fieldID != null)
					{						
						openSelectedField();												
					}
				}
			}	
//			if (intent.getAction().equals(Constants.ACTION_SNOOZE_ITEM))
//			{
//				Bundle data = intent.getExtras();
//				if (data != null)
//				{
//					String title = data.getString(Constants.EXTRA_ITEM_TITLE);
//					String body = data.getString(Constants.EXTRA_ITEM_BODY);
//					new SyncReceiver().setSnoozeAlarm(this, title, body);
//					finish();
//				}
//			}	
		}
		
		super.onNewIntent(intent);
	}

	private void clearMessageFromNotificationBar() {
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.cancel(Constants.ITEM_ARRIVED_NOTIFICATION_MESSAGE_ID);
	}

	private void initScreenSize() {
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
//		OnceADayApplication.setScreenDimensions(new Point(width, height));
	}

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			handleInternalMessage(Utils.getMessageFromIntent(intent));
		}
	};
	private ArrayList<PersistentItem> mItemsFromPersistency;
	private FragmentManager mFragmentManager;
	private FieldFragment mFieldFrag;

	private void handleInternalMessage(InternalMessage msg) {
		switch (msg.MessageID) {
		case InternalMessage.MESSAGE_LOGIN_SUCCESSFUL:

			break;
		case InternalMessage.MESSAGE_QUIT:
			finish();
			break;
		case InternalMessage.MESSAGE_OPEN_FIELD_FRAGMENT:
			reloadFilteredItemList();
//			Fields field = msg.Field;
//			if (msg == null)
//				Log.e(Constants.TAG,"MESSAGE_OPEN_FIELD_FRAGMENT message must contain Field Extra");
//			else
//				Log.v(Constants.TAG, "open field fragment: " + field.getName());
//				openSelectedField(field);
			openSelectedField();
			break;
		case InternalMessage.MESSAGE_OPEN_ITEM_PAGER_FRAGMENT:
			
			Integer position = Integer.valueOf(msg.MessageText);
			if (position == null)
				Log.e(Constants.TAG,"MESSAGE_OPEN_ITEM_PAGER_FRAGMENT message must contain 'position' Extra");
			else
				Log.v(Constants.TAG, "open pager item fragment in position: " + position);
				openSelectedItem(position);
			break;

		case InternalMessage.MESSAGE_SET_WELCOME_MESSAGE_VISIBILITY:
			boolean visible = Boolean.valueOf(msg.MessageText);
			setWelcomeMessageVisibility(visible);
			break;
		case InternalMessage.MESSAGE_SHOW_SPINNER_WITH_TEXT:
			boolean showSpinner = Boolean.valueOf(msg.MessageText);
			// setProgressBarIndeterminateVisibility(showSpinner);
			if (showSpinner) {
				if (msg.AdditionalContent != null) {
					mSpinnerText.setText(msg.AdditionalContent);
				} else {
					mSpinnerText.setText("Loading...");
				}
				mProgressLayout.setVisibility(View.VISIBLE);
			} else {
				mProgressLayout.setVisibility(View.GONE);
			}
			break;
		case InternalMessage.MESSAGE_SHOW_SPINNER_NO_BACKGROUND:
			boolean show = Boolean.valueOf(msg.MessageText);
			showSpinner(show);
			break;
		}
	}

	private void showSpinner(boolean show) {
		if (show) {				
			mProgressbarNoBackground.setVisibility(View.VISIBLE);
//				Log.v(Constants.TAG, String.format("Show spinner"));
		} else {
			mProgressbarNoBackground.setVisibility(View.GONE);
//				Log.v(Constants.TAG, String.format("Hide spinner"));
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		refreshTitle();
		clearMessageFromNotificationBar();
	}

	
	private void handleMenuItemSelection(final int menuID) {

		switch (menuID) {
		case MENU_ITEM_HOME:
			openHome();
			break;
		case MENU_ITEM_INVITATIONS:
//			openInvitations(null);
			break;
		case MENU_ITEM_LOGOUT:
//			logout();
			break;
		}
	}

	

	public void openHome() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment homeFrag = new HomeFragment();
		Bundle args = new Bundle();
//		args.putString(WebViewFragment.ARG_PROFILE_NAME, "todo");
		homeFrag.setArguments(args);

		
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.enter_anim,R.anim.exit_anim);
		transaction.replace(R.id.container, homeFrag)
				.commitAllowingStateLoss();		
	}
	
	public void openSelectedField() {		
		openSelectedField(SyncReceiver.FieldObjectID, getResources().getString(R.string.app_name));
		Utils.setNotificationBarColor(getResources().getColor(R.color.actionbar_bg),this);
		
		showShareMenu(false);
		
		
	}

	private void showShareMenu(boolean show) {
		if (mMenu!= null)
		{
			MenuItem item = mMenu.findItem(R.id.action_share);
			item.setVisible(show);
		}
	}
	
	public void openSelectedField(String fieldObjectID) {
		Fields field = Fields.getFieldByID(SyncReceiver.FieldObjectID);
		openSelectedField(fieldObjectID,field.getName());
	}
	
	public void openSelectedField(String fieldObjectID, String fieldName) {	
		try
		{			
			showSpinner(true);
			getActionBar().show();
			mFieldFrag = new FieldFragment();			
			Bundle args = new Bundle();
//			args.putParcelable(Constants.ARG_FIELD, field);
			args.putString(Constants.ARG_FIELD_ID, fieldObjectID);
			args.putString(Constants.ARG_FIELD_NAME, fieldName);
			args.putParcelableArrayList(Constants.ARG_FILTERED_ITEM_LIST, mItemsFromPersistency);
			mFieldFrag.setArguments(args);
			
			FragmentTransaction transaction = mFragmentManager.beginTransaction();
			transaction.setCustomAnimations(R.anim.exit_anim_reverse,R.anim.enter_anim_reverse);
			transaction.replace(R.id.container, mFieldFrag)
					.commitAllowingStateLoss();
			
		}
		catch (Exception ex)
		{
			Log.e(Constants.TAG,ex.getMessage());
		}
	}

	private void reloadFilteredItemList() {
		mItemsFromPersistency = Utils.filterNewItems(new ItemsPrefs().getItemList());
	}
	
	public void openSelectedItem(int position) {
		setWelcomeMessageVisibility(false);
		try
		{
			showSpinner(true);
			FragmentManager fragmentManager = getSupportFragmentManager();
			Fragment itemPagerFrag = new ItemsPagerFragment();
			Bundle args = new Bundle();
			args.putInt(Constants.ARG_ITEM_POSITION, position);
			itemPagerFrag.setArguments(args);
			
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.setCustomAnimations(R.anim.enter_anim,R.anim.exit_anim);
			transaction.replace(R.id.container, itemPagerFrag)
					.commitAllowingStateLoss();
			shouldDisplayHomeUp(true);
			mFadingActionBarHelper.setActionBarAlpha(0);
			
			showShareMenu(true);
		}
		catch (Exception ex)
		{
			Log.e(Constants.TAG,ex.getMessage());
		}
	}


	public void onFragmentAttached(String profileName) {
		refreshTitle();
	}

	private void refreshTitle() {
		mTitle = getActivityTitle();
		restoreActionBar();
	}

	public String getActivityTitle() {
		String title;		
		title = getResources().getString(R.string.app_name);		
		return title;
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();// getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		getMenuInflater().inflate(R.menu.main, menu); 
		mMenu = menu;
		showShareMenu(false);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
	
		showSpinner(true);
		Fragment f = getSupportFragmentManager().findFragmentById(
				R.id.container);
		if (f instanceof HomeFragment) {
			finish();
		} else if (f instanceof FieldFragment) {
			finish(); 			
		}
		else if (f instanceof ItemsPagerFragment)
		{
			//openSelectedField(SyncReceiver.FieldObjectID);
//			SyncReceiver.sendOpenFieldMessage(this);
			openSelectedField();
			shouldDisplayHomeUp(false);
		}
		else
		{
			openHome();
		}
			
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = menuItem.getItemId();
		if (id == R.id.action_share) {
			handleShareEvent(Utils.enShareButton.top);
			return true;
		}
		return super.onOptionsItemSelected(menuItem);
	}

	public void shareSideButton(View v)
	{		
		handleShareEvent(Utils.enShareButton.side);
	}
	
	public static Utils.enShareButton LastShareButtonClickType;
	private void handleShareEvent(Utils.enShareButton shareButtonType) {
		LastShareButtonClickType = shareButtonType;
		Fragment f = getSupportFragmentManager().findFragmentById(
				R.id.container);
		if (f instanceof ItemsPagerFragment) {				
			int pagerIndex = ((ItemsPagerFragment)f).getCurrentPageIndex();
			PersistentItem persistentItem = mItemsFromPersistency.get(pagerIndex);
			String singleLineTitle = Utils.getSingleLineTitle(persistentItem);
			
			if (shareButtonType == Utils.enShareButton.top)
			{
				GoogleAnalyticsAdapter.sendAnalyticsEvent(this, Share.CATEGORY,Share.SHARE_TOP_BUTTON, String.valueOf(persistentItem.ItemID));
			}
			else
			{
				GoogleAnalyticsAdapter.sendAnalyticsEvent(this, Share.CATEGORY,Share.SHARE_SIDE_BUTTON, String.valueOf(persistentItem.ItemID));
			}
			
			new ShareDialog(this, R.style.CustomAlertDialog,persistentItem,singleLineTitle).show();
		}
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
	
		if (data != null && data.getExtras() != null) {
			ExtractScanningParams(data);
		}
	}

	private void ExtractScanningParams(Intent data) {
		String contents = data.getStringExtra("SCAN_RESULT");
		String format = data.getStringExtra("SCAN_RESULT_FORMAT");
		if (contents != null && format != null)
		{
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(contents));
			startActivity(i);
		}
	}


	
	public FadingActionBarHelper getFadingActionBarHelper() {
        return mFadingActionBarHelper;
    }
	
	private void setWelcomeMessageVisibility(boolean visible) {
		if (visible)
		{
			mTxtNoItemsYetMessage.setVisibility(View.VISIBLE);
			new SyncReceiver().setWelcomeAlarm(this);
			showSpinner(true);
		}
		else
		{
			mTxtNoItemsYetMessage.setVisibility(View.INVISIBLE);			
		}
	}

	@Override
	public void onBackStackChanged() {
	    shouldDisplayHomeUp(false);
	}

	public void shouldDisplayHomeUp(boolean displayUp){
	   //Enable Up button only  if there are entries in the back stack
//	   boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
	   getSupportActionBar().setDisplayHomeAsUpEnabled(displayUp);
	}

	@Override
	public boolean onSupportNavigateUp() {
	    //This method is called when the up button is pressed. Just the pop back stack.
		onBackPressed();
//	    getSupportFragmentManager().popBackStack();
	    return true;
	}
}

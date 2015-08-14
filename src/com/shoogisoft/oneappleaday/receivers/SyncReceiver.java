package com.shoogisoft.oneappleaday.receivers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.shoogisoft.oneappleaday.MyApplication;
import com.shoogisoft.oneappleaday.common.Constants;
import com.shoogisoft.oneappleaday.common.InternalMessage;
import com.shoogisoft.oneappleaday.common.ItemsPrefs;
import com.shoogisoft.oneappleaday.common.NotificationHelper;
import com.shoogisoft.oneappleaday.common.Utils;
import com.shoogisoft.oneappleaday.parse.Fields;
import com.shoogisoft.oneappleaday.parse.Items;
import com.shoogisoft.oneappleaday.pojo.PersistentItem;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

public class SyncReceiver extends BroadcastReceiver {

	private static final String TIME_FORMAT = "HH:mm";
	private static final String DATE_FORMAT = "yyyyMMdd";
	public static String FieldObjectID = "bQ3X94eMIu"; // currently a hard coded
														// //FVcnlW1w4f
														// fieldObjectID since
														// we disabled multiple
														// fields option
	static final long ONE_MINUTE_IN_MILLIS = 60000;
	static final long ONE_HOUR_IN_MILLIS = ONE_MINUTE_IN_MILLIS * 60;
	static final long TIME_TO_GET_FIRST_MESSAGE_AFTER_INSTALL = 1 * ONE_MINUTE_IN_MILLIS;

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	// private PendingIntent pendingIntent;
	// private Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		int requestCode = intent.getExtras().getInt(Constants.REQUEST_CODE);
		Log.v(Constants.TAG, "SyncReceiver - onReceive Alarm");
		checkSync(context);
		if (requestCode == Constants.REQUEST_CODE_ALARM_DAILY) {
			Log.v(Constants.TAG, "SyncReceiver - daily event received");
			checkDailyItemIncrement(context);
		} else if (requestCode == Constants.REQUEST_CODE_ALARM_IMMEDIATE) {
			Log.v(Constants.TAG, "SyncReceiver - immediate event received");
			checkDailyItemIncrement(context);
			// notifyOfIncomingItem(context,
			// intent.getExtras().getString(Constants.EXTRA_ITEM_TITLE),
			// intent.getExtras().getString(Constants.EXTRA_ITEM_BODY));
		}
	}

	/**
	 * Check whether user got the daily item notification. if not then let him..
	 */
	private void checkDailyItemIncrement(Context c) {
		Date lastServerSync = getLastDailyIncrement();
		SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);
		boolean shouldIncrement = false;
		if (lastServerSync == null)
			shouldIncrement = true;
		else {
			boolean alreadyIncrementedToday = fmt.format(lastServerSync)
					.equals(fmt.format(System.currentTimeMillis()));
			if (!alreadyIncrementedToday)
				shouldIncrement = true;
		}

		if (shouldIncrement) {

			ItemsPrefs itemsPrefs = new ItemsPrefs();
			ArrayList<PersistentItem> items = itemsPrefs.getItemList();
			if (items == null) {
				Log.v(Constants.TAG, "No items found yet");
				return;
			}
			Utils.sortList(items, true);
			for (PersistentItem item : items) {
				if (item.IsNew) // find the first new item -> set it to old and
								// exit.
				{
					item.IsNew = false;

					notifyOfIncomingItem(item);
					sendOpenFieldMessage(0);
					setLastDailyIncrement(System.currentTimeMillis());
					break;
				}
			}
			itemsPrefs.setItemList(items);
		}
	}

	/**
	 * Check if a server sync already occurred today. if not, let it sync see
	 * {@link #setAlarm()} for more info scheduling.
	 */
	@SuppressLint("SimpleDateFormat")
	private void checkSync(Context c) {
		Log.i(Constants.TAG, "Check sync");
		SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);
		Date lastServerSync = getLastServerSync();
		boolean shouldSync = false;
		if (lastServerSync == null)
			shouldSync = true;
		else {
			boolean alreadyServerSyncedToday = fmt.format(lastServerSync)
					.equals(fmt.format(System.currentTimeMillis()));
			if (!alreadyServerSyncedToday)
				shouldSync = true;
			else
				Log.i(Constants.TAG, "Already synced today, dismiss");
		}

		if (shouldSync) {
			Log.i(Constants.TAG, "do server sync !");
			serverSync(c);
		}
	}

	/**
	 * Is called every time boot the device and on application launch. The
	 * reason is that we want to make sure the schedule is set in the
	 * AlarmManager and since we can't check if an alarm is already set in
	 * AlarmManager we make sure to register the alarm every time and replace
	 * the previous one. see {@link #setSchedule()} on scheduling options
	 */
	public synchronized void setAlarm(Context c) {
		Intent alarmIntent = new Intent(c, SyncReceiver.class);
		alarmIntent.putExtra(Constants.REQUEST_CODE,
				Constants.REQUEST_CODE_ALARM_DAILY);
		PendingIntent pi = PendingIntent.getBroadcast(c,
				Constants.REQUEST_CODE_ALARM_DAILY, alarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Calendar calendar = Calendar.getInstance();
		long currentTimeMillis = System.currentTimeMillis();
		calendar.setTimeInMillis(currentTimeMillis);
		calendar.set(Calendar.HOUR_OF_DAY, 10);
		calendar.set(Calendar.MINUTE, 00);
		calendar.add(Calendar.DATE, 0); // start from today

		setSchedule(c, pi, calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_HALF_DAY, true);
	}

	private void setAlarmManagerSchedule(Context c, PendingIntent pi,
			int hourInDay) {

	}

	public void setWelcomeAlarm(Context c) {
		checkSync(c);
		Intent alarmIntent = new Intent(c, SyncReceiver.class);
		alarmIntent.putExtra(Constants.REQUEST_CODE,
				Constants.REQUEST_CODE_ALARM_IMMEDIATE);
		PendingIntent pi = PendingIntent.getBroadcast(c,
				Constants.REQUEST_CODE_ALARM_IMMEDIATE, alarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		setSchedule(c, pi, System.currentTimeMillis()
				+ this.ONE_MINUTE_IN_MILLIS, 0, false);
	}

	/**
	 * schedules the app sync time. on first launch, time is set to
	 * {@link #TIME_TO_GET_FIRST_MESSAGE_AFTER_INSTALL} from first app launch in
	 * order to allow user to consume first item quickly
	 */
	public void setSchedule(Context c, PendingIntent pi, long time,
			long interval, boolean repeat) {

		AlarmManager manager = (AlarmManager) c
				.getSystemService(Context.ALARM_SERVICE);

		if (repeat) {
			Date syncTime = getSyncTime();
			if (syncTime == null) {
				syncTime = new Date(time);
				setSyncTime(time);
			}

			Log.v(Constants.TAG, "daily sync time set "
					+ getTimeFormat(syncTime));
			manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
					syncTime.getTime(), interval, pi);

		} else {
			Date syncTime = new Date(time);
			Log.v(Constants.TAG, "immediate sync time set "
					+ getTimeFormat(syncTime));
			manager.set(AlarmManager.RTC_WAKEUP, time, pi);
		}
		// Toast.makeText(mContext, "Alarm Set",Toast.LENGTH_SHORT).show();

	}

	// public void setSnoozeAlarm(Context c, String title, String body) {
	// Intent alarmIntent = new Intent(c, SyncReceiver.class);
	// alarmIntent.putExtra(Constants.EXTRA_ITEM_TITLE, title);
	// alarmIntent.putExtra(Constants.EXTRA_ITEM_BODY, body);
	// PendingIntent pi = PendingIntent.getBroadcast(c,
	// Constants.REQUEST_CODE_ALARM_SNOOZE, alarmIntent,
	// PendingIntent.FLAG_CANCEL_CURRENT);
	// long delay = ONE_MINUTE_IN_MILLIS;
	// setSchedule(c, pi,delay,false);
	// Log.v(Constants.TAG,String.format("Snooze alarm set in %s millis from now",delay));
	// }

	private void serverSync(final Context c) {

		Items.getServerItemsForField(FieldObjectID, new Items.OnQueryDone() {

			@Override
			public void onItemsQueryResult(List<Items> serverItemList) {
				if (serverItemList == null) {
					Utils.showToast(
							c,
							"Could not access server.\nplease check your connectivity and restart application.");
					return;
				}
				ItemsPrefs itemPrefs = new ItemsPrefs();
				updatePersistentItemsFromServer(c, serverItemList, itemPrefs);
				setLastServerSync(System.currentTimeMillis());
			}
		});
	}

	/**
	 * update persistent items according to current list in the server. add only
	 * new items to the persistent item list save updated item list into
	 * preferences
	 * 
	 * @param serverItemList
	 *            - a full list of items in the server
	 * @return - updated persistent item list
	 */
	private ArrayList<PersistentItem> updatePersistentItemsFromServer(
			Context c, List<Items> serverItemList, ItemsPrefs itemPrefs) {

//		getStartDate(serverItemList);
		getStartDate();

		ArrayList<PersistentItem> itemsToPersist;
		if (itemPrefs.getItemList() == null
				|| itemPrefs.getItemList().size() == 0) {
			Log.v(Constants.TAG, "Append all items from server, item count: "
					+ serverItemList.size());
			itemsToPersist = getFreshCopyFromServer(serverItemList);
		} else {
			itemsToPersist = appendOrUpdateItems(c, serverItemList, itemPrefs);
		}

		updateItemsAccordingToFeedStartDate();
		itemPrefs.setItemList(itemsToPersist);
		return itemsToPersist;
	}

	private static void updateItemsAccordingToFeedStartDate() {
		updateItemsAccordingToStartDateInternal();
		if (getIsFirstSync()) {
			setIsFirstSync(false);
			sendOpenFieldMessage(5000);
			sendOpenItemMessage(0, 6000);
		}
	}

	private static void updateItemsAccordingToStartDateInternal() {
		Date feedStartDate = SyncReceiver.getFeedStartDate();
		long daysCountSinceFeedStart = 0;
		if (feedStartDate != null) {
			daysCountSinceFeedStart = Utils.daysBetween(feedStartDate,
					new Date(System.currentTimeMillis()));
			daysCountSinceFeedStart++;// add the day of the first message. if
										// the first message was in 13 january
										// and today is 15, then user should get
										// 3 messages in order to be in sync
										// with all users
//			new ItemsPrefs().updateItemsStateAccordingToFeedStartDate(daysCountSinceFeedStart,true); // new users should sync all items from the start date set in the server and enforce symetric increment between users
			new ItemsPrefs().updateItemsStateAccordingToFeedStartDate(4,false); //new users should sync at first the initial 4 items and allow non-symetric increment between users
			setLastDailyIncrement(System.currentTimeMillis());
		}
	}

	public static void sendOpenItemMessage(final Integer index,
			final long delayInMillis) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(delayInMillis);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ArrayList<PersistentItem> filteredItems = Utils
						.filterNewItems(new ItemsPrefs().getItemList());
				// Integer adjustedItemIndex = filteredItems.size()-1 -
				// index;//since the list is sorted backwards
				Utils.sendMessage(
						MyApplication.getContext(),
						new InternalMessage(
								InternalMessage.MESSAGE_OPEN_ITEM_PAGER_FRAGMENT,
								index.toString()));
				notifyOfIncomingItem(filteredItems.get(index));
				// Utils.sendMessage(MyApplication.getContext(), new
				// InternalMessage(
				// InternalMessage.MESSAGE_QUIT, null));
				Log.v(Constants.TAG, "send item refresh message");
			}
		}).start();
	}

	public static void sendOpenFieldMessage(final long delayInMillis) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(delayInMillis);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Utils.sendMessage(MyApplication.getContext(),
						new InternalMessage(
								InternalMessage.MESSAGE_OPEN_FIELD_FRAGMENT,
								null));
				Log.v(Constants.TAG, "send item refresh message");
			}
		}).start();
	}

	private static String getTimeFormat(Date date) {
		SimpleDateFormat fmt = new SimpleDateFormat(TIME_FORMAT);
		String syncTime = fmt.format(date);
		return syncTime;
	}

	private static String getDateFormat(Date date) {
		SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);
		String syncTime = fmt.format(date);
		return syncTime;
	}

	public static void setSyncTime(long date) {
		SharedPreferences settings = Utils.getSharedPrefs();
		Editor editor;
		editor = settings.edit();
		editor.putLong(Constants.PREFS_SYNC_TIME, date);
		editor.commit();

		Log.v(Constants.TAG, "sync time saved to preferences "
				+ getTimeFormat(new Date(date)));
	}

	public static Date getSyncTime() {
		SharedPreferences settings = Utils.getSharedPrefs();

		long time = settings.getLong(Constants.PREFS_SYNC_TIME, 0);
		if (time == 0)
			return null;
		else
			return new Date(time);
	}

	public static void setLastServerSync(long date) {
		SharedPreferences settings = Utils.getSharedPrefs();
		Editor editor;
		editor = settings.edit();
		editor.putLong(Constants.PREFS_LAST_SERVER_SYNC, date);
		editor.commit();

		Log.v(Constants.TAG, "last server sync date saved to preferences "
				+ getDateFormat(new Date(date)));
	}

	public static Date getLastServerSync() {

		SharedPreferences settings = Utils.getSharedPrefs();
		long time = settings.getLong(Constants.PREFS_LAST_SERVER_SYNC, 0);
		if (time == 0)
			return null;
		else {
			Date d = new Date(time);
			Log.v(Constants.TAG, "last server sync date: " + getDateFormat(d));
			return d;
		}
	}

	// /
	public static void setLastDailyIncrement(long date) {
		SharedPreferences settings = Utils.getSharedPrefs();
		Editor editor;
		editor = settings.edit();
		editor.putLong(Constants.PREFS_LAST_DAILY_INCREMENT, date);
		editor.commit();

		Log.v(Constants.TAG, "last daily increment date saved to preferences "
				+ getDateFormat(new Date(date)));
	}

	public static Date getLastDailyIncrement() {

		SharedPreferences settings = Utils.getSharedPrefs();
		long time = settings.getLong(Constants.PREFS_LAST_DAILY_INCREMENT, 0);
		if (time == 0)
			return null;
		else {
			Date d = new Date(time);
			Log.v(Constants.TAG, "last daily increment date: "
					+ getDateFormat(d));
			return d;
		}
	}

	// /

	public void cancelSyncSchedule(Context c, PendingIntent pi) {
		AlarmManager manager = (AlarmManager) c
				.getSystemService(Context.ALARM_SERVICE);

		manager.cancel(pi);
		Toast.makeText(c, "Alarm Canceled", Toast.LENGTH_SHORT).show();
	}

	public void startAt10AM(Context c, PendingIntent pi) {
		AlarmManager manager = (AlarmManager) c
				.getSystemService(Context.ALARM_SERVICE);

		/* Set the alarm to start at 10:30 AM */
		Calendar calendar = Calendar.getInstance();
		long currentTimeMillis = System.currentTimeMillis();
		calendar.setTimeInMillis(currentTimeMillis);
		calendar.set(Calendar.HOUR_OF_DAY, 10);
		calendar.set(Calendar.MINUTE, 30);

		/* Repeating on every 20 minutes interval */
		manager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), 1000 * 60 * 20, pi);
	}

	private ArrayList<PersistentItem> appendOrUpdateItems(Context c,
			List<Items> serverItemList, ItemsPrefs itemPrefs) {
		ArrayList<PersistentItem> itemsToPersist;
		itemsToPersist = itemPrefs.getItemList();
		HashMap<Integer, PersistentItem> itemsMap = itemPrefs.getItemMap();
		for (Items serverItem : serverItemList) {
			boolean newItemsArrived = false;

			if (!itemsMap.containsKey(serverItem.getItemID())) {

				appendNewItem(itemsToPersist, serverItem, true);
			} else {
				updateExistingItem(itemsMap, serverItem);
			}
		}
		return itemsToPersist;
	}

	public void UpdateSingleItemFromServer(Items serverItem) {
		ItemsPrefs itemPrefs = new ItemsPrefs();
		HashMap<Integer, PersistentItem> itemsMap = itemPrefs.getItemMap();
		updateExistingItem(itemsMap, serverItem);
		itemPrefs.saveToPrefs();
	}

	private void updateExistingItem(HashMap<Integer, PersistentItem> itemsMap,
			Items serverItem) {
		PersistentItem oneItem = itemsMap.get(serverItem.getItemID());
		oneItem.knew = serverItem.getKnewCount();
		oneItem.didntKnow = serverItem.getDidntKnowCount();
		oneItem.ItemColor = serverItem.getItemColor();
		oneItem.Body = serverItem.getBody();
		oneItem.Title = serverItem.getTitle();
	}

	private void appendNewItem(ArrayList<PersistentItem> itemsToPersist,
			Items serverItem, boolean preloadImage) {
		boolean newItemsArrived;
		Log.v(Constants.TAG, "Append item number: " + serverItem.getItemID());

		ParseFile imageFile = serverItem.getImage();
		String imageUrl = getImageUrl(imageFile);
		itemsToPersist.add(new PersistentItem(serverItem.getTitle(), serverItem
				.getBody(), serverItem.getItemID(), serverItem.getObjectId(),
				true, imageUrl, serverItem.getKnewCount(), serverItem
						.getDidntKnowCount(), serverItem.getItemColor()));
		if (preloadImage)
			imageLoader
					.loadImage(imageUrl, Utils.getImageLoaderOptions(), null);
		newItemsArrived = true;
	}

	private String getImageUrl(ParseFile imageFile) {
		String imageUrl = null;
		if (imageFile != null) {
			imageUrl = imageFile.getUrl();
			Log.v(Constants.TAG, "image url: " + imageUrl);
		}
		return imageUrl;
	}

	private static void notifyOfIncomingItem(PersistentItem item) {
		new NotificationHelper().newItemArrived(item);
	}

	private ArrayList<PersistentItem> getFreshCopyFromServer(
			List<Items> serverItemList) {
		ArrayList<PersistentItem> itemsToPersist;
		itemsToPersist = new ArrayList<PersistentItem>();

		for (Items item : serverItemList) {
			ParseFile imageFile = item.getImage();
			String imageUrl = getImageUrl(imageFile);
			itemsToPersist.add(new PersistentItem(item.getTitle(), item
					.getBody(), item.getItemID(), item.getObjectId(), true,
					imageUrl, item.getKnewCount(), item.getDidntKnowCount(),
					item.getItemColor()));
			imageLoader
					.loadImage(imageUrl, Utils.getImageLoaderOptions(), null);
		}
		return itemsToPersist;
	}

	/**
	 * Extract the startDate from the related Fields object from the first item
	 * object.
	 * 
	 * @param serverItemList
	 */
	// public static void getStartDate(List<Items> serverItemList) {
	// if (getFeedStartDate() == null &&
	// serverItemList != null &&
	// serverItemList.size() > 0 &&
	// serverItemList.get(0) != null) {
	// serverItemList.get(0).getParseObject("field")
	// .fetchIfNeededInBackground(new GetCallback<Fields>() {
	//
	// @Override
	// public void done(Fields field, ParseException arg1) {
	// Date startDate = field.getStartDate();
	// setFeedStartDate(startDate.getTime());
	// updateItemsAccordingToFeedStartDate();
	// }
	// });
	// }
	// }
	public static void getStartDate() {
		if (getFeedStartDate() == null) {
			ParseQuery<Fields> query = ParseQuery.getQuery(Fields.class);
			query.whereEqualTo(Fields.COLUMN_OBJECT_ID, FieldObjectID);
			query.findInBackground(new FindCallback<Fields>() {
				@Override
				public void done(List<Fields> field, ParseException arg1) {
					Date startDate = field.get(0).getStartDate();
					setFeedStartDate(startDate.getTime());
					updateItemsAccordingToFeedStartDate();
				}
			});
		}
	}

	public static void setFeedStartDate(long date) {
		SharedPreferences settings = Utils.getSharedPrefs();
		Editor editor;
		editor = settings.edit();
		editor.putLong(Constants.PREFS_FEED_START_DATE, date);
		editor.commit();

		Log.v(Constants.TAG, "feed start date prefs set to : "
				+ getDateFormat(new Date(date)));
	}

	public static Date getFeedStartDate() {

		SharedPreferences settings = Utils.getSharedPrefs();
		long time = settings.getLong(Constants.PREFS_FEED_START_DATE, 0);
		if (time == 0)
			return null;
		else {
			Date d = new Date(time);
			Log.v(Constants.TAG, "feed start date is: " + getDateFormat(d));
			return d;
		}
	}

	public static void setIsFirstSync(boolean alreadyConsidered) {
		SharedPreferences settings = Utils.getSharedPrefs();
		Editor editor;
		editor = settings.edit();
		editor.putBoolean(Constants.PREFS_IS_FIRST_SYNC, alreadyConsidered);
		editor.commit();

		Log.v(Constants.TAG, "set 'feed start date already considered' to : "
				+ alreadyConsidered);
	}

	public static boolean getIsFirstSync() {

		SharedPreferences settings = Utils.getSharedPrefs();
		boolean isFirst = settings.getBoolean(Constants.PREFS_IS_FIRST_SYNC,
				true);

		Log.v(Constants.TAG, "'first sync' prefs value is : " + isFirst);

		return isFirst;
	}

}

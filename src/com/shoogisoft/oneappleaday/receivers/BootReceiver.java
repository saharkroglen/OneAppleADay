package com.shoogisoft.oneappleaday.receivers;

import com.shoogisoft.oneappleaday.common.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(Constants.TAG,"Boot receiver called");
         if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
        	 new SyncReceiver().setAlarm(context);
//                Intent i = new Intent(context, SochActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(i);
            }
    }

}
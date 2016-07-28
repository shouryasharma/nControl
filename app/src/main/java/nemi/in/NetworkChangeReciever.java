package nemi.in;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReciever extends BroadcastReceiver{


	@Override
	public void onReceive(Context context, Intent intent) {
		if(isOnline(context)){
			Intent i = new Intent(context, SyncService.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(i);
		}
	}

	 public boolean isOnline(Context context) {
         ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo netInfo = cm.getActiveNetworkInfo();
         //should check null because in air plan mode it will be null
         return (netInfo != null && netInfo.isConnected());
     }
}

//package nemi.in;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.util.Log;
//
//public class NetworkChangeReciever extends BroadcastReceiver{
//
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//     /*   ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
//                mobile != null && mobile.isConnectedOrConnecting();
//        if (isConnected) {
//
//            Log.d("Network Available ", "YES");
//        } else {
//            Log.d("Network Available ", "NO");
//        }*/
//        if(isOnline(context)){
//            Intent i = new Intent(context, MyService.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startService(i);
//        }
//    }
//    public boolean isOnline(Context context) {
//
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        //should check null because in airplane mode it will be null
//        return (netInfo != null && netInfo.isConnected());
//
//    }
//
//}

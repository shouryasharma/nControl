//package nemi.in;
//
///**
// * Created by Aman on 8/1/2016.
// */
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.os.AsyncTask;
//import android.os.Handler;
//import android.os.IBinder;
//import android.util.Log;
//import android.widget.Toast;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.protocol.HTTP;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import common.MyProgressDialog;
//import common.Utility;
//
//public class MyService extends Service {
//    DatabaseHelper databaseHelper;
//    String node, node_password;
//    Context context;
//
//    @Override
//    public IBinder onBind(Intent arg0) {
//        return null;
//    }
//
//    @Override
//    public void onStart(Intent intent, int startId) {
//        super.onStart(intent, startId);
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        AsyncTaskRunner runner = new AsyncTaskRunner(this);
//        runner.execute();
//        Toast.makeText(getApplicationContext(),"onStartComnman....",Toast.LENGTH_SHORT).show();
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
//    }
//
//    /*===========================================================================================================*/
//    private class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {
//        MyProgressDialog dialog;
//        Context context;
//
//        public AsyncTaskRunner(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            Toast.makeText(getApplicationContext(),"onPreExecute...",Toast.LENGTH_SHORT).show();
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            databaseHelper = new DatabaseHelper(context, null, null, 1);
//            Cursor c = databaseHelper.getItems();
//            Log.d("iiiidddd : ",c.getColumnName(0));
//            Log.d("iiiidddd : ",c.getColumnName(1));
//            Log.d("iiiidddd : ",c.getColumnName(2));
//            Log.d("iiiidddd : ",c.getColumnName(3));
//
//            // Let it continue running until it is stopped.
//            SharedPreferences settings = getSharedPreferences(FragmentSettings.MyPREFERENCES, Context.MODE_PRIVATE);
//            node = settings.getString(FragmentSettings.NODE_KEY, "");
//            node_password = settings.getString(FragmentSettings.NODE_PASSWORD_KEY,"");
//            try {
//                JSONArray jsonArray = new JSONArray();
//                JSONObject obj = new JSONObject();
//                try {
//                        obj.put(Utility.CLIENT_ID_KEY, node);
//                        Log.d("NODE:",node);
//                        obj.put(Utility.PASS_KEY, node_password);
//                        Log.d("NODE_PASSWORD :",node_password);
//                        obj.put(Utility.NUM_KEY,20);
//                        obj.put(Utility.ITEM_KEY,node);
//                        obj.put(Utility.QUANTITY,2);
//                        obj.put(Utility.CATEGORY_KEY,node);
//                        obj.put(Utility.PRICE_KEY,100);
//
////                    for (int i = 0; i < c.getCount(); i++) {
////                        obj.put(Utility.CLIENT_ID_KEY, node);
////                        obj.put(Utility.PASS_KEY, node_password);
////                        obj.put(Utility.PASS_NUMBER, c.getString(0));
////                        Log.d("id : ",c.getString(0));
////                        obj.put(Utility.ITEM_KEY, c.getString(1));
////                        Log.d("item : ",c.getString(1));
////                        obj.put(Utility.QUANTITY, 1);
////                        obj.put(Utility.CATEGORY_KEY, c.getString(2));
////                        Log.d("category : ",c.getString(2));
////                        obj.put(Utility.PRICE_KEY, c.getString(3));
////                        Log.d("price : ",c.getString(3));
////                        obj.put(Utility.IMAGE_PATH, c.getString(4));
////                        Log.d("image path :",c.getString(4));
////                        c.moveToNext();
////                    }
//                    jsonArray.put(obj);
//                } catch (Exception e) {
//
//
//                    e.printStackTrace();
//                }
//                HttpClient client = new DefaultHttpClient();
//                HttpPost post = new HttpPost(Utility.ITEM_URL);
//                StringEntity entity = new StringEntity(jsonArray.toString(), HTTP.UTF_8);
//                entity.setContentType("application/json");
//                post.setHeader("Content-Type", "application/json");
//                post.setEntity(entity);
//                HttpResponse response = client.execute(post);
//                int status = response.getStatusLine().getStatusCode();
//                Log.v("STATUS", String.valueOf(status));
//                BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//                String data = "";
//                while ((data = br.readLine()) != null) {
//                    Log.v("Response String", data);
//                }
//                if (status != 200) {
//                    Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show();
//                    Log.e("Server Status code", String.valueOf(status));
//                } else {
//                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//        @Override
//        protected void onPostExecute(Void result) {
//            Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show();
//        }
//    };
//    public void callAsynchronousTask() {
//        final Handler handler = new Handler();
//        Timer timer = new Timer();
//        final TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    public void run() {
//                        AsyncTaskRunner runner = new AsyncTaskRunner(context);
//                        runner.execute();
//                    }
//                });
//            }
//        };
//        timer.schedule(task, 0, 1000); //it executes this every 1000ms
//    }
//
//}
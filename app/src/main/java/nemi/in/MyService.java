package nemi.in;

/**
 * Created by Aman on 8/1/2016.
 */

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import common.MyProgressDialog;
import common.Utility;

public class MyService extends Service {

    Context mContext;
    DatabaseHelper databaseHelper;
    String node, node_password;
    Cursor c;




    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }



    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        stopService(new Intent(this, MyService.class));
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
//        AsyncTaskRunner runner = new AsyncTaskRunner(this);
//        runner.execute();
//        AsyncTaskRunner1 runner1 = new AsyncTaskRunner1(this);
//        runner1.execute();
        Asynctasrunner();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

    }

  /*===========================================================================================================*/
    public class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {
        MyProgressDialog dialog;
      Context context;


      public AsyncTaskRunner(Context context)
      {
         this.context=context;
      }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            Log.v("Data",databaseHelper.getItems().toString());

            SharedPreferences settings = getSharedPreferences(FragmentSettings.MyPREFERENCES, Context.MODE_PRIVATE);
            node = settings.getString(FragmentSettings.NODE_KEY, "");
            node_password = settings.getString(FragmentSettings.NODE_PASSWORD_KEY, "");

        }
        @Override
        protected Void  doInBackground(Void... params) {
            databaseHelper = new DatabaseHelper(context, null, null, 1);
            c = databaseHelper.getItems();
            // Let it continue running until it is stopped.


            try {
                JSONArray jsonArray = new JSONArray();

                try {
                        while (c.moveToNext()) {
                            JSONObject obj = new JSONObject();
                            obj.put(Utility.CLIENT_ID_KEY, node);
                            obj.put(Utility.PASS_KEY, node_password);
                            obj.put(Utility.PASS_NUMBER, c.getString(0));
                            obj.put(Utility.ITEM_KEY, c.getString(1));
                            obj.put(Utility.CATEGORY_KEY, c.getString(2));
                            obj.put(Utility.PRICE_KEY, c.getString(3));
                            obj.put(Utility.IMAGE_PATH, c.getString(4));
                            jsonArray.put(obj);
//                            obj.remove(Utility.CLIENT_ID_KEY);
//                            obj.remove(Utility.PASS_KEY);
//                            obj.remove(Utility.PASS_NUMBER);
//                            obj.remove(Utility.ITEM_KEY);
//                            obj.remove(Utility.CATEGORY_KEY);
//                            obj.remove(Utility.PRICE_KEY);
//                            obj.remove(Utility.IMAGE_PATH);
                        }
                    databaseHelper.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Here");
                Log.v("JSON",jsonArray.toString());

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(Utility.ITEM_URL);
                StringEntity entity = new StringEntity(jsonArray.toString(), HTTP.UTF_8);
                entity.setContentType("application/json");
                post.setHeader("Content-Type", "application/json");

                post.setEntity(entity);
                ResponseHandler<String> responseHandler=new BasicResponseHandler();
                HttpResponse response = client.execute(post);
                int status = response.getStatusLine().getStatusCode();
                Log.v("STATUS", String.valueOf(status));
                BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String data = "";
                while ((data = br.readLine()) != null) {
                    Log.v("Response String", data);
                }
                if (status != 200) {
                    Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show();
                    Log.e("Server Status code", String.valueOf(status));
                } else {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("harry", "index=" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show();

        }
    };
    public class AsyncTaskRunner1 extends AsyncTask<Void, Void, Void> {
        MyProgressDialog dialog;
        Context context;


        public AsyncTaskRunner1(Context context)
        {
            this.context=context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            databaseHelper = new DatabaseHelper(context, null, null, 1);
            Log.v("Data",databaseHelper.getItems().toString());
            c = databaseHelper.getItems();
            SharedPreferences settings = getSharedPreferences(FragmentSettings.MyPREFERENCES, Context.MODE_PRIVATE);
            node = settings.getString(FragmentSettings.NODE_KEY, "");
            node_password = settings.getString(FragmentSettings.NODE_PASSWORD_KEY, "");

        }
        @Override
        protected Void  doInBackground(Void... params) {


            // Let it continue running until it is stopped.
            Cursor cursor = databaseHelper.getBillsInfo();

            try {
                JSONArray jsonArray = new JSONArray();

                try {
                    while (cursor.moveToNext()) {
                        String trn = cursor.getString(0);
                        Cursor cursor1 = databaseHelper.getSale(Integer.parseInt(trn));
                        while(cursor1.moveToNext()){
                            JSONObject obj = new JSONObject();
                            obj.put(Utility.CLIENT_ID_KEY, node);
                            obj.put(Utility.PASS_KEY, node_password);
                            obj.put("flushflag",0);
                            obj.put(Utility.Entrynum,cursor.getString(0));
                            obj.put("date",cursor.getString(2));
                            obj.put(Utility.Total_Bill_Amount,cursor.getString(3));
                            obj.put(Utility.Customer_Name,cursor.getString(4));
                            obj.put(Utility.Customer_Contact,cursor.getString(5));
                            obj.put(Utility.ITEM_KEY, cursor1.getString(1));
                            obj.put(Utility.PRICE_KEY, cursor1.getString(3));
                            obj.put("show",1);
                            obj.put(Utility.QUANTITY,1);
                            obj.put(Utility.PASS_NUMBER, cursor1.getString(0));
                            obj.put(Utility.CATEGORY_KEY, cursor1.getString(2));
                            jsonArray.put(obj);
//                            obj.remove(Utility.CLIENT_ID_KEY);
//                            obj.remove(Utility.PASS_KEY);
//                            obj.remove(Utility.PASS_NUMBER);
//                            obj.remove(Utility.ITEM_KEY);
//                            obj.remove(Utility.CATEGORY_KEY);
//                            obj.remove(Utility.PRICE_KEY);
//                            obj.remove(Utility.IMAGE_PATH);
                    }}
                    databaseHelper.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Here");
                Log.v("JSON1",jsonArray.toString());

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(Utility.sale_URL);
                StringEntity entity = new StringEntity(jsonArray.toString(), HTTP.UTF_8);
                entity.setContentType("application/json");
                post.setHeader("Content-Type", "application/json");

                post.setEntity(entity);
                ResponseHandler<String> responseHandler=new BasicResponseHandler();
                HttpResponse response = client.execute(post);
                int status = response.getStatusLine().getStatusCode();
                Log.v("STATUS", String.valueOf(status));
                BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String data = "";
                while ((data = br.readLine()) != null) {
                    Log.v("Response String", data);
                }
                if (status != 200) {
                    Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show();
                    Log.e("Server Status code", String.valueOf(status));
                } else {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("harry", "index=" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show();

        }
    };
    void Asynctasrunner(){
        AsyncTaskRunner runner = new AsyncTaskRunner(this);
        runner.execute();
        AsyncTaskRunner1 runner1 = new AsyncTaskRunner1(this);
        runner1.execute();
    }


}
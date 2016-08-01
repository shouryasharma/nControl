package nemi.in;

/**
 * Created by Aman on 8/1/2016.
 */

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import common.MyProgressDialog;
import common.Utility;

public class MyService extends Service {
    Context mContext;
    DatabaseHelper databaseHelper;
    String node, node_password;




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
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AsyncTaskRunner runner = new AsyncTaskRunner(this);
        runner.execute();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

  /*===========================================================================================================*/
    private class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {
        MyProgressDialog dialog;
      Context context;

      public AsyncTaskRunner(Context context)
      {
         this.context=context;
      }
        @Override
        protected void onPreExecute() {

            databaseHelper = new DatabaseHelper(context, null, null, 1);
        }
        @Override
        protected Void doInBackground(Void... params) {
            Cursor c = databaseHelper.getItems();

            // Let it continue running until it is stopped.


            SharedPreferences settings = getSharedPreferences(FragmentSettings.MyPREFERENCES, Context.MODE_PRIVATE);
            node = settings.getString(FragmentSettings.NODE_KEY, "");
            node_password = settings.getString(FragmentSettings.NODE_PASSWORD_KEY, "");

            try {
                JSONArray jsonArray = new JSONArray();
                JSONObject obj = new JSONObject();
                try {
                    for (int i = 0; i < c.getCount(); i++) {
                        obj.put(Utility.CLIENT_ID_KEY, node);
                        obj.put(Utility.PASS_KEY, node_password);
                        obj.put(Utility.PASS_NUMBER, c.getString(0));
                        obj.put(Utility.ITEM_KEY, c.getString(1));
                        obj.put(Utility.QUANTITY, 1);
                        obj.put(Utility.CATEGORY_KEY, c.getString(2));
                        obj.put(Utility.PRICE_KEY, c.getString(3));
                        obj.put(Utility.IMAGE_PATH, c.getString(4));
                        c.moveToNext();
                    }
                    jsonArray.put(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(Utility.ITEM_URL);
                StringEntity entity = new StringEntity(jsonArray.toString(), HTTP.UTF_8);
                entity.setContentType("application/json");
                post.setHeader("Content-Type", "application/json");
                post.setEntity(entity);
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show();
        }
    };
//    public void callAsynchronousTask() {
//        final Handler handler = new Handler();
//        Timer timer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    public void run() {
//                        AsyncTaskRunner runner = new AsyncTaskRunner();
//                        runner.execute();
//                    }
//                });
//            }
//        };
//        timer.schedule(task, 0, 1000); //it executes this every 1000ms
//    }

}
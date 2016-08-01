//package nemi.in;
//
//import android.app.IntentService;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.util.Log;
//import android.widget.Toast;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.protocol.HTTP;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//
//import common.Utility;
//
///**
// * Created by Aman on 7/23/2016.
// */
//public class SyncService extends IntentService{
//
//    private DatabaseHelper databaseHelper;
//
//    public SyncService() {
//        super("SyncService");
//    }
//
//    @Override
//    public void onCreate() {
//        sendDataToServer();
//        super.onCreate();
//    }
//
//    private void sendDataToServer(){
//        Toast.makeText(getApplicationContext(),"i am running ",Toast.LENGTH_SHORT).show();
//        databaseHelper = new DatabaseHelper(this, null, null, 1);
//        Cursor c = databaseHelper.getItems();
//
//        //add data in a JSONObject
//            JSONArray jsonArray = new JSONArray();
//            JSONObject obj = new JSONObject();
//            try {
//                for (int i = 0; i < c.getCount(); i++) {
//                    obj.put(Utility.CLIENT_ID_KEY, c.getString(0));
//                    obj.put(Utility.ITEM_KEY, c.getString(1));
//                    obj.put(Utility.CATEGORY_KEY, c.getString(2));
//                    obj.put(Utility.PRICE_KEY, c.getString(3));
//                    obj.put(Utility.IMAGE_PATH, c.getString(4));
//                    c.moveToNext();
//                }
//                jsonArray.put(obj);
//                HttpClient client = new DefaultHttpClient();
//                HttpPost post = new HttpPost(Utility.ITEM_URL);
//                StringEntity entity = new StringEntity(jsonArray.toString(), HTTP.UTF_8);
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
//                Toast.makeText(this, "DATA INSERTED", Toast.LENGTH_SHORT).show();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//    }
//    @Override
//    protected void onHandleIntent(Intent intent) {
//    }
//}

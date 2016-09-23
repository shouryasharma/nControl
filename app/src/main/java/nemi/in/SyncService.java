////package nemi.in;
////
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
////
/////**
//// * Created by Aman on 7/23/2016.
//// */
////public class SyncService extends IntentService{
////
////    private DatabaseHelper databaseHelper;
////
////    public SyncService() {
////        super("SyncService");
////    }
////
////    @Override
////    public void onCreate() {
////        sendDataToServer();
////        super.onCreate();
////    }
////
////    private void sendDataToServer(){
////        Toast.makeText(getApplicationContext(),"i am running ",Toast.LENGTH_SHORT).show();
////        databaseHelper = new DatabaseHelper(this, null, null, 1);
////        Cursor c = databaseHelper.getItems();
////
////        //add data in a JSONObject
////            JSONArray jsonArray = new JSONArray();
////            JSONObject obj = new JSONObject();
////            try {
////                for (int i = 0; i < c.getCount(); i++) {
////                    obj.put(Utility.CLIENT_ID_KEY, c.getString(0));
////                    obj.put(Utility.ITEM_KEY, c.getString(1));
////                    obj.put(Utility.CATEGORY_KEY, c.getString(2));
////                    obj.put(Utility.PRICE_KEY, c.getString(3));
////                    obj.put(Utility.IMAGE_PATH, c.getString(4));
////                    c.moveToNext();
////                }
////                jsonArray.put(obj);
////                HttpClient client = new DefaultHttpClient();
////                HttpPost post = new HttpPost(Utility.ITEM_URL);
////                StringEntity entity = new StringEntity(jsonArray.toString(), HTTP.UTF_8);
////                post.setHeader("Content-Type", "application/json");
////                post.setEntity(entity);
////                HttpResponse response = client.execute(post);
////                int status = response.getStatusLine().getStatusCode();
////                Log.v("STATUS", String.valueOf(status));
////                BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
////                String data = "";
////                while ((data = br.readLine()) != null) {
////                    Log.v("Response String", data);
////                }
////                Toast.makeText(this, "DATA INSERTED", Toast.LENGTH_SHORT).show();
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////    }
////    @Override
////    protected void onHandleIntent(Intent intent) {
////    }
////}
//public void exportCSV() throws IOException implements runnable {
//
//
//        File folder = new File(Environment.getExternalStorageDirectory()
//        + "/Folder");
//
//        boolean var = false;
//        if (!folder.exists())
//        var = folder.mkdir();
//
//        System.out.println("" + var);
//
//
// String filename = folder.toString() + "/" + "Test.csv";
//
//        // show waiting screen
//        CharSequence contentTitle = getString(R.string.app_name);
//final void ProgressDialog progDailog = ProgressDialog.show(
//        MailConfiguration.this, contentTitle, "even geduld aub...",
//        true);//please wait
//final void  Handler handler = new Handler() {
//public void  handleMessage(Message msg) {"hey"
//
//
//
//
//        }
//        };
//
//        new Thread() {
//         run() {
//        try {
//
//        FileWriter fw = new FileWriter(filename);
//
//        Cursor cursor = db.selectAll();
//
//        fw.append("No");
//        fw.append(',');
//
//        fw.append("code");
//        fw.append(',');
//
//        fw.append("nr");
//        fw.append(',');
//
//        fw.append("Orde");
//        fw.append(',');
//
//        fw.append("Da");
//        fw.append(',');
//
//        fw.append("Date");
//        fw.append(',');
//
//        fw.append("Leverancier");
//        fw.append(',');
//
//        fw.append("Baaln");
//        fw.append(',');
//
//        fw.append("asd");
//        fw.append(',');
//
//        fw.append("Kwaliteit");
//        fw.append(',');
//
//        fw.append("asd");
//        fw.append(',');
//
//        fw.append('\n');
//
//        if (cursor.moveToFirst()) {
//        do {
//        fw.append(cursor.getString(0));
//        fw.append(',');
//
//        fw.append(cursor.getString(1));
//        fw.append(',');
//
//        fw.append(cursor.getString(2));
//        fw.append(',');
//
//        fw.append(cursor.getString(3));
//        fw.append(',');
//
//        fw.append(cursor.getString(4));
//        fw.append(',');
//
//        fw.append(cursor.getString(5));
//        fw.append(',');
//
//        fw.append(cursor.getString(6));
//        fw.append(',');
//
//        fw.append(cursor.getString(7));
//        fw.append(',');
//
//        fw.append(cursor.getString(8));
//        fw.append(',');
//
//        fw.append(cursor.getString(9));
//        fw.append(',');
//
//        fw.append(cursor.getString(10));
//        fw.append(',');
//
//        fw.append('\n');
//
//        } while (cursor.moveToNext());
//        }
//        if (cursor != null && !cursor.isClosed()) {
//        cursor.close();
//        }
//
//        // fw.flush();
//        fw.close();
//
//        } catch (Exception e) {
//        }
//        handler.sendEmptyMessage(0);
//        progDailog.dismiss();
//        }
//        }.start();
//
//        }
//

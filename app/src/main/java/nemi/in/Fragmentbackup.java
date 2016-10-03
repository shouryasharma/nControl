package nemi.in;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import common.Utility;
import common.logger.Log;
import in.nemi.ncontrol.R;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Aman on 5/3/2016.
 */
public class Fragmentbackup extends Fragment {

    private TextView filePath;
    private Button btnBrowse;
    private Button restoreBtn;
    private File selectedFile;
    private static final int REQUEST_PICK_FILE = 1;
    private  static  final int MODE_WORLD_READABLE = 0000;
    public Cursor cursor;

    public Fragmentbackup() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_backup, container, false);
            super.onCreate(savedInstanceState);
        filePath = (TextView)rootView.findViewById(R.id.file_picker_text);
        btnBrowse = (Button) rootView.findViewById(R.id.btnBackup);
        Button restorBtn = (Button)rootView.findViewById(R.id.restorebutton);
        restorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getActivity().getApplicationContext(), "restore buton clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setType("Documents/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_PICK_FILE);
            }




        });
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                itemBackup();
                salesBackup();
                syncItems();
//                MyService a= new MyService();
//                        a.callAsynchronousTask();
//                Toast.makeText(getActivity().getApplicationContext(), "browse button clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setType("Documents/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_PICK_FILE);
            }
        });

               return rootView;
                    }


    public  void itemBackup() {

        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), null, null, 1);
        Cursor cursor = databaseHelper.getItems();
        String content = "";
        File file;
        while(cursor.moveToNext()){
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String category = cursor.getString(2);
            String prize = cursor.getString(3);
            String path = cursor.getString(4);
            String content1 = id+","+name+","+prize+","+category+","+path+"\n"+"";
            content = content + content1;
        }
                databaseHelper.close();
        FileOutputStream outputStream;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "items.csv");

            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public  void salesBackup() {

        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), null, null, 1);
        Cursor cursor = databaseHelper.getBillsInfo();
        String content = "";
        File file;
        while(cursor.moveToNext()){
            DatabaseHelper databaseHelper1 = new DatabaseHelper(getActivity(), null, null, 1);
            String trn = cursor.getString(0);
            String bill = cursor.getString(1);
            String amount = cursor.getString(2);
            String ttime = cursor.getString(3);
            String cname = cursor.getString(4);
            String ccont = cursor.getString(5);
            Cursor cursor1 = databaseHelper.getSale(Integer.parseInt(trn));
            while(cursor1.moveToNext()){
                String iid = cursor1.getString(0);
                String item = cursor1.getString(1);
                String qtyy = cursor1.getString(2);
                String price = cursor1.getString(3);
            String content1 = trn+","+bill+","+amount+","+ttime+","+iid+","+cname+","+ccont+","+item+","+qtyy+","+price+"\n"+"";
            content = content + content1;
        }}
        databaseHelper.close();
        FileOutputStream outputStream;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "bills.csv");

            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
       public void syncItems(){
           String node, node_password;
           SharedPreferences settings = getActivity().getSharedPreferences(FragmentSettings.MyPREFERENCES, Context.MODE_PRIVATE);
           node = settings.getString(FragmentSettings.NODE_KEY, "");
           node_password = settings.getString(FragmentSettings.NODE_PASSWORD_KEY, "");
           DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), null, null, 1);
           Cursor c = databaseHelper.getItems();
                    try {
                            JSONArray jsonArrayItems = new JSONArray();
                            JSONObject obj = new JSONObject();

                            try {
//                                for (int i = 0; i < c.getCount(); i++) {
                                while(c.moveToNext()){
                                    obj.put(Utility.CLIENT_ID_KEY, node);
                                    obj.put(Utility.PASS_KEY, node_password);
                                    obj.put(Utility.PASS_NUMBER, c.getString(0));
                                    obj.put(Utility.ITEM_KEY, c.getString(1));
                                    obj.put(Utility.CATEGORY_KEY, c.getString(2));
                                    obj.put(Utility.PRICE_KEY, c.getString(3));
                                    obj.put(Utility.IMAGE_PATH, c.getString(4));

                                    jsonArrayItems.put(obj);

                                }

                                sendItems(jsonArrayItems);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


    public void sendItems(JSONArray jsonArray){
        Toast.makeText(getActivity().getApplicationContext(), jsonArray.toString(), Toast.LENGTH_SHORT).show();
                       try{
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
                                Toast.makeText(getActivity().getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();
                                Log.e("Server Status code", String.valueOf(status));
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            }

    }catch (Exception e){
                           Toast.makeText(getActivity().getApplicationContext(), "network error", Toast.LENGTH_SHORT).show();
                       }
    }
}



package nemi.in;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.Voice;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import common.logger.Log;
import in.nemi.ncontrol.R;


/**
 * Created by shouryas on 4/21/2016.
 */
public class ActivityMain extends Activity {



    DatabaseHelper databaseHelper;
    EditText username_super, password_super, confirm_password_super, username, password;
    Button add, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        databaseHelper = new DatabaseHelper(this, null, null, 1);
        databaseHelper.ClearloginStatus();
        startService(new Intent(this, MyService.class));
        stopService(new Intent(this, MyService.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        username = (EditText) findViewById(R.id.ed_username_1);
        password = (EditText) findViewById(R.id.ed_password_1);
        login = (Button) findViewById(R.id.button_login);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7B7BC0")));
        //check for superuser
        Boolean a = databaseHelper.checkS();
        if (!a) {
            //Dialog with custom layout to add super
            final Dialog d = new Dialog(ActivityMain.this);
            d.setContentView(R.layout.dialog_create_super);
            d.setTitle("Super doesn't exist!");
            d.setCancelable(false);
            d.show();
            username_super = (EditText) d.findViewById(R.id.editText);
            password_super = (EditText) d.findViewById(R.id.editText2);
            confirm_password_super = (EditText) d.findViewById(R.id.editText3);

            add = (Button) d.findViewById(R.id.addsuper);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String u = username_super.getText().toString();
                    String p = password_super.getText().toString();
                    String conf_pass = confirm_password_super.getText().toString();

                    String r = "SUPER";
                    if (u.equals("")) {
                        username_super.setError("User name");
                    } else if (p.equals("")) {
                        password_super.setError("Password");

                    } else if (p.compareTo(conf_pass) != 0) {
                        confirm_password_super.setError("Password is not correct");
                    } else {
                        databaseHelper.addUser(r, u, p);
                        d.dismiss();
                    }
                }
            });
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                if (user.equals("")) {
                    username.setError("Username");
                } else if (pass.equals("")) {
                    password.setError("password");
                } else if (pass.equals(databaseHelper.loginUser(user))) {
                    Intent i = new Intent(ActivityMain.this, ActivityNavDrawer.class);
//                    change login status of the logged in user
                    databaseHelper.loginStatus("true", user);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect username or password", Toast.LENGTH_LONG).show();
                }
                username.setText("");
                password.setText("");

            }
        });
//        username.addTextChangedListener(new TextWatcher() {
//            int a;
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                 a = username.getText().toString().length();
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if(a == 0){
//                if(username.getText().toString().length()==1){
//                    roled(username.getText().toString());
//                }}
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

    }
//    void roled(String a){
//        Cursor cursor = databaseHelper.getUsersname(a);
//        if(cursor.getCount() != 0){
//            if(!username.getText().toString().equalsIgnoreCase("")) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);
//                builder.setTitle("Please select a User");
//                ListView dialogCatList = new ListView(ActivityMain.this);
//
//                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ActivityMain.this,
//                        android.R.layout.simple_list_item_1);
//                while (cursor.moveToNext()){
//                    arrayAdapter.add(cursor.getString(0));
//                }
//                dialogCatList.setAdapter(arrayAdapter);
//                builder.setView(dialogCatList);
//                final Dialog dialog = builder.create();
//                dialogCatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        String strName = arrayAdapter.getItem(position);
//                        username.setText(strName);
//                        username.setEnabled(true);
//                        dialog.cancel();
//                    }
//                });
//                dialog.show();
//            }}
//
//    }
}
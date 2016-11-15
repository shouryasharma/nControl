package nemi.in;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import common.logger.Log;
import in.nemi.ncontrol.R;

/**
 * Created by Aman on 7/15/2016.
 */
public class FragmentSettings extends Fragment implements View.OnClickListener {
    EditText etAddress, et_name_company, et_address_company, et_thank_you, et_tin_number, et_service_tax, et_vat;
    EditText et_node, et_node_password,flushtime;
    RadioButton radioButton1,radioButton11,radioButton2,radioButton22;
    RadioGroup radioGroupb,radioGroupa,radioGroupc,radioGroupd;
    DatabaseHelper databaseHelper;
    EditText tax_value,tax_name,tax_paying_number;
    TextView idfsv;
    RadioButton kot1,kot2,klb1,klb2,ss2,ss1,ft1,ft2,ft3;
    Button taxAdd;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String BLUETOOTH_KEY = "Bluetooth_address";
    public static final String NAME_COMPANY_KEY = "name_shop";
    public static final String ADDRESS_COMPANY_KEY = "address_shop";
    public static final String THANK_YOU_KEY = "Thank_you";
    public static final String TIN_NUMBER_KEY = "Powered_by";
    public static final String SERVICE_TAX_KEY = "service_tax";
    public static final String VAT_KEY = "vat";
    public static final String NODE_KEY = "node";
    public static final String NODE_PASSWORD_KEY = "password";
    public static final String KEEP_LOCAL_BACKUP = "localback";
    public static final String FLUSH_TIME_INTERVAL = "flushtime";
    public static final String SERVERSYNC = "serversync";
    public static final String KOT = "kot";
    TaxAdapter taxAdapter;
    ListView taxview;


    private IntentFilter intentFilter = null;
    SharedPreferences sharedpreferences;
    String address;
    String name_company;
    String address_company;
    String thank_you;
    String tin_number;
    String service_tax, vat;
    String node_password;
    String node = null;
    String localbackup;
    String flusht;
    String kot;
    String ss;
    String flush;

    String value = "No Hardware Address";

    public FragmentSettings() {
    }

    @Override
    public void onResume() {
        SharedPreferences settings = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        address = settings.getString(FragmentSettings.BLUETOOTH_KEY, "");

        name_company = settings.getString(FragmentSettings.NAME_COMPANY_KEY, "");
        address_company = settings.getString(FragmentSettings.ADDRESS_COMPANY_KEY, "");
        thank_you = settings.getString(FragmentSettings.THANK_YOU_KEY, "");
        tin_number = settings.getString(FragmentSettings.TIN_NUMBER_KEY, "");
        service_tax = settings.getString(FragmentSettings.SERVICE_TAX_KEY, "");
        vat = settings.getString(FragmentSettings.VAT_KEY, "");
        node = settings.getString(FragmentSettings.NODE_KEY, null);
        node_password = settings.getString(FragmentSettings.NODE_PASSWORD_KEY,"");
        kot = settings.getString(FragmentSettings.KOT,"");
        ss = settings.getString(FragmentSettings.SERVERSYNC,"");
        localbackup = settings.getString(FragmentSettings.KEEP_LOCAL_BACKUP,"");
        flusht = settings.getString(FragmentSettings.FLUSH_TIME_INTERVAL,"");
        flushtime.setText(flusht);
        etAddress.setText(address);
        et_name_company.setText(name_company);
        et_address_company.setText(address_company);
        et_thank_you.setText(thank_you);
        et_tin_number.setText(tin_number);
        et_node.setText(node);
        et_node_password.setText(node_password);
        if(!kot.equalsIgnoreCase("")){
        if(kot.equalsIgnoreCase("0")){
            kot1.setChecked(true);
        }else{
            kot2.setChecked(true);
        }}
        if(!ss.equalsIgnoreCase("")){
        if(ss.equalsIgnoreCase("0")){
            ss1.setChecked(true);
        }else {
            ss2.setChecked(true);
        }}
        if(!localbackup.equalsIgnoreCase("")){
        if(localbackup.equalsIgnoreCase("0")){
            klb1.setChecked(true);
        }else {
            klb2.setChecked(true);
        }}

        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_view, container, false);
        databaseHelper = new DatabaseHelper(getActivity(), null, null, 1);
        kot1 = (RadioButton) view.findViewById(R.id.kot1);
        kot2 = (RadioButton) view.findViewById(R.id.kot2);
        ss1 = (RadioButton) view.findViewById(R.id.ss1);
        ss2 = (RadioButton) view.findViewById(R.id.ss2);
        klb1 = (RadioButton) view.findViewById(R.id.klb1);
        klb2 = (RadioButton) view.findViewById(R.id.klb2);
        flushtime = (EditText) view.findViewById(R.id.flushtime);

        taxAdapter = new TaxAdapter(getActivity(), databaseHelper.gettax());
        taxview = (ListView) view.findViewById(R.id.taxlist);
        taxview.setAdapter(taxAdapter);

//        Toast.makeText(getActivity(), "This is current printer : " + value, Toast.LENGTH_SHORT).show();
        etAddress = (EditText) view.findViewById(R.id.etAddress);
        et_name_company = (EditText) view.findViewById(R.id.name_company_id);
        et_address_company = (EditText) view.findViewById(R.id.address_company_id);
        et_thank_you = (EditText) view.findViewById(R.id.thank_you_id);
        et_tin_number = (EditText) view.findViewById(R.id.tin_number_id);
        et_node = (EditText) view.findViewById(R.id.et_node_id);
        et_node_password = (EditText) view.findViewById(R.id.et_node_password_id);
        radioGroupa = (RadioGroup) view.findViewById(R.id.klb);
        radioGroupc =(RadioGroup) view.findViewById(R.id.ss);
        radioGroupd = (RadioGroup) view.findViewById(R.id.kot);
        idfsv =(TextView) view.findViewById(R.id.tax_id_fsv);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        tax_name = (EditText)view.findViewById(R.id.tax_name);
        tax_value = (EditText)view.findViewById(R.id.tax_values);
        tax_paying_number = (EditText)view.findViewById(R.id.tax_paying_number);
        taxAdd = (Button)view.findViewById(R.id.taxes_confi_id);
        taxAdd.setOnClickListener(this);

        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                address = etAddress.getText().toString();
                if (address.trim().length() == 17) {
                    databaseHelper.addHAddress(address);
                } else {
                    etAddress.setError("Warning : This is not correct.(Ex:- 00:02:0A:03:1D:F5)");
                }

                //lets check whether the mac address length is equals to 17 or not
                if (address.trim().length() == 17) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(BLUETOOTH_KEY, address);
                    editor.commit();
                } else {

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        flushtime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
              flush = flushtime.getText().toString();
                if(!flush.equalsIgnoreCase("")){
                if(Integer.parseInt(flush)>0){
                    if(Integer.parseInt(flush)<8){
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(FLUSH_TIME_INTERVAL, flush);
                        editor.commit();
                    }else {flushtime.setError("value must be smaller then 8");}
                }else {flushtime.setError("value must be greater then 0");}
            }}

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_name_company.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name_company = et_name_company.getText().toString();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(NAME_COMPANY_KEY, name_company);
                    editor.commit();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_address_company.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                address_company =et_address_company.getText().toString();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(ADDRESS_COMPANY_KEY, address_company);
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_thank_you.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                thank_you =et_thank_you.getText().toString();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(THANK_YOU_KEY, thank_you);
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_tin_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tin_number =et_tin_number.getText().toString();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(TIN_NUMBER_KEY, tin_number);
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_node.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                node =et_node.getText().toString();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(NODE_KEY, node);
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_node_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                node_password =et_node_password.getText().toString();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(NODE_PASSWORD_KEY, node_password);
                editor.commit();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        radioGroupa.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = radioGroupa.getCheckedRadioButtonId();
                radioButton1 = (RadioButton)radioGroupa.findViewById(selectedId);
                int ida = radioGroupa.indexOfChild(radioButton1);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(KEEP_LOCAL_BACKUP, String.valueOf(ida));
                editor.commit();

            }
        });
//        radioGroupb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                String radiovalue = null;
//                int selectedId1 = radioGroupb.getCheckedRadioButtonId();
//                radioButton11 =(RadioButton) radioGroupb.findViewById(selectedId1);
//                int idb = radioGroupb.indexOfChild(radioButton11);
//                if(idb == 0)
//                {radiovalue = "1";}
//                if(idb ==1)
//                {radiovalue ="4";}
//                if(idb == 2)
//                {radiovalue = "7";}
//                SharedPreferences.Editor editor = sharedpreferences.edit();
//                editor.putString(FLUSH_TIME_INTERVAL, radiovalue);
//                editor.commit();
//
//            }
//        });
        radioGroupc.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId2 = radioGroupc.getCheckedRadioButtonId();
                radioButton2 = (RadioButton)radioGroupc.findViewById(selectedId2);
                int idc = radioGroupc.indexOfChild(radioButton2);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(SERVERSYNC,String.valueOf(idc));
                editor.commit();
            }
        });
        radioGroupd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedID3 = radioGroupd.getCheckedRadioButtonId();
                radioButton22 = (RadioButton)radioGroupd.findViewById(selectedID3);
                int idd = radioGroupd.indexOfChild(radioButton22);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(KOT,String.valueOf(idd));
                    editor.commit();

            }
        });
        return view;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.taxes_confi_id:
                String idf = idfsv.getText().toString();
                String name = tax_name.getText().toString();
                String value = tax_value.getText().toString();
                String taxpn = tax_paying_number.getText().toString();
                if(idf.equalsIgnoreCase("harry")){
                if(name.equalsIgnoreCase("")){
                    tax_name.setError("Warning : Please Fill Values");
                } else if(value.equalsIgnoreCase("")){
                    tax_value.setError("Warning : Please Fill Values");
                } else if(taxpn.equalsIgnoreCase("")){
                    tax_paying_number.setError("Waring : Please Fill Values");
                } else {
                    databaseHelper.addtax(name,value,taxpn);
                    Cursor cursor = databaseHelper.gettax();
                    taxAdapter.changeCursor(cursor);
                    databaseHelper.close();
                    tax_value.setText("");
                    tax_name.setText("");
                    tax_paying_number.setText("");
                }
                }else{
                    databaseHelper.updatetax(idf,name,value,taxpn);
                    Cursor cursor = databaseHelper.gettax();
                    taxAdapter.changeCursor(cursor);
                    databaseHelper.close();
                    tax_value.setText("");
                    tax_name.setText("");
                    tax_paying_number.setText("");
                    idfsv.setText("harry");
                }
                break;

            case R.id.etAddress:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Please select a Category");
                builder.setIcon(R.drawable.question_mark);
                ListView dialogCatList = new ListView(getActivity());
                HardwareAddressAdapter hardwareAddressAdapter = new HardwareAddressAdapter(getActivity()
                        , databaseHelper.getHAddress());
                dialogCatList.setAdapter(hardwareAddressAdapter);
                builder.setView(dialogCatList);
                final Dialog dialog_set_qty = builder.create();

                dialogCatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView tv_address = (TextView) view.findViewById(R.id.tv_old_haddress_id);
                        String address = tv_address.getText().toString();
                        etAddress.setText(address);

                        etAddress.setEnabled(false);
                        dialog_set_qty.cancel();
                    }
                });
                dialog_set_qty.show();
                break;
        }
    }



    // store h/w address in to the database
    public class HardwareAddressAdapter extends CursorAdapter {
        public HardwareAddressAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.adapter_hardware_number, parent, false);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tv_column = (TextView) view.findViewById(R.id.tv_haddress_old_column_id);
            TextView tv_store_haddress = (TextView) view.findViewById(R.id.tv_old_haddress_id);
            tv_column.setText(cursor.getString(0));
            tv_store_haddress.setText(cursor.getString(1));
        }
    }

    public class TaxAdapter extends CursorAdapter {
        public TaxAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.taxes, parent, false);
            return view;
        }

        @Override
        public void bindView(final View view, Context context, Cursor cursor) {
            TextView a = (TextView) view.findViewById(R.id.tax_id);
            TextView a1 = (TextView) view.findViewById(R.id.textout);
            TextView a2 = (TextView) view.findViewById(R.id.textout1);
            TextView a3 = (TextView) view.findViewById(R.id.textout2);
            ImageButton delete = (ImageButton) view.findViewById(R.id.remove);
            ImageButton update = (ImageButton) view.findViewById(R.id.update);
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.chkone);
            final CheckBox checkBox1 = (CheckBox) view.findViewById(R.id.chktwo);

            a.setText(cursor.getString(0));
            a1.setText(cursor.getString(1));
            a2.setText(cursor.getString(2));
            a3.setText(cursor.getString(3));


            final String val = a.getText().toString();
            final  String val1 = a1.getText().toString();
            final String val2 = a2.getText().toString();
            final String val3 = a3.getText().toString();
            String a4 = cursor.getString(4);
            String a5 = cursor.getString(5);
            if(!a4.equalsIgnoreCase("")){
                if(a4.equalsIgnoreCase("1")){
                    checkBox.setChecked(true);
                }}
            if(!a5.equalsIgnoreCase("")){
                if(a5.equalsIgnoreCase("1")){
                    checkBox1.setChecked(true);
                }}

            checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(checkBox1.isChecked()== true){
                        databaseHelper.taxidprint(val,"1");


                    }else{
                        databaseHelper.taxidprint(val,"0");
                    }
                }
            });
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(checkBox.isChecked()== true){
                        databaseHelper.taxvalueprint(val,"1");

                    }else{
                        databaseHelper.taxvalueprint(val,"0");
                    }
                }
            });




            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (LoggedInRole.equals("SUPER")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Please select an action!");
                    alertDialogBuilder.setIcon(R.drawable.question_mark);
                    alertDialogBuilder.setMessage("Are you sure you want to delete this Tax ?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    databaseHelper.deletetax(val);
                                    Cursor cursor = databaseHelper.gettax();
                                    taxAdapter.changeCursor(cursor);
                                    databaseHelper.close();
                                }
                            }).setCancelable(false).setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
//
                }
            });
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (LoggedInRole.equals("SUPER")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Please select an action!");
                    alertDialogBuilder.setIcon(R.drawable.question_mark);
                    alertDialogBuilder.setMessage("Are you sure you want to update this?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    idfsv.setText(val);

                                    tax_name.setText(val1);
                                    tax_value.setText(val2);
                                    tax_paying_number.setText(val3);
                                    Cursor cursor = databaseHelper.gettax();
                                    taxAdapter.changeCursor(cursor);
                                    databaseHelper.close();
                                }
                            }).setCancelable(false).setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
//                    } else {
//                        Toast.makeText(getActivity(), "You need to be logged in as SUPER to perform this action!", Toast.LENGTH_SHORT).show();
//                    }
                }
            });
            taxview.setOnTouchListener(new ListView.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(event);
                    return true;
                }
            });

        }


    }
}

package nemi.in;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import in.nemi.ncontrol.R;

/**
 * Created by Aman on 7/15/2016.
 */
public class FragmentSettings extends Fragment implements View.OnClickListener {
    EditText etAddress, et_name_company, et_address_company, et_thank_you, et_tin_number, et_service_tax, et_vat;
    EditText et_node, et_node_password;
    Button buttonAdd, add_bill_conf_btn, connect_btn;
    DatabaseHelper databaseHelper;
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
        node_password = settings.getString(FragmentSettings.NODE_PASSWORD_KEY, null);
        etAddress.setText(address);
        et_name_company.setText(name_company);
        et_address_company.setText(address_company);
        et_thank_you.setText(thank_you);
        et_tin_number.setText(tin_number);
        et_service_tax.setText(service_tax);
        et_vat.setText(vat);

        et_node.setText(node);
        et_node_password.setText(node_password);
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_view, container, false);
        databaseHelper = new DatabaseHelper(getActivity(), null, null, 1);

//        Toast.makeText(getActivity(), "This is current printer : " + value, Toast.LENGTH_SHORT).show();
        etAddress = (EditText) view.findViewById(R.id.etAddress);
        et_name_company = (EditText) view.findViewById(R.id.name_company_id);
        et_address_company = (EditText) view.findViewById(R.id.address_company_id);
        et_thank_you = (EditText) view.findViewById(R.id.thank_you_id);
        et_tin_number = (EditText) view.findViewById(R.id.tin_number_id);
        et_service_tax = (EditText) view.findViewById(R.id.service_tax_id);
        et_vat = (EditText) view.findViewById(R.id.vat_id);
        et_node = (EditText) view.findViewById(R.id.et_node_id);
        et_node_password = (EditText) view.findViewById(R.id.et_node_password_id);


        buttonAdd = (Button) view.findViewById(R.id.btnAdd);
        connect_btn = (Button) view.findViewById(R.id.connect_btn_id);
        add_bill_conf_btn = (Button) view.findViewById(R.id.print_bill_confi_id);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        connect_btn.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        add_bill_conf_btn.setOnClickListener(this);
        etAddress.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:

                address = etAddress.getText().toString();
                if (address.trim().length() == 17) {
                    databaseHelper.addHAddress(address);
                    etAddress.setText("");
                } else {
                    etAddress.setError("Warning : This is not correct.(Ex:- 00:02:0A:03:1D:F5)");
                }

                //lets check whether the mac address length is equals to 17 or not
                if (address.trim().length() == 17) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(BLUETOOTH_KEY, address);
                    editor.commit();
                } else {
                    //tell the user to check the mac address
                    Toast.makeText(getActivity(), "Your MAC address is not correct!", Toast.LENGTH_SHORT).show();
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
            case R.id.print_bill_confi_id:
                // Bill Configure here
                name_company = et_name_company.getText().toString();
                address_company = et_address_company.getText().toString();
                thank_you = et_thank_you.getText().toString();
                tin_number = et_tin_number.getText().toString();
                service_tax = et_service_tax.getText().toString();
                vat = et_vat.getText().toString();
                if (name_company.equals("")) {
                    et_name_company.setError("Warning: Company name is compulsory !");
                } else if (address_company.equals("")) {
                    et_address_company.setError("Warning: Company address is compulsory !");
                } else if (thank_you.equals("")) {
                    et_thank_you.setError("Warning: Thank you statement is compulsory !");
                } else {

                    et_name_company.setText("");
                    et_address_company.setText("");
                    et_thank_you.setText("");
                    et_tin_number.setText("");
                    et_service_tax.setText("");
                    et_vat.setText("");
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(NAME_COMPANY_KEY, name_company);
                    editor.putString(ADDRESS_COMPANY_KEY, address_company);
                    editor.putString(THANK_YOU_KEY, thank_you);
                    editor.putString(TIN_NUMBER_KEY, tin_number);
                    editor.putString(SERVICE_TAX_KEY, String.valueOf(service_tax));
                    editor.putString(VAT_KEY, String.valueOf(vat));
                    editor.commit();
                }
                break;
            case R.id.connect_btn_id:
                node = et_node.getText().toString();
                node_password = et_node_password.getText().toString();
                if (node.equals("")) {
                    et_node.setError("Warning: Node is compulsory !");
                } else if (node_password.equals("")) {
                    et_node_password.setError("Warning: Node and password is not correct !");
                } else {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(NODE_KEY, node);
                    editor.putString(NODE_PASSWORD_KEY, node_password);
                    editor.commit();
                    et_node.setText("");
                    et_node_password.setText("");

                }

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
}

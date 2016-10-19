package nemi.in;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import common.view.SlidingTabLayout;
import in.nemi.ncontrol.R;
import printing.DrawerService;
import printing.Global;

import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class FragmentPOS extends Fragment {
    ListView lv, items_list;
    static Button pay_button;
    Button clear_button, set_qty_btn, delete_bill_btn, set, cancel, tv_selected_qty_on_pos;
    TextView total_amo;
    EditText qty_et, c_name_et, c_contact_et;
    ArrayAdapter<BillItems> billAdap;
    ArrayList<BillItems> alist;
    TextView tv_id__pos_column, tv_item_on_pos, tv_price_on_pos;
    TextView tv_selected_id_on_pos, tv_selected_item_on_pos, tv_selected_price_on_pos, tv_selected_amount_on_pos;
    int decre = 0;
    DatabaseHelper databaseHelper;
    private IntentFilter intentFilter = null;
    BroadcastReceiver broadcastReceiver;

//    "00:02:0A:03:1D:F5": big
//    "88:68:2E:00:31:4A": small

    String c_name = null;
    String blank;
    String c_contact = null;
    String mode = "CARD";
    int flag;
    Button decrease;
    public static byte[] buf = null;
    BillItems billItems;
    public static ProgressDialog progress;

    int total = 0;
    private static final int TIME_TO_AUTOMATICALLY_DISMISS_ITEM = 3000;
    String bluetooth_address, company_name_sp, company_address_sp, thank_you_sp, tin_number_sp;
    String node,node_password,kot;
    String serivce_tax_sp, vat_sp;
    Thread t;

    int billnumber = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pos, container, false);


        SharedPreferences settings = getActivity().getSharedPreferences(FragmentSettings.MyPREFERENCES, Context.MODE_PRIVATE);
        // Reading from SharedPreferences
        bluetooth_address = settings.getString(FragmentSettings.BLUETOOTH_KEY, "");
        company_name_sp = settings.getString(FragmentSettings.NAME_COMPANY_KEY, "");
        company_address_sp = settings.getString(FragmentSettings.ADDRESS_COMPANY_KEY, "");
        thank_you_sp = settings.getString(FragmentSettings.THANK_YOU_KEY, "");
        tin_number_sp = settings.getString(FragmentSettings.TIN_NUMBER_KEY, "");
        serivce_tax_sp = settings.getString(FragmentSettings.SERVICE_TAX_KEY, "");
        vat_sp = settings.getString(FragmentSettings.VAT_KEY, "");
        node = settings.getString(FragmentSettings.NODE_KEY, "");
        node_password = settings.getString(FragmentSettings.NODE_PASSWORD_KEY, "");
        kot = settings.getString(FragmentSettings.KOT,"");

        tv_id__pos_column = (TextView) view.findViewById(R.id._id_on_pos_id);
        tv_item_on_pos = (TextView) view.findViewById(R.id.item_on_pos_id);
        tv_price_on_pos = (TextView) view.findViewById(R.id.price_on_pos_id);
        alist = new ArrayList<BillItems>();
        lv = (ListView) view.findViewById(R.id.userlist);
        total_amo = (TextView) view.findViewById(R.id.total_amo);
        databaseHelper = new DatabaseHelper(getActivity(), null, null, 1);
        pay_button = (Button) view.findViewById(R.id.pay);
        clear_button = (Button) view.findViewById(R.id.clear);
        c_name_et = (EditText) view.findViewById(R.id.c_name_id);
        c_contact_et = (EditText) view.findViewById(R.id.c_number_id);
        initBroadcast();
        //pay_button.setEnabled(false);



        pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!alist.isEmpty()) {

                    if (DrawerService.workThread.isConnected()) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle("Please select an action!");
                        alertDialogBuilder.setIcon(R.drawable.question_mark);
                        alertDialogBuilder.setMessage("Payment Mode").setCancelable(false)
                                .setPositiveButton("CASH", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mode = "CASH";
                                        if(kot.equalsIgnoreCase("1")){
                                            waitt();
                                        }

                                        billprint();
                                    }
                                }).setCancelable(false).setNeutralButton("CARD", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mode = "Card";
                                if(kot.equalsIgnoreCase("1")){
                                    waitt();
                                }

                                billprint();
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

//

                    } else {

                        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                        progress = new ProgressDialog(getActivity());
                        progress.setTitle("Connecting to Printer...");
                        progress.setMessage("Click the PAY button again after the printer connects.");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.show();



                        if (null == adapter) {
                        }
                        if (!adapter.isEnabled()) {
                            if (adapter.enable()) {
                                while (!adapter.isEnabled()) ;
                            } else {
                                Log.v("jay","bt off");
                            }
                        }
                        adapter.cancelDiscovery();
                        adapter.startDiscovery();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please select some item for paying Thank you ", Toast.LENGTH_SHORT).show();
                }


            }}
//        }
    );
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!alist.isEmpty()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Please select an action!");
                    alertDialogBuilder.setIcon(R.drawable.question_mark);
                    alertDialogBuilder.setMessage("Are you sure you want to clear all items!").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    billAdap.clear();
                                    total_amo.setText("0");
                                }
                            }).setCancelable(false).setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

        return view;

    }
    public static void harry(){
        progress.cancel();
        progress.dismiss();

    }
    public void waitt(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Please select an action!");
        alertDialogBuilder.setIcon(R.drawable.question_mark);
        alertDialogBuilder.setMessage("Tear the bill and proceed for KOT slip.").setCancelable(false)
                .setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        kot();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void billprint(){
        if (DrawerService.workThread.isConnected()) {

            c_name = c_name_et.getText().toString();
            c_contact = c_contact_et.getText().toString();
            String printDatap2 = "";


            //pura game yahi pr he .......................
            billnumber = databaseHelper.checkLastBillDate();
            billnumber++;

            int billnumber1 =databaseHelper.checkLastBillNumber();
            billnumber1++;

//                    Toast.makeText(getActivity(), "Billnumber is : " + billnumber, Toast.LENGTH_SHORT).show();
            // this is for sales items
            for (int i = 0; i < alist.size(); i++) {
                // bill number, item, qty, price inserted into sales table from here
                databaseHelper.sales(billnumber1, alist.get(i).getItem(), alist.get(i).getQty(), alist.get(i).getPrice());
                String item = alist.get(i).getItem();
                String qty = String.valueOf(alist.get(i).getQty());
                String price = String.valueOf(alist.get(i).getPrice());
                //this is code for substring of String
                blank = " ";
                if (item.length() >= 12) {
                    item = item.substring(0, 12);
                } else {
                    int b = 12 - item.length();
                    for (int k = 0; k < b; k++) {
                        item += blank;
                    }
                }

                if (price.length() > 0) {
                    int d = 4 - price.length();
                    for (int p = 0; p < d; p++) {
                        price = blank + price;
                    }
                }

                if (qty.length() > 0) {
                    int c = 2 - qty.length();
                    for (int q = 0; q < c; q++) {
                        qty = blank + qty;
                    }
                }


                String amount = String.valueOf(alist.get(i).getQty() * alist.get(i).getPrice());
                if (amount.length() >= 0) {
                    int m = 5 - amount.length();
                    for (int p = 0; p < m; p++) {
                        amount = blank + amount;
                    }
                }
                printDatap2 += item + " - " + price + " " + qty + "  " + amount + "\n";
            }

            // Calculation of items in sales
            int total = 0;
            for (int j = 0; j < alist.size(); j++) {
                total += alist.get(j).getPrice() * alist.get(j).getQty();
                total_amo.setText("" + total);
            }

            String t = String.valueOf(total);
            if (t.length() >= 0) {
                int tot = 5 - t.length();
                for (int p = 0; p < tot; p++) {
                    t = blank + t;
                }
            }
            // Calculation of service tax and vat here

            float service_tax_total = (Float.parseFloat(String.valueOf(total)) * Float.parseFloat(serivce_tax_sp)) / 100;
            String tax = String.valueOf(service_tax_total);
            if (tax.length() >= 0) {
                int tot = 6 - tax.length();
                for (int p = 0; p < tot; p++) {
                    tax = blank + tax;
                }
            }
            float vat_total = (Float.parseFloat(String.valueOf(total)) * Float.parseFloat(vat_sp)) / 100;
            String vat = String.valueOf(vat_total);
            if (vat.length() >= 0) {
                int tot = 6 - vat.length();
                for (int p = 0; p < tot; p++) {
                    vat = blank + vat;
                }
            }
            float total_Amount_Servie_Vat = total + service_tax_total + vat_total;
            String total_ASV = String.valueOf(total_Amount_Servie_Vat);
            if (total_ASV.length() >= 0) {
                int tot = 7 - total_ASV.length();
                for (int p = 0; p < tot; p++) {
                    total_ASV = blank + total_ASV;
                }
            }

                        /* This is using for bill configuration...*/
            if (company_name_sp.length() < 32) {
                int name_sp = 32 - company_name_sp.length();
                int name = name_sp / 2;
                for (int i = 0; i < name; i++) {
                    company_name_sp = blank + company_name_sp;
                }
            }
            StringBuffer finalString = new StringBuffer();
            String mainString = company_address_sp;
            String[] stringArray = mainString.split("\\s+");
            String tmpString = "";
            for (String singleWord : stringArray) {
                if ((tmpString + singleWord + " ").length() >= 32) {
                    finalString.append(tmpString + "\n");
                    tmpString = singleWord + " ";
                } else {
                    tmpString = tmpString + singleWord + " ";
                }
            }
            if (tmpString.length() > 0) {
                finalString.append(tmpString);
            }
            Log.e("this is : ", finalString.toString());
            if (thank_you_sp.length() < 32) {
                int address_sp = 32 - thank_you_sp.length();
                int address = address_sp / 2;
                for (int i = 0; i < address; i++) {
                    thank_you_sp = blank + thank_you_sp;
                }
            }
            String printDatap1 = "             *PAID*             \n" +
                    "--------------------------------\n" +
                    "" + company_name_sp + "\n" +
                    "" + finalString.toString() + "\n" +
                    "Tin number : " + tin_number_sp + "\n" +
                    "--------------------------------\n" +
                    "Date & Time: " + databaseHelper.getDateTime() + "\n" +
                    "BillNumber: " + billnumber + "  \n" +
                    "Payment Mode: " + mode + "  \n" +
                    "Name: " + c_name + "            \n" +
                    "Contact: " + c_contact + "      \n" +
                    "--------------------------------\n" +
                    "ITEM          PRICE QTY AMOUNT\n";


            String printDatap3 = "--------------------------------\n" +
                    "SUB TOTAL               " + t + "\n" +
                    "SERVICE TAX @ " + serivce_tax_sp + "%       " + tax + "\n" +
                    "VAT @ " + vat_sp + "%              " + vat + "\n" +
                    "--------------------------------\n" +
                    "TOTAL                   " + total_ASV + "\n" +
                    "                                \n" +
                    "" + thank_you_sp + "\n" +
                    "--------------------------------\n" +
                    "    nControl, Powered by nemi   \n" +
                    "           www.nemi.in          \n" +
                    "                                \n" +
                    "                                \n";


            //                        String printDatap1 = "             *PAID*             \n" +
            //                                "--------------------------------\n" +
            //                                "               D3               \n" +
            //                                "   III Floor, #330, 27th ActivityMain,  \n" +
            //                                "      Sector 2, HSR Layout,     \n" +
            //                                "       Bangalore-560102,        \n" +
            //                                "       Karnataka, INDIA.        \n" +
            //                                "--------------------------------\n" +
            //                                "Date & Time: " + databaseHelper.getDateTime() + "\n" +
            //                                "BillNumber: " + billnumber + "  \n" +
            //                                "Name: " + c_name + "            \n" +
            //                                "Contact: " + c_contact + "      \n" +
            //                                "--------------------------------\n" +
            //                                "ITEM          PRICE  QTY  AMOUNT\n";
            //
            //
            //                        String printDatap3 = "--------------------------------\n" +
            //                                "TOTAL                      " + t + "\n" +
            //                                "                                \n" +
            //                                "   Thank you for visiting D_3   \n" +
            //                                "--------------------------------\n" +
            //                                "    nControl, Powered by nemi   \n" +
            //                                "           www.nemi.in          \n" +
            //                                "                                \n" +
            //                                "                                \n";

            //                        String printDatap1 = "                     *PAID*                     \n" +
            //                                "------------------------------------------------\n" +
            //                                "                       D3                       \n" +
            //                                "      III Floor, #330, 27th ActivityMain, Sector 2,     \n" +
            //                                "          HSR Layout, Bangalore-560102,         \n" +
            //                                "               Karnataka, India.                \n" +
            //                                "------------------------------------------------\n" +
            //                                "Date & Time: " + databaseHelper.getDateTime() + "\n" +
            //                                "BillNumber: " + billnumber + "\n" +
            //                                "Name: " + c_name + "                     \n" +
            //                                "Contact: " + c_contact + "               \n" +
            //                                "------------------------------------------------\n" +
            //                                "ITEM               PRICE       QTY     AMOUNT   ";
            //
            //
            //                        String printDatap3 = "------------------------------------------------\n" +
            //                                "TOTAL                                    " + total_amo.getText().toString() + "\n" +
            //                                "                                                \n" +
            //                                "            Thank you for visiting D3           \n" +
            //                                "------------------------------------------------\n" +
            //                                "            nControl, Powered by nemi           \n" +
            //                                "                   www.nemi.in                  \n" +
            //                                "                                                \n" +
            //                                "                                                \n" +
            //                                "                                                \n";


            String printData = printDatap1 + printDatap2 + printDatap3;


            buf = printData.getBytes();

            int a = Integer.parseInt(total_amo.getText().toString());
            databaseHelper.bill(billnumber,c_name, c_contact, a,mode);
            c_name_et.setText("");
            c_contact_et.setText("");
            //Print
                        Bundle data = new Bundle();
                        data.putByteArray(Global.BYTESPARA1, FragmentPOS.buf);
                        data.putInt(Global.INTPARA1, 0);
                        data.putInt(Global.INTPARA2, buf.length);
                        DrawerService.workThread.handleCmd(Global.CMD_POS_WRITE, data);


                    lv.setAdapter(billAdap);   // set value
                    billAdap.notifyDataSetChanged();
                    for (int j = 0; j < alist.size(); j++) {
                        total += alist.get(j).getPrice() * alist.get(j).getQty();
                        total_amo.setText("" + total);
                    }
            if(kot.equalsIgnoreCase("0")){
                    billAdap.clear();
                    total_amo.setText("0");}

        }}
    public void kot(){
        if (DrawerService.workThread.isConnected()) {

            c_name = c_name_et.getText().toString();
            c_contact = c_contact_et.getText().toString();
            String printDatap2 = "";


            //pura game yahi pr he .......................
            billnumber = databaseHelper.checkLastBillDate();
            billnumber++;
//                    Toast.makeText(getActivity(), "Billnumber is : " + billnumber, Toast.LENGTH_SHORT).show();
            // this is for sales items
            for (int i = 0; i < alist.size(); i++) {
                // bill number, item, qty, price inserted into sales table from here
                databaseHelper.sales(billnumber, alist.get(i).getItem(), alist.get(i).getQty(), alist.get(i).getPrice());
                String item = alist.get(i).getItem();
                String qty = String.valueOf(alist.get(i).getQty());
                String price = String.valueOf(alist.get(i).getPrice());
                //this is code for substring of String
                blank = " ";
                if (item.length() >= 23) {
                    item = item.substring(0, 23);
                } else {
                    int b = 23 - item.length();
                    for (int k = 0; k < b; k++) {
                        item += blank;
                    }
                }


                if (qty.length() > 0) {
                    int c = 2 - qty.length();
                    for (int q = 0; q < c; q++) {
                        qty = blank + qty;
                    }
                }

                printDatap2 += item + " - "  + qty +  "\n";
            }

                        /* This is using for bill configuration...*/
            if (company_name_sp.length() < 32) {
                int name_sp = 32 - company_name_sp.length();
                int name = name_sp / 2;
                for (int i = 0; i < name; i++) {
                    company_name_sp = blank + company_name_sp;
                }
            }
            StringBuffer finalString = new StringBuffer();
            String mainString = company_address_sp;
            String[] stringArray = mainString.split("\\s+");
            String tmpString = "";
            for (String singleWord : stringArray) {
                if ((tmpString + singleWord + " ").length() >= 32) {
                    finalString.append(tmpString + "\n");
                    tmpString = singleWord + " ";
                } else {
                    tmpString = tmpString + singleWord + " ";
                }
            }
            if (tmpString.length() > 0) {
                finalString.append(tmpString);
            }
            Log.e("this is : ", finalString.toString());
            if (thank_you_sp.length() < 32) {
                int address_sp = 32 - thank_you_sp.length();
                int address = address_sp / 2;
                for (int i = 0; i < address; i++) {
                    thank_you_sp = blank + thank_you_sp;
                }
            }
            String printDatap1 = "             *KOT*             \n" +
                    "--------------------------------\n" +
                    "" + company_name_sp + "\n" +
                    "--------------------------------\n" +
                    "Date & Time: " + databaseHelper.getDateTime() + "\n" +
                    "BillNumber: " + billnumber + "  \n" +
                    "Name: " + c_name + "            \n" +
                    "Contact: " + c_contact + "      \n" +
                    "--------------------------------\n" +
                    "ITEM                       QTY\n";


            String printDatap3 = "--------------------------------\n" +

                    "--------------------------------\n" +
                    "    nControl, Powered by nemi   \n" +
                    "           www.nemi.in          \n" +
                    "                                \n" +
                    "                                \n";


            //                        String printDatap1 = "             *PAID*             \n" +
            //                                "--------------------------------\n" +
            //                                "               D3               \n" +
            //                                "   III Floor, #330, 27th ActivityMain,  \n" +
            //                                "      Sector 2, HSR Layout,     \n" +
            //                                "       Bangalore-560102,        \n" +
            //                                "       Karnataka, INDIA.        \n" +
            //                                "--------------------------------\n" +
            //                                "Date & Time: " + databaseHelper.getDateTime() + "\n" +
            //                                "BillNumber: " + billnumber + "  \n" +
            //                                "Name: " + c_name + "            \n" +
            //                                "Contact: " + c_contact + "      \n" +
            //                                "--------------------------------\n" +
            //                                "ITEM          PRICE  QTY  AMOUNT\n";
            //
            //
            //                        String printDatap3 = "--------------------------------\n" +
            //                                "TOTAL                      " + t + "\n" +
            //                                "                                \n" +
            //                                "   Thank you for visiting D_3   \n" +
            //                                "--------------------------------\n" +
            //                                "    nControl, Powered by nemi   \n" +
            //                                "           www.nemi.in          \n" +
            //                                "                                \n" +
            //                                "                                \n";

            //                        String printDatap1 = "                     *PAID*                     \n" +
            //                                "------------------------------------------------\n" +
            //                                "                       D3                       \n" +
            //                                "      III Floor, #330, 27th ActivityMain, Sector 2,     \n" +
            //                                "          HSR Layout, Bangalore-560102,         \n" +
            //                                "               Karnataka, India.                \n" +
            //                                "------------------------------------------------\n" +
            //                                "Date & Time: " + databaseHelper.getDateTime() + "\n" +
            //                                "BillNumber: " + billnumber + "\n" +
            //                                "Name: " + c_name + "                     \n" +
            //                                "Contact: " + c_contact + "               \n" +
            //                                "------------------------------------------------\n" +
            //                                "ITEM               PRICE       QTY     AMOUNT   ";
            //
            //
            //                        String printDatap3 = "------------------------------------------------\n" +
            //                                "TOTAL                                    " + total_amo.getText().toString() + "\n" +
            //                                "                                                \n" +
            //                                "            Thank you for visiting D3           \n" +
            //                                "------------------------------------------------\n" +
            //                                "            nControl, Powered by nemi           \n" +
            //                                "                   www.nemi.in                  \n" +
            //                                "                                                \n" +
            //                                "                                                \n" +
            //                                "                                                \n";


            String printData = printDatap1 + printDatap2 + printDatap3;


            buf = printData.getBytes();

            int a = Integer.parseInt(total_amo.getText().toString());
            databaseHelper.bill(billnumber,c_name, c_contact, a,mode);
            c_name_et.setText("");
            c_contact_et.setText("");
            //Print
            Bundle data = new Bundle();
            data.putByteArray(Global.BYTESPARA1, FragmentPOS.buf);
            data.putInt(Global.INTPARA1, 0);
            data.putInt(Global.INTPARA2, buf.length);
            DrawerService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
            billAdap.clear();
            total_amo.setText("0");

        }
    }
    private void initBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {


            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub

                System.out.println("data comig here" + bluetooth_address);
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    if (device != null && device.getName() != null) {
                        if (device == null)
                            return;
//                        DrawerService.workThread.connectBt("00:02:0A:02:E9:9E");
//                        DrawerService.workThread.connectBt("88:68:2E:00:31:4A");
                        DrawerService.workThread.connectBt(bluetooth_address);

                    }
                }
                intentFilter = new IntentFilter();
                intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
                //  intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                // intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                getActivity().registerReceiver(broadcastReceiver, intentFilter);
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        pay_button.setEnabled(false);
    }

//    privat`1

    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
//                    showToast("Paired");
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
//                    showToast("Unpaired");
                }

            }
        }
    };

    public class SlidingTabsBasicFragment extends Fragment {
        DatabaseHelper databaseHelper;
        static final String LOG_TAG = "SlidingTabsBasicFragment";
        private SlidingTabLayout mSlidingTabLayout;
        private ViewPager mViewPager;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_sample, container, false);
            databaseHelper = new DatabaseHelper(getActivity(), null, null, 1);
            // BillAdapre set here
            billAdap = new BillAdapter(getActivity(), alist);
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
            mViewPager.setAdapter(new SamplePagerAdapter());
            mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
            mSlidingTabLayout.setViewPager(mViewPager);

        }

        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< PagerAdapter >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        public class POSCursorAdapter extends CursorAdapter {


            public POSCursorAdapter(Context context, Cursor c) {
                super(context, c);
            }

            @Override
            public View newView(Context context, final Cursor cursor, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.adapter_pos_item_list, parent, false);
                return view;
            }

            @Override

            public void bindView(View view, final Context context, final Cursor cursor) {
                tv_id__pos_column = (TextView) view.findViewById(R.id._id_on_pos_id);
                tv_item_on_pos = (TextView) view.findViewById(R.id.item_on_pos_id);
                tv_price_on_pos = (TextView) view.findViewById(R.id.price_on_pos_id);
                ImageView tv_imagepath = (ImageView) view.findViewById(R.id.imageView1);
                tv_id__pos_column.setText(cursor.getString(0));
                tv_item_on_pos.setText(cursor.getString(1));
                tv_price_on_pos.setText(cursor.getString(3));
                tv_imagepath.setImageBitmap(BitmapFactory.decodeFile(cursor.getString(4)));
            }

        }

        class SamplePagerAdapter extends PagerAdapter {
            String col;

            @Override
            public int getCount() {
                Cursor c = databaseHelper.getCategories();
                return c.getCount();
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return o == view;
            }

            @Override
            public CharSequence getPageTitle(int position) {
//                Toast.makeText(getContext(), "Its on: " + position, Toast.LENGTH_SHORT).show();
                return "Item " + (position + 1);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                final View view = getActivity().getLayoutInflater().inflate(R.layout.pos_pager_item, container, false);

                container.addView(view);
                String ourTabName;
                POSCursorAdapter posCursorAdapter;
                databaseHelper = new DatabaseHelper(getActivity(), null, null, 1);
                final Cursor c = databaseHelper.getCategories();

                String[] TabbyName = new String[c.getCount()];
                c.moveToFirst();

                for (int i = 0; i < c.getCount(); i++) {
                    ourTabName = c.getString(0);
                    TabbyName[i] = ourTabName;
                    c.moveToNext();
                }

                String a = TabbyName[position];

                posCursorAdapter = new POSCursorAdapter(getActivity(), databaseHelper.getPOSItems(a));
                items_list = (ListView) view.findViewById(R.id.items_list_id);
                items_list.setAdapter(posCursorAdapter);


                items_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                        tv_id__pos_column = (TextView) view.findViewById(R.id._id_on_pos_id);
                        tv_item_on_pos = (TextView) view.findViewById(R.id.item_on_pos_id);
                        tv_price_on_pos = (TextView) view.findViewById(R.id.price_on_pos_id);
                        String itemidfetchvar = tv_id__pos_column.getText().toString();
                        String fetchitemvar = tv_item_on_pos.getText().toString();
                        int pricefetchvar = Integer.parseInt(tv_price_on_pos.getText().toString());

                        if (alist.isEmpty()) {
                            alist.add(new BillItems(itemidfetchvar, fetchitemvar, 1, pricefetchvar));
                            billAdap.notifyDataSetChanged();
                            lv.setAdapter(billAdap);
                        } else {
                            int flag = 0;
                            for (int i = 0; i < alist.size(); i++) {
                                flag = 0;
                                //                                //match _id
                                if (itemidfetchvar.equalsIgnoreCase(alist.get(i).getId())) {
                                    alist.set(i, new BillItems(itemidfetchvar, fetchitemvar, alist.get(i).getQty() + 1,
                                            pricefetchvar));
                                    //* alist.get(i).getQty() + pricefetchvar   increment by items
                                    lv.setAdapter(billAdap);
                                    break;
                                } else {
                                    flag = 1;
                                }
                            }
                            if (flag == 1) {
                                alist.add(new BillItems(itemidfetchvar, fetchitemvar, 1, pricefetchvar));
                                lv.setAdapter(billAdap);
                            }
                        }

                        int total = 0;

                        for (int j = 0; j < alist.size(); j++) {
                            total += alist.get(j).getPrice() * alist.get(j).getQty();
                            total_amo.setText("" + total);
                        }


                    }
                });
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        }

        public class BillAdapter extends ArrayAdapter<BillItems> {

            public BillAdapter(Context context, ArrayList<BillItems> alist) {
                super(context, 0, alist);
            }


            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                // Get the data item for this position
                billItems = getItem(position);
//                Log.e("ibillItems", billItems.toString());
                // Check if an existing view is being reused, otherwise inflate the view
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_pos_selected_item, parent, false);
                }
                // Lookup view for data population
                tv_selected_id_on_pos = (TextView) convertView.findViewById(R.id.selected_id_on_pos);
                tv_selected_item_on_pos = (TextView) convertView.findViewById(R.id.selected_item_on_pos);
                tv_selected_qty_on_pos = (Button) convertView.findViewById(R.id.selected_quantity_on_pos);
                tv_selected_price_on_pos = (TextView) convertView.findViewById(R.id.selected_price_on_pos);
                tv_selected_amount_on_pos = (TextView) convertView.findViewById(R.id.selected_amount_on_pos);


                delete_bill_btn = (Button) convertView.findViewById(R.id.minus_item);
                set_qty_btn = (Button) convertView.findViewById(R.id.qty_item);
//                decrease = (Button) convertView.findViewById(R.id.decrease_id);

                tv_selected_qty_on_pos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (alist.get(position).getQty() > 1) {
                            int quantity = alist.get(position).getQty();
                            quantity--;
                            alist.set(position, new BillItems(alist.get(position).getId(), alist.get(position).getItem(),
                                    quantity, alist.get(position).getPrice()));
                            lv.setAdapter(billAdap);   // set value
                            billAdap.notifyDataSetChanged();
                        }
                        int total = 0;
                        for (int j = 0; j < alist.size(); j++) {
                            total += alist.get(j).getPrice() * alist.get(j).getQty();
                            total_amo.setText("" + total);
                        }
                    }
                });

                set_qty_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Dialog d = new Dialog(getActivity());
                        d.setTitle("Update Quantity");
                        d.setContentView(R.layout.dialog_pos_for_set_qty);
                        set = (Button) d.findViewById(R.id.set_btn_id);
                        cancel = (Button) d.findViewById(R.id.cancel_btn_id);
                        qty_et = (EditText) d.findViewById(R.id.edit_qty_id);
//                        qty_et.
                        qty_et.setText(String.valueOf(alist.get(position).getQty()));
                        String sTextFromET = qty_et.getText().toString();
                        final int qty = new Integer(sTextFromET);


                        set.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View view) {

                                                       int quantity = Integer.parseInt(qty_et.getText().toString());
                                                       if (quantity <= 0) {
                                                           qty_et.setError("Quantity must be greater than 0");
                                                       } else {
                                                           alist.set(position, new BillItems(alist.get(position).getId(), alist.get(position).getItem(),
                                                                   quantity, alist.get(position).getPrice()));
                                                           lv.setAdapter(billAdap);   // set value
                                                           billAdap.notifyDataSetChanged();

                                                           //Re-total
                                                           int total = 0;
                                                           for (int j = 0; j < alist.size(); j++) {
                                                               total += alist.get(j).getPrice() * alist.get(j).getQty();
                                                               total_amo.setText("" + total);
                                                           }
                                                           d.dismiss();
                                                       }
                                                   }
                                               }
                        );
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                d.dismiss();
                            }
                        });
                        d.show();

                    }
                });

                delete_bill_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle("Please select an action!");
                        alertDialogBuilder.setIcon(R.drawable.question_mark);
                        alertDialogBuilder.setMessage("Are you sure you want to delete this item ?").setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        alist.remove(position);
                                        lv.setAdapter(billAdap);   // set value
                                        billAdap.notifyDataSetChanged();

                                        //Total calculation
                                        total = 0;
                                        if (alist.size() > 0) {
                                            for (int j = 0; j < alist.size(); j++) {
                                                total += alist.get(j).getPrice() * alist.get(j).getQty();
                                                total_amo.setText("" + total);
                                            }
                                        } else {
                                            total = 0;
                                            total_amo.setText("0");
                                        }
                                    }
                                }).setCancelable(false).setNeutralButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
                // Populate the data into the template view using the data object
                tv_selected_id_on_pos.setText(billItems.getId());
                tv_selected_item_on_pos.setText(billItems.getItem());
                tv_selected_price_on_pos.setText(String.valueOf(billItems.getPrice()));
                tv_selected_qty_on_pos.setText(String.valueOf(billItems.getQty()));
                int t = billItems.getPrice() * billItems.getQty();
                tv_selected_amount_on_pos.setText(String.valueOf(t));
                // Return the completed view to render on screen
                return convertView;
            }
        }
    }


    /*public static void enablePayButton(){
        if (DrawerService.workThread.isConnected() && pay_button != null) {
            Log.e("", "Thread Connected");
            pay_button.setEnabled(true);
        }
    }
    public static void disblePayButton(){
        if(pay_button != null){
            Log.e("", "Thread DisConnected");
            pay_button.setEnabled(false);
        }
    }*/
}
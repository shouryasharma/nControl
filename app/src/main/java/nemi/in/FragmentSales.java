package nemi.in;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog.OnDateSetListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import common.logger.Log;
import common.view.SlidingTabLayout;
import in.nemi.ncontrol.R;
import printing.DrawerService;
import printing.Global;

/**
 * Created by Aman on 6/3/2016.
 */
public class FragmentSales extends Fragment implements View.OnClickListener {
    DatabaseHelper databaseHelper;
    SalesManagmentAdapter salesManagmentAdapter;
    BillDetailAdapter billDetailAdapter;
    ListView bill_list, bill_details;
    ListView lv, items_list;
    TextView bill_number_tv2,billnumber_date, date_tv, mode_tv, amount_tv, customer_name_tv, customer_contact_tv;
    Button search_btn,reprint_btn, cancel_button;
    EditText et_bill_number, et_customer_name, et_customer_contact;
    private EditText fromDateEtxt, toDateEtxt,et_date;
    ImageButton fromDate_ToDate_imgBtn, bill_date_search_imgBtn, amount_btn, customernamebtn_imgBtn, customercontactbtn_imgBtn;
    Cursor c = null;
    public static ProgressDialog progress;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    public static byte[] buf = null;
    Button btn_datepicker;
    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;
    String bluetooth_address;
    String company_name_sp;
    String company_address_sp;
    String thank_you_sp;
    String tin_number_sp,klb;
    String serivce_tax_sp;
    String vat_sp;
    String blank;
    Bundle a;
    TextView tv_id__pos_column, tv_item_on_pos, tv_price_on_pos;
    TextView tv_selected_id_on_pos, tv_selected_item_on_pos, tv_selected_price_on_pos, tv_selected_amount_on_pos;
    int decre = 0;

    private int mHour, mMinute;

    public FragmentSales() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sales, container, false);
         a = savedInstanceState;
        SharedPreferences settings = getActivity().getSharedPreferences(FragmentSettings.MyPREFERENCES, Context.MODE_PRIVATE);
        // Reading from SharedPreferences
        bluetooth_address = settings.getString(FragmentSettings.BLUETOOTH_KEY, "");
        company_name_sp = settings.getString(FragmentSettings.NAME_COMPANY_KEY, "");
        company_address_sp = settings.getString(FragmentSettings.ADDRESS_COMPANY_KEY, "");
        thank_you_sp = settings.getString(FragmentSettings.THANK_YOU_KEY, "");
        tin_number_sp = settings.getString(FragmentSettings.TIN_NUMBER_KEY, "");
        klb = settings.getString(FragmentSettings.KEEP_LOCAL_BACKUP,"");
        databaseHelper = new DatabaseHelper(getActivity(), null, null, 1);
        search_btn = (Button) rootView.findViewById(R.id.btn_search_bill_id);
        reprint_btn = (Button) rootView.findViewById(R.id.btn_reprint);
        bill_number_tv2 = (TextView) rootView.findViewById(R.id.tv_bill_number_id);
        billnumber_date = (TextView) rootView.findViewById(R.id.tv_billnumber_date_id);
        date_tv = (TextView) rootView.findViewById(R.id.tv_bill_date_id);
        mode_tv = (TextView) rootView.findViewById(R.id.tv_bill_mode_id);
        amount_tv = (TextView) rootView.findViewById(R.id.tv_amount_id);
        customer_name_tv = (TextView) rootView.findViewById(R.id.tv_customer_name_id);
        customer_contact_tv = (TextView) rootView.findViewById(R.id.tv_customer_contact_id);

        search_btn.setOnClickListener(this);
        reprint_btn.setOnClickListener(this);

/*================================================SalesManagmentAdapter ========================================================================*/
        salesManagmentAdapter = new SalesManagmentAdapter(getActivity(), databaseHelper.getBill());
        bill_list = (ListView) rootView.findViewById(R.id.userlistview);
        bill_list.setAdapter(salesManagmentAdapter);

/*===================================================BillDetailAdapter=====================================================================*/
        billDetailAdapter = new BillDetailAdapter(getActivity(), null);
        bill_details = (ListView) rootView.findViewById(R.id.bill_sale_view);
        bill_details.setAdapter(billDetailAdapter);

        final int bill = databaseHelper.checkLastBillNumber();
        if (bill != 0) {
            Cursor c = databaseHelper.getSale(bill);
            billDetailAdapter.changeCursor(c);
            Cursor d = databaseHelper.getBillInfo(bill);
//            bill_number_tv2.setText("" + bill);

            d.moveToFirst();
            bill_number_tv2.setText(d.getString(0));
            billnumber_date.setText(d.getString(1));
            date_tv.setText(d.getString(2));
            amount_tv.setText(d.getString(3));
            customer_name_tv.setText(d.getString(4));
            customer_contact_tv.setText(d.getString(5));
            mode_tv.setText(d.getString(6));
        } else {
            Toast.makeText(getActivity(), "No bills to show!", Toast.LENGTH_SHORT).show();
        }

/*===================================================reprint===========================================================================*/
        reprint_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getActivity(),"reprint bill",Toast.LENGTH_SHORT).show();
                if (DrawerService.workThread.isConnected()) {
                    if (klb.equalsIgnoreCase("0")) {

                    String printDatap2 = "";
                    String printdatap3 = "";
                    String printid = "";
                    double taxamount = 0;
                    String a = bill_number_tv2.getText().toString();
                    if (!a.equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), a, Toast.LENGTH_LONG).show();
                        Cursor reprinta = databaseHelper.getBillInfo(Integer.parseInt(a));
                        reprinta.moveToFirst();
                        String c_name = reprinta.getString(4);
                        String c_contact = reprinta.getString(5);
                        String mode = reprinta.getString(6);
                        String salesdate = reprinta.getString(2);
                        double total = Integer.parseInt(reprinta.getString(3));
                        String taxvalue = reprinta.getString(7);
                        String discount = reprinta.getString(8);
                        if (!discount.equalsIgnoreCase("null")) {
                            total = total - total * Double.parseDouble(discount);
                        }
                        if (taxvalue.equalsIgnoreCase("")) {
                            printdatap3 = "";
                            printid = "";
                        } else {
                            for (String retval : taxvalue.split("\\,")) {
                                if (!retval.equalsIgnoreCase("")) {
                                    String[] result = retval.split("\\.");


                                    String tax_name = result[0];
                                    String tax_value = result[1];
                                    String tax_value_print = result[3];
                                    String tax_id = result[2];
                                    String tax_id_print = result[4];
                                    if (tax_id_print.equalsIgnoreCase("1")) {
                                        printid += tax_id + "\n";
                                    }
                                    blank = " ";
                                    if (tax_value_print.equalsIgnoreCase("1")) {
                                        if (tax_name.length() >= 12) {
                                            tax_name = tax_name.substring(0, 12);
                                        } else {
                                            int b = 12 - tax_name.length();
                                            for (int k = 0; k < b; k++) {
                                                tax_name += blank;
                                            }
                                        }

                                        if (tax_value.length() > 0) {
                                            int d = 4 - tax_value.length();
                                            for (int p = 0; p < d; p++) {
                                                tax_value = blank + tax_value;
                                            }
                                        }

                                        String amount = String.valueOf(total * Double.parseDouble(result[1]) / 100);
                                        taxamount = taxamount + Double.parseDouble(amount);
                                        if (amount.length() >= 0) {
                                            int m = 5 - amount.length();
                                            for (int p = 0; p < m; p++) {
                                                amount = blank + amount;
                                            }
                                        }
                                        printdatap3 += tax_name + " - " + "@" + " " + tax_value + "%" + "  " + amount + "\n";
                                    }
                                }

                            }
                        }


                        Cursor salesprinta = databaseHelper.getSale(Integer.parseInt(a));

//                    Toast.makeText(getActivity(), "Billnumber is : " + billnumber, Toast.LENGTH_SHORT).show();
                        // this is for sales items
                        while (salesprinta.moveToNext()) {

                            String item = salesprinta.getString(1);
                            String qty = salesprinta.getString(2);
                            String price = salesprinta.getString(3);
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


                            String amount = String.valueOf(Integer.parseInt(salesprinta.getString(2)) * Integer.parseInt(salesprinta.getString(3)));
                            if (amount.length() >= 0) {
                                int m = 5 - amount.length();
                                for (int p = 0; p < m; p++) {
                                    amount = blank + amount;
                                }
                            }
                            printDatap2 += item + " - " + price + " " + qty + "  " + amount + "\n";
                        }

                        // Calculation of items in sales


                        String t = String.valueOf(total);
                        if (t.length() >= 0) {
                            int tot = 5 - t.length();
                            for (int p = 0; p < tot; p++) {
                                t = blank + t;
                            }
                        }
                        // Calculation of service tax and vat here



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
                        android.util.Log.e("this is : ", finalString.toString());
                        if (thank_you_sp.length() < 32) {
                            int address_sp = 32 - thank_you_sp.length();
                            int address = address_sp / 2;
                            for (int i = 0; i < address; i++) {
                                thank_you_sp = blank + thank_you_sp;
                            }
                        }
                        String printDatap1 = "           *REPRINT*           \n" +
                                "--------------------------------\n" +
                                "" + company_name_sp + "\n" +
                                "" + finalString.toString() + "\n" +
                                "CIN number : " + tin_number_sp + "\n";
                        String printDatap11 = "--------------------------------\n" +
                                "Date & Time: " + salesdate + "\n" +
                                "BillNumber: " + a + "  \n" +
                                "Payment Mode: " + mode + "  \n" +
                                "Name: " + c_name + "            \n" +
                                "Contact: " + c_contact + "      \n" +
                                "--------------------------------\n" +
                                "ITEM          PRICE QTY AMOUNT\n";


                        String printDatap3 = "--------------------------------\n" +
                                "SUB TOTAL               " + t + "\n";
                        double grandtotal;
                        if (discount.equalsIgnoreCase("null")) {
                            grandtotal = total + taxamount;
                        } else {
                            grandtotal = total + taxamount;
                        }

                        String printdatap4 = "--------------------------------\n" +
                                "TOTAL                    " + String.valueOf(grandtotal) + "\n" +
                                "                                \n" +
                                "" + thank_you_sp + "\n" +
                                "--------------------------------\n" +
                                "    nControl, Powered by nemi   \n" +
                                "           www.nemi.in          \n" +
                                "                                \n" +
                                "                                \n";
                        String printdata6 = "";
                        if (!discount.equalsIgnoreCase("null")) {
                            discount = String.valueOf(Double.parseDouble(amount_tv.getText().toString()) * Double.parseDouble(discount));

                            printdata6 = "--------------------------------\n" +
                                    "DISCOUNT                " + discount + "\n";

                        }


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


                        String printData = printDatap1 + printid + printDatap11 + printDatap2 + printDatap3 + printdata6 + printdatap3 + printdatap4;


                        buf = printData.getBytes();
                        databaseHelper.close();


                        //Print
                        Bundle data = new Bundle();
                        data.putByteArray(Global.BYTESPARA1, FragmentSales.buf);
                        data.putInt(Global.INTPARA1, 0);
                        data.putInt(Global.INTPARA2, buf.length);
                        DrawerService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
                    } else {
                        Toast.makeText(getActivity(), "Nothing to print", Toast.LENGTH_SHORT).show();
                    }

                }else {
                        String printDatap2 = "";
                        String printdatap3 = "";
                        String printid = "";
                        double taxamount = 0;
                        String a = bill_number_tv2.getText().toString();
                        if (!a.equalsIgnoreCase("")) {
                            Toast.makeText(getActivity(), a, Toast.LENGTH_LONG).show();
                            Cursor reprinta = databaseHelper.getBillInfo(Integer.parseInt(a));
                            reprinta.moveToFirst();
                            String c_name = reprinta.getString(4);
                            String c_contact = reprinta.getString(5);
                            String mode = reprinta.getString(6);
                            String salesdate = reprinta.getString(2);
                            double total = Integer.parseInt(reprinta.getString(3));
                            String taxvalue = reprinta.getString(7);
                            String discount = reprinta.getString(8);
                            if (!discount.equalsIgnoreCase("null")) {
                                total = total - total * Double.parseDouble(discount);
                            }
                            if (taxvalue.equalsIgnoreCase("")) {
                                printdatap3 = "";
                                printid = "";
                            } else {
                                for (String retval : taxvalue.split("\\,")) {
                                    if (!retval.equalsIgnoreCase("")) {
                                        String[] result = retval.split("\\.");


                                        String tax_name = result[0];
                                        String tax_value = result[1];
                                        String tax_value_print = result[3];
                                        String tax_id = result[2];
                                        String tax_id_print = result[4];
                                        if (tax_id_print.equalsIgnoreCase("1")) {
                                            printid += tax_id + "\n";
                                        }
                                        blank = " ";
                                        if (tax_value_print.equalsIgnoreCase("1")) {
                                            if (tax_name.length() >= 30) {
                                                tax_name = tax_name.substring(0, 30);
                                            } else {
                                                int b = 30 - tax_name.length();
                                                for (int k = 0; k < b; k++) {
                                                    tax_name += blank;
                                                }
                                            }

                                            if (tax_value.length() > 0) {
                                                int d = 4 - tax_value.length();
                                                for (int p = 0; p < d; p++) {
                                                    tax_value = blank + tax_value;
                                                }
                                            }

                                            String amount = String.valueOf(total * Double.parseDouble(result[1]) / 100);
                                            taxamount = taxamount + Double.parseDouble(amount);
                                            if (amount.length() >= 0) {
                                                int m = 5 - amount.length();
                                                for (int p = 0; p < m; p++) {
                                                    amount = blank + amount;
                                                }
                                            }
                                            printdatap3 += tax_name + " - " + "@" + " " + tax_value + "%" + "  " + amount + "\n";
                                        }
                                    }

                                }
                            }


                            Cursor salesprinta = databaseHelper.getSale(Integer.parseInt(a));

//                    Toast.makeText(getActivity(), "Billnumber is : " + billnumber, Toast.LENGTH_SHORT).show();
                            // this is for sales items
                            while (salesprinta.moveToNext()) {

                                String item = salesprinta.getString(1);
                                String qty = salesprinta.getString(2);
                                String price = salesprinta.getString(3);
                                //this is code for substring of String
                                blank = " ";
                                if (item.length() >= 30) {
                                    item = item.substring(0, 30);
                                } else {
                                    int b = 30 - item.length();
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


                                String amount = String.valueOf(Integer.parseInt(salesprinta.getString(2)) * Integer.parseInt(salesprinta.getString(3)));
                                if (amount.length() >= 0) {
                                    int m = 5 - amount.length();
                                    for (int p = 0; p < m; p++) {
                                        amount = blank + amount;
                                    }
                                }
                                printDatap2 += item + " - " + price + " " + qty + "  " + amount + "\n";
                            }

                            // Calculation of items in sales


                            String t = String.valueOf(total);
                            if (t.length() >= 0) {
                                int tot = 5 - t.length();
                                for (int p = 0; p < tot; p++) {
                                    t = blank + t;
                                }
                            }
                            // Calculation of service tax and vat here



                        /* This is using for bill configuration...*/
                            if (company_name_sp.length() < 48) {
                                int name_sp = 48 - company_name_sp.length();
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
                                if ((tmpString + singleWord + " ").length() >= 48) {
                                    finalString.append(tmpString + "\n");
                                    tmpString = singleWord + " ";
                                } else {
                                    tmpString = tmpString + singleWord + " ";
                                }
                            }
                            if (tmpString.length() > 0) {
                                finalString.append(tmpString);
                            }
                            android.util.Log.e("this is : ", finalString.toString());
                            if (thank_you_sp.length() < 48) {
                                int address_sp = 48 - thank_you_sp.length();
                                int address = address_sp / 2;
                                for (int i = 0; i < address; i++) {
                                    thank_you_sp = blank + thank_you_sp;
                                }
                            }
                            String printDatap1 = "                    *COPY*           \n" +
                                    "------------------------------------------------\n" +
                                    "" + company_name_sp + "\n" +
                                    "" + finalString.toString() + "\n" +
                                    "CIN number : " + tin_number_sp + "\n";
                            String printDatap11 ="------------------------------------------------\n" +
                                    "Date & Time: " + salesdate + "\n" +
                                    "BillNumber: " + a + "  \n" +
                                    "Payment Mode: " + mode + "  \n" +
                                    "Name: " + c_name + "            \n" +
                                    "Contact: " + c_contact + "      \n" +
                                    "------------------------------------------------\n" +
                                    "ITEM                            PRICE QTY AMOUNT\n";


                            String printDatap3 = "------------------------------------------------\n" +
                                    "SUB TOTAL                                 " + t + "\n";
                            double grandtotal;
                            if (discount.equalsIgnoreCase("null")) {
                                grandtotal = total + taxamount;
                            } else {
                                grandtotal = total + taxamount;
                            }

                            String printdatap4 = "------------------------------------------------\n" +
                                    "TOTAL                                     " + String.valueOf(grandtotal) + "\n" +
                                    "                                \n" +
                                    "" + thank_you_sp + "\n" +
                                    "------------------------------------------------\n" +
                                    "           nControl, Powered by nemi           \n" +
                                    "                  www.nemi.in                 \n" +
                                    "                                \n" +
                                    "                                \n" +
                                    "                                \n";
                            String printdata6 = "";
                            if (!discount.equalsIgnoreCase("null")) {
                                discount = String.valueOf(Double.parseDouble(amount_tv.getText().toString()) * Double.parseDouble(discount));

                                printdata6 = "----------------------------------------------------\n" +
                                        "DISCOUNT                                  " + discount + "\n";

                            }

                            String printData = printDatap1 + printid + printDatap11 + printDatap2 + printDatap3 + printdata6 + printdatap3 + printdatap4;


                            buf = printData.getBytes();
                            databaseHelper.close();


                            //Print
                            Bundle data = new Bundle();
                            data.putByteArray(Global.BYTESPARA1, FragmentSales.buf);
                            data.putInt(Global.INTPARA1, 0);
                            data.putInt(Global.INTPARA2, buf.length);
                            DrawerService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
                        } else {
                            Toast.makeText(getActivity(), "Nothing to print", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {

                    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                    progress = new ProgressDialog(getActivity());
                    progress.setTitle("Connecting to Printer...");
                    progress.setMessage("Click the Reprint  button again after the printer connects.");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.show();
                    if (null == adapter) {
                    }
                    if (!adapter.isEnabled()) {
                        if (adapter.enable()) {
                            while (!adapter.isEnabled()) ;
                        } else {
                            android.util.Log.v("jay","bt off");
                        }
                    }
                    adapter.cancelDiscovery();
                    adapter.startDiscovery();
                }
            }
        });


/*=========================================================search Button===============================================================*/
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog d = new Dialog(getActivity());
                d.setContentView(R.layout.dialog_sales_for_search);
                d.setTitle("Search bill !");
                d.setCancelable(true);
                d.show();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(d.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                d.getWindow().setAttributes(lp);
                et_bill_number = (EditText) d.findViewById(R.id.tv_bill_number_id);
                et_date = (EditText) d.findViewById(R.id.tv_date_id);
                fromDateEtxt = (EditText) d.findViewById(R.id.etxt_fromdate);
                toDateEtxt = (EditText) d.findViewById(R.id.etxt_todate);
                et_customer_name = (EditText) d.findViewById(R.id.tv_customer_name_id);
                et_customer_contact = (EditText) d.findViewById(R.id.tv_customer_contact_id);

                et_bill_number.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setIcon(R.drawable.question_mark);
                        builder.setTitle("Please select a Bill number");
                        ListView dialogCatList = new ListView(getActivity());
                        OldBillNumberAdapter oldBillNumberAdapter = new OldBillNumberAdapter(getActivity()
                                ,databaseHelper.getOldBillNumber());
                        dialogCatList.setAdapter(oldBillNumberAdapter);
                        builder.setView(dialogCatList);
                        final Dialog dialog = builder.create();

                        dialogCatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                                TextView tv_billnumber = (TextView) view.findViewById(R.id.tv_old_billnumber_id);
                                String billnumber = tv_billnumber.getText().toString();
                                et_bill_number.setText(billnumber);
                                et_bill_number.setEnabled(true);
                                dialog.cancel();
                            }
                        });
                        dialog.show();
                    }
                });

                bill_date_search_imgBtn = (ImageButton) d.findViewById(R.id.bill_date_search_id);
                fromDate_ToDate_imgBtn = (ImageButton) d.findViewById(R.id.fromDate_ToDate_id);
                customernamebtn_imgBtn = (ImageButton) d.findViewById(R.id.customernamebtn_id);
                customercontactbtn_imgBtn = (ImageButton) d.findViewById(R.id.customercontactbtn_id);

//                ?????????????????????????????????????????????????????????????????????????????????
                bill_date_search_imgBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String bill = et_bill_number.getText().toString();
                        String date = et_date.getText().toString();
                        if (bill.equals("")) {
                            et_bill_number.setError("Warrning only numeric value use !");
                        }else if(date.equals("")){
                            et_date.setError("Warrning click on EditText !");
                        }else{
                            c = databaseHelper.searchByDate(Integer.parseInt(bill),date);
                            salesManagmentAdapter.changeCursor(c);
                            d.dismiss();
                        }

                    }
                });
                fromDate_ToDate_imgBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        String bill = et_bill_number.getText().toString();

                        String fromDate = fromDateEtxt.getText().toString();
                        String toDate = toDateEtxt.getText().toString();
                         if(fromDate.equals("")) {
                            fromDateEtxt.setError("Warning select FromDate !");
                        } else if (toDate.equals("")) {
                            toDateEtxt.setError("Warning select ToDate !");
                        } else {
                            c = databaseHelper.searchByBillNumber(fromDate,toDate);
                            salesManagmentAdapter.changeCursor(c);
                            d.dismiss();
                        }
                    }
                });


                customernamebtn_imgBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String customer_name = et_customer_name.getText().toString();
                        if (customer_name.equals("")) {
                            et_customer_name.setError("Warning only Alphabet value !");
                        } else {
                            c = databaseHelper.searchByCustomerName(customer_name);
                            salesManagmentAdapter.changeCursor(c);
                            d.dismiss();
                        }
                    }
                });

                customercontactbtn_imgBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String customer_contact = et_customer_contact.getText().toString();
                        if (customer_contact.equals("")) {
                            et_customer_contact.setError("Warning only numeric value!");
                        } else {
                            c = databaseHelper.searchByCustomerContact(customer_contact);
                            salesManagmentAdapter.changeCursor(c);
                            d.dismiss();
                        }
                    }
                });
                cancel_button = (Button) d.findViewById(R.id.btn_cancel_id);
                dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                findViewsById();
                setDateTimeField();
                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                    }
                });

            }
        });

        /*======================================================================================================================*/
        return rootView;
    }
    public static void harry(){
        progress.cancel();
        progress.dismiss();

    }

    private void setDateTimeField() {
        et_date.setOnClickListener(this);
        fromDateEtxt.setOnClickListener(this);
        toDateEtxt.setOnClickListener(this);

/*============================================>>>from date*/
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

/*============================================>>>To date*/
        toDatePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
/*============================================>>> date*/
        datePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                et_date.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
    }


    private void findViewsById() {
        et_date.setInputType(InputType.TYPE_NULL);
        et_date.requestFocus();

        fromDateEtxt.setInputType(InputType.TYPE_NULL);
        fromDateEtxt.requestFocus();

        toDateEtxt.setInputType(InputType.TYPE_NULL);
        toDateEtxt.requestFocus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_search_bill_id:
            case R.id.tv_date_id:
               }
        if(view==et_date){
            datePickerDialog.show();
        }
        if (view == fromDateEtxt) {
            fromDatePickerDialog.show();
        } else if (view == toDateEtxt) {
            toDatePickerDialog.show();
        }
    }

    /*========================================================Dialoge DatePicker================================================================*/
    public class SalesManagmentAdapter extends CursorAdapter {

        public SalesManagmentAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.adapter_sales_bills_listview, parent, false);
            return view;
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            TextView bill_status = (TextView) view.findViewById(R.id.status);
            TextView bill_id_tv = (TextView) view.findViewById(R.id.tv_bill__id);
            TextView bill_number_tv = (TextView) view.findViewById(R.id.tv_bill_number_fecth_id);
            TextView bill_date_tv = (TextView) view.findViewById(R.id.tv_bill_date_fetch_id);
            TextView bill_amount_tv = (TextView) view.findViewById(R.id.tv_bill_amount_fetch_id);

            Button delete_item_btn = (Button) view.findViewById(R.id.del_item);
            Button view_item_id = (Button) view.findViewById(R.id.view_item);;

            bill_id_tv.setText(cursor.getString(0));
            bill_number_tv.setText(cursor.getString(1));
            bill_date_tv.setText(cursor.getString(2));
            bill_amount_tv.setText(cursor.getString(3));
            view.setBackgroundColor(Color.WHITE);
            view.setEnabled(true);
            if(cursor.getString(4).equalsIgnoreCase("1")){
                view.setBackgroundColor(Color.LTGRAY);
                Log.v("Value",cursor.getString(0));
                view.setEnabled(false);
                delete_item_btn.setOnLongClickListener(null);
            }

            final String bill_number = bill_id_tv.getText().toString();

            view_item_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int a = Integer.parseInt(bill_number);
                    Cursor c = databaseHelper.getSale(a);
                    billDetailAdapter.changeCursor(c);
                    billDetailAdapter.notifyDataSetChanged();
                    Cursor b = databaseHelper.getBillInfo(a);
                    bill_number_tv2.setText(bill_number);
                    c = null;
                    b.moveToFirst();
                    billnumber_date.setText(b.getString(1));
                    date_tv.setText(b.getString(2));
                    amount_tv.setText(b.getString(3));
                    customer_name_tv.setText(b.getString(4));
                    customer_contact_tv.setText(b.getString(5));
                    mode_tv.setText(b.getString(6));

                }
            });

            delete_item_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Please select an action!");
                    alertDialogBuilder.setIcon(R.drawable.question_mark);
                    alertDialogBuilder.setMessage("Are you sure you want to delete this bill ?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    databaseHelper.deleteBill(Integer.parseInt(bill_number));
                                    databaseHelper.deletesales(Integer.parseInt(bill_number));
                                    Cursor c = databaseHelper.getBill();

                                    salesManagmentAdapter.changeCursor(c);
                                    bill_number_tv2.setText("");
                                    mode_tv.setText("");
                                    date_tv.setText("");
                                    billnumber_date.setText("");
                                    amount_tv.setText("");
                                    customer_name_tv.setText("");
                                    customer_contact_tv.setText("");
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
            delete_item_btn.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Please select an action!");
                    alertDialogBuilder.setIcon(R.drawable.question_mark);
                    alertDialogBuilder.setMessage("Are you sure you want to cancel this bill ?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    databaseHelper.statusBill(Integer.parseInt(bill_number));
                                    Cursor c = databaseHelper.getBill();
                                    salesManagmentAdapter.changeCursor(c);
                                }
                            }).setCancelable(true).setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                return true;
                }
            });
            view_item_id.setOnLongClickListener(new View.OnLongClickListener(){
                TextView textView;
                ListView ls;
                @Override
                public boolean onLongClick(View v) {
                    final Dialog d = new Dialog(getActivity());
                    d.setContentView(R.layout.billsummary);
                    d.setTitle("BILL SUMMARY ");
                    d.setCancelable(true);
                    d.show();
                    Button dis =(Button)d.findViewById(R.id.dismiss);
                    dis.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            d.dismiss();
                        }
                    });
                    String printdatap3="";
                    textView = (TextView) d.findViewById(R.id.dis_summary);
                    ls = (ListView)d.findViewById(R.id.tax_summary);
                   Cursor summary= databaseHelper.getBillInfo(Integer.parseInt(bill_number));
                    final ArrayList<String> taxess =new ArrayList<String>();


                    ListAdapter lst = null;
                    summary.moveToFirst();
                    textView.setText(summary.getString(8));
                    String billamount = summary.getString(3);
                    if(!summary.getString(8).equalsIgnoreCase("null")){
                        billamount =String.valueOf(Double.parseDouble(billamount)-Double.parseDouble(summary.getString(8)));
                    }
                    String taxvalue = summary.getString(7);
                    for (String retval : taxvalue.split("\\,")) {
                        if (!retval.equalsIgnoreCase("")){
                            String[] result = retval.split("\\.");


                            String tax_name = result[0];
                            String tax_value = result[1];
                            blank = " ";
                            if (tax_name.length() >= 23) {
                                tax_name = tax_name.substring(0, 23);
                            } else {
                                int b = 23 - tax_name.length();
                                for (int k = 0; k < b; k++) {
                                    tax_name += blank;
                                }
                            }

                            if (tax_value.length() > 0) {
                                int da = 4 - tax_value.length();
                                for (int p = 0; p < da; p++) {
                                    tax_value = blank + tax_value;
                                }
                            }

                            String amount = String.valueOf(Double.parseDouble(billamount) * Double.parseDouble(result[1]) / 100);
                            if (amount.length() >= 0) {
                                int m = 5 - amount.length();
                                for (int p = 0; p < m; p++) {
                                    amount = blank + amount;
                                }
                            }

                            taxess.add(tax_name + "  " + "@" + "  " + tax_value + "%" + " " + amount );
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(),android.R.layout.simple_list_item_1,taxess);
                        ls.setAdapter(adapter);
                    }


                return true;
                }
            });

        }
    }

    public class BillDetailAdapter extends CursorAdapter {

        public BillDetailAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View view = inflater.inflate(R.layout.adapter_sales_view, viewGroup, false);

            return view;
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
           final TextView item = (TextView) view.findViewById(R.id.tv_item_view_id);
           final TextView qty = (TextView) view.findViewById(R.id.tv_quantity_view_id);
            final TextView price = (TextView) view.findViewById(R.id.tv_price_view_id);

            item.setText(cursor.getString(1));
            qty.setText(cursor.getString(2));
            price.setText(cursor.getString(3));
            view.setBackgroundColor(Color.WHITE);
            view.setEnabled(true);
            if(Integer.parseInt(cursor.getString(5)) == 1){
                view.setBackgroundColor(Color.LTGRAY);
                view.setEnabled(false);
                view.setOnLongClickListener(null);

            }


            bill_details.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int i, long l) {

                    cursor.moveToPosition(i);
                    String item = adapterView.getItemAtPosition(i).toString();
                    final Dialog d = new Dialog(getActivity());
                    d.setContentView(R.layout.updatebill);
                    d.setTitle("Update Item");
                    d.setCancelable(true);
                    d.show();
                    Button rupdate = (Button) d.findViewById(R.id.update);
                    Button rcancel = (Button) d.findViewById(R.id.cancel);
                    final TextView t = (TextView) d.findViewById(R.id.ramount);
                    final TextView t1 = (TextView) d.findViewById(R.id.ramount1);
                    final TextView t2 = (TextView) d.findViewById(R.id.ramount2);
                    final EditText e = (EditText) d.findViewById(R.id.rqty);
                    final TextView witht1 = (TextView) d.findViewById(R.id.tax1);
                    final TextView witht2 = (TextView) d.findViewById(R.id.tax2);
                    TextView r = (TextView) d.findViewById(R.id.ranme);
                    final String id = cursor.getString(0);
                    final String qt = cursor.getString(2);
                    final String am = cursor.getString(3);
                    final String it = cursor.getString(1);
                    final Double finalamount = Double.parseDouble(am)*Double.parseDouble(qt);
                    r.setText(it);
                    t.setText("0");
                    t1.setText("0");
                    t2.setText("0");
                    witht2.setText("0");
                    witht1.setText(String.valueOf("0"));
                    e.setText(qt);
                    rcancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            d.dismiss();
                        }
                    });


                    rupdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String qtty = e.getText().toString();
                            if(!qtty.equalsIgnoreCase("")){
                                if(Integer.parseInt(qtty)>= Integer.parseInt(qt)){
                                    if(Integer.parseInt(qtty)> Integer.parseInt(qt)){
                                        e.setError("please go for new bill");
                                    }else {
                                        e.setError("No change in Quantity..!  go for cancel button");
                                    }

                                }else {
                            if(qtty.equalsIgnoreCase("0")){
                                String beforeamount = amount_tv.getText().toString();
                                double da = Double.parseDouble(beforeamount)-Double.parseDouble(t.getText().toString())*Double.parseDouble(e.getText().toString());
                                databaseHelper.billupdateamount(da,bill_number_tv2.getText().toString());
                                databaseHelper.voidsales(id);
                                view.setBackgroundColor(Color.LTGRAY);
                                view.setEnabled(false);
                                view.setOnClickListener(null);

                                Cursor c1 = databaseHelper.getBill();

                                salesManagmentAdapter.changeCursor(c1);

                                int a = Integer.parseInt(bill_number_tv2.getText().toString());
                                Cursor c = databaseHelper.getSale(a);
                                billDetailAdapter.changeCursor(c);
                                Cursor b = databaseHelper.getBillInfo(a);
                                bill_number_tv2.setText(bill_number_tv2.getText().toString());
                                b.moveToFirst();
                                amount_tv.setText(b.getString(3));
                                d.dismiss();

                            }
                            databaseHelper.salesupdate(id,qtty);
                            String beforeamount = amount_tv.getText().toString();
                            if(t2.getText().toString().equalsIgnoreCase("0")) {
                                double da = Double.parseDouble(beforeamount) - Double.parseDouble(t.getText().toString());
                                databaseHelper.billupdateamount(da, bill_number_tv2.getText().toString());
                            }else{
                                double da = Double.parseDouble(beforeamount) + Double.parseDouble(t2.getText().toString());

                                databaseHelper.billupdateamount(da, bill_number_tv2.getText().toString());
                            }

                            int a = Integer.parseInt(bill_number_tv2.getText().toString());
                            Cursor c = databaseHelper.getSale(a);
                            billDetailAdapter.changeCursor(c);
                            Cursor b = databaseHelper.getBillInfo(a);
                            bill_number_tv2.setText(bill_number_tv2.getText().toString());
                            b.moveToFirst();
                            amount_tv.setText(b.getString(3));
                            d.dismiss();

                        }}
                        }
                    });
                    e.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            String qtty = e.getText().toString();
                            double amount=Double.parseDouble(am);
                            if(!qtty.equalsIgnoreCase("")){
                            if(Integer.parseInt(qtty)<= Integer.parseInt(qt)){
                                t.setText(String.valueOf((Double.parseDouble(qt)*Double.parseDouble(am))-(Double.parseDouble(qtty)*amount)));
                                t2.setText("0");
                                witht2.setText("0");

                               Cursor c= databaseHelper.getBillInfo(Integer.parseInt(bill_number_tv2.getText().toString()));
                                c.moveToFirst();
                                String taxvalue = c.getString(7);
                                String discont = c.getString(8);
                                double d = (Double.parseDouble(qt)*Double.parseDouble(am)-(Double.parseDouble(qtty)*amount));
                                if(!discont.equalsIgnoreCase("null")){
                                    if(!discont.equalsIgnoreCase("0.0")){
                                        d = d*(1 - Double.parseDouble(discont));
                                    }
                                }
                                double d1 = d;
                                if (taxvalue.equalsIgnoreCase("")) {
                                    witht1.setText(String.valueOf(d));

                                } else {
                                    for (String retval : taxvalue.split("\\,")) {
                                        if (!retval.equalsIgnoreCase("")) {
                                            String[] result = retval.split("\\.");
                                            String tax_value = result[1];
                                            String tax_value_print =result[3];
                                            if(tax_value_print.equalsIgnoreCase("1")){
                                                d=d1*(Double.parseDouble(tax_value)/100);

                                            }
                                            }
                                        }
                                    if(d==d1){
                                        d1=0;
                                    }
                                    d=d+d1;
                                    witht1.setText(String.valueOf(d));

                                    }
                            }else {
                                e.setError("Please go for new bill");

                            }


                        }}

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    return true;
                }
            });
        }
    }
    public class OldBillNumberAdapter extends CursorAdapter {
        public OldBillNumberAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.adapter_sales_old_bill, parent, false);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tv_column = (TextView) view.findViewById(R.id.tv_sale_old_column_id);
            TextView tv_category = (TextView) view.findViewById(R.id.tv_old_billnumber_id);
            tv_column.setText(cursor.getString(0));
            tv_category.setText(cursor.getString(1));
        }
    }


}

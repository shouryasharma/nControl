package nemi.in;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog.OnDateSetListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import in.nemi.ncontrol.R;

/**
 * Created by Aman on 6/3/2016.
 */
public class FragmentSalesManagment extends Fragment implements View.OnClickListener {
    DatabaseHelper databaseHelper;
    SalesManagmentAdapter salesManagmentAdapter;
    BillDetailAdapter billDetailAdapter;
    ListView bill_list, bill_details;
    TextView bill_number_tv2,billnumber_date, date_tv, mode_tv, amount_tv, customer_name_tv, customer_contact_tv;
    Button search_btn, cancel_button;
    EditText et_bill_number, et_customer_name, et_customer_contact;
    private EditText fromDateEtxt, toDateEtxt,et_date;
    ImageButton fromDate_ToDate_imgBtn, bill_date_search_imgBtn, amount_btn, customernamebtn_imgBtn, customercontactbtn_imgBtn;
    Cursor c = null;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    Button btn_datepicker;
    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;

    private int mHour, mMinute;

    public FragmentSalesManagment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sales, container, false);
        databaseHelper = new DatabaseHelper(getActivity(), null, null, 1);
        search_btn = (Button) rootView.findViewById(R.id.btn_search_bill_id);
        bill_number_tv2 = (TextView) rootView.findViewById(R.id.tv_bill_number_id);
        billnumber_date = (TextView) rootView.findViewById(R.id.tv_billnumber_date_id);
        date_tv = (TextView) rootView.findViewById(R.id.tv_bill_date_id);
        mode_tv = (TextView) rootView.findViewById(R.id.tv_bill_mode_id);
        amount_tv = (TextView) rootView.findViewById(R.id.tv_amount_id);
        customer_name_tv = (TextView) rootView.findViewById(R.id.tv_customer_name_id);
        customer_contact_tv = (TextView) rootView.findViewById(R.id.tv_customer_contact_id);

        search_btn.setOnClickListener(this);

/*================================================SalesManagmentAdapter ========================================================================*/
        salesManagmentAdapter = new SalesManagmentAdapter(getActivity(), databaseHelper.getBill());
        bill_list = (ListView) rootView.findViewById(R.id.userlistview);
        bill_list.setAdapter(salesManagmentAdapter);

/*===================================================BillDetailAdapter=====================================================================*/
        billDetailAdapter = new BillDetailAdapter(getActivity(), null);
        bill_details = (ListView) rootView.findViewById(R.id.bill_sale_view);
        bill_details.setAdapter(billDetailAdapter);

        int bill = databaseHelper.checkLastBillNumber();
        if (bill != 0) {
            Cursor c = databaseHelper.getSale(bill);
            billDetailAdapter.changeCursor(c);
            Cursor d = databaseHelper.getBillInfo(bill);
//            bill_number_tv2.setText("" + bill);
            mode_tv.setText("CASH");
            d.moveToFirst();
            bill_number_tv2.setText(d.getString(0));
            billnumber_date.setText(d.getString(1));
            date_tv.setText(d.getString(2));
            amount_tv.setText(d.getString(3));
            customer_name_tv.setText(d.getString(4));
            customer_contact_tv.setText(d.getString(5));
        } else {
            Toast.makeText(getActivity(), "No bills to show!", Toast.LENGTH_SHORT).show();
        }

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
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
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
                                et_bill_number.setEnabled(false);
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
        public void bindView(View view, Context context, Cursor cursor) {
            TextView bill_id_tv = (TextView) view.findViewById(R.id.tv_bill__id);
            TextView bill_number_tv = (TextView) view.findViewById(R.id.tv_bill_number_fecth_id);
            TextView bill_date_tv = (TextView) view.findViewById(R.id.tv_bill_date_fetch_id);
            TextView bill_amount_tv = (TextView) view.findViewById(R.id.tv_bill_amount_fetch_id);

            Button delete_item_btn = (Button) view.findViewById(R.id.del_item);
            Button view_item_id = (Button) view.findViewById(R.id.view_item);

            bill_id_tv.setText(cursor.getString(0));
            bill_number_tv.setText(cursor.getString(1));
            bill_date_tv.setText(cursor.getString(2));
            bill_amount_tv.setText(cursor.getString(3));

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
                    mode_tv.setText("CASH");
                    b.moveToFirst();
                    billnumber_date.setText(b.getString(1));
                    date_tv.setText(b.getString(2));
                    amount_tv.setText(b.getString(3));
                    customer_name_tv.setText(b.getString(4));
                    customer_contact_tv.setText(b.getString(5));

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
                                    Cursor c = databaseHelper.getBill();
                                    salesManagmentAdapter.changeCursor(c);
                                    bill_number_tv2.setText("");
                                    mode_tv.setText("");
                                    date_tv.setText("");
                                    billnumber_date.setText("");
                                    amount_tv.setText("");
                                    customer_name_tv.setText("");
                                    customer_contact_tv.setText("");
                                    bill_details.setAdapter(null);
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
        public void bindView(View view, Context context, Cursor cursor) {
            TextView item = (TextView) view.findViewById(R.id.tv_item_view_id);
            TextView qty = (TextView) view.findViewById(R.id.tv_quantity_view_id);
            TextView price = (TextView) view.findViewById(R.id.tv_price_view_id);

            item.setText(cursor.getString(1));
            qty.setText(cursor.getString(2));
            price.setText(cursor.getString(3));
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

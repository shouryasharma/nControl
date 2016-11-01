package nemi.in;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import in.nemi.ncontrol.R;

/**
 * Created by Aman on 5/3/2016.
 */
public class FragmentItem extends Fragment implements View.OnClickListener {
    ItemsAdapter itemsAdapter;
    TextView textView;
    DatabaseHelper databaseHelper;
    EditText et_item, et_category, et_price;
    Button additem, upload_imagepath;
    String item, category, price, imagepath;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    Uri selectedImageUri;
    ImageView tv_imagepath;
    RadioGroup radioGroup;
    RadioButton new_rad, old_rad;
    AsyncTask<Void, Void, Void> mRegisterTask;
        OldCategoryAdapter oldCategoryAdapter;
    String selectedImagePath = "noimageselected";
    private static final int MY_INTENT_CLICK = 302;
    int selectedId;
    ListView itemview;
    Context mContext;
    String node, node_password;
    String id;

    public FragmentItem() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_items, container, false);

        mContext = getActivity();
        databaseHelper = new DatabaseHelper(getActivity(), null, null, 1);
        itemsAdapter = new ItemsAdapter(getActivity(), databaseHelper.getItems());
        itemview = (ListView) rootView.findViewById(R.id.itemlistview);
        textView=(TextView)rootView.findViewById(R.id.iten_id_edit);
        itemview.setAdapter(itemsAdapter);
        et_item = (EditText) rootView.findViewById(R.id.item_id);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.myRadioGroup);
        new_rad = (RadioButton) rootView.findViewById(R.id.new_rd);
        old_rad = (RadioButton) rootView.findViewById(R.id.old_rd);
        et_category = (EditText) rootView.findViewById(R.id.category_id);
        et_price = (EditText) rootView.findViewById(R.id.price_id);
        upload_imagepath = (Button) rootView.findViewById(R.id.buttonLoadPicture);

        additem = (Button) rootView.findViewById(R.id.additembutton);

        additem.setOnClickListener(this);

        et_price.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        et_category.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        SharedPreferences settings = getActivity().getSharedPreferences(FragmentSettings.MyPREFERENCES, Context.MODE_PRIVATE);
        node = settings.getString(FragmentSettings.NODE_KEY, "");
        node_password = settings.getString(FragmentSettings.NODE_PASSWORD_KEY, "");

        upload_imagepath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), MY_INTENT_CLICK);
            }
        });
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.new_rd) {
                    et_category.setText("");
                    et_category.setEnabled(true);
                    Toast.makeText(getActivity(), "new categroy", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.old_rd) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Please select a Category");
                    ListView dialogCatList = new ListView(getActivity());
                    OldCategoryAdapter oldCategoryAdapter = new OldCategoryAdapter(getActivity()
                            , databaseHelper.getOldCategories());
                    dialogCatList.setAdapter(oldCategoryAdapter);
                    builder.setView(dialogCatList);
                    final Dialog dialog = builder.create();

                    dialogCatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TextView tv_category = (TextView) view.findViewById(R.id.tv_old_category_id);
                            String category = tv_category.getText().toString();
                            et_category.setText(category);
                            et_category.setEnabled(false);
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                }
            }
        });
        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == MY_INTENT_CLICK) {
                if (null == data) return;
                selectedImageUri = data.getData();
                //MEDIA GALLERY
                selectedImagePath = ImageFilePath.getPath(getActivity(), selectedImageUri);
                Log.i("Image File Path", "" + selectedImagePath);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.additembutton:

                Toast.makeText(mContext, "DATA", Toast.LENGTH_SHORT).show();
                item = et_item.getText().toString().replace(' ', ' ').trim();
                category = et_category.getText().toString();
                price = et_price.getText().toString().trim();
                String text =textView.getText().toString();
                if(text.equalsIgnoreCase("harry")) {
                    if (item.equals("")) {
                        et_item.setError("Item");
                    } else if (category.equals("")) {
                        et_category.setError("Category");
                    } else if (price.equals("")) {
                        //please look after this before doing anything
                        et_price.setError("Price");
                    } else if (selectedImagePath.equals("noimageselected")) {
                        databaseHelper.addItem(item, category, Integer.parseInt(et_price.getText().toString()), selectedImagePath);
                        Cursor cursor = databaseHelper.getItems();
                        itemsAdapter.changeCursor(cursor);
                        databaseHelper.close();
                        et_item.setText("");
                        et_category.setText("");
                        et_price.setText("");
                        upload_imagepath.setText("");
                        Toast.makeText(getActivity(), "No Image Selected!", Toast.LENGTH_SHORT).show();
                        et_item.setSelection(0, 0);
                    } else {
                        databaseHelper.addItem(item, category, Integer.parseInt(et_price.getText().toString()), selectedImagePath);
                        Cursor cursor = databaseHelper.getItems();
                        itemsAdapter.changeCursor(cursor);
                        databaseHelper.close();
                        et_item.setText("");
                        et_category.setText("");
                        et_price.setText("");
                        upload_imagepath.setText("");
                        selectedImagePath = "noimageselected";
                        et_item.setSelection(0, 0);
                    }
                    upload_imagepath.setText("");
                }else{
                    if (item.equals("")) {
                        et_item.setError("Item");
                    } else if (category.equals("")) {
                        et_category.setError("Category");
                    } else if (price.equals("")) {
                        //please look after this before doing anything
                        et_price.setError("Price");
                    } else if (selectedImagePath.equals("noimageselected")) {
                        databaseHelper.updateitem(text,item, category, et_price.getText().toString(), selectedImagePath);
                        Cursor cursor = databaseHelper.getItems();
                        itemsAdapter.changeCursor(cursor);
                        databaseHelper.close();
                        et_item.setText("");
                        et_category.setText("");
                        et_price.setText("");
                        upload_imagepath.setText("");
                        Toast.makeText(getActivity(), "No Image Selected!", Toast.LENGTH_SHORT).show();
                        et_item.setSelection(0, 0);
                        textView.setText("harry");
                    } else {
                        databaseHelper.updateitem(text,item, category, et_price.getText().toString(), selectedImagePath);
                        Cursor cursor = databaseHelper.getItems();
                        itemsAdapter.changeCursor(cursor);
                        databaseHelper.close();
                        et_item.setText("");
                        et_category.setText("");
                        et_price.setText("");
                        upload_imagepath.setText("");
                        selectedImagePath = "noimageselected";
                        et_item.setSelection(0, 0);
                        textView.setText("harry");
                    }


                }
//                Toast.makeText(getActivity(),"network is going on",Toast.LENGTH_SHORT).show();
                getActivity().sendBroadcast(new Intent(getActivity(), NetworkChangeReciever.class));

        }
    }


    // itemAddOn is fuction used in addItem button
    public class ItemsAdapter extends CursorAdapter {

        public ItemsAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.adapter_items_listview, parent, false);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tv_column = (TextView) view.findViewById(R.id.tv_item_column_id);
            TextView tv_item = (TextView) view.findViewById(R.id.tv_item_id);
            TextView tv_category = (TextView) view.findViewById(R.id.tv_category_id);
            TextView tv_price = (TextView) view.findViewById(R.id.tv_price_id);
            tv_imagepath = (ImageView) view.findViewById(R.id.imgView);

            id = tv_column.getText().toString();

          /*this is for path update in item mgt*/
            TextView imagePath = (TextView) view.findViewById(R.id.image_path_id);
            imagePath.setText(cursor.getString(4));
            final String imagepath = imagePath.getText().toString();

            tv_column.setText(cursor.getString(0) );
            tv_item.setText(cursor.getString(1));
            tv_category.setText(cursor.getString(2));
            tv_price.setText(cursor.getString(3));
            tv_imagepath.setImageBitmap(BitmapFactory.decodeFile(cursor.getString(4)));


            final String item_columnid = tv_column.getText().toString();

            final String item = tv_item.getText().toString();
            final String category = tv_category.getText().toString();
            final String price = tv_price.getText().toString();


            ImageButton delete = (ImageButton) view.findViewById(R.id.dele_item_id);
            ImageButton update = (ImageButton) view.findViewById(R.id.update_item_id);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Please select an action!");
                    alertDialogBuilder.setIcon(R.drawable.question_mark);
                    alertDialogBuilder.setMessage("Are you sure you want to delete this item ?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    databaseHelper.deleteItems(item_columnid);
                                    //Refresh cursor
                                    Cursor cursor = databaseHelper.getItems();
                                    itemsAdapter.changeCursor(cursor);
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
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Please select an action!");
                    alertDialogBuilder.setIcon(R.drawable.question_mark);
                    alertDialogBuilder.setMessage("Are you sure you want ot update this item ?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    textView.setText(item_columnid);
                                    et_item.setText(item);
                                    et_category.setText(category);
                                    et_price.setText(price);
                                    upload_imagepath.setText(imagepath);
                                    selectedImagePath = imagepath;
                                    Cursor cursor = databaseHelper.getItems();
                                    itemsAdapter.changeCursor(cursor);
                                }
                            }).setCancelable(false).setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    // create alert dialog_pos_for_set_qty
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }
            });
        }
    }

    public class OldCategoryAdapter extends CursorAdapter {
        public OldCategoryAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.adapter_item_old_categroy, parent, false);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tv_column = (TextView) view.findViewById(R.id.tv_item_old_column_id);
            TextView tv_category = (TextView) view.findViewById(R.id.tv_old_category_id);
            tv_column.setText(cursor.getString(0));
            tv_category.setText(cursor.getString(1));
        }
    }


}

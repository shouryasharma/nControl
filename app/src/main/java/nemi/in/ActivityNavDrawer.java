package nemi.in;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import in.nemi.ncontrol.R;
import printing.DrawerService;

public class ActivityNavDrawer extends Activity {
    FragmentPOS abc = new FragmentPOS();
    DatabaseHelper databaseHelper;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    // nav drawer title
    private CharSequence mDrawerTitle;
    // used to store app title
    private CharSequence mTitle;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    PrinterBluetoothHandler printerBluetoothHandler;
    BroadcastReceiver broadcastReceiver;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private IntentFilter intentFilter = null;
    //        String data = "00:02:0A:02:E9:9E";
    String value;
//        String data = "00:02:0A:03:1D:F5";
//    String data = "88:68:2E:00:31:4A";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String address = "Bluetooth_address";

    //    String data = "00:12:6F:73:DA:04";  @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        databaseHelper = new DatabaseHelper(this, null, null, 1);
        super.onCreate(savedInstanceState);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7B7BC0")));
//        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#cccccc")));

        // We are using shared preferences for printer address
        SharedPreferences settings = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        // Reading from SharedPreferences
        value = settings.getString(FragmentSettings.BLUETOOTH_KEY, null);
//        Toast.makeText(ActivityNavDrawer.this, "This is navDrawer  : " + value, Toast.LENGTH_SHORT).show();

        setContentView(R.layout.navdrawer_frame_listview);
        mTitle = mDrawerTitle = getTitle();
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        String LoggedInRole = databaseHelper.getLoggedInRole();
        Toast.makeText(ActivityNavDrawer.this, "Logged in as : " + LoggedInRole, Toast.LENGTH_SHORT).show();
        if (LoggedInRole.equalsIgnoreCase("ADMIN") || LoggedInRole.equalsIgnoreCase("SUPER")) {
            // POS
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
            // Sales Mgmt
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
            // User Management
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
            // Item Management
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
            // backup
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        } else {
            // POS
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
            // Sales Mgmt
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));

        }
        // adding nav drawer items to array
        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

//        getActionBar().setHomeAsUpIndicator(R.drawable.2


//        getActionBar().setIcon(R.drawable.ic_ncontrol);

        // setting_fragment the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);

//        // enabling action bar app icon and behaving it as toggle button
        if (getActionBar() != null) {
            getActionBar().setDisplayShowHomeEnabled(true);
            getActionBar().setDisplayShowTitleEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeAsUpIndicator(R.drawable.ic_ncontrol);

            getActionBar().setHomeButtonEnabled(true);

        }
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                if (getActionBar() != null) {
                    getActionBar().setTitle(mTitle);
                }
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                if (getActionBar() != null) {
                    getActionBar().setTitle(mDrawerTitle);
                }
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };


        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            if (getActionBar() != null) {
                displayView(0);
            }
        }

        setBluetoothReceiver();
    }

    public void setBluetoothReceiver() {
        printerBluetoothHandler = new PrinterBluetoothHandler(this);
        DrawerService.addHandler(printerBluetoothHandler);

        Intent intent = new Intent(this, DrawerService.class);
        startService(intent);
        System.out.println("inside on create");
        initBroadcast();
    }


    private void initBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    if (device != null && device.getName() != null) {
                        if (device == null)
                            return;

                        if (value != null)
                            DrawerService.workThread.connectBt(value);
                        else
                            Toast.makeText(ActivityNavDrawer.this, "Please check your printer address!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            ;
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        //  intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // toggle nav drawer on selecting action bar app icon/title

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.logout:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Please select an action!");
                alertDialogBuilder.setIcon(R.drawable.question_mark);
                alertDialogBuilder.setMessage("Are you sure you want to logout?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String user = databaseHelper.getLoggedInUser();
                                databaseHelper.loginStatus("false", user);
                                Intent in = new Intent(ActivityNavDrawer.this, ActivityMain.class);
                                startActivity(in);
                                finish();
                            }
                        }).setCancelable(false).setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            case R.id.settingbtn:
                Fragment fragment = new FragmentSettings();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    // option menu for visibility or not
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.settingbtn).setVisible(!drawerOpen);
        menu.findItem(R.id.logout).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }


    private void displayView(int position) {
        // update the login_main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new FragmentPOS();
                break;
            case 1:
                fragment = new FragmentSales();
                break;
            case 2:
                fragment = new FragmentUser();
                break;
            case 3:
                fragment = new FragmentItem();
                break;
            case 4:
                fragment = new FragmentBackup();
        }

        if (fragment != null) {


            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            if (getActionBar() != null) {
                setTitle(navMenuTitles[position]);
            }
            mDrawerLayout.closeDrawer(mDrawerList);


        } else {
            // error in creating fragment
            Log.e("nemi.in.ActivityNavDrawer", "Error creating fragment");
        }
    }

    //for java.lang.InstantiationException: can't instantiate cla(used onResume)
    @Override
    public void onResume() {
        // TODO Auto-generated method stub

        super.onResume();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (getActionBar() != null) {
            getActionBar().setTitle(mTitle);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}

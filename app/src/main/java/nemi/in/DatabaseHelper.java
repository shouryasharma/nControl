package nemi.in;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Developer on 21-04-2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ncontrol.db";
    public static final String COLUMN_ID = "_id";

    //tax table
    public static final String TABLE_TAX = "tax";
    public static final String COLUMN_TAX_NAME = "taxname";
    public static final String COLUMN_TAX_VALUE = "taxvalue";


    //users table vars
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_LOGINSTATUS = "loginstatus";

    //items table vars
    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ITEM = "item";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IMAGE_PATH = "imagepath";


    //sales table vars
    public static final String TABLE_SALES = "sales";
    public static final String COLUMN_BILLNUMBER = "billnumber";
    public static final String COLUMN_QUANTITY = "quantity";


    //bill table vars
    public static final String TABLE_BILL = "bill";
    public static final String COLUMN_C_NAME = "c_name";
    public static final String COLUMN_C_CONTACT = "c_contact";
    public static final String COLUMN_BILL_DATE_TIME = "c_billdatetime";
    public static final String COLUMN_BILLAMOUNT = "billamount";
    public static final String COLUMN_FLUSH_FLAG = "flush";
    public static final String COLUMN_MODE = "mode";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_TAX= "tax";
    public static final String COLUMN_TAX_NUMBER= "taxnumber";
    public static final String COLUMN_DISCOUNT= "discount";


    // Hardware number table
    public static final String TABLE_HADDRESS = "haddress";
    public static final String COLUMN_H_NUMBER = "h_number";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String usersquery = "create table " + TABLE_USERS + "(" +
                COLUMN_ID + " integer primary key autoincrement," +
                COLUMN_ROLE + " text," +
                COLUMN_USERNAME + " text," +
                COLUMN_PASSWORD + " text," +
                COLUMN_LOGINSTATUS + " text DEFAULT 'false'" +
                ");";
        db.execSQL(usersquery);

        String taxquery = "create table " + TABLE_TAX + "(" +
                COLUMN_ID + " integer primary key autoincrement," +
                COLUMN_TAX_NAME + " text," +
                COLUMN_TAX_VALUE + " text," +
                COLUMN_TAX_NUMBER + " text" +
                ");";
        db.execSQL(taxquery);

        String itemsquery = "create table " + TABLE_ITEMS + "(" +
                COLUMN_ID + " integer primary key autoincrement," +
                COLUMN_ITEM + " text not null," +
                COLUMN_CATEGORY + " text not null," +
                COLUMN_PRICE + " integer not null," +
                COLUMN_IMAGE_PATH + " text not null" +
                ");";
        db.execSQL(itemsquery);

        String salesquery = "create table " + TABLE_SALES + "(" +
                COLUMN_ID + " INTEGER primary key autoincrement," +
                COLUMN_BILLNUMBER + " integer not null," +
                COLUMN_ITEM + " text not null," +
                COLUMN_QUANTITY + " text not null," +
                COLUMN_PRICE + " integer not null," +
                COLUMN_FLUSH_FLAG + " integer not null" +
                ");";
        db.execSQL(salesquery);

        String billquery = "create table " + TABLE_BILL + "(" +
                COLUMN_ID + " integer primary key autoincrement," +
                COLUMN_BILLNUMBER + " integer not null," +
                COLUMN_C_NAME + " text not null," +
                COLUMN_C_CONTACT + " text not null," +
                COLUMN_BILL_DATE_TIME + " text NOT NULL," +
                COLUMN_BILLAMOUNT + " INTEGER NOT NULL," +
                COLUMN_FLUSH_FLAG + " INTEGER NOT NULL," +
                COLUMN_MODE+ " text not null," +
                COLUMN_TAX_VALUE + " text not null,"+
                COLUMN_STATUS +" text not null,"+
                COLUMN_DISCOUNT +" text not null"+
                ");";
        db.execSQL(billquery);

        String haddressquery = "create table " + TABLE_HADDRESS + "(" +
                COLUMN_ID + " integer primary key autoincrement," +
                COLUMN_H_NUMBER + " text not null" +
                ");";
        db.execSQL(haddressquery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_BILL);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_SALES);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_TAX );
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_HADDRESS);

        onCreate(db);
    }

    //Check for superuser
    public boolean checkS() {
        Boolean exists = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{"role"}, "role = ?", new String[]{"SUPER"}, null, null, null);
        if (cursor.getCount() == 0) {
            exists = false;
        } else {
            exists = true;
        }
        cursor.close();
        db.close();
        return exists;
    }

    //user name check
    public boolean checkuser(String user) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username='" + user + "'", null);

        if (!(mCursor.moveToFirst()) || mCursor.getCount() == 0) {
            return false;
    /* record exist */
        } else {
            return true;
    /* record not exist */
        }
    }

    //Add a user to the db
    public void addUser(String role, String user, String pass) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERNAME, user);
        cv.put(COLUMN_ROLE, role);
        cv.put(COLUMN_PASSWORD, pass);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_USERS, null, cv);
        db.close();
    }
    public void addtax(String name, String value,String p ) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TAX_NAME, name);
        cv.put(COLUMN_TAX_VALUE, value);
        cv.put(COLUMN_TAX_NUMBER,p);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_TAX, null, cv);
        db.close();
    }

    //Change login status
    public void loginStatus(String loginstatus, String user) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LOGINSTATUS, loginstatus);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_USERS, cv, COLUMN_USERNAME + " = ?", new String[]{user});
        db.close();
    }
    public void updatetax(String id, String name,String values,String paying_id) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TAX_NAME, name);
        cv.put(COLUMN_TAX_VALUE, values);
        cv.put(COLUMN_TAX_NUMBER,paying_id);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_TAX, cv, COLUMN_ID + " = ?", new String[]{id});
        db.close();
    }
    public void statusBill(int a) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, 1);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_BILL, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(a)});
        db.close();
    }

    public void ClearloginStatus() {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LOGINSTATUS, "false");
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_USERS, cv, COLUMN_LOGINSTATUS + " = ?", new String[]{"true"});
        db.close();
    }
    public void flagUpdatebill( String idno) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FLUSH_FLAG, 1);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_BILL,cv,COLUMN_ID+ " = ?", new String[]{idno});
        db.close();
    }
    public void flagUpdatesales( String idno) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FLUSH_FLAG, 1);
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_SALES,cv,COLUMN_BILLNUMBER+ " = ?", new String[]{idno});
        db.close();
    }

    public void removebills() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_BILL + " WHERE " + COLUMN_FLUSH_FLAG + "=\"" + 1 + "\";");
        db.close();
    }
    public void removesales() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SALES + " WHERE " + COLUMN_FLUSH_FLAG + "=\"" + 1 + "\";");
        db.close();
    }

    public Cursor getUsers() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "select * from " + TABLE_USERS,
                null
        );
    }
    public Cursor gettax() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "select * from " + TABLE_TAX,
                null
        );
    }
     /*------------------remove a user from the db (please remember you'll get the username by touching it in its list view---------------------------------------*/

    public void deleteUser(String username) {
        int col_id = Integer.parseInt(username);
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_USERS + " WHERE " + COLUMN_ID + "=\"" + col_id + "\";");
        db.close();
    }
    public void deletetax(String id) {
        int col_id = Integer.parseInt(id);
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_TAX + " WHERE " + COLUMN_ID + "=\"" + col_id + "\";");
        db.close();
    }

    /*------------------------------------Add a user to the db-----------------------------------------------------------------*/
    // add item in to db
    public void addItem(String item, String category, int price, String path) {

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ITEM, item);
        cv.put(COLUMN_CATEGORY, category);
        cv.put(COLUMN_PRICE, price);
        cv.put(COLUMN_IMAGE_PATH, path);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ITEMS, null, cv);
        db.close();

    }

    // added hardware address in to db
    public void addHAddress(String hnumber) {

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_H_NUMBER, hnumber);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_HADDRESS, null, cv);
        db.close();

    }

    // get hardware address from the db
    public Cursor getHAddress() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("select _id, h_number from haddress where _id in (select min(_id) from " + TABLE_HADDRESS + " group by h_number)", null);
    }

    public Cursor getItems() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "select * from " + TABLE_ITEMS,
                null
        );
    }


      /*------------//remove a item from the db (please remember you'll get the username by touching it in its listview)----------------------*/

    public void deleteItems(String itemname) {
        int col_id = Integer.parseInt(itemname);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ITEMS + " WHERE " + COLUMN_ID + "=\"" + col_id + "\";");
        db.close();
    }

    public void deleteBill(int billnumber) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_BILL + " WHERE " + COLUMN_ID + "=\"" + billnumber + "\";");
        db.close();
    }
    public void deletesales(int billnumber) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SALES + " WHERE " + COLUMN_ID + "=\"" + billnumber + "\";");
        db.close();
    }

    /*-------------------------------------login-----------------------------------------------------------------*/

    public String loginUser(String u) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + COLUMN_USERNAME + "," + COLUMN_PASSWORD + " FROM " + TABLE_USERS;
        Cursor cursor = db.rawQuery(query, null);
        String a, b;
        b = "NOT FOUND";
        if (cursor.moveToFirst()) {
            do {
                a = cursor.getString(0);
                if (a.equals(u)) {
                    b = cursor.getString(1);
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return b;
    }


    /*-------------------------------------------------------Tab categories Fragment-----------------------------------------------------------------*/
    // all distinct category for tab
    public Cursor getCategories() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("select distinct category from " + TABLE_ITEMS + "", null);
        //category :- fruit,food,seafood;

    }

    //old category for radio button
    public Cursor getOldCategories() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("select _id, category from items where _id in (select min(_id) from " + TABLE_ITEMS + " group by category)", null);

    }

    public Cursor getOldBillNumber() {
        SQLiteDatabase db = getReadableDatabase();
//        return db.rawQuery("select distinct _id,billnumber from " + TABLE_SALES + "", null);
        return db.rawQuery("select _id,billnumber from sales where _id in (select min(_id) from sales group by billnumber)", null);

    }

    // get postion of item from fragment_pos
    public Cursor getPOSItems(String a) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from items where " + COLUMN_CATEGORY + "=\"" + a + "\";", null);
        return c;
    }

    /*-------------------------------------------------------fragment_pos bill-----------------------------------------------------------------*/
// check last bill number by _id and put in to trasaction number(sales fragment)
    public int checkLastBillNumber() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id FROM bill ORDER BY _id DESC LIMIT 1;", null);
        int billnumber = 0;
        if (cursor.moveToFirst()) {
            billnumber = Integer.parseInt(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return billnumber;
    }


    // This is giving us last bill number of date after that start by 1 from new days
    public int checkLastBillDate() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT billnumber, c_billdatetime, billamount FROM bill " +
                "WHERE strftime('%Y-%m-%d',c_billdatetime) ='" + getDate() + "' ORDER BY billnumber DESC LIMIT 1", null);
        int billnumber = 0;
        if (cursor.moveToFirst()) {
            billnumber = Integer.parseInt(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return billnumber;
    }

    public void sales(int billnumber, String item, int quantity, int price) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BILLNUMBER, billnumber);
        cv.put(COLUMN_ITEM, item);
        cv.put(COLUMN_QUANTITY, quantity);
        cv.put(COLUMN_PRICE, price);
        cv.put(COLUMN_FLUSH_FLAG,0);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SALES, null, cv);
        db.close();
    }

    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    public void bill(int billnum, String c_name, String c_number, int billamount,String mode,String tax,String dico) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BILLNUMBER, billnum);
        cv.put(COLUMN_C_NAME, c_name);
        cv.put(COLUMN_C_CONTACT, c_number);
        cv.put(COLUMN_BILLAMOUNT, billamount);
        cv.put(COLUMN_BILL_DATE_TIME, getDateTime());
        cv.put(COLUMN_FLUSH_FLAG, 0);
        cv.put(COLUMN_MODE,mode);
        cv.put(COLUMN_TAX_VALUE,tax);
        cv.put(COLUMN_STATUS,0);
        cv.put(COLUMN_DISCOUNT,dico);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_BILL, null, cv);
        db.close();
    }

    /*----------------------------------------------------------get bill magmt---------------------------------------------------------*/
    public Cursor getBill() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT _id,billnumber, c_billdatetime, billamount,status FROM bill ORDER BY _id DESC", null);
    }
    public Cursor getBilldate() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT _id, c_billdatetime, flush FROM bill ORDER BY _id DESC", null);
    }
    public Cursor getBillsInfo() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT _id, billnumber, c_billdatetime, billamount, c_name, c_contact, status FROM bill ORDER BY _id DESC", null);
    }
    public Cursor getSale(int billnumber) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT _id, item, quantity, price FROM sales WHERE billnumber = " + billnumber, null);
    }

    public Cursor getBillInfo(int billnumber) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT _id,billnumber, c_billdatetime, billamount, c_name, c_contact, mode,taxvalue,discount FROM bill WHERE _id = " + billnumber, null);
    }

    public String getLoggedInUser() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT username FROM users WHERE loginstatus = 'true'", null);
        c.moveToFirst();
        return c.getString(0);
    }

    public String getLoggedInRole() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT role FROM users WHERE loginstatus = 'true'", null);
        c.moveToFirst();
        return c.getString(0);
    }
    /*=========================================================Search query here=================================================================*/
    // search by billnumber and date
    public Cursor searchByDate(int billnumber, String date) {
        SQLiteDatabase db = getReadableDatabase();
        String qry = "SELECT _id,billnumber, c_billdatetime, billamount FROM bill WHERE billnumber = " + billnumber + " and c_billdatetime like '" + date + "%%%%%%%%'";
        return db.rawQuery(qry, null);
    }
    // search by Fromdate to Todate
    public Cursor searchByBillNumber(String fdate, String tdate) {
        SQLiteDatabase db = getReadableDatabase();
        String qry = "SELECT _id,billnumber, c_billdatetime, billamount FROM bill WHERE date(c_billdatetime) BETWEEN date('" + fdate + "') AND date('" + tdate + "') ORDER BY _id DESC";
        return db.rawQuery(qry, null);
    }

    // search by name
    public Cursor searchByCustomerName(String customername) {
        SQLiteDatabase db = getReadableDatabase();
        String qry = "SELECT _id, c_billdatetime, billamount FROM bill WHERE c_name LIKE '" + customername + "'";
        return db.rawQuery(qry, null);
    }
    // search by contact
    public Cursor searchByCustomerContact(String contact) {
        SQLiteDatabase db = getReadableDatabase();
        String qry = "SELECT _id, c_billdatetime, billamount FROM bill WHERE c_contact LIKE '" + contact + "'";
        return db.rawQuery(qry, null);
    }
}
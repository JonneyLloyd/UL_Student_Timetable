package ie.gavin.ulstudenttimetable.data;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

public class MyDBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ULtimetable.db";
    public static final String TABLE_MODULE = "module";
    public static final String TABLE_WEEK = "date";
    public static final String TABLE_STUDENT_TIMETABLE = "studentTimetable";
    //modules table
    public static final String COLUMN_MODULE_ID = "moduleID";
    public static final String COLUMN_START_TIME = "startTime";
    public static final String COLUMN_END_TIME = "endTime";
    public static final String COLUMN_WEEK_START = "weekStart";
    public static final String COLUMN_WEEK_END = "weekEnd";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_LECTURER = "lecturer";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_LAST_UPDATE = "lastUpdate";
    //dates table
    public static final String COLUMN_WEEK = "week";
    public static final String COLUMN_WEEK_LABEL = "weekLabel";
   //student table
    public static final String COLUMN_STUDENT_ID = "studentID";
    public static final String COLUMN_TIMETABLE_NAME = "timetableName";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_GROUP_NAME = "groupName";
    public static final String COLUMN_ATTRIB = "attrib";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1 = "CREATE TABLE " + TABLE_MODULE + "(" +
                COLUMN_MODULE_ID + " VARCHAR(10) " +
                COLUMN_START_TIME + " TIME " +
                COLUMN_END_TIME + " TIME " +
                COLUMN_WEEK_START + " INTEGER " +   //check data type
                COLUMN_WEEK_END + " INTEGER " +
                COLUMN_ROOM + " VARCHAR(10) " +
                COLUMN_LECTURER + " VARCHAR(10) " +
                COLUMN_DAY + " DATE " +             //check data type
                COLUMN_LAST_UPDATE + " DATE " +
                COLUMN_GROUP_NAME + " VARCHAR(5) " +
                COLUMN_ATTRIB + " VARCHAR(45) " +
                "PRIMARY KEY(" + COLUMN_MODULE_ID + " " + COLUMN_START_TIME + ")" +
                ");";

        String query2 = "CREATE TABLE " + TABLE_WEEK + "(" +
                COLUMN_WEEK + " INTEGER PRIMARY KEY" +
                COLUMN_WEEK_LABEL + " VARCHAR(15) " +
                COLUMN_WEEK_START + " DATE " +
                ");";

        String query3 = "CREATE TABLE " + TABLE_STUDENT_TIMETABLE + "(" +
                COLUMN_MODULE_ID + " VARCHAR(10) " +
                COLUMN_START_TIME + " TIME " +
                COLUMN_DAY + " DATE " +
                COLUMN_END_TIME + " TIME " +
                COLUMN_STUDENT_ID + " INTEGER " +
                COLUMN_TIMETABLE_NAME + " VARCHAR(20) " +
                COLUMN_NOTES + " TEXT " +
                COLUMN_LAST_UPDATE + " DATE " +
                COLUMN_GROUP_NAME + " VARCHAR(5) " +
                COLUMN_ATTRIB + " VARCHAR(45) " +
                "PRIMARY KEY(" + COLUMN_MODULE_ID + " " + COLUMN_START_TIME + ")" +
                ");";

        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_MODULE);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_WEEK);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_STUDENT_TIMETABLE);
        onCreate(db);

    }

    //Add a new row to the database
    public void addModule(Module module){
        ContentValues values = new ContentValues();
        values.put(COLUMN_MODULE_ID, module.get_ModuleID());
        values.put(COLUMN_START_TIME, module.get_startTime());
        values.put(COLUMN_END_TIME, module.get_endTime());
        values.put(COLUMN_WEEK_START, module.get_weekStart());
        values.put(COLUMN_WEEK_END, module.get_weekEnd());
        values.put(COLUMN_ROOM, module.get_room());
        values.put(COLUMN_LECTURER, module.get_lecturer());
        values.put(COLUMN_DAY, String.valueOf(module.get_day()));
        values.put(COLUMN_LAST_UPDATE, String.valueOf(module.get_last_update()));
        values.put(COLUMN_GROUP_NAME, module.get_groupName());
        values.put(COLUMN_ATTRIB, module.get_attrib());

        SQLiteDatabase db = getWritableDatabase();

        db.insert(TABLE_MODULE, null, values);
        db.close();
    }

    //Delete a row from database
    public void  deleteModule(String moduleID){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MODULE + " WHERE " + COLUMN_MODULE_ID + "=\"" + moduleID + "\";");
        db.close();


    }
}

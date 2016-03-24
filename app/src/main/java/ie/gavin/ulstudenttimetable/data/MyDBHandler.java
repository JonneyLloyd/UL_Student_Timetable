package ie.gavin.ulstudenttimetable.data;


import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.util.Log;
import android.widget.Toast;

public class MyDBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ULtimetable.db";
    public static final String TABLE_MODULE = "module";
    public static final String TABLE_WEEK = "date";
    public static final String TABLE_STUDENT_TIMETABLE = "studentTimetable";
    public static final String TABLE_CLASS_WEEKS = "classWeeks";
    public static final String TABLE_UID = "uid";
    public static final String TABLE_MODULE_NAMES = "moduleNames";
    //modules table
    public static final String COLUMN_ID_TABLE_POINTER = "idTablePointer";
    public static final String COLUMN_MODULE_CODE = "moduleCode";
    public static final String COLUMN_START_TIME = "startTime";
    public static final String COLUMN_END_TIME = "endTime";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_LECTURER = "lecturer";
    public static final String COLUMN_DAY = "day";
    //weeks table
    public static final String COLUMN_WEEK = "week";
    public static final String COLUMN_WEEK_LABEL = "weekLabel";
    public static final String COLUMN_WEEK_START = "startWeek";
   //student table
    public static final String COLUMN_STUDENT_ID = "studentID";
    public static final String COLUMN_MODULE_POINTER = "modulePointer";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_GROUP_NAME = "groupName";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TITLE = "title";

    //class weeks
    public static final String COLUMN_START_WEEK = "startWeek";
    public static final String COLUMN_END_WEEK = "endWeek";

    //module name
    public static final String COLUMN_MODULE_NAME = "moduleName";

    //uid
    public static final String COLUMN_ID = "id";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1 = "CREATE TABLE " + TABLE_MODULE + "(" +
                COLUMN_ID_TABLE_POINTER + " INTEGER PRIMARY KEY, " +
                COLUMN_MODULE_CODE + " VARCHAR(10), " +
                COLUMN_START_TIME + " TIME, " +
                COLUMN_END_TIME + " TIME, " +
                COLUMN_ROOM + " VARCHAR(10), " +
                COLUMN_LECTURER + " VARCHAR(10), " +
                COLUMN_DAY + " INT, " +             //check data type
                COLUMN_GROUP_NAME + " VARCHAR(5), " +
                COLUMN_TYPE + " VARCHAR(45) " +
                ");";

        String query2 = "CREATE TABLE " + TABLE_WEEK + "(" +
                COLUMN_WEEK + " INTEGER PRIMARY KEY ," +
                COLUMN_WEEK_LABEL + " VARCHAR(15), " +
                COLUMN_WEEK_START + " DATE " +
                ");";

        String query3 = "CREATE TABLE " + TABLE_STUDENT_TIMETABLE + "(" +
                COLUMN_ID_TABLE_POINTER + " INTEGER PRIMARY KEY, " +
                COLUMN_MODULE_POINTER + " INTEGER, " +
                COLUMN_MODULE_CODE + " VARCHAR(10), " +
                COLUMN_START_TIME + " TIME, " +
                COLUMN_DAY + " INTEGER, " +
                COLUMN_END_TIME + " TIME, " +
                COLUMN_STUDENT_ID + " INTEGER, " +
                COLUMN_NOTES + " TEXT, " +
                COLUMN_GROUP_NAME + " VARCHAR(5), " +
                COLUMN_TYPE + " VARCHAR(45), " +
                COLUMN_TITLE + " VARCHAR(45), " +
                COLUMN_LECTURER + " VARCHAR(45) " +
                COLUMN_ROOM + " VARCHAR(10), " +
                ");";

        String query4 = "CREATE TABLE " + TABLE_CLASS_WEEKS + "(" +
                COLUMN_START_WEEK + " INTEGER, " +
                COLUMN_END_WEEK + " INTEGER, " +
                COLUMN_ID_TABLE_POINTER + " INTEGER, " +
                "PRIMARY KEY(" + COLUMN_START_WEEK + ", " + COLUMN_END_WEEK + ", " + COLUMN_ID_TABLE_POINTER +
                ")" +
                ");";
        String query5 = "CREATE TABLE " + TABLE_UID + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                ");";

        String query6 = "CREATE TABLE " + TABLE_MODULE_NAMES + "(" +
                COLUMN_MODULE_CODE + " VARCHAR(10) PRIMARY KEY, " +
                COLUMN_MODULE_NAME + " VARCHAR(45) " +
                ");";

        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);
        db.execSQL(query4);
        db.execSQL(query5);
        db.execSQL(query6);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_MODULE);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_WEEK);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_STUDENT_TIMETABLE);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_CLASS_WEEKS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_UID);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_MODULE_NAMES);
        onCreate(db);

    }

    //Add a new row to the database
    public void addModule(Module module){
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_UID, COLUMN_ID, null); //get return value and pass to idTablePointer

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_TABLE_POINTER, id);//id taken from UID table
        values.put(COLUMN_MODULE_CODE, module.get_ModuleCode());
        values.put(COLUMN_START_TIME, String.valueOf(module.get_startTime()));
        values.put(COLUMN_END_TIME, String.valueOf(module.get_endTime()));
        values.put(COLUMN_ROOM, module.get_room());
        values.put(COLUMN_LECTURER, module.get_lecturer());
        values.put(COLUMN_DAY, String.valueOf(module.get_day()));
        values.put(COLUMN_GROUP_NAME, module.get_groupName());
        values.put(COLUMN_TYPE, module.get_type());

        db.insert(TABLE_MODULE, null, values);
        db.close();
    }

    //add row to studentTimetable table
    public void addToStudentTimetable(StudentTimetable entry){
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_UID, COLUMN_ID, null); //get return value and pass to idTablePointer
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID_TABLE_POINTER, id);
        values.put(COLUMN_MODULE_POINTER, entry.get_modulePointer());
        values.put(COLUMN_MODULE_CODE, entry.get_moduleCode());
        values.put(COLUMN_START_TIME, entry.get_start_time());
        values.put(COLUMN_DAY, String.valueOf(entry.get_day()));
        values.put(COLUMN_END_TIME, entry.get_endTime());
        values.put(COLUMN_STUDENT_ID, entry.get_endTime());
        values.put(COLUMN_NOTES, entry.get_studentID());
        values.put(COLUMN_GROUP_NAME, entry.get_groupName());
        values.put(COLUMN_TYPE, entry.get_type());
        values.put( COLUMN_TITLE, entry.get_title());
        values.put(COLUMN_LECTURER, entry.get_lecturer());
        values.put(COLUMN_ROOM, entry.get_room());

        db.insert(TABLE_STUDENT_TIMETABLE, null, values);
        db.close();
    }

    //return a module row from id
    public Module getModuleFromID(int id){
        Module result = null;
        String query = "SELECT * FROM " + TABLE_MODULE + " WHERE " +
                COLUMN_ID_TABLE_POINTER + " = " + id + ";";
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();
            result = new Module(
                    Integer.parseInt(c.getString(c.getColumnIndex("idTablePointer"))),
                    c.getString(c.getColumnIndex("moduleCode")),
                    c.getString(c.getColumnIndex("startTime")),
                    c.getString(c.getColumnIndex("endTime")),
                    c.getString(c.getColumnIndex("room")),
                    c.getString(c.getColumnIndex("lecturer")),
                    Integer.parseInt(c.getString(c.getColumnIndex("day"))),
                    c.getString(c.getColumnIndex("groupName")),
                    c.getString(c.getColumnIndex("type"))
            );
        }
            catch (SQLiteException e)
            {
                Log.d("SQL Error", e.getMessage());
                return null;
            }
            catch (CursorIndexOutOfBoundsException ce)
            {
                Log.d("ID not found", ce.getMessage());

            }
            finally
            {
                //release all resources
                c.close();
                db.close();
            }
        return result;
    }

    //return a studentTimetable row from id
    public StudentTimetable getStudentTimetableFroID(int id){
        StudentTimetable result = null;
        String query = "SELECT * FROM " + TABLE_STUDENT_TIMETABLE + " WHERE " +
                COLUMN_ID_TABLE_POINTER + " = " + id + ";";
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();
            result = new StudentTimetable(
                    Integer.parseInt(c.getString(c.getColumnIndex("idTablePointer"))),
                    c.getString(c.getColumnIndex("moduleCode")),
                    Integer.parseInt(c.getString(c.getColumnIndex("modulePointer"))),
                    c.getString(c.getColumnIndex("startTime")),
                    Integer.parseInt(c.getString(c.getColumnIndex("day"))),
                    c.getString(c.getColumnIndex("endTime")),
                    Integer.parseInt(c.getString(c.getColumnIndex("studentID"))),
                    c.getString(c.getColumnIndex("notes")),
                    c.getString(c.getColumnIndex("groupName")),
                    c.getString(c.getColumnIndex("type")),
                    c.getString(c.getColumnIndex("title")),
                    c.getString(c.getColumnIndex("lecturer")),
                    c.getString(c.getColumnIndex("room"))
            );
        }
        catch (SQLiteException e)
        {
            Log.d("SQL Error", e.getMessage());
            return null;
        }
        catch (CursorIndexOutOfBoundsException ce)
        {
            Log.d("ID not found", ce.getMessage());

        }
        finally
        {
            //release all resources
            c.close();
            db.close();
        }


        return result;
    }



    //Delete a row from moduleTimetable
    public void  deleteModuleEntryFromID(int IDTablePointer){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MODULE + " WHERE " + COLUMN_ID_TABLE_POINTER + "=\"" + IDTablePointer + "\";");
        db.close();

    }
    //delete all entries with this moduleID
    public void  deleteModule(int moduleID){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MODULE + " WHERE " + COLUMN_MODULE_CODE + "=\"" + moduleID + "\";");
        db.close();

    }

    //delete a row from studentTimetable
    public void  deleteStudentTimetableEntryFromID(int IDTablePointer){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_STUDENT_TIMETABLE + " WHERE " + COLUMN_ID_TABLE_POINTER + "=\"" + IDTablePointer + "\";");
        db.close();

    }
}
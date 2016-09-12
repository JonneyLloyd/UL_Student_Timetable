package ie.gavin.ulstudenttimetable;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ie.gavin.ulstudenttimetable.calendar.CalendarEvent;
import ie.gavin.ulstudenttimetable.calendar.CalendarView;
import ie.gavin.ulstudenttimetable.data.Module;
import ie.gavin.ulstudenttimetable.data.MyDBHandler;
import ie.gavin.ulstudenttimetable.data.StudentTimetable;
import ie.gavin.ulstudenttimetable.data.Week;

public class TimetableActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener, EventDialogFragment.closeEventDialogListener {

    FloatingActionButton fabMenu;
    FloatingActionButton fabClass;
    FloatingActionButton fabMemo;
    FloatingActionButton fabMeeting;

    private Spinner weekSpinner;
    private NavigationView navigationView;
    private RelativeLayout navHeader;
    private Menu navMenu;
    private CalendarView cv;
    ArrayList<CalendarEvent> events = new ArrayList<>();
    HashMap<Integer, String> users = new HashMap<>();
    private ArrayList<Week> weekDetails = new ArrayList<>();

    private int userId;         // load primary user
    private int daysVisible;    // load previous number of days visible

    private int puid;           // stores previous id
    private int weekId;         // the viewed week
    private int currentWeekId;  // the current week
    private Date weekStartDate;

    private final int REQUEST_CODE_ADD_TIMETABLE = 100;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHandler = MyDBHandler.getInstance(getApplicationContext());
        loadPreferences();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeader = (RelativeLayout) navigationView.getHeaderView(0);
        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toggle visibility of users list
                if (navMenu.getItem(0).isVisible()) {
                    navMenu.setGroupVisible(R.id.nav_group_users, false);
                } else {
                    navMenu.setGroupVisible(R.id.nav_group_users, true);
                }
            }
        });


        if (userId == 0) {   // if no user set yet
            addUser();
        }

        initCalendarView();
        loadNavigationViewUsers();
        loadActionbarWeeks();
        cv.focusCalendar();

        com.github.clans.fab.FloatingActionMenu fabMenu = (com.github.clans.fab.FloatingActionMenu) findViewById(R.id.floating_menu);
        FloatingActionButton fabClass       = (FloatingActionButton) findViewById(R.id.menu_item_class);
        FloatingActionButton fabMemo        = (FloatingActionButton) findViewById(R.id.menu_item_memo);
        FloatingActionButton fabMeeting     = (FloatingActionButton) findViewById(R.id.menu_item_meeting);
        fabMenu.setClosedOnTouchOutside(false);
        fabClass.setOnClickListener(new FloatingActionButtonClickListener());
        fabMemo.setOnClickListener(new FloatingActionButtonClickListener());
        fabMeeting.setOnClickListener(new FloatingActionButtonClickListener());

    }

    public class FloatingActionButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.menu_item_class:
                    openCreateEventDialog("LEC");
                    break;

                case R.id.menu_item_memo:
                    openCreateEventDialog("Memo");
                    break;

                case R.id.menu_item_meeting:
                    openCreateEventDialog("Meeting");
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (cv.isInEditMode()) {
            if (!moduleChooserAdd.isEmpty() || !moduleChooserRemove.isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("Module editor")
                        .setMessage("Are you sure you want to exit?\nYour changes won't be saved")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue
                                cv.setEditMode(false);
                                loadTimetable();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                return;
                            }
                        })
                        .show();
            } else {
                cv.setEditMode(false);
                loadTimetable();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (cv == null || !cv.isInEditMode()) {
            getMenuInflater().inflate(R.menu.timetable, menu);
        } else {
            getMenuInflater().inflate(R.menu.save_cancel, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_now) {
            weekSpinner.setSelection(currentWeekId - 1);    // returns to the current week if on a different week
            cv.focusCalendar();
            return true;
        } else if (id == R.id.action_delete) {
            removeUser();
            return true;

        } else if (id == R.id.action_cancel) {
            cv.setEditMode(false);
            loadTimetable();
            return true;
        } else if (id == R.id.action_save) {

            dbHandler.addOrRemoveModuleFromStudentTimetable(moduleChooserAdd, userId, MyDBHandler.ADD);
//            Log.v("choose add", "---------------");
//            for (Integer add : moduleChooserAdd)
//                Log.v("choose", ""+add);

            dbHandler.addOrRemoveModuleFromStudentTimetable(moduleChooserRemove, userId, MyDBHandler.DELETE);
//            Log.v("choose remove", "---------------");
//            for (Integer remove : moduleChooserRemove)
//                Log.v("choose", ""+remove);

            cv.setEditMode(false);
            loadTimetable();
            updateReminderService();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (users.containsKey(id)) {
            String name = users.get(id);
            setNavigationViewUser(id, name);
            loadTimetable();
        } else if (id == R.id.users_add) {
//            Toast.makeText(TimetableActivity.this, "Add user", Toast.LENGTH_SHORT).show();
            addUser();  // TODO add to list after
        } else if (id == R.id.nav_one_day) {
            daysVisible = 1;
            cv.setVisibleDays(daysVisible);
            cv.updateCalendar();
        } else if (id == R.id.nav_three_day) {
            daysVisible = 3;
            cv.setVisibleDays(daysVisible);
            cv.updateCalendar();
        } else if (id == R.id.nav_five_day) {
            daysVisible = 5;
            cv.setVisibleDays(daysVisible);
            cv.updateCalendar();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void removeUser() {
        dbHandler.deleteUser(userId);
        users.remove(userId);
        navigationView.getMenu().removeItem(userId);
        if (users.size() > 0) {
            // default to next user
            Map.Entry<Integer, String> entry = users.entrySet().iterator().next();
            userId = entry.getKey();
            setNavigationViewUser(userId, entry.getValue());
            loadTimetable();
        } else {
            userId = 0;
            addUser();
        }
        savePreferences();
    }

    public void addUser() {
        Intent intent = new Intent(getApplicationContext(), AddStudentTimetableActivity.class);
        intent.putExtra("resultCode", REQUEST_CODE_ADD_TIMETABLE);
        startActivityForResult(intent, REQUEST_CODE_ADD_TIMETABLE);
    }

    // Function to read the result from newly created activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == REQUEST_CODE_ADD_TIMETABLE) {
            String studentId = (String) data.getExtras().get("studentId");
            userId = Integer.parseInt(studentId);
            String studentName = data.getExtras().getString("studentName", "New user");

//            Toast.makeText(TimetableActivity.this, studentName + " " + userId + " was added!", Toast.LENGTH_SHORT).show();

            users.put(userId, studentName);
            addNavigationViewUser(userId, studentName);
            setNavigationViewUser(userId, studentName);
            loadTimetable();
            loadActionbarWeeks();
            savePreferences();
            updateReminderService();
        } else if (users.size() == 0) {
            finish();       // close the app if the user does not add their timetable
        }

    }

    public void loadNavigationViewUsers() {
        // TODO populate from DB or shared prefs??
        String userName = "";
        navMenu = navigationView.getMenu();

        users = dbHandler.getUsers();

        for (Map.Entry<Integer, String> user : users.entrySet()) {
            int id = user.getKey();
            String name = user.getValue();
            addNavigationViewUser(id, name);
            if (id == userId) userName = name;
        }

        navMenu.setGroupVisible(R.id.nav_group_users, false);

        if (userId != 0) {
            setNavigationViewUser(userId, userName);
        }
    }

    public void addNavigationViewUser(int userId, String userName) {
        navigationView.getMenu().add(R.id.nav_group_users, userId, Menu.NONE, userName)
                .setIcon(R.drawable.ic_account_circle);
        navigationView.getMenu().setGroupVisible(R.id.nav_group_users, false);
    }

    public void setNavigationViewUser(int userId, String userName) {
        this.userId = userId;
        if (navMenu.findItem(puid) != null)
            navMenu.findItem(puid).setChecked(false);
        navMenu.findItem(userId).setChecked(true);
        ((TextView) navHeader.findViewById(R.id.user_name)).setText(userName);
        ((TextView) navHeader.findViewById(R.id.user_id)).setText("" + userId);
        puid = userId;
    }

    public void loadActionbarWeeks() {
        // Spinner element
        weekSpinner = (Spinner) findViewById(R.id.weekSpinner);

        // Spinner click listener
        weekSpinner.setOnItemSelectedListener(this);

        weekDetails =  dbHandler.getWeekDetails();

        // Get the current weekId
        for (int i = weekDetails.size()-1; i >= 0; i--) {
            // Get the first date before the current week start date
            if (weekStartDate.compareTo(weekDetails.get(i).get_weekStart()) >= 0) {
                weekId = weekDetails.get(i).get_week();
                currentWeekId = weekId;
                break;
            }
        }

        // Creating adapter for spinner
        ArrayAdapter<Week> dataAdapter = new ArrayAdapter<Week>(this, R.layout.toolbar_spinner_item_actionbar, weekDetails);
        // Drop down layout style
        dataAdapter.setDropDownViewResource(R.layout.toolbar_spinner_item_dropdown);
        weekSpinner.setAdapter(dataAdapter);
        weekSpinner.setSelection(weekId - 1);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        Week item = (Week) parent.getItemAtPosition(position);

        weekId = item.get_week();
        weekStartDate = item.get_weekStart();

        // Showing selected spinner item
//        Toast.makeText(parent.getContext(), "Selected: " + item + ": " + item.get_weekStart() + " -> " + weekId, Toast.LENGTH_LONG).show();

        loadTimetable();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public String formatEvent(Object obj) {
        // TODO more + sortable
        if (obj instanceof StudentTimetable) {
            return ((StudentTimetable) obj).get_type()
                    + " " + ((StudentTimetable) obj).get_groupName()
                    + " " + ((StudentTimetable) obj).get_room()
                    + "\n" + ((StudentTimetable) obj).get_title();
        } else if (obj instanceof Module) {
            return ((Module) obj).get_type()
                    + " " + ((Module) obj).get_groupName()
                    + " " + ((Module) obj).get_room();
        } else {
            return obj.toString();
        }
    }

    // day 1-7
    public Calendar getEventDateTime(Date firstDayOfWeek, int dayOfWeek, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(firstDayOfWeek);
        cal.set(Calendar.DAY_OF_WEEK, cal.get(Calendar.DAY_OF_WEEK) + dayOfWeek - 1);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        return cal;
    }

    private ArrayList<Integer> moduleChooserRemove = new ArrayList<>();
    private ArrayList<Integer> moduleChooserAdd = new ArrayList<>();

    public void loadTimetableModuleChooser(int eventId) {
        cv.setEditMode(true);
        invalidateOptionsMenu();    // changing contents
        StudentTimetable studentTimetable = dbHandler.getStudentTimetableFromID(eventId);
        String moduleCode = studentTimetable.get_moduleCode();
//        Toast.makeText(TimetableActivity.this, "edit mode "+moduleCode, Toast.LENGTH_SHORT).show();

        ArrayList<Module> moduleTimetables = dbHandler.getAllFromModuleTable(moduleCode);

        if (moduleTimetables.size() == 0) {
            cv.setEditMode(false);
            Snackbar snackbar = Snackbar.make(findViewById(R.id.calendar_view), "No other entries exist for this module", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        // TODO
//        ArrayList<Module> moduleTimetables = dbHandler.getAllFromModuleTable(moduleCode, weekId);
//        Log.v("match", ""+moduleTimetables.size());

        moduleChooserRemove.clear();
        moduleChooserAdd.clear();
//        events.clear();
        ArrayList<CalendarEvent> moduleEvents = new ArrayList<CalendarEvent>();
        for (Module moduleTimetable : moduleTimetables) {
//            Log.v("strange", moduleTimetable.get_ModuleCode()+" "+moduleTimetable.get_startTime()+" "+moduleTimetable.get_endTime()+" "+moduleTimetable.get_groupName());

            String [] startTime = moduleTimetable.get_startTime().split(":");
            String [] endTime = moduleTimetable.get_endTime().split(":");
            int day = moduleTimetable.get_day();

            Calendar start = getEventDateTime(weekStartDate, day, Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1]));
            Calendar end = getEventDateTime(weekStartDate, day, Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1]));

            CalendarEvent moduleEvent = new CalendarEvent(
                    start,
                    end,
                    formatEvent(moduleTimetable),
                    studentTimetable.get_color(),   // keep the original color
                    moduleTimetable.get_idTablePointer(),
                    moduleTimetable.get_idTablePointer()    // modules don't have a parent, just for comparing with children
                );

            if (events.contains(moduleEvent)) {
                Log.v("match", moduleEvent.getTitle());
                moduleEvent.setOriginallyAttending(true);
            }

            moduleEvents.add(moduleEvent);

        }
        // Add callbacks
        cv.setEditorEventClickListener(new CalendarView.EditorEventClickListener() {
            @Override
            public void onEditorEventClick(int eventId, boolean checked) {
                // Toggles whether we are taking the class or not. Stores the changes in a list
                // but if the user reverts the change, the change is no longer part of either list
                if (checked) {
                    if (moduleChooserRemove.contains(eventId) /*in remove list*/) {
                        // remove from remove list
                        moduleChooserRemove.remove(new Integer(eventId));
                    } else {
                        // add to add list
                        moduleChooserAdd.add(eventId);
                    }

                } else {
                    if (moduleChooserAdd.contains(eventId) /*in add list*/) {
                        // remove from add list
                        moduleChooserAdd.remove(new Integer(eventId));
                    } else {
                        // add to remove list
                        moduleChooserRemove.add(eventId);
                    }

                }

            }
        });
        cv.updateCalendar(moduleEvents);
    }

    public void loadTimetable() {   // TODO fix double load
        cv.setEditMode(false);
        invalidateOptionsMenu();
//        Toast.makeText(TimetableActivity.this, "loading: "+userId, Toast.LENGTH_SHORT).show();
        ArrayList<StudentTimetable> studentTimetables = dbHandler.getAllFromStudentTimetable(userId, weekId);

        events.clear();
        for (StudentTimetable studentTimetable : studentTimetables) {

            String [] startTime = studentTimetable.get_start_time().split(":");
            String [] endTime = studentTimetable.get_endTime().split(":");
            int day = studentTimetable.get_day();   // 1-7

            Calendar start = getEventDateTime(weekStartDate, day, Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1]));
            Calendar end = getEventDateTime(weekStartDate, day, Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1]));

            events.add(new CalendarEvent(
                    start,
                    end,
                    formatEvent(studentTimetable),
                    studentTimetable.get_color(),
                    studentTimetable.get_idTablePointer(),
                    studentTimetable.get_modulePointer()
            ));

        }

        // Add callbacks
        cv.setEventClickListener(new CalendarView.EventClickListener() {
            @Override
            public void onEventClick(int eventId) {
                openEventDialog(eventId, false);
            }

            @Override
            public void onEventLongClick(int eventId) {
//                Toast.makeText(TimetableActivity.this, "" + eventId, Toast.LENGTH_SHORT).show();
                loadTimetableModuleChooser(eventId);
            }
        });

        Calendar weekStart = Calendar.getInstance();
        weekStart.setTime(weekStartDate);
        cv.setweekStartDate(weekStart);
        cv.updateCalendar(events);
    }

    public void initCalendarView() {

        cv = ((CalendarView)findViewById(R.id.calendar_view));

        if (daysVisible == 5)
            navigationView.setCheckedItem(R.id.nav_five_day);
        else if (daysVisible == 3)
            navigationView.setCheckedItem(R.id.nav_three_day);
        else if (daysVisible == 1)
            navigationView.setCheckedItem(R.id.nav_one_day);
        cv.setVisibleDays(daysVisible);

        Calendar weekStart = Calendar.getInstance();
        weekStart.set(Calendar.DAY_OF_WEEK, weekStart.getFirstDayOfWeek());     // Default to start of current week
        cv.setweekStartDate(weekStart);

        weekStartDate = weekStart.getTime();

    }

    public void openCreateEventDialog(String eventType) {
        openEventDialog(0, true, eventType);
    }

    public void openEventDialog(int eventId, boolean editable) {
        openEventDialog(eventId, editable, null);
    }

    public void openEventDialog(int eventId, boolean editable, String eventType) {
        String tag = "fragment_view_event";
        if (editable) tag = "fragment_edit_event";

        // close existing dialog fragments
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(tag);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }

        Bundle args = new Bundle();
        args.putInt("eventId", eventId);

        args.putInt("studentId", userId);
        args.putInt("weekNumber", weekId);

        if (eventType != null)
            args.putString("eventType", eventType);

        EventDialogFragment eventDialogFragment;

        if (!editable) {
            eventDialogFragment = new EventViewDialogFragment();
        } else {
            eventDialogFragment = new EventEditDialogFragment();
        }

        eventDialogFragment.setArguments(args);
        eventDialogFragment.show(manager, tag);
    }

    @Override
    public void onCloseEventDialog(String action, StudentTimetable studentTimetable) {

        switch (action) {
            case EventDialogFragment.CANCEL_ACTION:
                break;

            case EventDialogFragment.EDIT_ACTION:
                openEventDialog(studentTimetable.get_idTablePointer(), true);
                loadTimetable();
                break;

            case EventDialogFragment.SAVE_ACTION:
                loadTimetable();
                updateReminderService();
                break;

            case EventDialogFragment.DELETE_ACTION:
                loadTimetable();
                // TODO
                break;

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
//        Toast.makeText(TimetableActivity.this, "saving data", Toast.LENGTH_SHORT).show();
    }

    public void loadPreferences() {
        SharedPreferences sharedPref = getSharedPreferences("ie.gavin.ulstudenttimetable.SHARED_PREFS_KEY", Context.MODE_PRIVATE);
        daysVisible = sharedPref.getInt("daysVisible", 3);
        userId = sharedPref.getInt("userId", 0);
    }

    public void savePreferences() {
        SharedPreferences sharedPref = getSharedPreferences("ie.gavin.ulstudenttimetable.SHARED_PREFS_KEY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("daysVisible", daysVisible);
        editor.putInt("userId", userId);
        editor.commit();
    }

    public void updateReminderService() {
        Intent myIntent = new Intent(this , EventReminderService.class);
        Log.v("updateReminderService", "ACTIVITY");
        myIntent.setAction("RELOAD");
        startService(myIntent);

    }
}

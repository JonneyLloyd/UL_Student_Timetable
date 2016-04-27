package ie.gavin.ulstudenttimetable;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ie.gavin.ulstudenttimetable.calendar.CalendarEvent;
import ie.gavin.ulstudenttimetable.calendar.CalendarView;
import ie.gavin.ulstudenttimetable.data.Module;
import ie.gavin.ulstudenttimetable.data.MyDBHandler;
import ie.gavin.ulstudenttimetable.data.StudentTimetable;
import ie.gavin.ulstudenttimetable.data.Week;

public class TimetableActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener, EventDialogFragment.closeEventDialogListener {

    private Spinner weekSpinner;
    private NavigationView navigationView;
    private RelativeLayout navHeader;
    private Menu navMenu;
    private CalendarView cv;
    ArrayList<CalendarEvent> events = new ArrayList<>();
    HashMap<Integer, String> users = new HashMap<>();

    // TODO load defaults from shared prefs
    private int userId;         // load primary user
    private int puid;           // stores previous id
    private int weekId;         // load the curent week
    private int daysVisible;    // load previous number of days visible

    private final int REQUEST_CODE_ADD_TIMETABLE = 100;
    MyDBHandler dbHandler;
    Module tempModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        loadNavigationUsers();
        loadActionbarWeeks();
        loadTimetable();

        if (userId == 0) {   // if no user set yet
            addUser();
        }

        com.github.clans.fab.FloatingActionButton fab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.menu_item_class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // TODO remove
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_now) {
            cv.focusCalendar();
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
            Toast.makeText(TimetableActivity.this, name + "," + id, Toast.LENGTH_SHORT).show();
            setNavigationViewUser(id, name);
            loadTimetable();
        } else if (id == R.id.users_add) {
            Toast.makeText(TimetableActivity.this, "Add user", Toast.LENGTH_SHORT).show();
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
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

        if(resultCode == REQUEST_CODE_ADD_TIMETABLE){
            String studentId = (String) data.getExtras().get("studentId");
            userId = Integer.parseInt(studentId);
            String studentName = (String) data.getExtras().getString("studentName", "New user");

            Toast.makeText(TimetableActivity.this, studentId + " " + studentName + " was added!", Toast.LENGTH_SHORT).show();

            navigationView.getMenu().add(R.id.nav_group_users, userId, Menu.NONE, studentName);
            setNavigationViewUser(userId, studentName);
            loadTimetable();
            loadActionbarWeeks();
        }

    }

    public void loadNavigationUsers() {
        // TODO populate from DB or shared prefs??
        String userName = "";
        navMenu = navigationView.getMenu();

        dbHandler = MyDBHandler.getInstance(getApplicationContext());
        users = dbHandler.getUsers();

        for (Map.Entry<Integer, String> user : users.entrySet()) {
            int id = user.getKey();
            String name = user.getValue();
            navMenu.add(R.id.nav_group_users, id, Menu.NONE, name);
            if (id == userId) userName = name;
        }

        navMenu.setGroupVisible(R.id.nav_group_users, false);

        if (userId != 0) {
            setNavigationViewUser(userId, userName);
        }
    }

    public void setNavigationViewUser(int userId, String userName) {
        this.userId = userId;
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

        // TODO load from DB
        List<Week> weekDetails =  MyDBHandler.getInstance(getApplicationContext()).getWeekDetails();
        // Spinner Drop down elements
//        List<String> categories = new ArrayList<String>();
//        categories.add("Week 1"); categories.add("Week 2"); categories.add("Week 3"); categories.add("Week 4"); categories.add("Week 5"); categories.add("Week 6"); categories.add("Week 7"); categories.add("Week 8"); categories.add("Easter"); categories.add("Week 9"); categories.add("Week 10"); categories.add("Week 11"); categories.add("Week 12"); categories.add("Study Week"); categories.add("Exam Week");

        // Creating adapter for spinner
        ArrayAdapter<Week> dataAdapter = new ArrayAdapter<Week>(this, R.layout.toolbar_spinner_item_actionbar, weekDetails);
        // Drop down layout style
        dataAdapter.setDropDownViewResource(R.layout.toolbar_spinner_item_dropdown);
        weekSpinner.setAdapter(dataAdapter);
        weekSpinner.setSelection(weekId);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        Week item = (Week) parent.getItemAtPosition(position);

        weekId = item.get_week();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item + ": " + item.get_weekStart() + " -> " + weekId, Toast.LENGTH_LONG).show();

        loadTimetable();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public String formatEvent(Object obj) {
        // TODO more + sortable
        if (obj instanceof StudentTimetable) {
            return ((StudentTimetable) obj).get_moduleCode()
                    + " " + ((StudentTimetable) obj).get_type()
                    + " " + ((StudentTimetable) obj).get_groupName()
                    + " " + ((StudentTimetable) obj).get_room();
        } else if (obj instanceof Module) {
            return ((Module) obj).get_type()
                    + " " + ((Module) obj).get_groupName()
                    + " " + ((Module) obj).get_room();
        } else {
            return obj.toString();
        }
    }

    public void loadTimetable() {   // TODO fix double load
        Toast.makeText(TimetableActivity.this, "loading: "+userId, Toast.LENGTH_SHORT).show();
        dbHandler = MyDBHandler.getInstance(this.getApplicationContext());
        ArrayList<StudentTimetable> StudentTimetables = dbHandler.getAllFromStudentTimetable(userId, weekId);

        events.clear();
        for (StudentTimetable studentTimetable : StudentTimetables) {

            String [] startTime = studentTimetable.get_start_time().split(":");
            String [] endTime = studentTimetable.get_endTime().split(":");
            int day = studentTimetable.get_day();
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            start.set(2016, 4-1, 18 + day - 1, Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1]));
            end.set(2016, 4 - 1, 18 + day - 1, Integer.parseInt(endTime[0]), Integer.parseInt(endTime[1]));

            events.add(new CalendarEvent(
                    start,
                    end,
                    formatEvent(studentTimetable),
                    studentTimetable.get_color(),
                    studentTimetable.get_idTablePointer()
            ));

        }

        cv = ((CalendarView)findViewById(R.id.calendar_view));
        // Add callbacks
        cv.setEventClickListener(new CalendarView.EventClickListener() {
            @Override
            public void onEventClick(int eventId) {
                openEventDialog(eventId, false);

            }

            @Override
            public void onEventLongClick(int eventId) {
                Toast.makeText(TimetableActivity.this, "" + eventId, Toast.LENGTH_SHORT).show();
            }
        });

        if (daysVisible == 5)
            navigationView.setCheckedItem(R.id.nav_five_day);
        else if (daysVisible == 3)
            navigationView.setCheckedItem(R.id.nav_three_day);
        else if (daysVisible == 1)
            navigationView.setCheckedItem(R.id.nav_one_day);
        cv.setVisibleDays(daysVisible);
        Calendar weekStart = Calendar.getInstance();
//        weekStart.set(2016, 4-1, 18);
        weekStart.set(Calendar.DAY_OF_WEEK, weekStart.getFirstDayOfWeek());     // TODO take week No. into account
        cv.setweekStartDate(weekStart);
        cv.updateCalendar(events);
    }

    public void openEventDialog(int eventId, boolean editable) {
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

        if (!editable) {
            EventViewDialogFragment eventViewDialog = new EventViewDialogFragment();
            eventViewDialog.setArguments(args);
            eventViewDialog.show(manager, tag);
        } else {
            EventEditDialogFragment eventEditDialog = new EventEditDialogFragment();
            eventEditDialog.setArguments(args);
            eventEditDialog.show(manager, tag);
        }
    }

    @Override
    public void onCloseEventDialog(String action, StudentTimetable studentTimetable) {

        switch (action) {
            case EventDialogFragment.CANCEL_ACTION:
                break;

            case EventDialogFragment.EDIT_ACTION:
                openEventDialog(studentTimetable.get_idTablePointer(), true);
                break;

            case EventDialogFragment.SAVE_ACTION:
                Toast.makeText(this, "s, " + studentTimetable.get_moduleCode(), Toast.LENGTH_SHORT).show();
                // TODO save to DB
                break;

            case EventDialogFragment.DELETE_ACTION:
                Toast.makeText(this, "d, " + studentTimetable.get_moduleCode(), Toast.LENGTH_SHORT).show();
                // TODO
                break;

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
        Toast.makeText(TimetableActivity.this, "saving data", Toast.LENGTH_SHORT).show();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        loadPreferences();
//    }

    public void loadPreferences() {
        SharedPreferences sharedPref = getSharedPreferences("ie.gavin.ulstudenttimetable.SHARED_PREFS_KEY", Context.MODE_PRIVATE);
//        int defaultValue = getResources().getInteger(R.string.saved_high_score_default);
        daysVisible = sharedPref.getInt("daysVisible", 3);
        userId = sharedPref.getInt("userId", 0);
        weekId = 10;    // TODO DB
    }

    public void savePreferences() {
        SharedPreferences sharedPref = getSharedPreferences("ie.gavin.ulstudenttimetable.SHARED_PREFS_KEY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("daysVisible", daysVisible);
        editor.putInt("userId", userId);
        editor.apply();
    }
}

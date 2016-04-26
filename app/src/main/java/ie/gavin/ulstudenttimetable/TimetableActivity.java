package ie.gavin.ulstudenttimetable;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
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
import java.util.List;

import ie.gavin.ulstudenttimetable.calendar.CalendarEvent;
import ie.gavin.ulstudenttimetable.calendar.CalendarView;
import ie.gavin.ulstudenttimetable.data.Module;
import ie.gavin.ulstudenttimetable.data.MyDBHandler;
import ie.gavin.ulstudenttimetable.data.StudentTimetable;

public class TimetableActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener, EventDialogFragment.closeEventDialogListener {

    private Spinner weekSpinner;
    private NavigationView navigationView;
    private RelativeLayout navHeader;
    private Menu navMenu;
    private CalendarView cv;
    ArrayList<CalendarEvent> events = new ArrayList<>();

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

        if (true) {   // if no user set yet
            Intent intent = new Intent(getApplicationContext(), AddStudentTimetableActivity.class);
            intent.putExtra("resultCode", REQUEST_CODE_ADD_TIMETABLE);
            startActivityForResult(intent, REQUEST_CODE_ADD_TIMETABLE);
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

        if (id == 14161044) {
            // TODO dynamically
            Toast.makeText(TimetableActivity.this, "Oliver", Toast.LENGTH_SHORT).show();
            setNavigationViewUser(14161044, "Oliver Gavin");
        } else if (id == 14161045) {
            Toast.makeText(TimetableActivity.this, "Jonney", Toast.LENGTH_SHORT).show();
            setNavigationViewUser(14161045, "Jonathan Lloyd");
        } else if (id == R.id.users_add) {
            Toast.makeText(TimetableActivity.this, "Add user", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_one_day) {
            cv.setVisibleDays(1);
            cv.updateCalendar();
        } else if (id == R.id.nav_three_day) {
            cv.setVisibleDays(3);
            cv.updateCalendar();
        } else if (id == R.id.nav_five_day) {
            cv.setVisibleDays(5);
            cv.updateCalendar();
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Function to read the result from newly created activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == REQUEST_CODE_ADD_TIMETABLE){
            String studentId = data.getExtras().get("studentId").toString();
            Toast.makeText(TimetableActivity.this, studentId, Toast.LENGTH_SHORT).show();



            //adding a module to DB for testing purposes

            /*
            tempModule = new Module(2, "CS4014", "10:30:00" , "11:30:00", "AB100", "Conor Ryan", 1, "2A", "Lab");
            dbHandler = new MyDBHandler(TimetableActivity.this, null, null, 1);
            dbHandler.addModule(tempModule);
            Toast.makeText(TimetableActivity.this, "CS4014 added", Toast.LENGTH_SHORT).show();
            */

            //testing module search & handle no result

            /*

            dbHandler = new MyDBHandler(TimetableActivity.this, null, null, 1);
            tempModule = dbHandler.getModuleFromID(101);
            if (tempModule != null) {
                Toast.makeText(TimetableActivity.this, tempModule.get_lecturer(), Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(TimetableActivity.this, "ID not found", Toast.LENGTH_SHORT).show();
            */

        }

    }

    public void loadNavigationUsers() {
        // TODO populate from DB or shared prefs??
        navMenu = navigationView.getMenu();

        navMenu.add(R.id.nav_group_users, 14161044, Menu.NONE, "Oliver Gavin");
        navMenu.add(R.id.nav_group_users, 14161045, Menu.NONE, "Jonathan Lloyd");
        navMenu.setGroupVisible(R.id.nav_group_users, false);

        setNavigationViewUser(14161044, "Oliver Gavin");
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
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Week 1"); categories.add("Week 2"); categories.add("Week 3"); categories.add("Week 4"); categories.add("Week 5"); categories.add("Week 6"); categories.add("Week 7"); categories.add("Week 8"); categories.add("Easter"); categories.add("Week 9"); categories.add("Week 10"); categories.add("Week 11"); categories.add("Week 12");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.toolbar_spinner_item_actionbar, categories);
        // Drop down layout style
        dataAdapter.setDropDownViewResource(R.layout.toolbar_spinner_item_dropdown);
        weekSpinner.setAdapter(dataAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
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

    public void loadTimetable() {
        // TODO load events from DB
        dbHandler = MyDBHandler.getInstance(this.getApplicationContext());
        ArrayList<StudentTimetable> StudentTimetables = dbHandler.getAllFromStudentTimetable();

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
                    "#888888",
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

        navigationView.setCheckedItem(R.id.nav_five_day);
        cv.setVisibleDays(5);
        Calendar weekStart = Calendar.getInstance();
        weekStart.set(2016, 4-1, 18);
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

    public void loadPreferences() {
        // TODO
        userId = 14161044;
        weekId = 11;
        daysVisible = 3;
    }
}

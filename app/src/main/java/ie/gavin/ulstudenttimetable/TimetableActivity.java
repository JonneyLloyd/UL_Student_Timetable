package ie.gavin.ulstudenttimetable;

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
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ie.gavin.ulstudenttimetable.calendar.CalendarEvent;
import ie.gavin.ulstudenttimetable.calendar.CalendarView;
import ie.gavin.ulstudenttimetable.data.Module;
import ie.gavin.ulstudenttimetable.data.MyDBHandler;

public class TimetableActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private CalendarView cv;

    private final int REQUEST_CODE_ADD_TIMETABLE = 100;
    MyDBHandler dbHandler;
    Module tempModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (true) {
            Intent intent = new Intent(getApplicationContext(), AddStudentTimetableActivity.class);
            intent.putExtra("resultCode", REQUEST_CODE_ADD_TIMETABLE);
            startActivityForResult(intent, REQUEST_CODE_ADD_TIMETABLE);
        }

        final ArrayList<CalendarEvent> events = new ArrayList<>();

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.set(2016, 3-1, 21, 15, 0);
        end.set(2016, 3-1, 21, 17, 0);
        events.add(new CalendarEvent(
                start,
                end,
                "Testing",
                "#222222",
                22
        ));

        Calendar astart = Calendar.getInstance();
        Calendar aend = Calendar.getInstance();
        astart.set(2016, 3 - 1, 23, 11, 0);
        aend.set(2016, 3 - 1, 23, 12, 0);
        events.add(new CalendarEvent(
                astart,
                aend,
                "Testing",
                "#ff2222",
                20
        ));

        cv = ((CalendarView)findViewById(R.id.calendar_view));
//        cv.setVisibleDays(5);
        Calendar weekStart = Calendar.getInstance();
        weekStart.set(2016, 3-1, 28);
        cv.setweekStartDate(weekStart);
        cv.updateCalendar(events);

        com.github.clans.fab.FloatingActionButton fab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.menu_item_class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // TODO remove
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.weekSpinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Week 1");
        categories.add("Week 2");
        categories.add("Week 3");
        categories.add("Week 4");
        categories.add("Week 5");
        categories.add("Week 6");
        categories.add("Week 7");
        categories.add("Week 8");
        categories.add("Easter");
        categories.add("Week 9");
        categories.add("Week 10");
        categories.add("Week 11");
        categories.add("Week 12");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.toolbar_spinner_item_actionbar, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.toolbar_spinner_item_dropdown);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
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

        if (id == R.id.nav_one_day) {
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
}

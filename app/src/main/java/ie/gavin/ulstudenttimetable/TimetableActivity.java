package ie.gavin.ulstudenttimetable;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class TimetableActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CalendarView cv;

    private final int REQUEST_CODE_ADD_TIMETABLE = 100;

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
        cv.setVisibleDays(5);
        Calendar weekStart = Calendar.getInstance();
        weekStart.set(2016, 3-1, 21);
        cv.setweekStartDate(weekStart);
        cv.updateCalendar(events);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // TODO remove
                cv.setVisibleDays(3);
                cv.updateCalendar();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

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
        }

    }
}

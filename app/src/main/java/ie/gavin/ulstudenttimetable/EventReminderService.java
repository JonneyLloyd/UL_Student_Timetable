package ie.gavin.ulstudenttimetable;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ie.gavin.ulstudenttimetable.data.MyDBHandler;
import ie.gavin.ulstudenttimetable.data.StudentTimetable;
import ie.gavin.ulstudenttimetable.data.Week;

public class EventReminderService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean BOOT;
    private boolean RELOAD;
    private int eventId;

    public EventReminderService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BOOT = false;
        RELOAD = false;
        if (intent != null) {
            if (intent.getAction() != null) {
                if (intent.getAction().equalsIgnoreCase("BOOT")) {
                    Log.v("servicetest", "BOOT");
                    BOOT = true;
                } else if (intent.getAction().equalsIgnoreCase("RELOAD")) {
                    Log.v("servicetest", "RELOAD");
                    RELOAD = true;
                }
            }

            if (intent.getExtras() != null) {
                eventId = intent.getExtras().getInt("eventId", 0);
            }
        }

        if (BOOT || RELOAD) {
            // Set up all alarms
                            Intent intent1 = new Intent(this, TimetableActivity.class);
                            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);

                            // build notification
                            Notification n = new Notification.Builder(this)
                                    .setContentTitle("Notification Service Started")
                                    .setContentText("processing")
                                    .setSmallIcon(R.drawable.ic_account_circle)
                                    .setContentIntent(pIntent)
                                    .setAutoCancel(true)
                                    .build();
                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            notificationManager.notify(0, n);

            int weekId = 0;
            Calendar weekStart = Calendar.getInstance();
            weekStart.set(Calendar.DAY_OF_WEEK, weekStart.getFirstDayOfWeek());     // Default to start of current week
            Date weekStartDate = weekStart.getTime();

            ArrayList<Week> weekDetails =  MyDBHandler.getInstance(getApplicationContext()).getWeekDetails();

            // Get the current weekId
            for (int i = weekDetails.size()-1; i >= 0; i--) {
                // Get the first date before the current week start date
                if (weekStartDate.compareTo(weekDetails.get(i).get_weekStart()) >= 0) {
                    weekId = weekDetails.get(i).get_week();
                    break;
                }
            }
            Log.v("weekID", ""+weekId);


            SharedPreferences sharedPref = getSharedPreferences("ie.gavin.ulstudenttimetable.SHARED_PREFS_KEY", Context.MODE_PRIVATE);
            int studentId = sharedPref.getInt("userId", 0);

            ArrayList<StudentTimetable> studentTimetables = MyDBHandler.getInstance(getApplicationContext()).getAllFromStudentTimetable(studentId, weekId);

            Log.v("servicetestsize", ""+studentTimetables.size());


            AlarmManager mgrAlarm = (AlarmManager) this.getSystemService(ALARM_SERVICE);

            for(StudentTimetable studentTimetable : studentTimetables) // Each event
            {
                int id = studentTimetable.get_idTablePointer();
                Intent eventIntent = new Intent(this, EventReminderService.class);
                eventIntent.putExtra("eventId", id);
                PendingIntent pendingIntent = PendingIntent.getService(this, id, eventIntent, 0);

                int hour = Integer.parseInt(studentTimetable.get_start_time().split(":")[0]);
                int minute = Integer.parseInt(studentTimetable.get_start_time().split(":")[1]);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.DAY_OF_WEEK, (studentTimetable.get_day()+1)%7);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Log.v("servicetest", dateFormat.format(calendar.getTime()));


                if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                    // event has already started
                    continue;
                }

                // Notify 30 minutes in advance
                calendar.add(Calendar.MINUTE, -30);

                mgrAlarm.set(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent);

            }

            if (BOOT) {
                // Check on Mondays for the next week of events
                Intent myIntent = new Intent(this, EventReminderService.class);
                myIntent.setAction("RELOAD");
                PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.HOUR_OF_DAY, 8);
                calendar.set(Calendar.DAY_OF_WEEK, 2);
                calendar.add(Calendar.WEEK_OF_YEAR, 1); // next week
                mgrAlarm.set(AlarmManager.RTC,
                        calendar.getTimeInMillis(),
                        pendingIntent);
            }

            // remove processing notification
            notificationManager.cancel(0);

        } else if (eventId != 0) {
            StudentTimetable studentTimetable = MyDBHandler.getInstance(getApplicationContext()).getStudentTimetableFromID(eventId);

            if (studentTimetable != null) {
                // Event still exists

                // Event notification
                Intent intent1 = new Intent(this, TimetableActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);

                // build notification
                Notification.Builder builder = new Notification.Builder(this);
                builder.setContentTitle(studentTimetable.get_start_time() + " - " + studentTimetable.get_endTime() + "   " + studentTimetable.get_type() + " " + studentTimetable.get_groupName())
                        .setContentText(studentTimetable.get_moduleCode() + " " + studentTimetable.get_title())
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setSmallIcon(R.drawable.ic_today)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    builder.setColor(studentTimetable.get_color());

                Notification n = builder.build();

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(eventId, n);
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.intent.action.BOOT_COMPLETED");
//        this.registerReceiver(new BootCompleted(), filter, "android.permission.RECEIVE_BOOT_COMPLETED", null);

    }

    public static class BootCompleted extends BroadcastReceiver {
        public BootCompleted() {
            super();
        }

        // called when boot completed
        @Override
        public void onReceive(Context context, Intent intent) {
            // check for boot complete event
            if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                //here we start the service
                Intent serviceIntent = new Intent(context, EventReminderService.class);
                serviceIntent.setAction("BOOT");
                context.startService(serviceIntent);
            }


        }

    }

}

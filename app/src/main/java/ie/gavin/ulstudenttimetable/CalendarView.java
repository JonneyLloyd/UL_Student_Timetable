package ie.gavin.ulstudenttimetable;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class CalendarView extends LinearLayout {

    private final String LOG_TAG = this.getClass().getSimpleName();

    LayoutInflater inflater;

    private float SCREEN_DENSITY = getContext().getResources().getDisplayMetrics().density;
    private int CALENDAR_MIN_HEIGHT;
    private int CALENDAR_MAX_HEIGHT;
    private int CALENDAR_PREF_HEIGHT;
    private int CALENDAR_HEIGHT;
    private int CALENDAR_TIME_WIDTH;
    private float CALENDAR_DAY_WIDTH;

    private LinearLayout calendarViewContainer;

    private ScrollView calendarHorizontalScrollView;

    private LinearLayout timeColumnLinearLayout;
    private ViewPager contentColumnViewPager;

    private static final int NUM_DAYS = 7;

    // SparseArray allows empty indexes
    private SparseArray<RelativeLayout> dayViews = new SparseArray<>();

    public CalendarView(Context context) {
        super(context);
        loadLayout(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadLayout(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadLayout(context, attrs);
    }

    public void setVisibleDays(int number) {
        CALENDAR_DAY_WIDTH = 1f/number;
    }

    private void loadLayout(Context context, AttributeSet attrs) {
        inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.calendar_view, this);

        calculateDisplaySettings(context);

        assignUiElements(context);
        assignClickHandlers();

        updateCalendar();
        drawUiElements();
    }


    private void assignUiElements(Context context) {
        // layout is inflated, assign local variables to components
        calendarViewContainer = (LinearLayout) findViewById(R.id.calendar_view_container);

        calendarHorizontalScrollView = (ScrollView) findViewById(R.id.calendarHorizontalScrollView);

        contentColumnViewPager = (ViewPager) findViewById(R.id.contentColumnViewPager);
        timeColumnLinearLayout = (LinearLayout) findViewById(R.id.timeColumnLinearLayout);

    }

    private int dpToPx(int dp) {
        return (int) (dp * SCREEN_DENSITY + 0.5f);
    }

    // converts the current time to a position relative to the top of the calendar based on the current height
    public int timeToScreenPosition(Calendar cal) {
        int minutes = cal.get(Calendar.HOUR_OF_DAY)*60 + cal.get(Calendar.MINUTE);
        float relativeTimePosition = minutes / (24 * 60f);
        int positionOffset = (int) (CALENDAR_HEIGHT * relativeTimePosition);
        return dpToPx(positionOffset);
    }

    private void calculateDisplaySettings(Context context) {

        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        CALENDAR_MIN_HEIGHT = (int) (outMetrics.heightPixels / SCREEN_DENSITY);
        CALENDAR_MAX_HEIGHT = (int) (CALENDAR_MIN_HEIGHT * 3f);

        CALENDAR_PREF_HEIGHT = 1000;    // TODO sahredpref

        CALENDAR_HEIGHT = Math.min(
                CALENDAR_MAX_HEIGHT,
                Math.max(CALENDAR_MIN_HEIGHT, CALENDAR_PREF_HEIGHT) // Don't allow below minimum
        );                                                          // Don't allow above maximum

        CALENDAR_TIME_WIDTH = 50;
        CALENDAR_DAY_WIDTH = 1/3f;      // TODO sahredpref

    }

    private void drawUiElements() {
        timeColumnLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(CALENDAR_TIME_WIDTH), dpToPx(CALENDAR_HEIGHT)));

        for (int i = 0; i <= 24; i++) {
            TextView timeView = (TextView) inflater.inflate(R.layout.calendar_time_view, timeColumnLinearLayout, false);

            if (i == 0 || i == 24) {
                timeView.setText("");
                timeView.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.5f));
            } else {
                if (i == 12) {
                    timeView.setText("noon");
                } else {
                    timeView.setText(i%12 + ((i < 12) ? " am" : " pm"));
                }
                timeView.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
            }
            timeColumnLinearLayout.addView(timeView);
        }
    }

    private void assignClickHandlers() {
//        // add one month and refresh UI
//        btnNext.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                currentDate.add(Calendar.MONTH, 1);
//                updateCalendar();
//            }
//        });
//
//        // subtract one month and refresh UI
//        btnPrev.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentDate.add(Calendar.MONTH, -1);
//                updateCalendar();
//            }
//        });
//
//        // long-pressing a day
//        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> view, View cell, int position, long id) {
//                // handle long-press
//                if (eventHandler == null)
//                    return false;
//
//                eventHandler.onDayLongPress((Date) view.getItemAtPosition(position));
//                return true;
//            }
//        });
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar()
    {
        updateCalendar(null);
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar(HashSet<Date> events)
    {
//        ArrayList<Date> cells = new ArrayList<>();
//        Calendar calendar = (Calendar)currentDate.clone();
//
//        // determine the cell for current month's beginning
//        calendar.set(Calendar.DAY_OF_MONTH, 1);
//        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;
//
//        // move calendar backwards to the beginning of the week
//        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);
//
//        // fill cells
//        while (cells.size() < DAYS_COUNT)
//        {
//            cells.add(calendar.getTime());
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//        }

        // update grid
//        grid.setAdapter(new CalendarAdapter(getContext(), cells, events));

        contentColumnViewPager.setAdapter(new CalendarPagerAdapter(getContext()));

        // Scrolls to the current day
        final Calendar cal = Calendar.getInstance();
        contentColumnViewPager.setCurrentItem(cal.DAY_OF_WEEK - 1);

        // Scrolls to the current time (with padding above)
        calendarHorizontalScrollView.post(new Runnable() {
            public void run() {
                int position = (int) (timeToScreenPosition(cal) - CALENDAR_MIN_HEIGHT * (1 / 3f));
                calendarHorizontalScrollView.scrollTo(0, position);
            }
        });

    }



    class CalendarPagerAdapter extends PagerAdapter {

        // days with events
        private HashSet<Date> eventDays;

        public CalendarPagerAdapter(Context context) {
//            super(context, R.layout.control_calendar_day, days);
//            this.eventDays = eventDays;
        }

        @Override
        public float getPageWidth(int position) {
            return CALENDAR_DAY_WIDTH;
        }

        @Override
        public int getCount() {
            return NUM_DAYS;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View page = null;
            Log.d(LOG_TAG, "instantiateItem: pre");
            if (dayViews.get(position) == null) {
                RelativeLayout dayView = (RelativeLayout) inflater.inflate(R.layout.calendar_day_view, container, false);
                // TODO use cursor adapter

                    LinearLayout eventView = (LinearLayout) inflater.inflate(R.layout.calendar_event_view, container, false);

                    String text = "";
                    int eventPosition = 0;
                    int eventEndPosition = 0;
                    int eventHeight = 0;
                    Calendar start = Calendar.getInstance();
                    Calendar end = Calendar.getInstance();
                    switch (position) {
                        case 0:
                            start.set(2016, 3, 18, 15, 0);
                            end.set(2016, 3, 18, 16, 0);
                            eventPosition = timeToScreenPosition(start);
                            eventEndPosition = timeToScreenPosition(end);
                            eventHeight = eventEndPosition - eventPosition;
                            break;
                        case 1:
                            start.set(2016, 3, 19, 14, 0);
                            end.set(2016, 3, 19, 16, 0);
                            eventPosition = timeToScreenPosition(start);
                            eventEndPosition = timeToScreenPosition(end);
                            eventHeight = eventEndPosition - eventPosition;
                            break;
                        default:
                            start.set(2016, 3, 20, 9, 0);
                            end.set(2016, 3, 20, 10, 0);
                            eventPosition = timeToScreenPosition(start);
                            eventEndPosition = timeToScreenPosition(end);
                            eventHeight = eventEndPosition - eventPosition;
                            break;
                    }
                    eventView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, eventHeight));

                    TextView eventViewText = (TextView) eventView.findViewById(R.id.eventTextView);
                    eventViewText.setText("hello");

                    dayView.addView(eventView);

                    MarginLayoutParams params = (MarginLayoutParams) eventView.getLayoutParams();
                    params.topMargin = eventPosition;

                    eventView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("test", "" + position);
                        }
                    });

                Log.d(LOG_TAG, "instantiateItem: views created " + position);

                if (position == Calendar.getInstance().DAY_OF_WEEK - 1) {
                    addTimeIndicator(dayView, position);
                }

                dayViews.put(position, dayView);
            }
            page = dayViews.get(position);

            ((ViewPager) container).addView(page, 0);
            Log.d(LOG_TAG, "instantiateItem: post");

            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object page) {
            ((ViewPager) container).removeView((View) page);
            dayViews.remove(position);
        }

        public void addTimeIndicator(ViewGroup container, int position) {
            View timeSlider = inflater.inflate(R.layout.calendar_time_indicator_view, container, false);
            ((RelativeLayout) container).addView(timeSlider);

            MarginLayoutParams params = (MarginLayoutParams) timeSlider.getLayoutParams();
            params.topMargin = timeToScreenPosition(Calendar.getInstance()) - dpToPx(1);
        }

    }

}

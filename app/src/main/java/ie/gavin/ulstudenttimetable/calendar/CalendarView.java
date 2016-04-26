package ie.gavin.ulstudenttimetable.calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ie.gavin.ulstudenttimetable.R;

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
    private int CALENDAR_DAYS_VISIBLE;

    private LinearLayout calendarViewContainer;

    private ViewPager headerColumnViewPager;

    private ScrollView calendarHorizontalScrollView;

    private LinearLayout timeColumnLinearLayout;
    private ViewPager contentColumnViewPager;

    private static final int NUM_DAYS = 7;

    private Calendar weekStartDate;

    // SparseArray allows empty indexes
    private SparseArray<View> dayViews = new SparseArray<>();
    private SparseArray<View> dayHeaderViews = new SparseArray<>();

    // events
    private ArrayList<CalendarEvent> events = new ArrayList<>();

    // listener on events - for callbacks
    public interface EventClickListener {
        public void onEventClick(int eventId);
        public void onEventLongClick(int eventId);
    }

    private EventClickListener eventClickListener;

    public void setEventClickListener(EventClickListener eventClickListener) {
        this.eventClickListener = eventClickListener;
    }


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
        CALENDAR_DAYS_VISIBLE = number;
        CALENDAR_DAY_WIDTH = 1f/number;
    }

    public void setweekStartDate(Calendar cal) {
        weekStartDate = cal;
    }

    private void loadLayout(Context context, AttributeSet attrs) {
        inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.calendar_view, this);

        calculateDisplaySettings(context);

        assignUiElements();
        assignClickHandlers();

        drawUiElements();
        updateCalendar();
    }


    private void assignUiElements() {
        // layout is inflated, assign local variables to components
        calendarViewContainer = (LinearLayout) findViewById(R.id.calendar_view_container);

        headerColumnViewPager = (ViewPager) findViewById(R.id.headerColumnViewPager);

        calendarHorizontalScrollView = (ScrollView) findViewById(R.id.calendarHorizontalScrollView);

        contentColumnViewPager = (ViewPager) findViewById(R.id.contentColumnViewPager);
        timeColumnLinearLayout = (LinearLayout) findViewById(R.id.timeColumnLinearLayout);

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
     * Redraw calendar with same data set
     */
    public void updateCalendar() {
        updateCalendar(events);
    }

    /**
     * Redraw calendar with different data set
     */
    public void updateCalendar(ArrayList<CalendarEvent> events) {
        this.events = events;

        headerColumnViewPager.setAdapter(new CalendarDatePagerAdapter(getContext()));
        contentColumnViewPager.setAdapter(new CalendarPagerAdapter(getContext()/*, events*/));

        // Synchronise both ViewPagers
        headerColumnViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                contentColumnViewPager.onTouchEvent(event);
                return false;
            }
        });
        contentColumnViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                headerColumnViewPager.onTouchEvent(event);
                return false;
            }
        });

        focusCalendar();
    }

    public void focusCalendar() {
        // Scrolls to the current day
        final Calendar cal = Calendar.getInstance();
        contentColumnViewPager.setCurrentItem(getDayOfWeekIndex(cal));
        headerColumnViewPager.setCurrentItem(getDayOfWeekIndex(cal));

        // Scrolls to the current time (with padding above)
        calendarHorizontalScrollView.post(new Runnable() {
            public void run() {
                int position = (int) (timeToScreenPosition(cal) - CALENDAR_MIN_HEIGHT * (1 / 3f));
                calendarHorizontalScrollView.scrollTo(0, position);
            }
        });
    }

    // returns index of a day from 0-6 where Monday is 0
    public int getDayOfWeekIndex(Calendar cal) {
        return (cal.get(Calendar.DAY_OF_WEEK) + 7 - 2) % 7;
    }

    // converts display pixels to pixels
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


    class CalendarDatePagerAdapter extends PagerAdapter {

        public CalendarDatePagerAdapter(Context context/*, ArrayList<CalendarEvent> events*/) {
//            super(context, R.layout.control_calendar_day, days);
//            super.events = events;
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
            if (dayHeaderViews.get(position) == null) {
                LinearLayout dayHeaderView = (LinearLayout) inflater.inflate(R.layout.calendar_day_header_view, container, false);

                Calendar weekDayDate = (Calendar) weekStartDate.clone();
                weekDayDate.add(Calendar.DAY_OF_YEAR, position);

                SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat(getContext().getString(R.string.wideDayOfWeekFormat));
                if (CALENDAR_DAYS_VISIBLE > 3) dayOfWeekFormat = new SimpleDateFormat(getContext().getString(R.string.narrowDayOfWeekFormat));

                SimpleDateFormat dateOfMonthFormat = new SimpleDateFormat(getContext().getString(R.string.dateOfMonthFormat) + (weekDayDate.get(Calendar.DAY_OF_MONTH) == 1? getContext().getString(R.string.monthOfYearFormat):""));

                String dayOfWeek = dayOfWeekFormat.format(weekDayDate.getTime());
                String dateOfMonth = dateOfMonthFormat.format(weekDayDate.getTime());

                TextView dayOfWeekViewText = (TextView) dayHeaderView.findViewById(R.id.headerDayOfWeekTextView);
                dayOfWeekViewText.setText(dayOfWeek);

                TextView dateOfMonthViewText = (TextView) dayHeaderView.findViewById(R.id.headerDateOfMonthTextView);
                dateOfMonthViewText.setText(dateOfMonth);

                Calendar today = Calendar.getInstance();

                if (today.get(Calendar.YEAR) == weekDayDate.get(Calendar.YEAR) &&
                        today.get(Calendar.DAY_OF_YEAR) == weekDayDate.get(Calendar.DAY_OF_YEAR)) {
                    dayOfWeekViewText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorHighlight));
                    dateOfMonthViewText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorHighlight));
                }
                else if (today.get(Calendar.YEAR) <= weekDayDate.get(Calendar.YEAR) &&
                        today.get(Calendar.DAY_OF_YEAR) < weekDayDate.get(Calendar.DAY_OF_YEAR)) {
                    dayOfWeekViewText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    dateOfMonthViewText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                }

                dayHeaderViews.put(position, dayHeaderView);
            }
            View page = dayHeaderViews.get(position);

            container.addView(page, 0);

            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object page) {
            container.removeView((View) page);
            dayHeaderViews.remove(position);
        }
    }

    class CalendarPagerAdapter extends PagerAdapter {

        public CalendarPagerAdapter(Context context/*, ArrayList<CalendarEvent> events*/) {
//            super(context, R.layout.control_calendar_day, days);
//            super.events = events;
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
            if (dayViews.get(position) == null) {
                RelativeLayout dayView = (RelativeLayout) inflater.inflate(R.layout.calendar_day_view, container, false);

                Calendar today = Calendar.getInstance();
                if (position == getDayOfWeekIndex(today)) {

                    Calendar weekDayDate = (Calendar) weekStartDate.clone();
                    weekDayDate.add(Calendar.DAY_OF_YEAR, position);

                    if (today.get(Calendar.YEAR) == weekDayDate.get(Calendar.YEAR) &&
                            today.get(Calendar.DAY_OF_YEAR) == weekDayDate.get(Calendar.DAY_OF_YEAR)) {

                        addTimeIndicator(dayView);

                        dayView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorContentBackgroundDark));
                    }
                }

                for(CalendarEvent event : events) {
                    if (getDayOfWeekIndex(event.getStartDateTime()) == position) {
                        CalendarEventView eventView = (CalendarEventView) inflater.inflate(R.layout.calendar_event_view, container, false);

                        int eventPosition = timeToScreenPosition(event.getStartDateTime());
                        int eventEndPosition = timeToScreenPosition(event.getEndDateTime());
                        int eventHeight = eventEndPosition - eventPosition;

                        eventView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, eventHeight));

                        GradientDrawable bgShape = (GradientDrawable)eventView.getBackground();
                        bgShape.setColor(Color.parseColor(event.getColor()));

                        TextView eventViewText = (TextView) eventView.findViewById(R.id.eventTextView);
                        eventViewText.setText(event.getTitle());

                        dayView.addView(eventView);

                        MarginLayoutParams params = (MarginLayoutParams) eventView.getLayoutParams();
                        params.topMargin = eventPosition;
                        params.leftMargin = (int) getResources().getDimension(R.dimen.event_margin_left);
                        params.rightMargin = (int) getResources().getDimension(R.dimen.event_margin_right);

                        eventView.setTag(event.getDatabaseId());
                        eventView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("test", "" + v.getTag());
                                eventClickListener.onEventClick((int) v.getTag());
                            }
                        });
                        eventView.setOnLongClickListener(new OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                eventClickListener.onEventLongClick((int) v.getTag());
                                return true;
                            }
                        });

                    }
                }

                dayViews.put(position, dayView);
            }
            View page = dayViews.get(position);

            container.addView(page, 0);

            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object page) {
            container.removeView((View) page);
            dayViews.remove(position);
        }

        public void addTimeIndicator(ViewGroup container) {
            View timeSlider = inflater.inflate(R.layout.calendar_time_indicator_view, container, false);
            container.addView(timeSlider);

            MarginLayoutParams params = (MarginLayoutParams) timeSlider.getLayoutParams();
            int timeIndicatorHeight =  (int) (getResources().getDimension(R.dimen.time_indicator_height));  // value in px
            params.topMargin = timeToScreenPosition(Calendar.getInstance()) - timeIndicatorHeight / 2;    // half the indicators height
        }

    }

}

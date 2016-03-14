package ie.gavin.ulstudenttimetable;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class CalendarView extends LinearLayout {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private LinearLayout calendarViewContainer;

    private LinearLayout timeColumnLinearLayout;
    private ViewPager contentColumnViewPager;

    private static final int NUM_DAYS = 7;

    private ArrayList<RelativeLayout> dayViews = new ArrayList<>();

    public CalendarView(Context context) {
        super(context);
        loadLayout(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        loadLayout(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        loadLayout(context, attrs);
    }

    private void loadLayout(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calendar_view, this);

        assignUiElements(context);
        assignClickHandlers();

        updateCalendar();
    }


    private void assignUiElements(Context context)
    {
        // layout is inflated, assign local variables to components
        calendarViewContainer = (LinearLayout) findViewById(R.id.calendar_view_container);
        contentColumnViewPager = (ViewPager) findViewById(R.id.contentColumnViewPager);
        timeColumnLinearLayout = (LinearLayout) findViewById(R.id.timeColumnLinearLayout);


        // TODO move from assignUiElements
        LayoutInflater inflater = LayoutInflater.from(context);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (50 * scale + 0.5f);

        for (int i = 0; i <= 24; i++) {
            TextView timeView = (TextView) inflater.inflate(R.layout.calendar_time_view, null);

            if (i == 0 || i == 24) {
                timeView.setText("");
                timeView.setHeight(pixels / 2);
            } else {
                if (i == 12) {
                    timeView.setText("noon");
                } else {
                    timeView.setText(i%12 + ((i < 12) ? " am" : " pm"));
                }
                timeView.setHeight(pixels);
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
//        contentColumnViewPager.setCurrentItem(3);
        contentColumnViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // update title
//        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
//        txtDate.setText(sdf.format(currentDate.getTime()));
    }
















    class CalendarPagerAdapter extends PagerAdapter {

        // days with events
        private HashSet<Date> eventDays;

        // for view inflation
        private LayoutInflater inflater;

        public CalendarPagerAdapter(Context context)
        {
//            super(context, R.layout.control_calendar_day, days);
//            this.eventDays = eventDays;
            inflater = LayoutInflater.from(context);
        }

        @Override public float getPageWidth(int position) {
            // TODO determine 1/3/5 day view
            return(0.45f);
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
        public Object instantiateItem(ViewGroup container, int position) {
            View page = null;
            Log.d(LOG_TAG, "instantiateItem: pre");
//            if (dayViews.get(position) == null) {
//            if (dayViews.size() > position && dayViews.get(position) == null) {
                RelativeLayout dayView = (RelativeLayout) inflater.inflate(R.layout.calendar_day_view, null);
                // TODO use cursor adapter
                LinearLayout eventView = (LinearLayout) inflater.inflate(R.layout.calendar_event_view, null);
                TextView eventViewText = (TextView) eventView.findViewById(R.id.eventTextView);
                eventViewText.setText("hello");
                dayView.addView(eventView);

                Log.d(LOG_TAG, "instantiateItem: views created " + position);


                // TODO move to update view
                View timeSlider = (View) dayView.findViewById(R.id.time_slider);
                MarginLayoutParams params = (MarginLayoutParams) timeSlider.getLayoutParams();
                params.topMargin = 200;

                if (position == 2) {
                    timeSlider.setBackgroundColor(Color.parseColor("#111199"));
                }

                dayViews.add(position, dayView);
//            }
            page = dayViews.get(position);

            ((ViewPager) container).addView(page, 0);
            Log.d(LOG_TAG, "instantiateItem: post");

            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object page) {
            ((ViewPager) container).removeView((View) page);
        }
    }

}

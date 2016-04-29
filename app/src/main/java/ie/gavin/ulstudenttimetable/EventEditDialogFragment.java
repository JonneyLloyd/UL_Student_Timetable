package ie.gavin.ulstudenttimetable;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.colorpicker.ColorPickerPalette;
import com.android.colorpicker.ColorPickerSwatch;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import ie.gavin.ulstudenttimetable.data.MyDBHandler;
import ie.gavin.ulstudenttimetable.data.StudentTimetable;
import ie.gavin.ulstudenttimetable.data.Week;

public class EventEditDialogFragment extends EventDialogFragment {

    Toolbar toolbar;

    private StudentTimetable studentTimetable;
    private ArrayList<Week> weekDetails = new ArrayList<>();
    private String[] weeks;
    private boolean[] seletedWeeks;
//    private EditText mEditText;

    private TextView dayEditTextView;
    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private TextView weekEditTextView;
//    private TextView moduleCodeTextView;
//    private TextView titleTextView;
//    private TextView typeTextView;
//    private TextView locationTextView;
//    private TextView weeksTextView;
//    private TextView notesTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        studentTimetable = super.getStudentTimetable();

        weekDetails = MyDBHandler.getInstance(getActivity()).getWeekDetails();
        weeks = new String[weekDetails.size()];
        for (int i = 0; i < weekDetails.size(); i++)
            weeks[i] = weekDetails.get(i).toString();

        seletedWeeks = new boolean[weekDetails.size()];
        for (Pair<Integer, Integer> weekPair : studentTimetable.get_weeks()) {
            for (int i = weekPair.first-1; i <= weekPair.second-1; i++)
                seletedWeeks[i] = true;
        }

        for (int i = 0; i < weekDetails.size(); i++)
            Log.v("bool", ""+seletedWeeks[i]);

        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final ColorPickerPalette colorPickerPalette = (ColorPickerPalette) layoutInflater
                .inflate(R.layout.color_picker, null);
        final int[] colors = getResources().getIntArray(R.array.pickerColors);
        colorPickerPalette.init(colors.length, 20, new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                colorPickerPalette.drawPalette(colors, color);
                toolbar.setBackgroundColor(color);
            }
        });
        colorPickerPalette.drawPalette(colors, colors[0]);

        HorizontalScrollView c = (HorizontalScrollView) view.findViewById(R.id.paletteScrollView);
        c.addView(colorPickerPalette);


//        int[] colors = getResources().getIntArray(R.array.myColors);
//        ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
//        colorPickerDialog.initialize(
//                R.string.color_picker_default_title, colors, colors[0], 4, colors.length);
//        colorPickerDialog.show(getFragmentManager(), "colorpicker");


        dayEditTextView = (TextView) view.findViewById(R.id.dayEditTextView);
        startTimeTextView = (TextView) view.findViewById(R.id.startTimeTextView);
        endTimeTextView = (TextView) view.findViewById(R.id.endTimeTextView);
        weekEditTextView = (TextView) view.findViewById(R.id.weekEditTextView);
//        moduleCodeTextView = (TextView) view.findViewById(R.id.moduleCodeTextView);
//        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
//        typeTextView = (TextView) view.findViewById(R.id.typeTextView);
//        locationTextView = (TextView) view.findViewById(R.id.locationTextView);
//        weeksTextView = (TextView) view.findViewById(R.id.weeksTextView);
//        notesTextView = (TextView) view.findViewById(R.id.notesTextView);


        dayEditTextView.setText(DateFormatSymbols.getInstance().getWeekdays()[studentTimetable.get_day() + 1]);
        startTimeTextView.setText(studentTimetable.get_start_time());
        endTimeTextView.setText(studentTimetable.get_endTime());
        weekEditTextView.setText("Weeks: " + studentTimetable.get_weeksFormattedList());
//        moduleCodeTextView.setText(studentTimetable.get_moduleCode());
//        titleTextView.setText(studentTimetable.get_title());
//        typeTextView.setText(studentTimetable.get_type());
//        locationTextView.setText(studentTimetable.get_room());
//
//        String weeks = "Weeks: ";
//        for (Pair<Integer, Integer> week : studentTimetable.get_weeks()) weeks += week.first + "-" + week.second + " ";
//        weeksTextView.setText(weeks);
//
//        notesTextView.setText(studentTimetable.get_notes());

//        TextView tv = (TextView) view.findViewById(R.id.text);
//        tv.setText("This is an instance of ActionBarDialog edit");

//        mEditText = (EditText) view.findViewById(R.id.username);

        startTimeTextView.setOnClickListener(new timeOnClickListener());
        endTimeTextView.setOnClickListener(new timeOnClickListener());
        weekEditTextView.setOnClickListener(new weekOnClickListener());

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(studentTimetable.get_color());
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                int id = item.getItemId();

                if (id == R.id.action_cancel) {
                    // Return to activity
                    closeEventDialogListener activity = (closeEventDialogListener) getActivity();
                    activity.onCloseEventDialog(CANCEL_ACTION, studentTimetable);
                    dismiss();
                    return true;
                } else if (id == R.id.action_save) {
                    // Return to activity
                    closeEventDialogListener activity = (closeEventDialogListener) getActivity();
                    activity.onCloseEventDialog(SAVE_ACTION, studentTimetable);
                    dismiss();
                    return true;
                }

                return true;
            }
        });
        toolbar.inflateMenu(R.menu.save_cancel);
        toolbar.setTitle(studentTimetable.get_moduleCode());

        return view;

    }

    private class weekOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {

            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Select weeks")
                    .setMultiChoiceItems(weeks, seletedWeeks, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                            if (isChecked) {
                                // If the user checked the item, add it to the selected items
//                                seletedWeeks[indexSelected] = true;
                            } else {
                                // Else, if the item is already in the array, remove it
//                                seletedWeeks[indexSelected] = false;
                            }
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //  Your code when user clicked on OK
                            //  You can write the code  to save the selected item here
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //  Your code when user clicked on Cancel
                        }
                    }).create();
            dialog.show();

        }
    }

    private class timeOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    ((TextView) v).setText( selectedHour + ":" + selectedMinute);
                }
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        }
    }

}
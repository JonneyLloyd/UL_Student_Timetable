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
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.colorpicker.ColorPickerPalette;
import com.android.colorpicker.ColorPickerSwatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

    private boolean creating = false;

    private Spinner daySpinner;
    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private TextView weekTextView;
    private TextView moduleCodeEditTextView;
    private TextView titleEditTextView;
    private Spinner typeSpinner;
    private TextView locationEditTextView;
    private TextView notesEditTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final int[] colors = getResources().getIntArray(R.array.pickerColors);

        studentTimetable = super.getStudentTimetable();
        if (studentTimetable == null) {
            // Create event
            creating = true;
            // default to current week/time
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 1);
            String start = sdf.format(calendar.getTime());
            int day = (calendar.get(Calendar.DAY_OF_WEEK) + 7 - 2)%7;
            calendar.add(Calendar.HOUR, 1);
            String end = sdf.format(calendar.getTime());

            studentTimetable = new StudentTimetable(0, null, 0, start, day, end, super.getStudentId(), "", "", super.getEventType(), "", "", "", colors[(int) (Math.random()*colors.length)]);
            boolean defaultWeeks [] = new boolean[super.getWeekNumber()];
            defaultWeeks[super.getWeekNumber()-1] = true;
            studentTimetable.set_weeks(defaultWeeks);
        }

        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);

        daySpinner = (Spinner) view.findViewById(R.id.daySpinner);
        startTimeTextView = (TextView) view.findViewById(R.id.startTimeTextView);
        endTimeTextView = (TextView) view.findViewById(R.id.endTimeTextView);
        weekTextView = (TextView) view.findViewById(R.id.weekTextView);
        moduleCodeEditTextView = (TextView) view.findViewById(R.id.moduleCodeEditTextView);
        titleEditTextView = (TextView) view.findViewById(R.id.titleEditTextView);
        typeSpinner = (Spinner) view.findViewById(R.id.typeSpinner);
        locationEditTextView = (TextView) view.findViewById(R.id.locationEditTextView);
        notesEditTextView = (TextView) view.findViewById(R.id.notesEditTextView);

        /* Get data for week picker (labels/values) */
        weekDetails = MyDBHandler.getInstance(getActivity()).getWeekDetails();
        weeks = new String[weekDetails.size()];
        for (int i = 0; i < weekDetails.size(); i++)
            weeks[i] = weekDetails.get(i).toString();

        seletedWeeks = new boolean[weekDetails.size()];
        for (Pair<Integer, Integer> weekPair : studentTimetable.get_weeks()) {
            for (int i = weekPair.first-1; i <= weekPair.second-1; i++)
                seletedWeeks[i] = true;
        }


        /* Create color picker */
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final ColorPickerPalette colorPickerPalette = (ColorPickerPalette) layoutInflater
                .inflate(R.layout.color_picker, null);
        colorPickerPalette.init(colors.length, 20, new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                colorPickerPalette.drawPalette(colors, color);
                toolbar.setBackgroundColor(color);
                studentTimetable.set_color(color);

            }
        });
        colorPickerPalette.drawPalette(colors, studentTimetable.get_color());
        // add the picker
        HorizontalScrollView c = (HorizontalScrollView) view.findViewById(R.id.paletteScrollView);
        c.addView(colorPickerPalette);

        /* Populate days spinner */
        final ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" });
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);
        daySpinner.setSelection(studentTimetable.get_day() - 1);
        Log.v("daytest", ""+studentTimetable.get_day());

        /* Populate type spinner */
        ArrayList<String> types = new ArrayList<String>(Arrays.asList(new String[]{"LEC", "LAB", "TUT", "Meeting", "Memo", "EXAM"}));
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setSelection(types.indexOf(studentTimetable.get_type()));

        /* Load time/week text values */
        startTimeTextView.setText(studentTimetable.get_start_time());
        endTimeTextView.setText(studentTimetable.get_endTime());
        weekTextView.setText("Weeks: " + studentTimetable.get_weeksFormattedList());
        Log.v("TESTING sTIMES", ":" + studentTimetable.get_start_time());
        Log.v("TESTING eTIMES", ":" +studentTimetable.get_endTime());



        /* Load text values */
        moduleCodeEditTextView.setText(studentTimetable.get_moduleCode());
        titleEditTextView.setText(studentTimetable.get_title());
        locationEditTextView.setText(studentTimetable.get_room());
        notesEditTextView.setText(studentTimetable.get_notes());

        /* Set up click listeners for pickers */
        startTimeTextView.setOnClickListener(new timeOnClickListener());
        endTimeTextView.setOnClickListener(new timeOnClickListener());
        weekTextView.setOnClickListener(new weekOnClickListener());

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
                    if (!creating)
                        if(!studentTimetable.get_moduleCode().equals(moduleCodeEditTextView.getText().toString()) || !studentTimetable.get_start_time().equals(startTimeTextView.getText().toString())
                                || !studentTimetable.get_endTime().equals(endTimeTextView.toString()) || studentTimetable.get_day() != daySpinner.getSelectedItemPosition()+1
                                || !studentTimetable.get_type().equals(typeSpinner.toString()) || !studentTimetable.get_title().equals(titleEditTextView.getText().toString())
                                || !studentTimetable.get_room().equals(locationEditTextView.getText().toString())) {

                            studentTimetable.set_modulePointer(0);
                        }


                    studentTimetable.set_start_time(startTimeTextView.getText().toString());
                    studentTimetable.set_endTime(endTimeTextView.getText().toString());
                    studentTimetable.set_day(daySpinner.getSelectedItemPosition()+1);
                    studentTimetable.set_moduleCode(moduleCodeEditTextView.getText().toString());
                    studentTimetable.set_title(titleEditTextView.getText().toString());
                    studentTimetable.set_type(typeSpinner.getSelectedItem().toString());
                    //lecturer
                    studentTimetable.set_room(locationEditTextView.getText().toString());
                    studentTimetable.set_notes(notesEditTextView.getText().toString());

//                    Log.v("Testing edit: ", "" + moduleCodeEditTextView.getText().toString());
//                    Log.v("Testing edit: ", "" + startTimeTextView.getText().toString());
//                    Log.v("Testing edit: ", "" + endTimeTextView.getText().toString());
//                    Log.v("Testing edit: ", "" + typeSpinner.toString());
                    MyDBHandler dbHandler;
                    dbHandler = MyDBHandler.getInstance(getActivity());
                    dbHandler.updateOrAddStudentTimetable(studentTimetable);

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

            final boolean [] seletedWeeksBackup = seletedWeeks.clone();

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
                            studentTimetable.set_weeks(seletedWeeks);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //  Your code when user clicked on Cancel
                            seletedWeeks = seletedWeeksBackup.clone();
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

            String time = ((TextView) v).getText().toString();
            if (!time.equals("")) {
                String [] timeParts = time.split(":");
                hour = Integer.parseInt(timeParts[0]);
                minute = Integer.parseInt(timeParts[1]);
//                Log.v("TESTING hTIMES", ":" +hour);
//                Log.v("TESTING mTIMES", ":" +minute);
            }

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
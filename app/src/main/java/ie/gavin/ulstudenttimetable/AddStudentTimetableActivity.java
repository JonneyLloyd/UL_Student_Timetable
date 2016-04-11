package ie.gavin.ulstudenttimetable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import ie.gavin.ulstudenttimetable.data.Constants;
import ie.gavin.ulstudenttimetable.data.GetStudentData;

public class AddStudentTimetableActivity extends AppCompatActivity {

    int resultCode;
    String studentId;
    private Snackbar snackbar;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstudenttimetable);

        mProgress = (ProgressBar) findViewById(R.id.progress_bar);

        // Receiving the Data
        resultCode = getIntent().getIntExtra("resultCode", 0);

        // Filter's action is BROADCAST_ACTION
        IntentFilter mStatusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
        // Instantiates a new receiver
        final BroadcastReceiver mResponseReveiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mProgress.setIndeterminate(false);
                String message = intent.getStringExtra(Constants.EXTENDED_DATA_STATUS);
                snackbar = Snackbar.make(findViewById(R.id.addStudentContainer), message, Snackbar.LENGTH_LONG);
                snackbar.setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                                // TODO
                            }
                        })
                        .show();

                if (message.equals("Success")) {

                    LocalBroadcastManager.getInstance(AddStudentTimetableActivity.this).unregisterReceiver(this);

                    Intent i = new Intent();
                    // Sending key 'studentId' and value
                    i.putExtra("studentId", studentId);

                    // Setting resultCode to 100 to identify on old activity
                    AddStudentTimetableActivity.this.setResult(resultCode, i);
                    AddStudentTimetableActivity.this.finish();
                }
            }
        };
        // Registers the ResponseReceiver and its Intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(mResponseReveiver, mStatusIntentFilter);

        final EditText etStudentId = (EditText) findViewById(R.id.etStudentId);
        Button bLogin = (Button) findViewById(R.id.bLogin);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentId = etStudentId.getText().toString();

                mProgress.setIndeterminate(true);
                new Thread(new Runnable() {
                    // Handles processing (AsyncTasks download)
                    @Override
                    public void run() {
                        GetStudentData studentData = new GetStudentData(studentId, AddStudentTimetableActivity.this);
                        studentData.execute();
                    }
                }).start();

            }
        });

    }
}

package ie.gavin.ulstudenttimetable;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddStudentTimetableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstudenttimetable);

        // Receiving the Data
        final int resultCode = getIntent().getIntExtra("resultCode", 0);

        final EditText etStudentId = (EditText) findViewById(R.id.etStudentId);
        Button bLogin = (Button) findViewById(R.id.bLogin);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = etStudentId.getText().toString();

                try {
                    GetStudentData studentData = new GetStudentData(id);
                    studentData.execute();
                } catch (StudentTimetableException e) {
                    Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }

                Intent i = new Intent();
                // Sending param key as 'studentId' and value as 'androidhive.info'
                i.putExtra("studentId", id);

                // Setting resultCode to 100 to identify on old activity
                setResult(resultCode, i);
                finish();

            }
        });

    }
}

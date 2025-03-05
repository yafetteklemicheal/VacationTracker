package com.example.vacationtracker.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vacationtracker.Database.VacationDatabaseBuilder;
import com.example.vacationtracker.Entities.Excursion;
import com.example.vacationtracker.R;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExcursionDetails extends AppCompatActivity {
    private EditText etExcursionName, etExcursionStart;
    private ExecutorService executorService;
    private int vacationID;
    private int excursionID;
    private String vacationStartDate;
    private String vacationEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_excursion_details);

        etExcursionName = findViewById(R.id.etExcursionName);
        etExcursionStart = findViewById(R.id.etExcursionStart);
        Button btnExcursionSumbit = findViewById(R.id.btnExcursionSumbit);
        Button btnGoBackToVacation = findViewById(R.id.btnGoBackToVacation);

        vacationID = getIntent().getIntExtra("vacationId", -1);
        excursionID = getIntent().getIntExtra("excursionId", -1);

        vacationStartDate = getIntent().getStringExtra("vacationStartDate");
        vacationEndDate = getIntent().getStringExtra("vacationEndDate");

        TextView headerTitle = findViewById(R.id.headerTitle);
        headerTitle.setText("Excursion Details");

        etExcursionName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        etExcursionStart.setFocusable(false);
        etExcursionStart.setClickable(true);

        etExcursionStart.setOnClickListener(view -> showDatePickerDialog(etExcursionStart));

        executorService = Executors.newSingleThreadExecutor();

        if (excursionID != -1) {
            loadThisExcursion(excursionID);
        }

        btnExcursionSumbit.setOnClickListener(view -> {
            String excursionName = etExcursionName.getText().toString();
            String excursionDate = etExcursionStart.getText().toString();
            String vacationStart = vacationStartDate;
            String vacationEnd = vacationEndDate;

            if (excursionName.isEmpty() || excursionDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all excursion fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (vacationID == -1) {
                Toast.makeText(this, "Please create a vacation before adding an excursion", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] startDateParts = vacationStart.split("/");
            String[] endDateParts = vacationEnd.split("/");
            String[] excursionDateParts = excursionDate.split("/");

            int vacationStartMonth = Integer.parseInt(startDateParts[0]);
            int vacationStartDay = Integer.parseInt(startDateParts[1]);
            int vacationStartYear = Integer.parseInt(startDateParts[2]);

            int vacationEndMonth = Integer.parseInt(endDateParts[0]);
            int vacationEndDay = Integer.parseInt(endDateParts[1]);
            int vacationEndYear = Integer.parseInt(endDateParts[2]);

            int excursionStartMonth = Integer.parseInt(excursionDateParts[0]);
            int excursionStartDay = Integer.parseInt(excursionDateParts[1]);
            int excursionStartYear = Integer.parseInt(excursionDateParts[2]);

            if ((vacationStartYear > excursionStartYear || excursionStartYear > vacationEndYear) ||
                    (vacationStartYear == excursionStartYear && vacationStartMonth > excursionStartMonth) ||
                    (excursionStartYear == vacationEndYear && excursionStartMonth > vacationEndMonth) ||
                    (vacationStartYear == excursionStartYear && vacationStartMonth == excursionStartMonth && vacationStartDay > excursionStartDay) ||
                    (excursionStartYear == vacationEndYear && excursionStartMonth == vacationEndMonth && excursionStartDay > vacationEndDay)) {
                Toast.makeText(this, "Excursion date has to be during the vacation dates", Toast.LENGTH_SHORT).show();
                return;
            }

            Excursion excursion = new Excursion(excursionName, excursionDate, vacationID);
            if (excursionID != -1) {
                excursion.setExcursionID(excursionID);
                updateExcursion(excursion);
                Intent backToVacationDetailsIntent = new Intent(ExcursionDetails.this, VacationDetails.class);
                backToVacationDetailsIntent.putExtra("vacationId", vacationID);
                startActivity(backToVacationDetailsIntent);
                Toast.makeText(this, "Excursion updated", Toast.LENGTH_SHORT).show();
            } else {
                saveExcursion(excursion);
                Toast.makeText(this, "Excursion saved", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoBackToVacation.setOnClickListener(view -> {
            Intent backToVacationDetailsIntent = new Intent(ExcursionDetails.this, VacationDetails.class);
            backToVacationDetailsIntent.putExtra("vacationId", vacationID);
            startActivity(backToVacationDetailsIntent);
        });
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> editText.setText((month1 + 1) + "/" + dayOfMonth + "/" + year1),
                year, month, day
        );
        datePickerDialog.show();
    }

    private void saveExcursion(Excursion excursion) {
        executorService.execute(() -> {
            VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(ExcursionDetails.this);
            db.excursionDAO().insert(excursion);
            runOnUiThread(() -> {
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    private void updateExcursion(Excursion excursion) {
        executorService.execute(() -> {
            VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(ExcursionDetails.this);
            db.excursionDAO().update(excursion);
            runOnUiThread(() -> {
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    private void loadThisExcursion(int excursionID) {
        executorService.execute(() -> {
            VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(ExcursionDetails.this);
            Excursion excursion = db.excursionDAO().getExcursionByID(excursionID);
            runOnUiThread(() -> {
                if (excursion != null) {
                    etExcursionName.setText(excursion.getExcursionName());
                    etExcursionStart.setText(excursion.getExcursionDate());
                    vacationID = excursion.getVacationID();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
package com.example.vacationtracker.Activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacationtracker.Database.VacationDatabaseBuilder;
import com.example.vacationtracker.Entities.Excursion;
import com.example.vacationtracker.Entities.Vacation;
import com.example.vacationtracker.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VacationDetails extends AppCompatActivity {
    private static final int Add_Excursion_Code = 2;
    private EditText etVacationName, etVacationHotel, etVacationStart, etVacationEnd;
    private RecyclerView recyclerViewExcursions;
    private ExcursionAdapter excursionAdapter;
    private List<Excursion> excursionList;
    private ExecutorService executorService;
    private int vacationID;

    private final ActivityResultLauncher<Intent> addExcursionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadAllExcursions(vacationID);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);

        etVacationName = findViewById(R.id.etVacationName);
        etVacationHotel = findViewById(R.id.etVacationHotel);
        etVacationStart = findViewById(R.id.etVacationStart);
        etVacationEnd = findViewById(R.id.etVacationEnd);
        recyclerViewExcursions = findViewById(R.id.recyclerViewExcursions);
        Button btnVacationSumbit = findViewById(R.id.btnVacationSumbit);
        Button btnGoBacktoVacationList = findViewById(R.id.btnGoBacktoVacationList);
        FloatingActionButton fabAddNewExcursion = findViewById(R.id.fabAddNewExcursion);

        vacationID = getIntent().getIntExtra("vacationId", -1);

        TextView headerTitle = findViewById(R.id.headerTitle);
        headerTitle.setText("Vacation Details and Excursion List");

        etVacationName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        etVacationHotel.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        etVacationStart.setFocusable(false);
        etVacationStart.setClickable(true);
        etVacationEnd.setFocusable(false);
        etVacationEnd.setClickable(true);

        etVacationStart.setOnClickListener(view -> showDatePickerDialog(etVacationStart));
        etVacationEnd.setOnClickListener(view -> showDatePickerDialog(etVacationEnd));

        excursionList = new ArrayList<>();
        excursionAdapter = new ExcursionAdapter(excursionList, this);
        recyclerViewExcursions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewExcursions.setAdapter(excursionAdapter);

        executorService = Executors.newSingleThreadExecutor();

        if (vacationID != -1) {
            loadThisVacation(vacationID);
        }

        btnVacationSumbit.setOnClickListener(view -> {
            String vacationName = etVacationName.getText().toString();
            String hotel = etVacationHotel.getText().toString();
            String startDate = etVacationStart.getText().toString();
            String endDate = etVacationEnd.getText().toString();

            if (vacationName.isEmpty() || hotel.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all vacation fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] startDateParts = startDate.split("/");
            String[] endDateParts = endDate.split("/");

            int startMonth = Integer.parseInt(startDateParts[0]);
            int startDay = Integer.parseInt(startDateParts[1]);
            int startYear = Integer.parseInt(startDateParts[2]);

            int endMonth = Integer.parseInt(endDateParts[0]);
            int endDay = Integer.parseInt(endDateParts[1]);
            int endYear = Integer.parseInt(endDateParts[2]);

            if ((startYear > endYear) || (startYear == endYear && startMonth > endMonth) ||
                    (startYear == endYear && startMonth == endMonth && startDay > endDay)) {
                Toast.makeText(this, "Start date cannot be after end date", Toast.LENGTH_SHORT).show();
                return;
            }

            for (Excursion excursion : excursionList) {

                String[] excursionDateParts = excursion.getExcursionDate().split("/");
                int excursionStartMonth = Integer.parseInt(excursionDateParts[0]);
                int excursionStartDay = Integer.parseInt(excursionDateParts[1]);
                int excursionStartYear = Integer.parseInt(excursionDateParts[2]);

                if ((startYear > excursionStartYear || excursionStartYear > endYear) ||
                        (startYear == excursionStartYear && startMonth > excursionStartMonth) ||
                        (excursionStartYear == endYear && excursionStartMonth > endMonth) ||
                        (startYear == excursionStartYear && startMonth == excursionStartMonth && startDay > excursionStartDay) ||
                        (excursionStartYear == endYear && excursionStartMonth == endMonth && excursionStartDay > endDay)) {
                    Toast.makeText(this, "All excursion dates have to be during the vacation dates", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Vacation vacation = new Vacation(vacationName, hotel, startDate, endDate);
            if (vacationID != -1) {
                vacation.setVacationID(vacationID);
                updateVacation(vacation);
                Intent backToVacationListIntent = new Intent(VacationDetails.this, VacationList.class);
                startActivity(backToVacationListIntent);
                Toast.makeText(this, "Vacation updated", Toast.LENGTH_SHORT).show();
            } else {
                saveVacation(vacation);
                Toast.makeText(this, "Vacation saved", Toast.LENGTH_SHORT).show();
            }
        });

        fabAddNewExcursion.setOnClickListener(view -> {
            Intent addNewVacationIntent = new Intent(VacationDetails.this, ExcursionDetails.class);
            addNewVacationIntent.putExtra("vacationId", vacationID);
            addNewVacationIntent.putExtra("vacationStartDate", etVacationStart.getText().toString());
            addNewVacationIntent.putExtra("vacationEndDate", etVacationEnd.getText().toString());
            addExcursionLauncher.launch(addNewVacationIntent);
        });

        btnGoBacktoVacationList.setOnClickListener(view -> {
            Intent backToVacationListIntent = new Intent(VacationDetails.this, VacationList.class);
            startActivity(backToVacationListIntent);
        });

        if (vacationID != -1) {
            loadThisVacation(vacationID);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Add_Excursion_Code && resultCode == RESULT_OK) {
            loadAllExcursions(vacationID);
        }
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

    private void saveVacation(Vacation vacation) {
        executorService.execute(() -> {
            VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(VacationDetails.this);
            db.vacationDAO().insert(vacation);
            runOnUiThread(() -> {
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    private void updateVacation(Vacation vacation) {
        executorService.execute(() -> {
            VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(VacationDetails.this);
            db.vacationDAO().update(vacation);
            runOnUiThread(() -> {
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    public void loadThisVacation(int vacationID) {
        executorService.execute(() -> {
            VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(VacationDetails.this);
            Vacation vacation = db.vacationDAO().getVacationByID(vacationID);
            List<Excursion> excursions = db.excursionDAO().getExcursionsByVacationID(vacationID);
            runOnUiThread(() -> {
                if (vacation != null) {
                    etVacationName.setText(vacation.getVacationName());
                    etVacationHotel.setText(vacation.getHotel());
                    etVacationStart.setText(vacation.getStartDate());
                    etVacationEnd.setText(vacation.getEndDate());
                }
                if (excursions != null) {
                    excursionList.clear();
                    excursionList.addAll(excursions);
                    excursionAdapter.updateExcursions(excursionList);
                }
            });
        });
    }

    private void loadAllExcursions(int vacationID) {
        executorService.execute(() -> {
            VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(VacationDetails.this);
            List<Excursion> excursions = db.excursionDAO().getExcursionsByVacationID(vacationID);
            runOnUiThread(() -> {
                if (excursions != null) {
                    excursionList.clear();
                    excursionList.addAll(excursions);
                    excursionAdapter.updateExcursions(excursionList);
                }
            });
        });
    }

    public void deleteExcursion(Excursion excursion) {
        executorService.execute(() -> {
            VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(VacationDetails.this);
            db.excursionDAO().delete(excursion);
            runOnUiThread(() -> {
                excursionList.remove(excursion);
                excursionAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Excursion deleted", Toast.LENGTH_SHORT).show();
            });
        });
    }

    public void setAlertForExcursion(String date, String message, int requestCode) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        try {
            Date alertDate = dateFormat.parse(date);

            if (alertDate != null) {
                Calendar alertCalendar = Calendar.getInstance();
                alertCalendar.setTime(alertDate);

                Calendar now = Calendar.getInstance();

                long trigger = alertDate.getTime();

                if (alertCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        alertCalendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
                    alertCalendar = Calendar.getInstance();
                    trigger = alertCalendar.getTimeInMillis();
                } else if (alertCalendar.after(now)) {
                    alertCalendar.set(Calendar.HOUR_OF_DAY, 8);
                    alertCalendar.set(Calendar.MINUTE, 0);
                    alertCalendar.set(Calendar.SECOND, 0);
                    trigger = alertCalendar.getTimeInMillis();
                } else {
                    return;
                }

                Intent intent = new Intent(this, MyReceiver.class);
                intent.putExtra("ExcursionKey", message);

                PendingIntent sender = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getVacationStartDate() {
        return etVacationStart.getText().toString();
    }

    public String getVacationEndDate() {
        return etVacationEnd.getText().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
package com.example.vacationtracker.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VacationList extends AppCompatActivity {
    private RecyclerView recyclerViewVacations;
    private VacationAdapter vacationAdapter;
    private List<Vacation> vacationList;
    private ExecutorService executorService;

    private final ActivityResultLauncher<Intent> addVacationLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadAllVacations();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_list);

        TextView headerTitle = findViewById(R.id.headerTitle);
        headerTitle.setText("Vacation List");

        recyclerViewVacations = findViewById(R.id.recyclerViewExcursions);
        recyclerViewVacations.setLayoutManager(new LinearLayoutManager(this));

        executorService = Executors.newSingleThreadExecutor();
        loadAllVacations();

        FloatingActionButton fabAddNewVacation = findViewById(R.id.fabAddNewVacation);
        fabAddNewVacation.setOnClickListener(view -> {
            Intent addNewVacationIntent = new Intent(VacationList.this, VacationDetails.class);
            addVacationLauncher.launch(addNewVacationIntent);
        });

        Button btnGoBacktoMain = findViewById(R.id.btnGoBacktoMain);
        btnGoBacktoMain.setOnClickListener(view -> {
            Intent goBacktoMainIntent = new Intent(VacationList.this, MainActivity.class);
            addVacationLauncher.launch(goBacktoMainIntent);
        });
    }

    private void loadAllVacations() {
        executorService.execute(() -> {
            VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(VacationList.this);
            List<Vacation> vacations = db.vacationDAO().getAllVacations();
            runOnUiThread(() -> {
                vacationList = vacations;
                if (vacationAdapter == null) {
                    vacationAdapter = new VacationAdapter(vacationList, this);
                    recyclerViewVacations.setAdapter(vacationAdapter);
                } else {
                    vacationAdapter.updateVacations(vacationList);
                }
            });
        });
    }

    public void deleteVacation(Vacation vacation) {
        executorService.execute(() -> {
            VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(VacationList.this);
            List<Excursion> excursions = db.excursionDAO().getAllExcursions();
            boolean canDelete = true;
            for (Excursion excursion : excursions) {
                if (excursion.getVacationID() == vacation.getVacationID()) {
                    canDelete = false;
                    break;
                }
            }
            if (canDelete) {
                db.vacationDAO().delete(vacation);
                runOnUiThread(() -> {
                    Toast.makeText(VacationList.this, "Vacation deleted", Toast.LENGTH_SHORT).show();
                    loadAllVacations();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(VacationList.this, "Cannot delete a vacation with excursions", Toast.LENGTH_SHORT).show());
            }
        });
    }

    public void setAlertForVacation(String date, String message, int requestCode) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        try {
            Date alertDate = dateFormat.parse(date);

            if (alertDate != null) {
                Calendar alertCalendar = Calendar.getInstance();
                alertCalendar.setTime(alertDate);

                Calendar now = Calendar.getInstance();
                long trigger;

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
                intent.putExtra("VacationKey", message);

                PendingIntent sender = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void shareVacationDetails(Vacation vacation) {
        executorService.execute(() -> {
            VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(VacationList.this);
            List<Excursion> excursions = db.excursionDAO().getExcursionsByVacationID(vacation.getVacationID());
            runOnUiThread(() -> {
                String vacationDetails = generateVacationDetails(vacation, excursions);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, vacationDetails);

                Intent chooser = Intent.createChooser(shareIntent, "Share Vacation Details");
                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
            });
        });
    }

    private String generateVacationDetails(Vacation vacation, List<Excursion> excursions) {
        StringBuilder details = new StringBuilder();
        details.append("Vacation Name: ").append(vacation.getVacationName()).append("\n")
                .append("Vacation Hotel: ").append(vacation.getHotel()).append("\n")
                .append("Start Date: ").append(vacation.getStartDate()).append("\n")
                .append("End Date: ").append(vacation.getEndDate()).append("\n\n");

        if (excursions != null && !excursions.isEmpty()) {
            details.append("Excursions:\n");
            for (Excursion excursion : excursions) {
                details.append(" - ").append(excursion.getExcursionName())
                        .append(" on ").append(excursion.getExcursionDate()).append("\n");
            }
        } else {
            details.append("No excursions planned.");
        }

        return details.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
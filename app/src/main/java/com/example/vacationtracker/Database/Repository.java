package com.example.vacationtracker.Database;

import android.app.Application;

import com.example.vacationtracker.DAO.ExcursionDAO;
import com.example.vacationtracker.DAO.VacationDAO;
import com.example.vacationtracker.Entities.Excursion;
import com.example.vacationtracker.Entities.Vacation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    private final ExcursionDAO myExcursionDAO;
    private final VacationDAO myVacationDAO;
    private List<Excursion> allExcursion;
    private List<Vacation> allVacation;

    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application){
        VacationDatabaseBuilder vacationDatabaseBuilder = VacationDatabaseBuilder.getDatabase(application);
        myExcursionDAO = vacationDatabaseBuilder.excursionDAO();
        myVacationDAO = vacationDatabaseBuilder.vacationDAO();
    }

    public List<Excursion> getAllExcursion() {
        databaseExecutor.execute(() -> {
            allExcursion = myExcursionDAO.getAllExcursions();
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allExcursion;
    }

    public void insert(Excursion excursion) {
        databaseExecutor.execute(() -> {
            myExcursionDAO.insert(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(Excursion excursion) {
        databaseExecutor.execute(() -> {
            myExcursionDAO.update(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delete(Excursion excursion) {
        databaseExecutor.execute(() -> {
            myExcursionDAO.delete(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Vacation> getAllVacation() {
        databaseExecutor.execute(() -> {
            allVacation = myVacationDAO.getAllVacations();
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allVacation;
    }

    public void insert(Vacation vacation) {
        databaseExecutor.execute(() -> {
            myVacationDAO.insert(vacation);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(Vacation vacation) {
        databaseExecutor.execute(() -> {
            myVacationDAO.update(vacation);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delete(Vacation vacation) {
        databaseExecutor.execute(() -> {
            myVacationDAO.delete(vacation);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
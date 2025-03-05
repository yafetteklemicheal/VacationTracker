package com.example.vacationtracker.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.vacationtracker.Entities.Vacation;

import java.util.List;

@Dao
public interface VacationDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Vacation vacation);

    @Update
    void update(Vacation vacation);

    @Delete
    void delete(Vacation vacation);

    @Query("SELECT * FROM vacation WHERE vacationID = :vacationId LIMIT 1")
    Vacation getVacationByID(int vacationId);

    @Query("SELECT * FROM VACATION ORDER BY vacationID ASC")
    List<Vacation> getAllVacations();
}
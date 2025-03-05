package com.example.vacationtracker.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.vacationtracker.Entities.Excursion;
import com.example.vacationtracker.Entities.Vacation;

import java.util.List;

@Dao
public interface ExcursionDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    @Query("SELECT * FROM excursion WHERE excursionID = :excursionID LIMIT 1")
    Excursion getExcursionByID(int excursionID);

    @Query("SELECT * FROM excursion WHERE vacationID = :vacationID")
    List<Excursion> getExcursionsByVacationID(int vacationID);

    @Query("SELECT * FROM EXCURSION ORDER BY excursionID ASC")
    List<Excursion> getAllExcursions();
}
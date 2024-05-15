package me.reserv.crosshero.repository;

import me.reserv.crosshero.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, String> {
    @Query(value = "SELECT * FROM workout w WHERE w.name= ?1 and w.time = ?2 and w.date >= ?3 and w.date <= ?4",
            nativeQuery = true)
    List<Workout> findNextNWorkouts(String name,
                                    LocalTime time,
                                    LocalDate dateFrom,
                                    LocalDate dateTo);

    @Query(value = "SELECT * FROM workout w WHERE date + time BETWEEN ?1 AND ?2 " +
            "ORDER BY date asc, time asc",
            nativeQuery = true)
    List<Workout> findAllWorkoutsBetween(Timestamp dateTimeFrom,
                                         Timestamp dateTimeTo);
}

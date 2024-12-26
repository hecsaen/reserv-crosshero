package me.reserv.crosshero.service;

import lombok.RequiredArgsConstructor;
import me.reserv.crosshero.entity.Subscription;
import me.reserv.crosshero.entity.Workout;
import me.reserv.crosshero.repository.WorkoutRepository;
import org.hibernate.jdbc.Work;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {
    private final WorkoutRepository workoutRepository;

    public Optional<Workout> findWorkout(String name, LocalDate date, LocalTime time) {
        return workoutRepository.findOne(Example.of(Workout.builder()
                .name(name)
                .date(date)
                .time(time)
                .build()));
    }

    public List<Workout> findNextNWorkouts(Workout Workout, Integer n) {
        if (n < 1) n = 1;

        return workoutRepository. findNextNWorkouts(Workout.getName(),
                Workout.getTime(),
                Workout.getDate(),
                Workout.getDate().plusDays(n - 1));
    }

    public List<Workout> findNextNWorkouts(Subscription subscription, Integer n) {
        if (n < 1) n = 1;
        return workoutRepository.findNextNWorkouts(subscription.getWorkoutName(),
                subscription.getTime(),
                LocalDate.now(),
                LocalDate.now().plusDays(n - 1));
    }

    public List<Workout> findAllWorkoutsBetween(LocalDateTime from, LocalDateTime to) {
        Timestamp fromTimestamp = Timestamp.valueOf(from);
        Timestamp toTimestamp = Timestamp.valueOf(to);
        return workoutRepository.findAllWorkoutsBetween(fromTimestamp, toTimestamp);
    }

    /**
     * Store only non-existant workouts
     *
     * @param workoutList
     * @return
     */
    public List<Workout> saveWorkouts(List<Workout> workoutList) {
        List<Workout> existingWorkouts = workoutRepository.findByLinkIn(workoutList.stream()
                .map(Workout::getLink)
                .toList());

        Set<String> existingWorkoutLinks = existingWorkouts.stream().map(Workout::getLink).collect(Collectors.toSet());

        List<Workout> nonExistingWorkouts = workoutList.stream()
                .filter(workout -> !existingWorkoutLinks.contains(workout.getLink()))
                .toList();

        // Return saved workouts
        return workoutRepository.saveAll(nonExistingWorkouts);
    }
}

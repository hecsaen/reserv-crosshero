//package me.reserv.crosshero.service;
//
//import me.reserv.crosshero.entity.Workout;
//import me.reserv.crosshero.repository.WorkoutRepository;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import static java.time.ZoneOffset.UTC;
//
//@SpringBootTest
//@ActiveProfiles("test")
//public class WorkoutServiceTests {
//
//    @Autowired
//    private WorkoutRepository workoutRepository;
//
//    @Autowired
//    private WorkoutService workoutService;
//
//    private static final int MONTH = 7;
//    private static final int YEAR = 2024;
//
//    @BeforeEach
//    void saveWod() {
//        saveWeeklySchedule();
//    }
//
//    @AfterEach
//    void deleteWods() {
//        workoutRepository.deleteAll();
//    }
//
//    @Test
//    void saveMultipleWods() {
//        Workout wod1 = Workout.builder()
//                .name("WOD")
//                .date(LocalDate.of(2024, 9, 13))
//                .time(LocalTime.of(13, 00))
//                .link("1234")
//                .build();
//        Workout wod2 = Workout.builder()
//                .name("PULSE")
//                .date(LocalDate.of(2024, 9, 13))
//                .time(LocalTime.of(13, 00))
//                .link("1234")
//                .build();
//        workoutService.saveWorkouts(List.of(wod1));
//        workoutService.saveWorkouts(List.of(wod1, wod2));
//
//        Assertions.assertEquals(2, workoutRepository.findAll().size());
//    }
//
//    @Test
//    void findNextNWeeklyWods() {
//        final Workout workout = Workout.builder()
//                .name("WOD")
//                .time(LocalTime.of(9, 0))
//                .date(LocalDate.of(YEAR, MONTH, 1)).build();
//
//        final List<Workout> foundWorkoutList = workoutService.findNextNWorkouts(workout, 5);
//        final List<Workout> foundWorkoutList2 = workoutService.findNextNWorkouts(workout, 0);
//
//        System.out.println(foundWorkoutList);
//        System.out.println(foundWorkoutList2);
//        System.out.println(foundWorkoutList2);
//
//    }
//
//    @Test
//    void findAllNextWods() {
//        var dateTimeFrom = LocalDateTime.of(YEAR, MONTH, 2, 12, 00);
//        var dateTimeTo = dateTimeFrom.plusDays(1);
//
//        var wods = workoutRepository.findAllWorkoutsBetween(dateTimeFrom, dateTimeTo);
//        System.out.println(wods);
//        wods.forEach(wod -> System.out.println(wod.getDate().toString() + " # " + wod.getTime().toString() + " | " + wod.getName()));
//        System.out.println(wods.size());
//    }
//
//    private void saveWeeklySchedule() {
//        List<Workout> workoutList = new ArrayList<>();
//        List<String> wodNames = List.of("WOD", "PULSE");
//        for (String wodName : wodNames) {
//            for (int day = 1; day < 7; day++) {
//                for (int hour = 9; hour < 15; hour++) {
//                    workoutList.add(Workout.builder()
//                            .name(wodName)
//                            .date(LocalDate.of(YEAR, MONTH, day))
//                            .time(LocalTime.of(hour, 0))
//                            .link("https://crosshero.com/" + UUID.randomUUID().toString())
//                            .build()
//                    );
//                }
//            }
//        }
//        workoutService.saveWorkouts(workoutList);
//        System.out.println(String.format("Saved %d workouts.", workoutList.size()));
//    }
//}

package me.reserv.crosshero.controller;

import lombok.RequiredArgsConstructor;
import me.reserv.crosshero.entity.Client;
import me.reserv.crosshero.entity.Subscription;
import me.reserv.crosshero.entity.Workout;
import me.reserv.crosshero.repository.ClientRepository;
import me.reserv.crosshero.repository.SubscriptionRepository;
import me.reserv.crosshero.repository.WorkoutRepository;
import me.reserv.crosshero.service.BrowserService;
import me.reserv.crosshero.service.WorkoutService;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BasicController {

    private final BrowserService browserService;
    private final SubscriptionRepository subscriptionRepository;
    private final ClientRepository clientRepository;
    private final WorkoutService workoutService;
    private final WorkoutRepository workoutRepository;

    @GetMapping("/schedule")
    public String getSchedule() {
        browserService.getClasses();
        return "Schedule";
    }

    @GetMapping("/reserve")
    public void reserve() {
        //browserService.reserveClass(null, null);
    }

    @GetMapping("/subs")
    public void subs() {

        // Extract parts if necessary
        String workoutName = "WOD";    // name (workout of the day) part
        Integer dayOfWeek = 4;    // Day of the week part
        int hour = 17;   // Hour part

        Client client = clientRepository.findAll().getFirst();

        final Subscription subscription = Subscription.builder()
                .client(client)
                .workoutName(workoutName)
                .dayOfWeek(dayOfWeek)
                .time(LocalTime.of(hour, 0))
                .build();

        var exists = subscriptionRepository.exists(Example.of(subscription));
        var subscriptionFound = subscriptionRepository.findOne(Example.of(subscription));
        var sub = subscriptionRepository.save(subscription);
    }

    @GetMapping("/db")
    public void testDb() {
        subscriptionRepository.save(Subscription.builder().build());

    }

    @GetMapping("/st")
    public String st() {
        saveWeeklySchedule();

        findAllNextWods();
        return "";
    }


    private void saveWeeklySchedule() {
        List<Workout> workoutList = new ArrayList<>();
        List<String> wodNames = List.of("WOD", "PULSE");
        for (String wodName : wodNames) {
            for (int day = 1; day < 7; day++) {
                for (int hour = 9; hour < 15; hour++) {
                    workoutList.add(Workout.builder()
                            .name(wodName)
                            .date(LocalDate.of(2024, 9, day))
                            .time(LocalTime.of(hour, 0))
                            .link("https://crosshero.com/" + UUID.randomUUID().toString())
                            .build()
                    );
                }
            }
        }
        workoutService.saveWorkouts(workoutList);
        System.out.println(String.format("Saved %d workouts.", workoutList.size()));
    }
    void findAllNextWods() {
        var dateTimeFrom = LocalDateTime.of(2024, 9, 2, 12, 00);
        var dateTimeTo = dateTimeFrom.plusDays(1);

//        var wods = workoutRepository.findAllWorkoutsBetween(dateTimeFrom, dateTimeTo);
//        System.out.println(wods);
//        wods.forEach(wod -> System.out.println(wod.getDate().toString() + " # " + wod.getTime().toString() + " | " + wod.getName()));
//        System.out.println(wods.size());
    }
}

package me.reserv.crosshero.service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.reserv.crosshero.bot.ReservBot;
import me.reserv.crosshero.entity.Client;
import me.reserv.crosshero.entity.Reservation;
import me.reserv.crosshero.entity.ReservationStatus;
import me.reserv.crosshero.entity.Subscription;
import me.reserv.crosshero.entity.Workout;
import me.reserv.crosshero.repository.ClientRepository;
import me.reserv.crosshero.repository.ReservationRepository;
import me.reserv.crosshero.repository.SubscriptionRepository;
import me.reserv.crosshero.repository.WorkoutRepository;
import me.reserv.crosshero.scraping.ExtractorUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.jdbc.Work;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrowserService {

    private final WorkoutService workoutService;
    private final ClientRepository clientRepository;
    private final ReservationRepository reservationRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ReservBot reservBot;

//    @Scheduled(fixedDelay = 1_000 * 60 * 60)
    @Scheduled(fixedDelay = 1000 * 60)
    public void getClasses() {
        final List<Client> clients = clientRepository.findByActiveTrueOrderByPriorityAsc();

        final List<Client> filteredClients = clients.stream()
                .filter(Client::getActive)
                .filter(client -> StringUtils.isNotBlank(client.getSessionCookie()))
                .toList();

        if (filteredClients.isEmpty()) {
            log.info("No active clients found.");
            return;
        }

        final Client client = filteredClients.getFirst();
        final LocalDate date = LocalDate.now();
        final List<Workout> workouts = getClasses(client, date);
        workoutService.saveWorkouts(workouts);
    }

    @Scheduled(fixedDelay = 1000 * 60)
    public void reserveWorkouts() {
        final List<Client> clients = clientRepository.findByActiveTrueOrderByPriorityAsc();
        final LocalDateTime now = LocalDateTime.now();
        final List<Workout> workouts = workoutService.findAllWorkoutsBetween(now, now.plusDays(5));

        for (var client : clients) {
            final List<Subscription> subscriptions = subscriptionRepository.findByClient(client);
            // TODO improve performance
            final List<Workout> filteredWorkouts = workouts.stream()
                    .filter(workout -> subscriptions.stream().anyMatch(subscription ->
                            Objects.equals(workout.getName(), subscription.getWorkoutName())
                                    && Objects.equals(workout.getDate().getDayOfWeek().getValue(), subscription.getDayOfWeek())
                                    && Objects.equals(workout.getTime(), subscription.getTime())
                    ))
                    .toList();
            filteredWorkouts.forEach(workout -> {
                final Optional<Reservation> reservation = reservationRepository.findByClientAndWorkout(client, workout);
                if (reservation.isEmpty()) {
                    try {
                        reserveClass(client, workout);
                        reservationRepository.save(Reservation.builder()
                                .status(ReservationStatus.OK)
                                .attempts(1)
                                .client(client)
                                .workout(workout)
                                .date(LocalDate.now())
                                .build());
                        reservBot.sendMessage(client.getTelegramId(), String.format("Reserved '%s' on '%s' at '%s'.",
                                workout.getName(),
                                workout.getDate().toString(),
                                workout.getTime().toString()
                        ));
                    } catch (Exception e) {
                        log.error("Error reserving class.", e);
                    }
                }
            });
        }
    }

    public void reserveClass(Client client, Workout workout) {

        // classes-sign-in
        try (Playwright playwright = Playwright.create()) {
            BrowserContext browserContext = getBrowserContext(playwright, client);

            Page page = browserContext.newPage();
            page.navigate(workout.getLink());

            page.locator("#classes-sign-in").click();
        }
    }

    private List<Workout> getClasses(Client client, LocalDate date) {
        try (Playwright playwright = Playwright.create()) {
            BrowserContext browserContext = getBrowserContext(playwright, client);

            Page page = browserContext.newPage();
            page.navigate("https://crosshero.com/dashboard/recurring_classes?date=" +
                    date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            List<Workout> workouts = ExtractorUtils.getWorkouts(page);

            // Get next week (not always necessary, rf.)
            page.navigate("https://crosshero.com/dashboard/recurring_classes?date=" +
                    date.plusWeeks(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            workouts.addAll(ExtractorUtils.getWorkouts(page));

            // DEBUG
            // page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("example.png")));
            browserContext.clearCookies();
            browserContext.close();
            return workouts;
        }
    }

    private static BrowserContext getBrowserContext(Playwright playwright, Client client) {
        Browser browser = playwright.webkit().launch();
        BrowserContext browserContext = browser.newContext();

        Cookie authCookie = new Cookie("_crosshero_session", client.getSessionCookie());
        authCookie.setDomain("crosshero.com");
        authCookie.setPath("/");

        Cookie acceptedCookies = new Cookie("all_cookies_accepted", "false");
        acceptedCookies.setDomain("crosshero.com");
        acceptedCookies.setPath("/");

        browserContext.addCookies(List.of(authCookie, acceptedCookies));
        return browserContext;
    }
}

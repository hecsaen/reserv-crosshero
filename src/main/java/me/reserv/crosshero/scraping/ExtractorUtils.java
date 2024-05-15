package me.reserv.crosshero.scraping;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.micrometer.common.util.StringUtils;
import me.reserv.crosshero.entity.Workout;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ExtractorUtils {

    private static final List<String> months = List.of("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");

    /**
     * Needs to return list of lessons
     *
     * @param page
     * @return
     */
    public static List<Workout> getWorkouts(Page page) {
        // Time
        Locator recurringClassesTable = page.locator(".recurring-classes-table-container");

        String monthYear = page.locator("h1").textContent();
        int month = months.indexOf(monthYear.split(" ")[0]) + 1;
        int year = Integer.parseInt(monthYear.split(" ")[1]);

        List<Integer> weekDays = recurringClassesTable.locator("thead").locator("th")
                .all().stream().map(Locator::textContent)
                .filter(StringUtils::isNotBlank)
                .map(str -> str.substring(4))
                .map(Integer::valueOf)
                .toList();

        List<Locator> weeklyRowLocators = recurringClassesTable.locator("tbody").locator("tr").all();

        List<Workout> workouts = new ArrayList<>();
        for (Locator weeklyRowLocator : weeklyRowLocators) {
            Locator timeLocator = weeklyRowLocator.locator("td.recurring-classes-table-hour");
            int hour = Integer.parseInt(timeLocator.textContent().split(":")[0]);
            int minute = Integer.parseInt(timeLocator.textContent().split(":")[1]);

            // Week schedule with classes at time provided in <classTimeLocator>
            for (int i = 0; i < 7; i++) {
                Locator timeSlot = weeklyRowLocator.locator("td").all().get(i + 1);
                List<Locator> classes = timeSlot.locator("a").all();
                if (!classes.isEmpty()) {
                    for (Locator clas : classes) {
                        int dayOfMonth = weekDays.get(i);
                        int adjustedMonth = month;
                        if (weekDays.getFirst() > dayOfMonth) {
                            adjustedMonth = month + 1;
                            if (month > 12) {
                                adjustedMonth = 1;
                            }
                        }

                        final String name = clas.locator(".info-box-text").innerText();
                        final String link = clas.getAttribute("href");
                        final LocalDate date = LocalDate.of(year, adjustedMonth, dayOfMonth);
                        final LocalTime time = LocalTime.of(hour, minute);

                        workouts.add(Workout.builder()
                                .date(date)
                                .time(time)
                                .link(link)
                                .name(name)
                                .build());
                    }
                }
            }

        }
        return workouts;
    }
}
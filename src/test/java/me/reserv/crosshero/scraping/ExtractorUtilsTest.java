package me.reserv.crosshero.scraping;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class ExtractorUtilsTest {

//    @Test
//    void getClasses(Page page) {
//        ExtractorUtils.getClasses();
//    }

//    @Test
    void testPlusDays() {
        LocalDate date = LocalDate.of(2024, 6, 29);
        System.out.println(date.plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.println(date.plusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.println(date.plusDays(3).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}
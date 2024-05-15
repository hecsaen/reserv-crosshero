package me.reserv.crosshero.scraping;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Playground {
//    @Test
    void playground() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            BrowserContext context = browser.newContext();

            Page page = context.newPage();

            page.route("**/*", route -> {
                try {
                    route.fulfill(new Route.FulfillOptions()
                            .setStatus(200)
                            .setBody(new String(Files.readAllBytes(
                                    Paths.get("./src/test/resources/recurring_classes.html")), "UTF-8")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            page.navigate("https://crosshero.com/dashboard/recurring_classes");
            var wods = ExtractorUtils.getWorkouts(page);
            // Close the browser
            browser.close();
        }
    }


}

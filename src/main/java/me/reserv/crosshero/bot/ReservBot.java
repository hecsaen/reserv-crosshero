package me.reserv.crosshero.bot;

import lombok.extern.slf4j.Slf4j;
import me.reserv.crosshero.entity.Client;
import me.reserv.crosshero.entity.Subscription;
import me.reserv.crosshero.repository.ClientRepository;
import me.reserv.crosshero.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Slf4j
@Component
public class ReservBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String botToken;
    private final TelegramClient telegramClient;
    private final ClientRepository clientRepository;
    private final SubscriptionRepository subscriptionRepository;

    public ReservBot(@Value("${BOT_TOKEN}") String botToken,
                     ClientRepository clientRepository,
                     SubscriptionRepository subscriptionRepository) {
        this.botToken = botToken;
        this.clientRepository = clientRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            log.info("Not a command.");
            return;
        }

        if (!update.getMessage().isCommand()) {
            log.info("Not a command.");
            return;
        }

        final String text = update.getMessage().getText();

        final Long telegramId = update.getMessage().getFrom().getId();
        Optional<Client> client = clientRepository.findByTelegramId(telegramId);

        if (client.isEmpty()) {
            log.info("New client with telegramId '{}'.", telegramId);
            Client newClient = Client.builder().telegramId(telegramId).build();
            if (clientRepository.count() < 10) {
                newClient.setActive(true);
                newClient.setPriority(Long.valueOf(clientRepository.count()).intValue() + 100);
            }
            client = Optional.of(clientRepository.save(newClient));
        }

        client.ifPresent(cli -> {
            final String command = text.trim().split(" ")[0];
            switch (command) {
                case "/start" -> start(cli);
                case "/status" -> status(cli);
                case "/cookie" -> setCookie(cli, text);
                case "/sub" -> subscribe(cli, text);
                case "/unsub" -> unsubscribe(cli, text);
                case "/unsuball" -> unsubscribeAll(cli);
                case "/subscriptions" -> getSubscriptions(cli);
            }
        });
    }

    private void start(Client client) {
        sendMessage(client.getTelegramId(), "Welcome");
    }

    private void status(Client client) {
        final String message = String.format("""
                • TelegramId: '%s'
                • Active: %b
                • SessionCookie: '%s'
                """, client.getTelegramId(), client.getActive(), client.getSessionCookie());
        sendMessage(client.getTelegramId(), message);
    }

    private void getSubscriptions(Client client) {
        final List<Subscription> subscriptions = subscriptionRepository.findByClientOrderByDayOfWeekAscTimeAsc(client);
        String message;
        if (CollectionUtils.isEmpty(subscriptions)) {
            message = "No subscriptions yet.";
        } else {
            message = subscriptions.stream()
                    .map(sub -> String.format("• %s on %s at %s",
                            sub.getWorkoutName(),
                            DayOfWeek.of(sub.getDayOfWeek()).name(),
                            sub.getTime().toString()))
                    .collect(Collectors.joining("\n"));
        }
        sendMessage(client.getTelegramId(), message);
    }

    private void setCookie(Client client, String text) {
        final String sessionCookie = text.replace("/cookie", "").trim();
        client.setSessionCookie(sessionCookie);
        client = clientRepository.save(client);
        sendMessage(client.getTelegramId(), "Session cookie registered.");
    }

    private void subscribe(Client client, String text) {
        final String RESERV_PATTERN = "^/sub\\s+(\\S+)\\s+(mon|tue|wed|thu|fri|sat|sun)\\s+([0-9]|1[0-9]|2[0-3])$";

        // Compile the pattern
        Pattern pattern = Pattern.compile(RESERV_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        List<String> daysOfWeek = List.of("mon|tue|wed|thu|fri|sat|sun".split("\\|"));

        // Check if the input matches the pattern
        if (matcher.matches()) {
            // Extract parts if necessary
            String workoutName = matcher.group(1);    // name (workout of the day) part
            int dayOfWeek = daysOfWeek.indexOf(matcher.group(2)) + 1;    // Day of the week part
            int hour = Integer.parseInt(matcher.group(3));   // Hour part

            Subscription subscription = Subscription.builder()
                    .client(client)
                    .workoutName(workoutName)
                    .dayOfWeek(dayOfWeek)
                    .time(LocalTime.of(hour, 0))
                    .build();

            if (subscriptionRepository.exists(Example.of(subscription))) {
                sendMessage(client.getTelegramId(), String.format("Already subscribed to %s", subscription.toPrettyString()));
            } else {
                subscription = subscriptionRepository.save(subscription);
                sendMessage(client.getTelegramId(), String.format("Subscribed to %s", subscription.toPrettyString()));
            }
        }
    }

    private void unsubscribeAll(Client client) {
        final List<Subscription> subscriptions = subscriptionRepository.findByClient(client);
        subscriptionRepository.deleteAllInBatch(subscriptions);
        sendMessage(client.getTelegramId(), String.format("Unsubscribed to %d subscriptions", subscriptions.size()));
    }

    private void unsubscribe(Client client, String text) {
        final String RESERV_PATTERN = "^/unsub\\s+(\\S+)\\s+(mon|tue|wed|thu|fri|sat|sun)\\s+([0-9]|1[0-9]|2[0-3])$";

        // Compile the pattern
        Pattern pattern = Pattern.compile(RESERV_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        List<String> daysOfWeek = List.of("mon|tue|wed|thu|fri|sat|sun".split("\\|"));

        // Check if the input matches the pattern
        if (matcher.matches()) {
            // Extract parts if necessary
            String workoutName = matcher.group(1);    // name (workout of the day) part
            int dayOfWeek = daysOfWeek.indexOf(matcher.group(2)) + 1;    // Day of the week part
            int hour = Integer.parseInt(matcher.group(3));   // Hour part

            Subscription subscription = Subscription.builder()
                    .client(client)
                    .workoutName(workoutName)
                    .dayOfWeek(dayOfWeek)
                    .time(LocalTime.of(hour, 0))
                    .build();

            Optional<Subscription> subs = subscriptionRepository.findOne(Example.of(subscription));
            if (subs.isPresent()) {
                subscriptionRepository.delete(subs.get());
                sendMessage(client.getTelegramId(), String.format("Unsubscribed from %s", subscription.toPrettyString()));
            } else {
                sendMessage(client.getTelegramId(), "No subscription found.");
            }
        }
    }

    public void sendMessage(Long telegramId, String text) {
        try {
            telegramClient.execute(SendMessage.builder().chatId(telegramId).text(text).build());
        } catch (TelegramApiException e) {
            // TODO: send to queue?
            throw new RuntimeException(e);
        }
    }
}

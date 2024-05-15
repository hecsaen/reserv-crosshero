package me.reserv.crosshero.service;

import lombok.RequiredArgsConstructor;
import me.reserv.crosshero.entity.Subscription;
import me.reserv.crosshero.repository.ClientRepository;
import me.reserv.crosshero.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ClientRepository clientRepository;

    /**
     * Get active subscriptions:
     * - Get active client subscriptions
     * - Get non successful reservations
     * - Get subscriptions in the next 5 days
     * - Get wods
     * @param time
     * @return
     */
    public Subscription getActiveSubscriptions(LocalTime time) {
        return null;
    }
}

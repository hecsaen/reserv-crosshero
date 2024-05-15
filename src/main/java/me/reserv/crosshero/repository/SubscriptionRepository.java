package me.reserv.crosshero.repository;

import me.reserv.crosshero.entity.Client;
import me.reserv.crosshero.entity.Subscription;
import me.reserv.crosshero.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    List<Subscription> findByClient(Client client);
}

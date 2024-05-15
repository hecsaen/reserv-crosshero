package me.reserv.crosshero.repository;

import me.reserv.crosshero.entity.Client;
import me.reserv.crosshero.entity.Reservation;
import me.reserv.crosshero.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    Optional<Reservation> findByClientAndWorkout(Client client, Workout workout);
}

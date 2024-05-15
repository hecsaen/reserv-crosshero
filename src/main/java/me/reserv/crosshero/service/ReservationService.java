package me.reserv.crosshero.service;

import lombok.RequiredArgsConstructor;
import me.reserv.crosshero.entity.Reservation;
import me.reserv.crosshero.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;


}

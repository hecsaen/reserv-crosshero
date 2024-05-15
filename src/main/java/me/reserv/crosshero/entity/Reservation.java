package me.reserv.crosshero.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Data
@Builder
@Table(name = "reservation")
@NoArgsConstructor
@AllArgsConstructor
public class Reservation implements PrettyPrintable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    private Integer attempts;
    private LocalDate date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;
    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;

    @Override
    public String toPrettyString() {
        return String.format("Reservation - %s @ %s on %s",
                workout.getName(),
                workout.getTime().toString(),
                workout.getDate().toString()
        );
    }
}

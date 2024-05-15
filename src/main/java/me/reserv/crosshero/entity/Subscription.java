package me.reserv.crosshero.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "subscription",
        uniqueConstraints = {@UniqueConstraint(name = "UniqueNumberAndStatus", columnNames = {"workout_name", "day_of_week", "time", "client_id"})}
)
public class Subscription implements PrettyPrintable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "workout_name")
    private String workoutName;
    @Column(name = "day_of_week")
    private Integer dayOfWeek;
    @Column(name = "time")
    private LocalTime time;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;

    @Override
    public String toPrettyString() {
        return String.format("Subscription - %s @ %s on %s",
                workoutName,
                time.toString(),
                DayOfWeek.of(dayOfWeek).name()
        );
    }
}

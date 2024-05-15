package me.reserv.crosshero.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.*;

@Entity
@Data
@Builder
@Table(
        name = "workout",
        uniqueConstraints = {@UniqueConstraint(name = "Unique nameDateTime", columnNames = {"name", "date", "time"})}
)
@NoArgsConstructor
@AllArgsConstructor
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String link;
    private String name;
    private LocalDate date;
    private LocalTime time;
    @CreationTimestamp
    private Instant createdAt;
}

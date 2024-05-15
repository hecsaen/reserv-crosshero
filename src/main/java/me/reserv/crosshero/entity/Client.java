package me.reserv.crosshero.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Data
@Builder
@Table(name = "client")
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "telegram_id", unique=true)
    private Long telegramId;
    @Column(name = "session_cookie")
    private String sessionCookie;
    @Builder.Default
    private Boolean active = false;
    @Builder.Default
    private Integer priority = 9999;
    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;
}

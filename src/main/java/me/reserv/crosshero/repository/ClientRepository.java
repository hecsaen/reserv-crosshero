package me.reserv.crosshero.repository;

import me.reserv.crosshero.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByTelegramId(Long id);
    List<Client> findByActiveTrueOrderByPriorityAsc();
}

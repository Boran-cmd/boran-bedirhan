package com.beispiel.raumreservierung.repository;

import com.beispiel.raumreservierung.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByPublicKey(String publicKey);

    Optional<Reservation> findByPrivateKey(String privateKey);

    // ✅ Für Admin: Alle Reservierungen nach Datum
    List<Reservation> findByDate(LocalDate date);

    // ✅ Für Verfügbarkeitsprüfung beim Erstellen
    boolean existsByRoomRoomNumberAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
            int roomNumber,
            LocalDate date,
            LocalTime endTime,
            LocalTime startTime
    );
}

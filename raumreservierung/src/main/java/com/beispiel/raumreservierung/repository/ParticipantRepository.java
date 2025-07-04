package com.beispiel.raumreservierung.repository;

import com.beispiel.raumreservierung.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}

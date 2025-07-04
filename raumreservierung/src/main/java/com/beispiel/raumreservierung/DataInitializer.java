package com.beispiel.raumreservierung;

import com.beispiel.raumreservierung.model.*;
import com.beispiel.raumreservierung.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RoomRepository roomRepo, ReservationRepository resRepo) {
        return args -> {
            // Räume anlegen (101–105)
            for (int i = 101; i <= 105; i++) {
                if (!roomRepo.existsById(i)) {
                    roomRepo.save(new Room(i, "Raum " + i));
                }
            }

            // Beispiel-Reservierung 1
            if (resRepo.count() == 0) {
                Room room = roomRepo.findById(101).orElseThrow();

                Reservation res = new Reservation();
                res.setDate(LocalDate.now().plusDays(1));
                res.setStartTime(LocalTime.of(10, 0));
                res.setEndTime(LocalTime.of(11, 0));
                res.setRoom(room);
                res.setRemark("Teammeeting Raum 101");
                res.setPublicKey(UUID.randomUUID().toString().substring(0, 8));
                res.setPrivateKey(UUID.randomUUID().toString().substring(0, 8));

                Participant p1 = new Participant("Anna Meier", res);
                Participant p2 = new Participant("Tom Schulz", res);
                res.setParticipants(List.of(p1, p2));

                resRepo.save(res);
            }
        };
    }
}

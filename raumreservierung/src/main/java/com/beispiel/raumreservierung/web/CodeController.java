package com.beispiel.raumreservierung.web;

import com.beispiel.raumreservierung.repository.ReservationRepository;
import com.beispiel.raumreservierung.model.Reservation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
public class CodeController {

    private final ReservationRepository reservationRepo;

    public CodeController(ReservationRepository reservationRepo) {
        this.reservationRepo = reservationRepo;
    }

    @GetMapping("/code")
    public RedirectView handleCode(@RequestParam String key) {
        Optional<Reservation> byPrivate = reservationRepo.findByPrivateKey(key);
        if (byPrivate.isPresent()) {
            return new RedirectView("/edit.html?key=" + key);
        }

        Optional<Reservation> byPublic = reservationRepo.findByPublicKey(key);
        if (byPublic.isPresent()) {
            return new RedirectView("/view.html?key=" + key);
        }

        return new RedirectView("/index.html?error=notfound");
    }
}

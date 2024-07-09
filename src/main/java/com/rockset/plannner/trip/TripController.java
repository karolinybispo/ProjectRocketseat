package com.rockset.plannner.trip;

import com.rockset.plannner.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trips")
public class TripController {

        @Autowired
        private ParticipantService participantService;

        @Autowired
        private TripRepository repository;
        @PostMapping
        public ResponseEntity<String> createTrip (@RequestBody TripRequestPayLoad payload){
                Trip newTrip = new Trip(payload);

                this.repository.save(newTrip);
                this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip.getId());

                return ResponseEntity.ok("Sucesso!");
        }
}

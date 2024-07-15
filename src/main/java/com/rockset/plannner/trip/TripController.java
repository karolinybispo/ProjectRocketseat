package com.rockset.plannner.trip;

import com.rockset.plannner.activitie.ActivityDate;
import com.rockset.plannner.activitie.ActivityRequestPayLoud;
import com.rockset.plannner.activitie.ActivityResponse;
import com.rockset.plannner.activitie.ActivityService;
import com.rockset.plannner.link.LinkData;
import com.rockset.plannner.link.LinkRequestPayLoud;
import com.rockset.plannner.link.LinkResponse;
import com.rockset.plannner.link.LinkService;
import com.rockset.plannner.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/trips")
public class TripController {

        @Autowired
        private ParticipantService participantService;

        @Autowired
        private LinkService linkService;

        @Autowired
        private ActivityService activityService;

        @Autowired
        private TripRepository repository;

        @PostMapping
        public ResponseEntity<TripCreateResponse> createTrip (@RequestBody TripRequestPayLoad payload){
                Trip newTrip = new Trip(payload);

                this.repository.save(newTrip);
                this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

                return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
        }
        @GetMapping("/{id}")
        public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id){
                Optional<Trip> trip = this.repository.findById(id);
                return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }

        @PutMapping("/{id}")
        public ResponseEntity<Trip> upDateTrip(@PathVariable UUID id, @RequestBody TripRequestPayLoad payload){
                Optional<Trip> trip = this.repository.findById(id);

                 if(trip.isPresent()){

                   Trip rawTrip = trip.get();
                   rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
                   rawTrip.setEndsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
                   rawTrip.setDestination(payload.destination());

                        this.repository.save(rawTrip);

                                return  ResponseEntity.ok(rawTrip);
                 }
                                return ResponseEntity.notFound().build();
          }



        @GetMapping("/{id}/confirm")
        public ResponseEntity<Trip> confirmTrip (@PathVariable UUID id){
                Optional<Trip> trip = this.repository.findById(id);

                if(trip.isPresent()){

                        Trip rawTrip = trip.get();
                        rawTrip.setIsConfirmed(true);

                        this.repository.save(rawTrip);
                        this.participantService.triggerConfirmationEmailToParticipants(id);

                        return  ResponseEntity.ok(rawTrip);
                }

                return ResponseEntity.notFound().build();
        }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant (@PathVariable UUID id, @RequestBody ParticipantRequestPayloud payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()) {
            Trip rawTrip = trip.get();


            ParticipantCreateResponse participantResponse = this.participantService.registerParticipantToEvent(payload.email(), rawTrip);

            if (rawTrip.getIsConfirmed()) this.participantService.triggerConfirmationEmailToParticipant(payload.email());

            return ResponseEntity.ok(participantResponse);
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityDate>> getAllActivities(@PathVariable UUID id){
            List<ActivityDate> activityDateList = this.activityService.getAllActivitiesFromId(id);

            return ResponseEntity.ok(activityDateList);
        }






        @GetMapping("/{id}/participants")
            public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id){
                    List<ParticipantData> participantList = this.participantService.getAllParticipantsFromEvent(id);

                    return ResponseEntity.ok(participantList);

        }


        @PostMapping("{id}/activities")
            public ResponseEntity<ActivityResponse> registerActivity (@PathVariable UUID id, @RequestBody ActivityRequestPayLoud payLoud) {
            Optional<Trip> trip = this.repository.findById(id);

            if (trip.isPresent()) {
                Trip rawTrip = trip.get();

                ActivityResponse activityResponse = this.activityService.registerActivity(payLoud, rawTrip);

                return ResponseEntity.ok(activityResponse);

            }
            return ResponseEntity.notFound().build();
        }

        @PostMapping("/{id}/links")
        public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayLoud payLoud){
            Optional<Trip> trip = this.repository.findById(id);

            if (trip.isPresent()) {
                Trip rawTrip = trip.get();

                LinkResponse linkResponse = this.linkService.registerLink(payLoud, rawTrip);
                 return ResponseEntity.ok(linkResponse);
            }
            return  ResponseEntity.notFound().build();
        }

    @GetMapping("{id}/links")
    public ResponseEntity<List<LinkData>> getAllLinks (@PathVariable UUID id) {
List<LinkData>  linkDataList = this.linkService.getAllLinksFromTripId(id);

            return ResponseEntity.ok(linkDataList);


    }
}

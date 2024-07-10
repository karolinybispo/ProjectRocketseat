package com.rockset.plannner.participant;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Component
public class ParticipantService {
    public void registerParticipantsToEvent(List<String> participantsToInvite, UUID tripId){

    }

    public void triggerConfirmationEmailToParticipants(UUID tripId){

    }
}

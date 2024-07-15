package com.rockset.plannner.activitie;

import com.rockset.plannner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

        @Autowired
        private  ActivityRepository repository;

    public ActivityResponse registerActivity (ActivityRequestPayLoud payloud, Trip trip){
    Activity newActivity = new Activity (payloud.title(), payloud.occurs_at(), trip);
            this.repository.save(newActivity);

            return new ActivityResponse(newActivity.getId());
    }

    public List<ActivityDate> getAllActivitiesFromId(UUID tripId ){
       return  this.repository.findById(tripId).stream().map(activity -> new ActivityDate(activity.getId(), activity.getTitle(), activity.getOccursAt())).toList();
    }
}

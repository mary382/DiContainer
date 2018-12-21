package com.vizorgames.interview.data.service;

import com.vizorgames.interview.data.dao.event.EventDAO;
import com.vizorgames.interview.data.dao.profile.ProfileDAO;

import javax.inject.Inject;

public class InjectAmbiguityService
{
    private EventDAO eventDAO;
    private ProfileDAO profileDAO;

    @Inject
    public InjectAmbiguityService(EventDAO eventDAO)
    {
        this.eventDAO = eventDAO;
    }

    @Inject
    public InjectAmbiguityService(ProfileDAO profileDAO)
    {
        this.profileDAO = profileDAO;
    }
}

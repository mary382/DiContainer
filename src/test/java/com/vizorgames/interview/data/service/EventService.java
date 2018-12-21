package com.vizorgames.interview.data.service;

import com.vizorgames.interview.data.dao.event.EventDAO;

import javax.inject.Inject;

public class EventService
{
    private final EventDAO dao;

    @Inject
    public EventService(EventDAO dao)
    {
        this.dao = dao;
    }
}

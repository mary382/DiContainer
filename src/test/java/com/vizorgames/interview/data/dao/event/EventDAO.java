package com.vizorgames.interview.data.dao.event;

import com.vizorgames.interview.data.domain.Entity;

import java.util.Collection;

public interface EventDAO
{
    Collection<Entity> getEvents();
}

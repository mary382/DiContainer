package com.vizorgames.interview.data.dao.event;

import com.vizorgames.interview.data.domain.Entity;

import java.util.Collection;
import java.util.Collections;

public class InMemoryEventDAO implements EventDAO
{
    @Override
    public Collection<Entity> getEvents()
    {
        return Collections.emptyList();
    }
}

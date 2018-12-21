package com.vizorgames.interview.data.dao.profile;

import com.vizorgames.interview.data.domain.Entity;

import java.util.Collection;
import java.util.Collections;

public class InMemoryProfileDAO implements ProfileDAO
{
    @Override
    public Collection<Entity> getProfiles()
    {
        return Collections.emptyList();
    }
}

package com.vizorgames.interview.data.dao.profile;

import com.vizorgames.interview.data.domain.Entity;

import java.util.Collection;

public interface ProfileDAO
{
    Collection<Entity> getProfiles();
}

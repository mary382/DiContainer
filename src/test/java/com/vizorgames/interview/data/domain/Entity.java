package com.vizorgames.interview.data.domain;

import java.util.Map;

public class Entity
{
    public static String ID_ATTRIBUTE_KEY = "id";

    private Map<String, Object> data;

    public Entity(Map<String, Object> data)
    {
        if (!data.containsKey(ID_ATTRIBUTE_KEY))
        {
            throw new RuntimeException("An event MUST have id attribute");
        }

        this.data = data;
    }

    public Map<String, Object> getData()
    {
        return data;
    }

    @Override
    public boolean equals(Object e)
    {
        if (this == e)
        {
            return true;
        }

        if (e == null || getClass() != e.getClass())
        {
            return false;
        }

        Entity otherEvent = (Entity) e;

        Object thisId = this.data.get(ID_ATTRIBUTE_KEY);
        Object otherId = otherEvent.data.get(ID_ATTRIBUTE_KEY);

        return thisId.equals(otherId);
    }

    @Override
    public int hashCode()
    {
        return 21 * this.data.get(ID_ATTRIBUTE_KEY).hashCode() + 1;
    }
}

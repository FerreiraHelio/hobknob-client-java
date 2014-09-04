package com.opentable.hobknob.client;

public class CacheUpdate
{
    public String Key;
    public Boolean OldValue;
    public Boolean NewValue;

    public CacheUpdate(String key, Boolean oldValue, Boolean newValue)
    {
        Key = key;
        OldValue = oldValue;
        NewValue = newValue;
    }

    public String getKey() {
        return Key;
    }
}

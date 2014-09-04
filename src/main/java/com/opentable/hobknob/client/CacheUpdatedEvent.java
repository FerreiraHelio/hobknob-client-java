package com.opentable.hobknob.client;

import java.util.List;

public interface CacheUpdatedEvent {
    void cacheUpdated(List<CacheUpdate> cacheUpdates);
}


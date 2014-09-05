package com.opentable.hobknob.client;

public interface CacheUpdateFailedEvent {
    void cacheUpdateFailed(Exception exception);
}

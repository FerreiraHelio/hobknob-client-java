package com.opentable.hobknob.client;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FeatureToggleCache {

    private FeatureToggleProvider _featureToggleProvider;
    private int _updateIntervalMs;
    private Timer _timer;
    private HashMap<String, Boolean> _cache;
    private CountDownLatch _countDownLatch;
    private Exception _updateException;

    public FeatureToggleCache(FeatureToggleProvider featureToggleProvider, int cacheUpdateIntervalMs) throws IllegalArgumentException {
        _featureToggleProvider = featureToggleProvider;
        _updateIntervalMs = cacheUpdateIntervalMs;
        _countDownLatch = new CountDownLatch(1);
        _timer = new Timer("CacheUpdateTimer");

        if (_updateIntervalMs < 1000) {
            throw new IllegalArgumentException("Cache update interval must be at least 1 second");
        }
    }

    public void initialize(int cacheInitialisationTimeoutMs) throws Exception {
        _timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateCache();
                }
            }, 0, _updateIntervalMs);

        if (!_countDownLatch.await(cacheInitialisationTimeoutMs, TimeUnit.MILLISECONDS))
        {
            throw new Exception("Timed out waiting for cache to initialise");
        }

        if (_updateException != null) {
            throw new Exception("Failed to update cache for the first time", _updateException);
        }
    }

    public Boolean get(String featureToggleName) {
        return _cache.getOrDefault(featureToggleName, null);
    }

    private void updateCache() {

        HashMap<String,Boolean> featureToggles;
        try {
            featureToggles = _featureToggleProvider.get();
        }
        catch (Exception ex) {

            raiseCacheUpdateFailedEvent(ex);

            _updateException = ex;
            return;
        }

        List<CacheUpdate> updates = GetUpdates(_cache, featureToggles);
        _cache = featureToggles;

        raiseCacheUpdatedEvent(updates);

        _countDownLatch.countDown();
    }

    private List<CacheUpdate> GetUpdates(HashMap<String, Boolean> existingToggles, HashMap<String, Boolean> newToggles) {

        HashMap<String, Boolean> existingNotNull = existingToggles != null ? existingToggles : new HashMap<String, Boolean>();

        List<CacheUpdate> updates = new ArrayList<CacheUpdate>();
        for (Map.Entry<String, Boolean> newToggle : newToggles.entrySet())
        {
            Boolean existingValue = existingNotNull.getOrDefault(newToggle.getKey(), null);
            if (existingValue != null)
            {
                if (existingValue != newToggle.getValue())
                {
                    updates.add(new CacheUpdate(newToggle.getKey(), existingValue, newToggle.getValue()));
                }
            }
            else
            {
                updates.add(new CacheUpdate(newToggle.getKey(), null, newToggle.getValue()));
            }
        }

        updates.addAll(existingNotNull
            .entrySet()
            .stream()
            .filter(existing -> !newToggles.containsKey(existing.getKey()))
            .map(existing -> new CacheUpdate(existing.getKey(), existing.getValue(), null))
            .collect(Collectors.toList()));

        return updates;
    }

    private List<CacheUpdatedEvent> _cacheUpdatedListeners = new ArrayList<CacheUpdatedEvent>();
    private List<CacheUpdateFailedEvent> _cacheUpdateFailedListeners = new ArrayList<CacheUpdateFailedEvent>();

    public void addCacheUpdatedLstener(CacheUpdatedEvent listener) {
        _cacheUpdatedListeners.add(listener);
    }

    public void addCacheUpdateFailedLstener(CacheUpdateFailedEvent listener) {
        _cacheUpdateFailedListeners.add(listener);
    }

    private void raiseCacheUpdatedEvent(List<CacheUpdate> updates) {
        for(CacheUpdatedEvent listener : _cacheUpdatedListeners) {
            listener.cacheUpdated(updates);
        }
    }

    private void raiseCacheUpdateFailedEvent(Exception exception) {
        for(CacheUpdateFailedEvent listener : _cacheUpdateFailedListeners) {
            listener.cacheUpdateFailed(exception);
        }
    }
}


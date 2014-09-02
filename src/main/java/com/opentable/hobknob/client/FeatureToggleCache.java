package com.opentable.hobknob.client;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
        _timer.schedule(
            new TimerTask() {
                @Override
                public void run() {
                    updateCache();
                }
            }, 0, _updateIntervalMs);

        _countDownLatch.await(cacheInitialisationTimeoutMs, TimeUnit.MILLISECONDS);

        if (_updateException != null) {
            throw new Exception("Failed to update cache for the first time", _updateException);
        }
    }

    public Boolean get(String featureToggleName) {
        return _cache.getOrDefault(featureToggleName, null);
    }

    private void updateCache() {
        try {
            HashMap<String,Boolean> applicationFeatureToggles = _featureToggleProvider.get();
            _cache = applicationFeatureToggles;
        }
        catch (Exception ex) {
            _updateException = ex;
            return;
        }

        _countDownLatch.countDown();
    }
}

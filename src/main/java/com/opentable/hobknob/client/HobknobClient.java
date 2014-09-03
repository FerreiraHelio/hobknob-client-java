package com.opentable.hobknob.client;

public class HobknobClient {

    FeatureToggleCache _featureToggleCache;
    String _applicationName;

    public HobknobClient(FeatureToggleCache featureToggleCache, String applicationName) {

        _featureToggleCache = featureToggleCache;
        _applicationName = applicationName;
    }

    public boolean get(String toggleName) throws Exception {

        Boolean value = _featureToggleCache.get(toggleName);
        if (value == null)
        {
            throw new Exception(String.format("Key not found for toggle %s/%s", _applicationName, toggleName));
        }
        return value;
    }

    public boolean getOrDefault(String featureToggleName, boolean defaultValue) {

        Boolean value =_featureToggleCache.get(featureToggleName);
        return value != null ? value : defaultValue;
    }
}
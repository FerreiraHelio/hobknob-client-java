package com.opentable.hobknob.client;

import mousio.etcd4j.EtcdClient;
import java.net.URI;

public class HobknobClientFactory {

    public HobknobClient create(String etcdHost, int etcdPort, String applicationName, int cacheUpdateIntervalMs) throws Exception {

        String applicationDirectoryKey = String.format("http://%s:%s/v2/keys/", etcdHost, etcdPort);
        int cacheInitialisationTimeoutMs = 60 * 1000;

        EtcdClient etcdClient = new EtcdClient(URI.create(applicationDirectoryKey));
        FeatureToggleProvider featureToggleProvider = new FeatureToggleProvider(etcdClient, applicationName);
        FeatureToggleCache featureToggleCache = new FeatureToggleCache(featureToggleProvider, cacheUpdateIntervalMs);
        HobknobClient hobknobClient = new HobknobClient(featureToggleCache, applicationName);

        featureToggleCache.initialize(cacheInitialisationTimeoutMs);

        return hobknobClient;
    }
}

package com.opentable.hobknob.client;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.responses.EtcdKeysResponse;

import java.util.HashMap;

public class FeatureToggleProvider
{
    private EtcdClient _etcdClient;
    private String _applicationDirectoryKey;

    public FeatureToggleProvider(EtcdClient etcdClient, String applicationName)
    {
        _etcdClient = etcdClient;
        _applicationDirectoryKey = "v1/toggles/" + applicationName;
    }

    public HashMap<String,Boolean> get() throws Exception
    {
        EtcdKeysResponse etcdKeysResponse = _etcdClient.getDir(_applicationDirectoryKey).recursive().send().get();

        // todo: handle errors

        HashMap<String,Boolean> hashMap = new HashMap<>();
        for(EtcdKeysResponse.EtcdNode node : etcdKeysResponse.node.nodes)
        {
            String key = getKey(node.key);
            Boolean featureToggleValue = parseFeatureToggleValue(node.value);

            if (featureToggleValue != null)
            {
                hashMap.put(key, featureToggleValue);
            }
        }
        return hashMap;
    }

    private static String getKey(String path)
    {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }

    private static Boolean parseFeatureToggleValue(String value)
    {
        if (value == null) return null;

        switch (value.toLowerCase())
        {
            case "true":
                return true;
            case "false":
                return false;
            default:
                return null;
        }
    }
}

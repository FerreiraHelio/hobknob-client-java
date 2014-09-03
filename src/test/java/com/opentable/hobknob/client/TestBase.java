package com.opentable.hobknob.client;

import mousio.etcd4j.EtcdClient;
import org.junit.After;

import java.net.URI;
import java.util.HashSet;

import static org.junit.Assert.fail;

public class TestBase
{
    private EtcdClient _etcdClient;
    private HobknobClient _hobknobClient;
    private String _applicationName;

    private String EtcdHost = "localhost";
    private int EtcdPort = 4001;

    private HashSet<String> _applicationKeysToClearOnTearDown = new HashSet<>();
    private int _cacheUpdateIntervalMs = 60 * 1000;

    protected TestBase() {
        _etcdClient = new EtcdClient(URI.create(String.format("http://%s:%s/v2/keys/", EtcdHost, EtcdPort)));
    }

    @After
    public void tearDown() {

        if (_hobknobClient != null)
        {
            _hobknobClient = null;
        }

        for (String application : _applicationKeysToClearOnTearDown)
        {
            _etcdClient.deleteDir("v1/toggles/" + application);
        }
        _applicationKeysToClearOnTearDown.clear();
    }

    protected void set_application_name(String applicationName) {
        _applicationName = applicationName;
    }

    protected void set_cache_update_interval(int cacheUpdateIntervalMs) {
        _cacheUpdateIntervalMs = cacheUpdateIntervalMs;
    }

    protected void given_a_toggle(String applicationName, String toggleName, String value) {
        String key = String.format("v1/toggles/%s/%s", applicationName, toggleName);
        try {
            _etcdClient.put(key, value).send().get();
        }
        catch(Exception ex){
            fail(ex.toString());
        }
        _applicationKeysToClearOnTearDown.add(applicationName);
    }

    protected boolean when_I_get(String toggleName) throws Exception {
        _hobknobClient = new HobknobClientFactory().create(EtcdHost, EtcdPort, _applicationName, _cacheUpdateIntervalMs);
        return _hobknobClient.get(toggleName);
    }

    protected boolean when_I_get_without_initialising_a_new_hobknob_instance(String toggleName) throws Exception {
        return _hobknobClient.get(toggleName);
    }

    protected boolean when_I_get_with_default(String toggleName, boolean defaultValue) throws Exception {
        _hobknobClient = new HobknobClientFactory().create(EtcdHost, EtcdPort, _applicationName, 60000);
        return _hobknobClient.getOrDefault(toggleName, defaultValue);
    }
}

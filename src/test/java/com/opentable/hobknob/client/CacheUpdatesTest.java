package com.opentable.hobknob.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class CacheUpdatesTest extends TestBase
{
    @Before
    public void SetUp() throws Exception {
        TearDown();
        set_cache_update_interval(1000);
        set_application_name("cacheUpdateTest");
    }

    @After
    public void TearDown() throws Exception {
        try {
            _etcdClient.deleteDir("v1/toggles/cacheUpdateTest").recursive().send().get();
        }
        catch (Exception ex) {
        }
    }

    @Test
    public void Cache_is_not_updated_when_update_interval_is_not_passed() throws Exception {
        given_a_toggle("cacheUpdateTest", "toggle1", "true");
        boolean value = when_I_get("toggle1");
        assertThat(value, equalTo(true));

        given_a_toggle("cacheUpdateTest", "toggle1", "false");

        boolean value2 = when_I_get_without_initialising_a_new_hobknob_instance("toggle1");
        assertThat(value2, equalTo(true));
    }

    @Test
    public void Cache_is_updated_when_update_interval_is_passed() throws Exception {
        given_a_toggle("cacheUpdateTest", "toggle1", "true");
        boolean value = when_I_get("toggle1");
        assertThat(value, equalTo(true));

        given_a_toggle("cacheUpdateTest", "toggle1", "false");
        Thread.sleep(1200);

        boolean value2 = when_I_get_without_initialising_a_new_hobknob_instance("toggle1");
        assertThat(value2, equalTo(false));
    }


    @Test
    public void Cache_updated_information_is_correct() throws Exception {
        given_a_toggle("cacheUpdateTest", "existingNoChange", "true");
        given_a_toggle("cacheUpdateTest", "existingChange", "true");
        given_a_toggle("cacheUpdateTest", "existingRemoved", "true");

        HobknobClient hobknobClient = create_hobknob_client();

        given_a_toggle("cacheUpdateTest", "newToggle", "true");
        given_a_toggle("cacheUpdateTest", "existingChange", "false");
        given_a_toggle_is_removed("cacheUpdateTest", "existingRemoved");

        List<CacheUpdate> cacheUpdates = new ArrayList<>();

        hobknobClient.addCacheUpdatedLstener(cacheUpdates::addAll);

        // wait for client to update
        Thread.sleep(1200);

        assertNotNull(cacheUpdates);

        HashMap<String, CacheUpdate> cacheUpdateMap = new HashMap<>();
        for (CacheUpdate i : cacheUpdates) cacheUpdateMap.put(i.Key, i);

        assertThat(cacheUpdates.size(), equalTo(3));
        assertToggleUpdate(cacheUpdateMap, "existingChange", true, false);
        assertToggleUpdate(cacheUpdateMap, "existingRemoved", true, null);
        assertToggleUpdate(cacheUpdateMap, "newToggle", null, true);
    }

    private void assertToggleUpdate(HashMap<String, CacheUpdate> updates, String key, Boolean oldValue, Boolean newValue) throws Exception
    {
        CacheUpdate cacheUpdate = updates.get(key);
        assertThat(cacheUpdate.OldValue, equalTo(oldValue));
        assertThat(cacheUpdate.NewValue, equalTo(newValue));
    }
}


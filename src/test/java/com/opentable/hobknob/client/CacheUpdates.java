package com.opentable.hobknob.client;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class CacheUpdates extends TestBase
{
    @Before
    public void SetUp()
    {
        set_cache_update_interval(1000);
        set_application_name("app1");
    }

    @Test
    public void Cache_is_not_updated_when_update_interval_is_not_passed() throws Exception {
        given_a_toggle("app1", "toggle1", "true");
        boolean value = when_I_get("toggle1");
        assertThat(value, equalTo(true));

        given_a_toggle("app1", "toggle1", "false");

        boolean value2 = when_I_get_without_initialising_a_new_hobknob_instance("toggle1");
        assertThat(value2, equalTo(true));
    }

    @Test
    public void Cache_is_updated_when_update_interval_is_passed() throws Exception {
        given_a_toggle("app1", "toggle1", "true");
        boolean value = when_I_get("toggle1");
        assertThat(value, equalTo(true));

        given_a_toggle("app1", "toggle1", "false");
        Thread.sleep(1200);

        boolean value2 = when_I_get_without_initialising_a_new_hobknob_instance("toggle1");
        assertThat(value2, equalTo(false));
    }
}
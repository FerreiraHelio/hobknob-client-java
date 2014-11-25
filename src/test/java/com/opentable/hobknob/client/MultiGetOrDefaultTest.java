package com.opentable.hobknob.client;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class MultiGetOrDefaultTest extends TestBase
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void Init() {
        set_application_name("app1");
    }

    @Test
    public void Default_not_used_when_key_exists() throws Exception {
        given_a_toggle("app1", "multifeature1", "toggle1", "true");
        boolean value = when_I_get_with_default("multifeature1", "toggle1", false);
        assertThat(value, equalTo(true));
    }

    @Test
    public void Default_is_used_when_toggle_does_not_exist() throws Exception {
        boolean value = when_I_get_with_default("multifeature1", "toggle1", true);
        assertThat(value, equalTo(true));
    }

    @Test
    public void Default_is_used_when_tries_to_get_multi_feature_directory() throws Exception {
        given_a_toggle("app1", "multifeature1", "toggle1", "true");
        boolean value = when_I_get_with_default("multifeature1", true);
        assertThat(value, equalTo(true));
    }

    @Test
    public void Feature_toggles_in_different_applications_do_not_clash() throws Exception {
        given_a_toggle("app1", "multifeature1", "toggle1", "true");
        given_a_toggle("app2", "multifeature1", "toggle1", "false");
        boolean value = when_I_get_with_default("multifeature1", "toggle1", false);
        assertThat(value, equalTo(true));
    }

    @Test
    public void Feature_toggles_in_different_features_do_not_clash() throws Exception {
        given_a_toggle("app1", "multifeature1", "toggle1", "true");
        given_a_toggle("app1", "multifeature2", "toggle1", "false");
        boolean value = when_I_get_with_default("multifeature1", "toggle1", false);
        assertThat(value, equalTo(true));
    }

    @Test
    public void Bad_etcd_value_forces_default_to_be_returned() throws Exception {
        given_a_toggle("app1", "multifeature1", "toggle1", "bad");
        boolean value = when_I_get_with_default("multifeature1", "toggle1", true);
        assertThat(value, equalTo(true));
    }
}
package com.opentable.hobknob.client;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class MultiGetTest extends TestBase
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private String applicationName = "app1";

    @Before
    public void Init() {
        set_application_name(applicationName);
    }

    @Test
    public void Get_existing_toggle_with_true_value() throws Exception {
        given_a_toggle(applicationName, "multifeature1", "toggle1", "true");
        boolean value = when_I_get("multifeature1", "toggle1");
        assertThat(value, equalTo(true));
    }

    @Test
    public void Get_existing_toggle_with_false_value() throws Exception {
        given_a_toggle(applicationName, "multifeature1", "toggle1", "true");
        boolean value = when_I_get("multifeature1", "toggle1");
        assertThat(value, equalTo(true));
    }

    @Test
    public void Get_throws_exception_when_toggle_does_not_exist() throws Exception {
        exception.expect(Exception.class);
        when_I_get("multifeature1", "toggle2");
    }

    @Test
    public void Get_throws_exception_when_feature_does_not_exist() throws Exception {
        exception.expect(Exception.class);
        when_I_get("feature4", "toggle1");
    }

    @Test
    public void Feature_toggles_in_different_applications_do_not_clash() throws Exception {
        given_a_toggle(applicationName, "multifeature1", "toggle1", "true");
        given_a_toggle("app2", "multifeature1", "toggle1", "false");
        boolean value = when_I_get("multifeature1", "toggle1");
        assertThat(value, equalTo(true));
    }

    @Test
    public void Toggles_in_different_features_do_not_clash() throws Exception {
        given_a_toggle(applicationName, "multifeature1", "toggle1", "true");
        given_a_toggle(applicationName, "multifeature2", "toggle1", "false");
        boolean value = when_I_get("multifeature1", "toggle1");
        assertThat(value, equalTo(true));
    }

    @Test
    public void Bad_etcd_value_throws_exception() throws Exception {
        given_a_toggle("app1", "multifeature1", "toggle1", "bad");
        exception.expect(Exception.class);
        when_I_get("multifeature1", "toggle1");
    }
}
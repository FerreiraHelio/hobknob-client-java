package com.opentable.hobknob.client;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class SimpleGetTest extends TestBase
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private String applicationName = "app1";

    @Before
    public void Init() {
        set_application_name(applicationName);
    }

    @Test
    public void Get_existing_feature_with_true_value() throws Exception {
        given_a_toggle(applicationName, "simplefeature1", "true");
        boolean value = when_I_get("simplefeature1");
        assertThat(value, equalTo(true));
    }

    @Test
    public void Get_existing_feature_with_false_value() throws Exception {
        given_a_toggle(applicationName, "simplefeature1", "false");
        boolean value = when_I_get("simplefeature1");
        assertThat(value, equalTo(false));
    }

    @Test
    public void Get_throws_exception_when_feature_does_not_exist() throws Exception {
        exception.expect(Exception.class);
        when_I_get("feature2");
    }

    @Test
    public void Features_in_different_applications_do_not_clash() throws Exception {
        given_a_toggle(applicationName, "simplefeature1", "true");
        given_a_toggle("app2", "simplefeature1", "false");
        boolean value = when_I_get("simplefeature1");
        assertThat(value, equalTo(true));
    }

    @Test
    public void Bad_etcd_value_throws_exception() throws Exception {
        given_a_toggle(applicationName, "simplefeature1", "bad");
        exception.expect(Exception.class);
        when_I_get("simplefeature1");
    }
}
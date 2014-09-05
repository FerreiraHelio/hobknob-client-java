package com.opentable.hobknob.client;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class GetTest extends TestBase
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init() {
        set_application_name("app1");
    }

    @Test
    public void simple_get_true() throws Exception {
        given_a_toggle("app1", "toggle1", "true");
        boolean value = when_I_get("toggle1");
        assertThat(value, equalTo(true));
    }

    @Test
    public void simple_get_false() throws Exception {
        given_a_toggle("app1", "toggle1", "false");
        boolean value = when_I_get("toggle1");
        assertThat(value, equalTo(false));
    }

    @Test
    public void Get_throws_exception_when_key_does_not_exist() throws Exception {
        exception.expect(Exception.class);
        when_I_get("toggle3");
    }

    @Test
    public void Applications_do_not_clash() throws Exception {
        given_a_toggle("app1", "toggle1", "true");
        given_a_toggle("app2", "toggle1", "false");
        boolean value = when_I_get("toggle1");
        assertThat(value, equalTo(true));
    }

    @Test
    public void Bad_etcd_value_throws_exception() throws Exception {
        given_a_toggle("app1", "toggle1", "bad");
        exception.expect(Exception.class);
        when_I_get("toggle1");
    }
}
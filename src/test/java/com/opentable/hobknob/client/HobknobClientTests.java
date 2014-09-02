package com.opentable.hobknob.client;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class HobknobClientTests
{
    @Test
    public void simple_get() throws Exception {

        HobknobClientFactory hobknobClientFactory = new HobknobClientFactory();
        HobknobClient hobknobClient = hobknobClientFactory.create("localhost", 4001, "app1", 60*1000);

        boolean toggle1 = hobknobClient.get("toggle1");
        assertThat(toggle1, equalTo(true));
    }
}

package com.scaledrone.lib;

import net.jodah.concurrentunit.Waiter;
import org.junit.Test;

public class BaseTest {

    final String channel = System.getenv("AUTHLESS_CHANNEL");

    @Test
    public void test() throws Exception {
        final Waiter waiter = new Waiter();
        final Scaledrone drone = new Scaledrone(channel);
        waiter.assertEquals(null, drone.getClientID());
        drone.connect(new Listener() {
            @Override
            public void onOpen() {
                waiter.assertNotNull(drone.getClientID());
                waiter.resume();
            }

            @Override
            public void onOpenFailure(Exception ex) {
                waiter.fail(ex.getMessage());
            }

            @Override
            public void onFailure(Exception ex) {
                waiter.fail(ex.getMessage());
            }

            @Override
            public void onClosed(String reason) {
                waiter.fail(reason);
            }
        });

        waiter.await(10000);
    }
}
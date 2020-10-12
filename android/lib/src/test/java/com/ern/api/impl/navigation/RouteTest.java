package com.ern.api.impl.navigation;

import android.os.Bundle;

import androidx.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
public class RouteTest {

    @Test
    public void testRoute() {
        Route route = new Route.Builder(new Bundle()).build();
        assertThat(route).isNotNull();
        assertThat(route.getArguments().size()).isEqualTo(0);
        assertThat(route.getActionId()).isEqualTo(0);
        assertThat(route.isCompleted()).isFalse();
    }

    @Test
    public void testRoutingNotifierSuccess() {
        RoutingNotifier notifier = new RoutingNotifier() {
            @Override
            public void routingComplete(@NonNull RoutingResult result) {
                assertThat(result.isComplete).isTrue();
                assertThat(result.message).isNull();
            }
        };
        Route route = new Route.Builder(new Bundle()).routingNotifier(notifier).build();
        route.setResult(true, null);
    }

    @Test
    public void testRoutingNotifierFailure() {
        final String FAILURE_MESSAGE = "some message";
        RoutingNotifier notifier = new RoutingNotifier() {
            @Override
            public void routingComplete(@NonNull RoutingResult result) {
                assertThat(result.isComplete).isFalse();
                assertThat(result.message).isEqualTo(FAILURE_MESSAGE);
            }
        };
        Route route = new Route.Builder(new Bundle()).routingNotifier(notifier).build();
        route.setResult(false, FAILURE_MESSAGE);
    }

    @Test
    public void testRoutingNotifierOnNullNotifier() {
        Route route = new Route.Builder(new Bundle()).routingNotifier(null).build();
        route.setResult(true, null);
        assertThat(route.isCompleted());
    }

    @Test
    public void testMultipleSetResult() {
        Route route = new Route.Builder(new Bundle()).routingNotifier(null).build();
        route.setResult(true, null);
        assertThat(route.isCompleted());
        boolean isCaught = false;
        try {
            route.setResult(true, null);
        } catch (IllegalStateException e) {
            isCaught = true;
        }
        assertThat(isCaught).isTrue();
    }
}

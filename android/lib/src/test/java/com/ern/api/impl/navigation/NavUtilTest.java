package com.ern.api.impl.navigation;

import android.os.Bundle;

import com.ernnavigationApi.ern.model.NavigationBar;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
public class NavUtilTest {

    @Test
    public void testGetPathReturnsNullForInValidBundle() {
        Bundle b = new Bundle();
        assertThat(NavUtils.getPath(b)).isNull();
        b.putString("someKey", "someValue");
        assertThat(NavUtils.getPath(b)).isNull();
        b.putString("path", "validPath");
        assertThat(NavUtils.getPath(b)).isEqualTo("validPath");
    }

    @Test
    public void testGetPayload() {
        Bundle b = new Bundle();
        assertThat(NavUtils.getPayload(b)).isNull();
        b.putString("jsonPayload", "badJson");
        assertThat(NavUtils.getPayload(b)).isNull();
        b.putString("jsonPayload", "{}");
        assertThat(NavUtils.getPayload(b)).isInstanceOf(JSONObject.class);
    }

    @Test
    public void testGetNavBar() {
        Bundle b = new Bundle();
        Bundle navBar = new NavigationBar.Builder("NavBar").build().toBundle();
        assertThat(NavUtils.getNavBar(b)).isNull();
        b.putString("navigationBar", "badJson");
        assertThat(NavUtils.getNavBar(b)).isNull();
        b.putString("navigationBar", "{}");
        assertThat(NavUtils.getNavBar(b)).isNull();
        b.putBundle("navigationBar", navBar);
        assertThat(NavUtils.getNavBar(b)).isInstanceOf(NavigationBar.class);
        navBar.remove("title"); //Remove required parameter
        assertThat(NavUtils.getNavBar(b)).isNull();
    }
}

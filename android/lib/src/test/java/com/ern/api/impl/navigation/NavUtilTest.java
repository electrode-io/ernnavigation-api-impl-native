package com.ern.api.impl.navigation;

import android.os.Bundle;

import com.ernnavigation.ern.model.NavigationBar;

import org.json.JSONException;
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

    @Test
    public void testMergeBundlesWithJsonPayloadEntry() {
        JSONObject oldJsonPayload = new JSONObject();
        try {
            oldJsonPayload.put("oldJsonKey1", "oldJsonKey1Value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Bundle oldBundle = new Bundle();
        oldBundle.putString("oldBundleKey1", "oldBundleKey1Value");
        oldBundle.putString(NavUtils.KEY_JSON_PAYLOAD, oldJsonPayload.toString());


        JSONObject newJsonPayload = new JSONObject();
        try {
            newJsonPayload.put("newJsonKey1", "newJsonKey1Value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Bundle newBundle = new Bundle();
        newBundle.putString("newBundleKey1", "newBundleKey1Value");
        newBundle.putString(NavUtils.KEY_JSON_PAYLOAD, newJsonPayload.toString());

        assertThat(oldBundle.containsKey("newBundleKey1")).isFalse();
        assertThat(NavUtils.getPayload(oldBundle)).isNotNull();
        assertThat(NavUtils.getPayload(oldBundle).has("newJsonKey1")).isFalse();
        assertThat(NavUtils.getPayload(oldBundle).has("oldJsonKey1")).isTrue();

        NavUtils.mergeBundleWithJsonPayloads(oldBundle, newBundle);

        assertThat(oldBundle.containsKey("newBundleKey1")).isTrue();
        assertThat(NavUtils.getPayload(oldBundle)).isNotNull();
        assertThat(NavUtils.getPayload(oldBundle).has("newJsonKey1")).isTrue();
        assertThat(NavUtils.getPayload(oldBundle).has("oldJsonKey1")).isTrue();
    }

    @Test
    public void testMergeBundlesWithOutJsonPayloadEntryInOld() {
        Bundle oldBundle = new Bundle();
        oldBundle.putString("oldBundleKey1", "oldBundleKey1Value");


        JSONObject newJsonPayload = new JSONObject();
        try {
            newJsonPayload.put("newJsonKey1", "newJsonKey1Value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Bundle newBundle = new Bundle();
        newBundle.putString("newBundleKey1", "newBundleKey1Value");
        newBundle.putString(NavUtils.KEY_JSON_PAYLOAD, newJsonPayload.toString());

        assertThat(oldBundle.containsKey("newBundleKey1")).isFalse();
        assertThat(NavUtils.getPayload(oldBundle)).isNull();

        NavUtils.mergeBundleWithJsonPayloads(oldBundle, newBundle);

        assertThat(oldBundle.containsKey("newBundleKey1")).isTrue();
        assertThat(NavUtils.getPayload(oldBundle)).isNotNull();
        assertThat(NavUtils.getPayload(oldBundle).has("newJsonKey1")).isTrue();
    }

    @Test
    public void testMergeBundlesWithoutJsonPayloadEntryInNew() {
        JSONObject oldJsonPayload = new JSONObject();
        try {
            oldJsonPayload.put("oldJsonKey1", "oldJsonKey1Value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Bundle oldBundle = new Bundle();
        oldBundle.putString("oldBundleKey1", "oldBundleKey1Value");
        oldBundle.putString(NavUtils.KEY_JSON_PAYLOAD, oldJsonPayload.toString());


        Bundle newBundle = new Bundle();
        newBundle.putString("newBundleKey1", "newBundleKey1Value");

        assertThat(oldBundle.containsKey("newBundleKey1")).isFalse();
        assertThat(NavUtils.getPayload(oldBundle)).isNotNull();
        assertThat(NavUtils.getPayload(oldBundle).has("newJsonKey1")).isFalse();
        assertThat(NavUtils.getPayload(oldBundle).has("oldJsonKey1")).isTrue();

        NavUtils.mergeBundleWithJsonPayloads(oldBundle, newBundle);

        assertThat(oldBundle.containsKey("newBundleKey1")).isTrue();
        assertThat(NavUtils.getPayload(oldBundle)).isNotNull();
        assertThat(NavUtils.getPayload(oldBundle).has("newJsonKey1")).isFalse();
        assertThat(NavUtils.getPayload(oldBundle).has("oldJsonKey1")).isTrue();
    }

    @Test
    public void testMergeBundlesWithoutJsonPayloadEntry() {
        Bundle oldBundle = new Bundle();
        oldBundle.putString("oldBundleKey1", "oldBundleKey1Value");

        Bundle newBundle = new Bundle();
        newBundle.putString("newBundleKey1", "newBundleKey1Value");

        assertThat(oldBundle.containsKey("newBundleKey1")).isFalse();
        assertThat(NavUtils.getPayload(oldBundle)).isNull();

        NavUtils.mergeBundleWithJsonPayloads(oldBundle, newBundle);

        assertThat(oldBundle.containsKey("newBundleKey1")).isTrue();
        assertThat(NavUtils.getPayload(oldBundle)).isNull();
    }

    @Test
    public void testMergeBundlesWithNullNewBundle() {
        Bundle oldBundle = new Bundle();
        oldBundle.putString("oldBundleKey1", "oldBundleKey1Value");

        NavUtils.mergeBundleWithJsonPayloads(oldBundle, null);

        assertThat(oldBundle.containsKey("oldBundleKey1")).isTrue();
        assertThat(NavUtils.getPayload(oldBundle)).isNull();
    }

    @Test
    public void testMergeBundlesWithInvalidOldPayload() {
        Bundle oldBundle = new Bundle();
        oldBundle.putString("oldBundleKey1", "oldBundleKey1Value");
        oldBundle.putString(NavUtils.KEY_JSON_PAYLOAD, "invalid json entry");


        JSONObject newJsonPayload = new JSONObject();
        try {
            newJsonPayload.put("newJsonKey1", "newJsonKey1Value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Bundle newBundle = new Bundle();
        newBundle.putString("newBundleKey1", "newBundleKey1Value");
        newBundle.putString(NavUtils.KEY_JSON_PAYLOAD, newJsonPayload.toString());

        assertThat(oldBundle.containsKey("newBundleKey1")).isFalse();
        assertThat(NavUtils.getPayload(oldBundle)).isNull();

        NavUtils.mergeBundleWithJsonPayloads(oldBundle, newBundle);

        assertThat(oldBundle.containsKey("newBundleKey1")).isTrue();
        assertThat(NavUtils.getPayload(oldBundle)).isNotNull();
        assertThat(NavUtils.getPayload(oldBundle).has("newJsonKey1")).isTrue();
    }

    @Test
    public void testMergeBundlesWithInvalidNewPayload() {
        JSONObject oldJsonPayload = new JSONObject();
        try {
            oldJsonPayload.put("oldJsonKey1", "oldJsonKey1Value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Bundle oldBundle = new Bundle();
        oldBundle.putString("oldBundleKey1", "oldBundleKey1Value");
        oldBundle.putString(NavUtils.KEY_JSON_PAYLOAD, oldJsonPayload.toString());

        Bundle newBundle = new Bundle();
        newBundle.putString("newBundleKey1", "newBundleKey1Value");
        newBundle.putString(NavUtils.KEY_JSON_PAYLOAD, "invalid new json entry");

        assertThat(oldBundle.containsKey("newBundleKey1")).isFalse();
        assertThat(NavUtils.getPayload(oldBundle)).isNotNull();
        assertThat(NavUtils.getPayload(oldBundle).has("oldJsonKey1")).isTrue();

        NavUtils.mergeBundleWithJsonPayloads(oldBundle, newBundle);

        assertThat(oldBundle.containsKey("newBundleKey1")).isTrue();
        assertThat(NavUtils.getPayload(oldBundle)).isNotNull();
        assertThat(NavUtils.getPayload(oldBundle).has("oldJsonKey1")).isTrue();
    }
}

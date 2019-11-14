package com.walmartlabs.moviesreloaded.demo.navmenuhandler;

import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ern.api.impl.core.LaunchConfig;
import com.ern.api.impl.navigation.ElectrodeBaseActivity;
import com.walmartlabs.moviesreloaded.R;

public class NavMenuActivity extends ElectrodeBaseActivity {
    @Override
    protected int mainLayout() {
        return R.layout.activity_default;
    }

    @NonNull
    @Override
    protected String getRootComponentName() {
        return "MoviesReloaded";
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @NonNull
    @Override
    protected Class<? extends Fragment> miniAppFragmentClass() {
        return MenuItemDemoFragment.class;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //This menu item id is set inside MenuItemDemoFragment
        if (item.getItemId() == R.id.refresh) {
            Toast.makeText(this, "Refresh handled inside activity", Toast.LENGTH_SHORT).show();
            //If you don't return true then the Fragment's onOptionsItemSelected() will be invoked
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected LaunchConfig createDefaultLaunchConfig() {
        LaunchConfig config =  super.createDefaultLaunchConfig();
        config.setForceUpEnabled(true);
        return config;
    }
}

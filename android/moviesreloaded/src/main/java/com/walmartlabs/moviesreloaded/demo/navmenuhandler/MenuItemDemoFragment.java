package com.walmartlabs.moviesreloaded.demo.navmenuhandler;

import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ern.api.impl.navigation.DefaultMenuItemProperties;
import com.ern.api.impl.navigation.ElectrodeNavigationFragmentDelegate;
import com.ern.api.impl.navigation.MenuItemDataProvider;
import com.ern.api.impl.navigation.MenuItemProperties;
import com.ern.api.impl.navigation.MiniAppNavigationFragment;
import com.ern.api.impl.navigation.OnNavBarItemClickListener;
import com.ernnavigationApi.ern.model.NavigationBarButton;
import com.walmartlabs.moviesreloaded.R;

public class MenuItemDemoFragment extends MiniAppNavigationFragment implements OnNavBarItemClickListener {
    @NonNull
    @Override
    protected ElectrodeNavigationFragmentDelegate createFragmentDelegate() {
        ElectrodeNavigationFragmentDelegate delegate = super.createFragmentDelegate();
        // Setting a menu item property is useful when:
        // 1. A React native component wants to reuse the icon that is already available inside the native app. Helps with size reduction.
        // 2. Allow native to provide an @IdRes for the menuItem inorder to handle the menu item click inside Activitie's or Fragment's onOptionsItemSelected() method.
        delegate.setMenuItemDataProvider(new MenuItemDataProvider() {
            @Nullable
            @Override
            public MenuItemProperties menuItemPropertiesFor(@NonNull NavigationBarButton navigationBarButton) {
                if ("MoviesReloaded.refresh".equals(navigationBarButton.getId())) {
                    return new DefaultMenuItemProperties.Builder()
                            .icon(R.drawable.ic_refresh_24px)
                            .itemId(R.id.refresh)
                            .handleClickInActivity(true) //Set this to true when you want to handle the click action on the native side.
                            .build();
                }
                return null;
            }
        });
        return delegate;
    }
    
    // OnNavBarItemClickListener is another way of handling the navigation menu item click inside the Fragment.
    // Return true if you handle click for an item click. Returning false will ensure that the click is delegated back to react native component for handling.
    @Override
    public boolean onNavBarButtonClicked(@NonNull NavigationBarButton button, @NonNull MenuItem item) {
        if ("MoviesReloaded.about".equals(button.getId())) {
            Toast.makeText(getActivity(), "Clicked about button", Toast.LENGTH_SHORT).show();
            //Returning false here will send an OnButtonClicked event to the react native component as well.
            //Return true when you don't want RN to control this button click.
            return false;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // This menu item id is set inside MenuItemDemoFragment
        if (item.getItemId() == R.id.refresh) {
            Toast.makeText(this.getActivity(), "Refresh handled inside fragment", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

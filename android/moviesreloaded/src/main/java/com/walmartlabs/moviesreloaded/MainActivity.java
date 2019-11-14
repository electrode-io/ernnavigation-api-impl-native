package com.walmartlabs.moviesreloaded;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.walmartlabs.moviesreloaded.demo.bottomsheet.modal.ModalBottomSheetActivity;
import com.walmartlabs.moviesreloaded.demo.bottomsheet.persistent.PersistentBottomSheetActivity;
import com.walmartlabs.moviesreloaded.demo.customview.CustomActivity;
import com.walmartlabs.moviesreloaded.demo.defaultbehavior.DefaultActivity;
import com.walmartlabs.moviesreloaded.demo.navmenuhandler.NavMenuActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(this.getString(R.string.launcher_title));
    }


    public void handleButtonClick(View view) {
        Class<? extends Activity> clazz;
        switch (view.getId()) {
            case R.id.id_button_custom:
                clazz = CustomActivity.class;
                break;
            case R.id.id_button_navbar:
                clazz = NavMenuActivity.class;
                break;
            case R.id.id_button_bottomsheet:
                clazz = ModalBottomSheetActivity.class;
                break;
            case R.id.id_button_persistent_bottomsheet:
                clazz = PersistentBottomSheetActivity.class;
                break;
            default:
                clazz = DefaultActivity.class;
                break;
        }
        startActivity(new Intent(this, clazz));
    }
}

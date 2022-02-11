package com.mroxny.mymovie;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class SideMenuManager implements NavigationView.OnNavigationItemSelectedListener{

    private final Context context;
    private final DrawerLayout drawer;
    private final NavigationView navigationView;
    private final Button toggle;

    public SideMenuManager(Context context, DrawerLayout drawer, NavigationView navigationView, Button toggle){
        this.context = context;
        this.drawer = drawer;
        this.navigationView = navigationView;
        this.toggle = toggle;
    }

    public void setSideMenu(){

        navigationView.setNavigationItemSelectedListener(this);

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawer.isDrawerOpen(GravityCompat.START)) drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    public boolean closeSideMenu(){
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        return drawer.isDrawerOpen(GravityCompat.START);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_account:
                goToActivity(AccountActivity.class);
                break;
            case R.id.nav_addMovie:
                goToActivity(AddMovieActivity.class);
                break;
            case R.id.nav_logout:
                showAlertDialog();
                break;
        }
        return true;
    }

    private void goToActivity(Class c){
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }

    private void logOut(){
        PreferencesManager.clearData(PreferencesManager.LOGGED_USER_ID, context);
        PreferencesManager.clearData(PreferencesManager.LOGGED_USER_NAME, context);
        PreferencesManager.clearData(PreferencesManager.LOGGED_USER_PASSWORD, context);

        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    private void showAlertDialog(){
        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.tag_logout))
                .setMessage(context.getResources().getString(R.string.info_log_out))

                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                    }
                })

                .setNegativeButton(R.string.button_no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}

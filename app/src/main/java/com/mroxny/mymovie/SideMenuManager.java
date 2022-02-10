package com.mroxny.mymovie;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

    private Context context;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FloatingActionButton toggle;

    public SideMenuManager(Context context, DrawerLayout drawer, NavigationView navigationView, FloatingActionButton toggle){
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
                logOut();
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

}

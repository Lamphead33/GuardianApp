package com.example.finalproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

        public static final String SEARCH_QUERY = "SEARCH";
        public String searchQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setListeners();




        //create toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setActionBar(myToolbar);

        //create NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Button searchButton = (Button)findViewById(R.id.searchButton);
        EditText searchText = findViewById(R.id.searchField);

        // Sets searchText to saved query from SharedPrefs
        SharedPreferences sp = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEdit = sp.edit();
        searchQuery = sp.getString("SEARCH", "");
        searchText.setText(searchQuery);



            searchButton.setOnClickListener( (click) -> {
            Intent i = new Intent(getApplicationContext(), ResultsActivity.class);

            // Grabs text from search box
            searchQuery = searchText.getText().toString();
            // Creates bundle and adds search text to it
            Bundle dataToPass = new Bundle();
            dataToPass.putString(SEARCH_QUERY, searchQuery);
            // Passes bundle to ResultsActivity
            i.putExtras(dataToPass);

            startActivity(i);

        });
    }

    // Saves search query to SharedPrefs
    protected void onPause() {
        super.onPause();
        SharedPreferences sp = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEdit = sp.edit();
        spEdit.putString("SEARCH", searchQuery);
        spEdit.commit();

    }

    //create click listener to change to search activity
    private void setListeners() {

        findViewById(R.id.goHome)
                .setOnClickListener(v ->
                        startActivity(
                                new Intent(this, MainActivity.class)
                        )
                );
    }


    private void setActionBar(Toolbar myToolbar) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int message = 0;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {

            case R.id.search:
                message = R.string.goToSearch;
                startActivity(
                        new Intent(this, SearchActivity.class)
                );
                break;
            case R.id.favorites:
                message = R.string.goToFavorites;
                startActivity(
                        new Intent(this, FavoritesActivity.class)
                );
                break;
            case R.id.article:
                message = R.string.goToLastArticle;
                startActivity(
                        new Intent(this, FavoritesActivity.class)
                );
                break;
            case R.id.help:
                message = R.string.clickHelp;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.help)
                        .setMessage(R.string.searchHelp)
                        .setNegativeButton(R.string.back, (click, arg) ->{
                        } )
                        .create().show();
                break;
            case R.id.home:
                message = R.string.goToHome;
                startActivity(
                        new Intent(this, MainActivity.class)
                );
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        return true;
    }
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        int message = 0;
        switch(item.getItemId())
        {
            case R.id.search:
                message = R.string.goToSearch;
                startActivity(
                        new Intent(this, SearchActivity.class)
                );
                break;
            case R.id.favorites:
                message = R.string.goToFavorites;
                startActivity(
                        new Intent(this, FavoritesActivity.class)
                );
                break;
            case R.id.article:
                message = R.string.goToLastArticle;
                startActivity(
                        new Intent(this, FavoritesActivity.class)
                );
                break;
            case R.id.help:
                message = R.string.clickHelp;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.help)
                        .setMessage(R.string.searchHelp)
                        .setNegativeButton(R.string.back, (click, arg) ->{
                        } )
                        .create().show();
                break;
            case R.id.home:
                message = R.string.goToHome;
                startActivity(
                        new Intent(this, MainActivity.class)
                );
                break;

        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);


        return false;
    }

}
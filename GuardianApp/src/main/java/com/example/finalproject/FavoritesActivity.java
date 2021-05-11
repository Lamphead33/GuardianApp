package com.example.finalproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private ArrayList<Article> articles = new ArrayList<>();
    SQLiteDatabase db;
    MyListAdapter adapter = new MyListAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
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

        // Populates saved article list from database
        populateFavourites();
        ListView favList = findViewById(R.id.favList);
        favList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Click to open articles
        favList.setOnItemClickListener((list, item, pos, id) -> {
            String url = articles.get(pos).getUrl();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        // Long click - display details, add to favourites
        favList.setOnItemLongClickListener( (p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(articles.get(pos).getTitle())
                    .setMessage("URL: " + articles.get(pos).getUrl())

                    // Add selected article to favourites --- needs translation
                    .setPositiveButton("DELETE FROM FAVOURITES", (click, arg) -> {
                        deleteArticle(articles.get(pos));
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(R.string.back, (click, arg) ->{

                    } )
                    .create().show();
            return true;
        });




    }


    // Populate favourites list with articles from DB
    public void populateFavourites(){
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();

        String[] columns = {MyOpener.COL_ID, MyOpener.COL_TITLE, MyOpener.COL_SECTION, MyOpener.COL_URL};
        Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int idIndex = results.getColumnIndex(MyOpener.COL_ID);
        int titleIndex = results.getColumnIndex(MyOpener.COL_TITLE);
        int sectionIndex = results.getColumnIndex(MyOpener.COL_SECTION);
        int urlIndex = results.getColumnIndex(MyOpener.COL_URL);

        while(results.moveToNext()){
            String favTitle = results.getString(titleIndex);
            String favSection = results.getString(sectionIndex);
            String favUrl = results.getString(urlIndex);
            articles.add(new Article(favTitle, favUrl, favSection));
        }

    }

    public void deleteArticle(Article a){
        db.delete(MyOpener.TABLE_NAME, MyOpener.COL_TITLE + "= ?", new String[] {a.getTitle()});
        articleDeletedToast();
    }

    public void articleDeletedToast(){
        String message = "Article deleted from favourites";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private class MyListAdapter extends BaseAdapter {
        public MyListAdapter(FavoritesActivity favoritesActivity) {
        }

        @Override
        public int getCount() {
            return articles.size();
        }

        @Override
        public Object getItem(int position) {
            return articles.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();


            View resultDisplay = inflater.inflate(R.layout.result, null);
            TextView displayTitle = resultDisplay.findViewById(R.id.resultTitle);
            TextView displaySection = resultDisplay.findViewById(R.id.resultSection);
            TextView displayUrl = resultDisplay.findViewById(R.id.resultUrl);
            displayTitle.setTypeface(displayTitle.getTypeface(), Typeface.BOLD);
            displaySection.setTypeface(displaySection.getTypeface(), Typeface.ITALIC);


            displayTitle.setText(articles.get(position).getTitle());
            displaySection.setText("Section: " + articles.get(position).getSection());
            displayUrl.setText("URL: " + articles.get(position).getUrl());

            return resultDisplay;

        }

        @Override
        public long getItemId(int position) {
            return position;
        }
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

        //code for toolbar
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
                        .setMessage(R.string.favoritesHelp)
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
            //code for navigation drawer
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
                        .setMessage(R.string.favoritesHelp)
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
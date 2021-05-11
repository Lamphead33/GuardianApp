package com.example.finalproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.solver.state.HelperReference;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Bundle dataFromActivity;
    public TextView theText;
    private String searchText;
    private ArrayList<Article> articles = new ArrayList<>();
    MyListAdapter adapter = new MyListAdapter(this);
    SQLiteDatabase db;
    ProgressBar progressBar;
    LinearLayout resultsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        resultsLayout = findViewById(R.id.resultsLayout);



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

        // Initialize progressBar
        progressBar = findViewById(R.id.resultsProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        // Initiate list and set adapter
        ListView resultList = findViewById(R.id.resultList);
        resultList.setAdapter(adapter);

        // Pull search query from SearchActivity
        Intent in = getIntent();
        dataFromActivity = in.getExtras();
        searchText = dataFromActivity.getString(SearchActivity.SEARCH_QUERY);

        //creates the snackbar. tells the user "Searching for content including: " plus what he searched for.
        Snackbar.make(resultsLayout, (getText(R.string.searchForContentInc) + " ") + searchText , Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                                startActivity(
                                        new Intent(ResultsActivity.this, MainActivity.class)

                        );
                    }
                }).show();

        // Hold query against results
        SearchResults search = new SearchResults();
        search.execute("https://content.guardianapis.com/search?api-key=1fb36b70-1588-4259-b703-2570ea1fac6a&q=" + searchText);
        adapter.notifyDataSetChanged();

        // Listener to open articles on click
        resultList.setOnItemClickListener((list, item, pos, id) -> {
            String url = articles.get(pos).getUrl();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

            });

        // Long click - display details, add to favourites
        resultList.setOnItemLongClickListener( (p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(articles.get(pos).getTitle())
                    .setMessage("URL: " + articles.get(pos).getUrl())

                    // Add selected article to favourites
                    .setPositiveButton(R.string.addFavorites, (click, arg) -> {
                        addArticleToDB(articles.get(pos));



                    })
                    .setNegativeButton(R.string.back, (click, arg) ->{

                    } )
                    .create().show();
            return true;
        });




        // Following block was used to test that search query was being passed properly
        /*
        Intent in = getIntent();
        dataFromActivity = in.getExtras();
        searchText = dataFromActivity.getString(SearchActivity.SEARCH_QUERY);

        if (searchText == null){
            searchText = "It didn't work lol";
        }

        theText = findViewById(R.id.testText);
        theText.setText(searchText);
         */


    }

    private class SearchResults extends AsyncTask< String, Integer, String>
    {
        public String doInBackground(String ... args)
        {
            try {

                //create a URL object of what server to contact:
                URL url = new URL(args[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();


                //JSON reading:
                //Build the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String jsonString = sb.toString(); //result is the whole string

                // Used following to verify in console that json was printed properly
                // System.out.println(jsonString);


                // convert string to JSON and pull array:
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject jsonResponse = jsonObject.getJSONObject("response");
                JSONArray jArray = jsonResponse.getJSONArray("results");


                // loop through results and create Article objects
                for (int i = 0; i < jArray.length(); i++){
                    // pull data from JSON results
                    JSONObject object = jArray.getJSONObject(i);
                    String t = object.getString("webTitle");
                    String u = object.getString("webUrl");
                    String s = object.getString("sectionName");

                    // Used following to verify results in console
                    System.out.println(t);

                    // create new Article object using data, and add to articles ArrayList
                    Article a = new Article(t, u, s);
                    articles.add(a);
                    publishProgress(((i+1) * 10));

                }


            }
            catch(UnsupportedEncodingException e){
                System.out.println(R.string.UEE);
            }
            catch(IOException e){
                System.out.println(R.string.IOE);
            }
            catch(JSONException e){
                System.out.println(R.string.JSE);
            }

            return String.valueOf(R.string.done);
        }

        //TO DO - add progress bar
        public void onProgressUpdate(Integer ... value){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }
        // populate Listview here
        public void onPostExecute(String fromDoInBackground)
        {
            // Display toast if no results are found
            if (articles.isEmpty()){
                noResultsToast();
            }

            progressBar.setVisibility(View.INVISIBLE);
            adapter.notifyDataSetChanged();




            Log.i("HTTP", fromDoInBackground);
        }
    }




    private class MyListAdapter extends BaseAdapter {
        public MyListAdapter(ResultsActivity resultsActivity) {
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



    // ADD ARTICLE TO FAVOURITES
    public void addArticleToDB(Article a){
        MyOpener opener = new MyOpener(this);
        db = opener.getWritableDatabase();
        ContentValues row = new ContentValues();
        row.put(MyOpener.COL_TITLE, a.getTitle());
        row.put(MyOpener.COL_SECTION, a.getSection());
        row.put(MyOpener.COL_URL, a.getUrl());
        long newID = db.insert(MyOpener.TABLE_NAME, null, row);
        addedToFavouritesToast();

    }







    //create click listener to change to results activity
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

    public void noResultsToast(){
        String message = "No results found";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    public void addedToFavouritesToast(){
        int message = R.string.favsToast;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                        .setMessage(R.string.resultsHelp)
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
                        .setMessage(R.string.resultsHelp)
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
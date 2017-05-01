package com.example.meetdave.myapplication;

import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends headerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  String flag;
        setHeader(R.id.main_activity_layoutID);
        Button b=(Button)findViewById(R.id.homeButton);
        b.setEnabled(false);
        getNewlyArrivedMovies();
        SearchView searchView=(SearchView)findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        ListView movieListView=(ListView)findViewById(R.id.movieListView);
        movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieListModel model = (MovieListModel) parent.getItemAtPosition(position);
                String movie_id = null;
                movie_id = model.getMovieID();
                Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
                intent.putExtra("movie_id", movie_id);
                startActivity(intent);
            }
        });
    }


    public void search(String query){
        if(query.equals("")){
            return;
        }
        TextView t=(TextView)findViewById(R.id.textView);
        t.setText("Search Results");
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Searching..");
        progressDialog.show();
        AsyncHttpClient client=new AsyncHttpClient();
        RequestParams params=new RequestParams();
        params.put("query",query);
        String url="http://"+Connection.host+":"+Connection.port+"/onlinemoviestore/movies/search";
        client.get(url, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                try {
                    progressDialog.setMessage("retrieveing list..");
                    ArrayList<MovieListModel> arrayList = new ArrayList<MovieListModel>();
                    JSONArray array = new JSONArray(response);
                    if(array.length()==0){
                        ((TextView)findViewById(R.id.textView)).setText("No Result Found");

                    }
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        MovieListModel model = new MovieListModel(obj.getString("movie_id"),obj.getString("movie_name"), "Rs "+obj.getString("movie_price"), obj.getString("movie_image_url"));
                        arrayList.add(model);


                    }
                    progressDialog.hide();
                    MovieListAdapter adapter = new MovieListAdapter(arrayList, getBaseContext());

                    setListView(adapter);
                } catch (Exception exception) {
                }
            }

            public void onFailure(int statusCode, Throwable error, String content) {
                progressDialog.hide();
                AlertDialog.Builder alBuilder=new AlertDialog.Builder(MainActivity.this);
                alBuilder.setMessage(error.getMessage());
                alBuilder.setTitle("Message");
                alBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                alBuilder.create().show();
            }
        });
    }


    public void getNewlyArrivedMovies(){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        AsyncHttpClient client=new AsyncHttpClient();
        String url="http://"+Connection.host+":"+Connection.port+"/onlinemoviestore/movies/newlyarrived";
        client.get(url, null, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                try {
                    ArrayList<MovieListModel> arrayList = new ArrayList<MovieListModel>();
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        MovieListModel model = new MovieListModel(obj.getString("movie_id"),obj.getString("movie_name"), "Rs "+obj.getString("movie_price"), obj.getString("movie_image_url"));
                        arrayList.add(model);
                        progressDialog.hide();
                    }

                    MovieListAdapter adapter = new MovieListAdapter(arrayList, getBaseContext());
                    setListView(adapter);
                } catch (Exception exception) {

                }
            }

            public void onFailure(int statusCode, Throwable error, String content) {
                progressDialog.hide();
                AlertDialog.Builder alBuilder=new AlertDialog.Builder(MainActivity.this);
                alBuilder.setMessage(error.getMessage());
                alBuilder.setTitle("Message");
                alBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                alBuilder.create().show();
            }
        });
    }


    public void setListView(MovieListAdapter adapter){
        ListView view=(ListView)findViewById(R.id.movieListView);
        view.setAdapter(adapter);


    }
}

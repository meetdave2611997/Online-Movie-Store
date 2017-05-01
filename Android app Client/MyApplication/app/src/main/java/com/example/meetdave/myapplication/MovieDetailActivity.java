package com.example.meetdave.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieDetailActivity extends AppCompatActivity {
    JSONObject cartObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        getMovieDetails();
    }

    public void getMovieDetails(){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("Getting Movie Details...");
        progressDialog.show();
        AsyncHttpClient client=new AsyncHttpClient();
        RequestParams params=new RequestParams();
        params.put("movie_id", getIntent().getStringExtra("movie_id"));
        String url="http://"+Connection.host+":"+Connection.port+"/onlinemoviestore/movies/moviedetails";
        client.get(url, params, new AsyncHttpResponseHandler() {
            public void onSuccess(String response) {
                try {
                    Context context = getBaseContext();
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.activity_movie_detail, null);

                    JSONObject obj = new JSONObject(response);
                    cartObject = new JSONObject(response);
                    if (obj.getString("movie_stock").equals("0")) {
                        ((Button) findViewById(R.id.addtocartButton)).setVisibility(View.INVISIBLE);
                        ((Button) findViewById(R.id.buyButton)).setVisibility(View.INVISIBLE);
                        ((TextView) findViewById(R.id.outofstockLabel)).setVisibility(View.VISIBLE);
                    } else {
                        ((Button) findViewById(R.id.addtocartButton)).setVisibility(View.VISIBLE);
                        ((Button) findViewById(R.id.buyButton)).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.outofstockLabel)).setVisibility(View.INVISIBLE);
                    }
                    ((TextView) findViewById(R.id.movieNameLabel)).setText(obj.getString("movie_name"));
                    ((TextView) findViewById(R.id.categoryLabel)).setText("Category: " + obj.getString("movie_category"));
                    ((TextView) findViewById(R.id.priceLabel)).setText("Price: " + obj.getString("movie_price"));
                    ((TextView) findViewById(R.id.tableMovieName)).setText(obj.getString("movie_name"));
                    ((TextView) findViewById(R.id.tableDirectorName)).setText(obj.getString("movie_director"));
                    ((TextView) findViewById(R.id.tableMovieDescription)).setText(obj.getString("movie_description"));
                    ((TextView) findViewById(R.id.tableMovieCategory)).setText(obj.getString("movie_category"));
                    ((TextView) findViewById(R.id.tableMovieLanguage)).setText(obj.getString("movie_language"));
                    ((TextView) findViewById(R.id.tableMovieReleaseYear)).setText(obj.getString("movie_releaseyear"));
                    ((TextView) findViewById(R.id.tableMovieFormat)).setText(obj.getString("movie_format"));
                    ((TextView) findViewById(R.id.tableMovieStarCast)).setText(obj.getString("movie_starcast"));
                    ((TextView) findViewById(R.id.tableMoviePrice)).setText(obj.getString("movie_price"));
                    setCartLabel();
                    ImageView image = (ImageView) findViewById(R.id.movieImage);
                    new DownloadImageTask(image).execute(obj.getString("movie_image"));
                    progressDialog.hide();
                } catch (Exception exception) {

                }
            }

            public void onFailure(int statusCode, Throwable error, String content) {
                progressDialog.hide();
                AlertDialog.Builder alBuilder=new AlertDialog.Builder(MovieDetailActivity.this);
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

    public void setCartLabel(){
        try {
            Button b=(Button)findViewById(R.id.addtocartButton);
            SharedPreferences sharedPreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
            String cartJson = sharedPreferences.getString("myCart", null);
            JSONArray array = new JSONArray(cartJson);
            for(int i=0;i<array.length();i++){
                JSONObject tempObject=array.getJSONObject(i);
                if(cartObject.getString("movie_id").equals(tempObject.getString("movie_id"))){
                    b.setText("Remove from Cart");
                    return;
                }
            }
            b.setText("Add to Cart");
        }catch (Exception ex){

        }
    }

    public void buyButtonClick(View v){
        String cartJson;
        int flag=0;
        SharedPreferences sharedPreferences=getSharedPreferences("MyPREFERENCES",Context.MODE_PRIVATE);
        if(sharedPreferences.contains("myCart")){
            try {
                cartJson = sharedPreferences.getString("myCart", null);
                JSONArray array = new JSONArray(cartJson);
                for(int i=0;i<array.length();i++){
                    JSONObject tempObject=array.getJSONObject(i);
                    if(tempObject.getString("movie_id").equals(cartObject.getString("movie_id"))){
                        flag=1;
                        break;
                    }
                }

                if(flag==0){
                    array.put(cartObject);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("myCart",array.toString());
                    editor.commit();
                }
            }catch (Exception ex){

            }
        }else{
            try{
                JSONArray array=new JSONArray();
                array.put(cartObject);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("myCart",array.toString());
                editor.commit();
            }catch (Exception ex){

            }
        }
        setCartLabel();
        Intent intent=new Intent(this,CartActivity.class);
        startActivity(intent);
    }

    public void addToCartButtonClick(View v){
        String cartJson;
        Button b=(Button)findViewById(R.id.addtocartButton);
        if(b.getText().equals("Add to Cart")){
            SharedPreferences sharedPreferences=getSharedPreferences("MyPREFERENCES",Context.MODE_PRIVATE);
            if(sharedPreferences.contains("myCart")){
                try {
                    cartJson = sharedPreferences.getString("myCart", null);
                    JSONArray array = new JSONArray(cartJson);
                    array.put(cartObject);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("myCart",array.toString());
                    editor.commit();
                }catch (Exception ex){

                }
            }else{
                try{
                    JSONArray array=new JSONArray();
                    array.put(cartObject);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("myCart",array.toString());
                    editor.commit();
                }catch (Exception ex){

                }
            }
        }else{
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
                cartJson = sharedPreferences.getString("myCart", null);
                JSONArray array = new JSONArray(cartJson);
                for(int i=0;i<array.length();i++){
                    JSONObject tempObject=array.getJSONObject(i);
                    if(cartObject.getString("movie_id").equals(tempObject.getString("movie_id"))){
                        array.remove(i);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("myCart", array.toString());
                        editor.commit();
                        break;
                    }
                }
            }catch (Exception ex){

            }
        }
        setCartLabel();
    }

    public class DownloadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;
        protected DownloadImageTask(ImageView imageView){
            this.imageView=imageView;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap  doInBackground(String... url) {
            String urlDisplay=url[0];
            Bitmap bmp=null;
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(urlDisplay).openConnection();
                InputStream is=httpURLConnection.getInputStream();
                bmp= BitmapFactory.decodeStream(is);
            }catch(Exception exception) {

            }
            return bmp;
        }
    }
}

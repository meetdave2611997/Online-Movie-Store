package com.example.meetdave.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Meet Dave on 05-03-2017.
 */
public class MovieListAdapter extends ArrayAdapter<MovieListModel> {
    ArrayList<MovieListModel> data;
    Context context;
    public MovieListAdapter(ArrayList<MovieListModel> data,Context context){
        super(context,R.layout.movie_list_row,data);
        this.data=data;
        this.context=context;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.movie_list_row, null);
        try {
            MovieListModel model = getItem(position);
            TextView movieID=(TextView)row.findViewById(R.id.movieID);
            TextView movieName = (TextView) row.findViewById(R.id.movieName);
            TextView moviePrice = (TextView) row.findViewById(R.id.moviePrice);
            ImageView movieImage = (ImageView) row.findViewById(R.id.movieImage);
            movieName.setText(model.getMovieName());
            moviePrice.setText(model.getMoviePrice());
            movieID.setText(model.getMovieID());
            new DownloadImageTask(movieImage).execute(model.getMovieImage());
        }catch (Exception exception){
           System.out.print(exception.getMessage());
        }
        return row;
    }

    public class DownloadImageTask extends AsyncTask<String,Void,Bitmap>{
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
                bmp=BitmapFactory.decodeStream(is);
            }catch(Exception exception) {

            }
            return bmp;
            }
    }
}

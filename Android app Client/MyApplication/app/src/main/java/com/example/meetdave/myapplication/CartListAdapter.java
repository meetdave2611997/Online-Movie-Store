package com.example.meetdave.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.zip.Inflater;

/**
 * Created by Meet Dave on 11-03-2017.
 */
public class CartListAdapter extends ArrayAdapter<MovieListModel> {
    Context context;
    ArrayList<MovieListModel> data;
    int totalAmount=0;

    public CartListAdapter(ArrayList<MovieListModel> data,Context context){
       super(context,R.layout.cart_list_row,data);
        this.data=data;
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.cart_list_row, null);
        try{
            final MovieListModel model=getItem(position);
            TextView movieName=(TextView)row.findViewById(R.id.cartMovieName);
            final TextView moviePrice=(TextView)row.findViewById(R.id.cartMoviePrice);
            Spinner movieQty=(Spinner)row.findViewById(R.id.cartMovieQty);
            updateSpinnerValues(model,movieQty);
            movieQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    totalAmount=0;
                    int price= (int) ((id+1)*Integer.parseInt(model.getMoviePrice()));
                    moviePrice.setText("Rs: " + String.valueOf(price));
                 //   model.setMovieQty(String.valueOf(id + 1));
                    updateTotalAmount(id+1,model);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            Button b=(Button)row.findViewById(R.id.removefromcartButton);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int i=0;i<data.size();i++){
                        MovieListModel temp=data.get(i);
                        if(temp.getMovieID().equals(model.getMovieID())){
                            data.remove(i);
                            updateArray(model.getMovieID());
                            if(data.size()==0){
                                returnToMainActivity();
                            }
                            notifyDataSetChanged();
                            break;
                        }
                    }
                }
            });
            ImageView movieImage=(ImageView)row.findViewById(R.id.cartMovieImage);
            movieName.setText(model.getMovieName());
            new DownloadImageTask(movieImage).execute(model.getMovieImage());
        }catch (Exception ex){

        }


        return row;
    }

    public void updateTotalAmount(long qty,MovieListModel model){
        try {

            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
            JSONArray array = new JSONArray(sharedPreferences.getString("myCart", null));
            for(int i=0;i<array.length();i++){
                JSONObject tempObject=array.getJSONObject(i);
                if(tempObject.getString("movie_id").equals(model.getMovieID())){
                    tempObject.put("movie_qty", String.valueOf(qty));
                    JSONObject object=new JSONObject(tempObject.toString());
                    array.remove(i);
                    array.put(object);
                }
            }

            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("myCart",array.toString());
            editor.commit();

            for (int i=0;i<array.length();i++){
                JSONObject tempObject=array.getJSONObject(i);
                if(tempObject.has("movie_qty")){
                    totalAmount=totalAmount+Integer.parseInt(tempObject.getString("movie_price"))*Integer.parseInt(tempObject.getString("movie_qty"));
                }else{
                    totalAmount=totalAmount+Integer.parseInt(tempObject.getString("movie_price"));
                }
            }

        }catch (Exception ex){
            AlertDialog.Builder alBuilder=new AlertDialog.Builder(context);
            alBuilder.setMessage(ex.getMessage());
            alBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alBuilder.create().show();
        }
    }

    public void updateSpinnerValues(MovieListModel model,Spinner spinner){
        try{
            SharedPreferences sharedPreferences=context.getSharedPreferences("MyPREFERENCES",Context.MODE_PRIVATE);
            JSONArray array=new JSONArray(sharedPreferences.getString("myCart",null));
            ArrayList<String> movieQty=new ArrayList<String>();
            for (int i=0;i<array.length();i++){
                JSONObject tempObject=array.getJSONObject(i);
                if(model.getMovieID().equals(tempObject.getString("movie_id"))){
                    int stock=Integer.parseInt(tempObject.getString("movie_stock"));
                        if(stock < 5){
                            for (int j=1;j<=stock;j++){
                                movieQty.add(String.valueOf(j));
                            }
                        }else{
                            for(int j=1;j<=5;j++){
                                movieQty.add(String.valueOf(j));
                            }
                        }
                    ArrayAdapter<String> spinneradapter=new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,movieQty);
                    spinner.setAdapter(spinneradapter);
                    break;
                    }
                }

        }catch (Exception ex){

        }
    }

    public  void returnToMainActivity(){
        AlertDialog.Builder alBuilder=new AlertDialog.Builder(context);
        alBuilder.setTitle("Message");
        alBuilder.setMessage("No items in your cart");
        alBuilder.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((CartActivity) context).finish();
            }
        });
        alBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ((CartActivity) context).finish();
            }
        });
        alBuilder.create().show();
    }

    public void updateArray(String movieID){
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
            String cartJson = sharedPreferences.getString("myCart", null);
            JSONArray array = new JSONArray(cartJson);
            for(int i=0;i<array.length();i++){
                JSONObject tempObj=array.getJSONObject(i);
                if(tempObj.getString("movie_id").equals(movieID)){
                    array.remove(i);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("myCart", array.toString());
                    editor.commit();
                    break;
                }
            }
        }catch (Exception e){
            System.out.print(e.getMessage());
        }

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

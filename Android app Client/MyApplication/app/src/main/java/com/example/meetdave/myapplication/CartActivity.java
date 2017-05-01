package com.example.meetdave.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Gettting cart details...");
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        updateCartArray();

    }

    public void updateCartArray(){
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
            String cartJson = sharedPreferences.getString("myCart", null);
            sharedPreferences=null;
            AsyncHttpClient client=new AsyncHttpClient();
            RequestParams params=new RequestParams();
            params.put("movies",cartJson);
            String url="http://"+Connection.host+":"+Connection.port+"/onlinemoviestore/movies/updatecart";
            client.get(url, params, new AsyncHttpResponseHandler() {
                public void onSuccess(String response) {
                    SharedPreferences sharedPreference = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putString("myCart", response);
                    editor.commit();
                    progressDialog.hide();
                    renderCart();
                }

                public void onFailure(int statusCode, Throwable error, String content) {
                    progressDialog.hide();
                    AlertDialog.Builder alBuilder=new AlertDialog.Builder(CartActivity.this);
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
        }catch (Exception e){

        }
    }

    public void renderCart(){
        try {
            ArrayList<MovieListModel> cartArrayList=new ArrayList<MovieListModel>();
            SharedPreferences sharedPreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
            String cartJson = sharedPreferences.getString("myCart", null);
            JSONArray array = new JSONArray(cartJson);
            if(array.length()==0){
                AlertDialog.Builder alBuilder=new AlertDialog.Builder(this);
                alBuilder.setTitle("Message");
                alBuilder.setMessage("No items in your cart");
                alBuilder.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
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
            for (int i = 0 ; i < array.length(); i++) {
                JSONObject object=array.getJSONObject(i);
                MovieListModel model=new MovieListModel(object.getString("movie_id"),object.getString("movie_name"),object.getString("movie_price"),object.getString("movie_image"));
                cartArrayList.add(model);
            }

            CartListAdapter adapter=new CartListAdapter(cartArrayList,this);
            ListView view=(ListView)findViewById(R.id.cartList);
            view.setAdapter(adapter);

        }catch (Exception e){

        }


    }

    public void checkoutButton(View v){
        SharedPreferences sharedPreferences=getSharedPreferences("MyPREFERENCES",Context.MODE_PRIVATE);
        if(sharedPreferences.contains("email")){
            Intent intent=new Intent(this,ShippingDetailActivity.class);
            startActivity(intent);
        }else{
            Intent intent=new Intent(this,LoginActivity.class);
            intent.putExtra("redirectError","You need to login first");
            startActivity(intent);
        }
    }


}

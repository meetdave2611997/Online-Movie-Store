package com.example.meetdave.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        Intent intent=getIntent();
        if(intent.hasExtra("msg")){
            TextView t=(TextView)findViewById(R.id.myOrderMsg);
            t.setText(intent.getStringExtra("msg"));
        }

        getMyOrders();
    }

    public void getMyOrders(){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Retrieving order history...");
        progressDialog.show();
        SharedPreferences sharedPreferences=getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        RequestParams params=new RequestParams();
        params.put("user_id",sharedPreferences.getString("user_id",null));
        AsyncHttpClient client=new AsyncHttpClient();
        String url="http://"+Connection.host+":"+Connection.port+"/onlinemoviestore/orders/myorders";
        client.get(url,params,new AsyncHttpResponseHandler(){
           public void onSuccess(String response){
               try{
                   JSONObject object=new JSONObject(response);
                   if(object.getString("flag").equals("success")){

                       JSONArray array=object.getJSONArray("movies");
                       ArrayList<OrderModel> arrayList=new ArrayList<OrderModel>();
                       for(int i=0;i<array.length();i++){
                           JSONObject tempObject=array.getJSONObject(i);
                           OrderModel model=new OrderModel(tempObject.getString("order_id"),tempObject.getString("movie_name"),tempObject.getString("movie_price"),tempObject.getString("movie_qty"),tempObject.getString("order_date"),tempObject.getString("order_status"),tempObject.getString("movie_image"));
                           arrayList.add(model);
                       }
                       OrderListAdapter adapter=new OrderListAdapter(arrayList,MyOrderActivity.this);
                       ListView listView=(ListView)findViewById(R.id.myOrderListView);
                       listView.setAdapter(adapter);
                       progressDialog.hide();
                   }else{
                       progressDialog.hide();
                       AlertDialog.Builder alBuilder=new AlertDialog.Builder(MyOrderActivity.this);
                       alBuilder.setMessage(object.getString("errorMsg"));
                       alBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                           }
                       });
                       alBuilder.create().show();

                   }
               }catch (Exception ex){
                   progressDialog.hide();
                   AlertDialog.Builder alBuilder=new AlertDialog.Builder(MyOrderActivity.this);
                   alBuilder.setMessage(ex.getMessage());
                   alBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {

                       }
                   });
                   alBuilder.create().show();
               }
           }

            public void onFailure(int statusCode,Throwable error,String content){
                progressDialog.hide();
                AlertDialog.Builder alBuilder=new AlertDialog.Builder(MyOrderActivity.this);
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
}

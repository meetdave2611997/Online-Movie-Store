package com.example.meetdave.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Meet Dave on 18-03-2017.
 */
public class OrderListAdapter extends ArrayAdapter<OrderModel> {

    ArrayList<OrderModel> data;
    Context context;


    public OrderListAdapter(ArrayList<OrderModel> data,Context context){
        super(context,R.layout.my_order_list_row,data);
        this.context=context;
        this.data=data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.my_order_list_row, null);
        try{
            final OrderModel model=getItem(position);
            TextView orderId=(TextView)row.findViewById(R.id.orderID);
            TextView movieName=(TextView)row.findViewById(R.id.movieName_order);
            ImageView movieImage=(ImageView)row.findViewById(R.id.movieImage_order);
            TextView moviePrice=(TextView)row.findViewById(R.id.moviePrice_order);
            TextView movieQty=(TextView)row.findViewById(R.id.movieQty_order);
            TextView orderStatus=(TextView)row.findViewById(R.id.orderStatus);
            TextView orderDate=(TextView)row.findViewById(R.id.orderDate);
            Button cancelOrderButton=(Button)row.findViewById(R.id.cancelOrderButton);
            if(model.orderStatus.equals("delivered")){
                cancelOrderButton.setEnabled(false);
                cancelOrderButton.setVisibility(View.INVISIBLE);
            }

            if(model.orderStatus.equals("cancelled")){
                cancelOrderButton.setEnabled(false);
                cancelOrderButton.setVisibility(View.INVISIBLE);
            }
            cancelOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelOrder(model);
                }
            });
            orderId.setText("ORDER ID: "+model.getOrderID());
            movieName.setText(model.getMovieName());
            moviePrice.setText("Price: "+model.getMoviePrice());
            movieQty.setText("Qty: "+model.getMovieQty());
            orderStatus.setText("Order Status: "+model.getOrderStatus());
            orderDate.setText("Order Date: "+model.getOrderDate());
            new DownloadImageTask(movieImage).execute(model.getMovieImage());
        }catch (Exception ex){

        }
        return row;
    }

    public void cancelOrder(final OrderModel model){
        final ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Cancelling your order please wait...");
        progressDialog.setTitle("Processing");
        progressDialog.show();
        SharedPreferences sharedPreferences=context.getSharedPreferences("MyPREFERENCES",Context.MODE_PRIVATE);
        RequestParams params=new RequestParams();
        params.put("user_id",sharedPreferences.getString("user_id", null));
        params.put("order_id",model.getOrderID());
        String url="http://"+Connection.host+":"+Connection.port+"/onlinemoviestore/orders/cancelorder";
        AsyncHttpClient client=new AsyncHttpClient();
        client.get(url,params,new AsyncHttpResponseHandler(){
           public void onSuccess(String response){
               try {
                   progressDialog.hide();
                   JSONObject object = new JSONObject(response);
                   if (object.getString("flag").equals("error")) {
                       AlertDialog.Builder alBuilder=new AlertDialog.Builder(context);
                       alBuilder.setMessage(object.getString("errorMsg"));
                       alBuilder.setTitle("Message");
                       alBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                           }
                       });
                       alBuilder.create().show();
                   } else {
                       AlertDialog.Builder alBuilder=new AlertDialog.Builder(context);
                       alBuilder.setMessage("Your order has been cancelled");
                       alBuilder.setTitle("Message");
                       alBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                           }
                       });
                       alBuilder.create().show();
                       for (int i=0;i<data.size();i++){
                           OrderModel temp=data.get(i);
                           if(temp.orderID.equals(model.getOrderID())){
                               temp.setOrderStatus("cancelled");
                           }
                       }
                       notifyDataSetChanged();
                   }
               }catch (Exception e){

               }
           }

            public void onFailure(int statusCode,Throwable error,String content){
                AlertDialog.Builder alBuilder=new AlertDialog.Builder(context);
                alBuilder.setMessage(error.getMessage());
                alBuilder.setTitle("Message");
                alBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alBuilder.create().show();
            }
        });
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

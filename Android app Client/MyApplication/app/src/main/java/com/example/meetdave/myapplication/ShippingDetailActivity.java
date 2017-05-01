package com.example.meetdave.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

public class ShippingDetailActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_detail);
        try{

        }catch (Exception ex){

        }
    }


    public void placeOrder(View v){
        EditText fname=(EditText)findViewById(R.id.shippingdetail_fname);
        EditText lname=(EditText)findViewById(R.id.shippingdetail_lname);
        EditText email=(EditText)findViewById(R.id.shippingdetail_email);
        EditText address=(EditText)findViewById(R.id.shippingdetail_address);
        EditText city=(EditText)findViewById(R.id.shippingdetail_city);
        EditText state=(EditText)findViewById(R.id.shippingdetail_state);
        EditText pincode=(EditText)findViewById(R.id.shippingdetail_pincode);
        EditText phone_no=(EditText)findViewById(R.id.shippingdetail_phone_no);
        TextView shippingdetailError=(TextView)findViewById(R.id.shippingdetailError);

        if(fname.getText().toString().equals("") || lname.getText().toString().equals("") || email.getText().toString().equals("") || address.getText().toString().equals("") || city.getText().toString().equals("") || state.getText().toString().equals("") || pincode.getText().toString().equals("") || phone_no.getText().toString().equals("")){
            shippingdetailError.setText("Please enter all fields");
            return;
        }

        String emailPattern= "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String phonePattern="\\d+";

        if(!email.getText().toString().matches(emailPattern)){
            shippingdetailError.setText("Please enter correct email address");
            return;
        }

        if(!pincode.getText().toString().matches(phonePattern)){
            shippingdetailError.setText("Please enter valid pincode number");
            return;
        }

        if(pincode.getText().toString().length() != 6){
            shippingdetailError.setText("Please enter valid pincode number");
            return;
        }

        if(!phone_no.getText().toString().matches(phonePattern)){
            shippingdetailError.setText("Please enter valid phone number");
            return;
        }

        if(phone_no.getText().toString().length() != 10){
            shippingdetailError.setText("Please enter valid phone number");
            return;
        }
        try {
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Placing your order...");
            progressDialog.setTitle("Loading...");
            progressDialog.show();
            final SharedPreferences sharedPreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
            JSONArray array = new JSONArray(sharedPreferences.getString("myCart",null));
            JSONArray orderDetail=new JSONArray();
            for(int i=0;i<array.length();i++){
                JSONObject temp=array.getJSONObject(i);
                JSONObject object=new JSONObject();
                object.put("movie_id",temp.getString("movie_id"));
                object.put("movie_qty",temp.getString("movie_qty"));
                orderDetail.put(object);
            }

            JSONObject shippingDetail=new JSONObject();
            shippingDetail.put("user_id",sharedPreferences.getString("user_id", null));
            shippingDetail.put("fname",fname.getText().toString());
            shippingDetail.put("lname",lname.getText().toString());
            shippingDetail.put("email",email.getText().toString());
            shippingDetail.put("address",address.getText().toString());
            shippingDetail.put("city",city.getText().toString());
            shippingDetail.put("state",state.getText().toString());
            shippingDetail.put("pincode",pincode.getText().toString());
            shippingDetail.put("phone_no",phone_no.getText().toString());
            RequestParams params=new RequestParams();
            params.put("shippingDetail",shippingDetail.toString());
            params.put("orderDetail",orderDetail.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            String url="http://"+Connection.host+":"+Connection.port+"/onlinemoviestore/orders/placeorder";
            client.get(url,params,new AsyncHttpResponseHandler(){
               public void onSuccess(String response){
                   try {
                       JSONObject object = new JSONObject(response);
                       if (object.getString("flag").equals("success")) {
                           SharedPreferences sharedPreferences1=getSharedPreferences("MyPREFERENCES",Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor= sharedPreferences1.edit();
                           JSONArray array1=new JSONArray();
                           editor.putString("myCart",array1.toString());
                           editor.commit();
                           progressDialog.hide();
                           Intent intent = new Intent(ShippingDetailActivity.this, MyOrderActivity.class);
                           intent.putExtra("msg", "Your order has been placed");
                           startActivity(intent);
                       }else{
                           AlertDialog.Builder alBuilder=new AlertDialog.Builder(ShippingDetailActivity.this);
                           alBuilder.setMessage(object.getString("errorMsg"));
                           alBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {

                               }
                           });
                           alBuilder.create().show();
                           progressDialog.hide();
                       }
                   }catch (Exception e){

                   }
               }

                public void onFailure(int statusCode,Throwable error,String content){

                    progressDialog.hide();
                    AlertDialog.Builder alBuilder=new AlertDialog.Builder(ShippingDetailActivity.this);
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
        }catch (Exception ex){

        }



    }

}

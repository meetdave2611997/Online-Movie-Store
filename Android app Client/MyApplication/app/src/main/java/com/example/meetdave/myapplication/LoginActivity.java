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
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent=getIntent();
        if(intent.hasExtra("redirectError")){
            TextView t=(TextView)findViewById(R.id.loginerrorMsg);
            t.setText(intent.getStringExtra("redirectError"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent=getIntent();
        if(intent.hasExtra("redirectError")){
            TextView t=(TextView)findViewById(R.id.loginerrorMsg);
            t.setText(intent.getStringExtra("redirectError"));

        }
    }


    public void loginProcess(View view){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Logging in...");
        progressDialog.show();
        final String email,password;
        email=((EditText)findViewById(R.id.emailTextBox)).getText().toString();
        password=((EditText)findViewById(R.id.passwordTextBox)).getText().toString();
        if(email.equals("") || password.equals("")){
            TextView t=(TextView)findViewById(R.id.loginerrorMsg);
            t.setText("Invalid Username or Password");
            progressDialog.hide();
            return;
        }

        AsyncHttpClient client=new AsyncHttpClient();
        String url="http://"+Connection.host+":"+Connection.port+"/onlinemoviestore/users/authenticateuser";
        RequestParams params=new RequestParams();
        params.put("email",email);
        params.put("password", password);

        client.get(url, params, new AsyncHttpResponseHandler() {

            public void onSuccess(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.get("flag").toString().equals("success")) {
                        TextView t = (TextView) findViewById(R.id.loginerrorMsg);
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.putString("user_id",obj.get("user_id").toString());
                        editor.putString("fname",obj.get("fname").toString());
                        editor.putString("lname",obj.get("lname").toString());
                        editor.putString("phone_no",obj.get("phone_no").toString());
                        editor.commit();
                        progressDialog.hide();
                        loginSucess();
                    } else {
                        progressDialog.hide();
                        TextView t = (TextView) findViewById(R.id.loginerrorMsg);
                        t.setText(obj.get("errorMsg").toString());
                    }
                } catch (Exception e) {

                }
            }

            public void onFailure(int statusCode, Throwable error, String content) {
                progressDialog.hide();
                AlertDialog.Builder alBuilder=new AlertDialog.Builder(LoginActivity.this);
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

    public void loginSucess(){
        SharedPreferences sharedPreferences=getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

        if(sharedPreferences.contains("email")){
            Intent intent=new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}

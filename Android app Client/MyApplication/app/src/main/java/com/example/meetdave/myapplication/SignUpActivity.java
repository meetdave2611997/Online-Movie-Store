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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

    }

    public void signupProcess(View view){
        TextView t=(TextView)findViewById(R.id.signuperrorMsg);
        String fname=((EditText)findViewById(R.id.fname)).getText().toString();
        String lname=((EditText)findViewById(R.id.lname)).getText().toString();
        final String email=((EditText)findViewById(R.id.emailS)).getText().toString();
        String password=((EditText)findViewById(R.id.passwordS)).getText().toString();
        String cpassword=((EditText)findViewById(R.id.cpasswordS)).getText().toString();
        String phone_no=((EditText)findViewById(R.id.phoneno)).getText().toString();
        String emailPattern= "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String phonePattern="\\d+";
        if(fname.equals("") || lname.equals("") || email.equals("") || password.equals("") || cpassword.equals("") || phone_no.equals("")){
            t.setText("Please enter all details");
            return;
        }

        if(!email.matches(emailPattern)){
            t.setText("Please enter correct email address");
            return;
        }

        if(password.length() < 8 || password.length() > 15){
            t.setText("Password should be 8 to 15 characters long");
            return;
        }

        if(!password.equals(cpassword)){
            t.setText("Password didn't match");
            return;
        }

        if(!phone_no.matches(phonePattern)){
            t.setText("Please enter correct 10 digit mobile number");
            return;

        }

        if(phone_no.length() != 10){
            t.setText("Please enter correct 10 digit mobile number");
            return;
        }
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Creating an account...");
        progressDialog.show();
        AsyncHttpClient client=new AsyncHttpClient();
        RequestParams params=new RequestParams();
        params.put("fname",fname);
        params.put("lname",lname);
        params.put("email",email);
        params.put("password",password);
        params.put("phone_no",phone_no);
        String url="http://"+Connection.host+":"+Connection.port+"/onlinemoviestore/users/signup";
        client.get(url,params,new AsyncHttpResponseHandler(){
           public void onSuccess(String response){
                try{
                    JSONObject obj=new JSONObject(response);
                    if(obj.get("flag").toString().equals("success")){
                        SharedPreferences sharedPreferences=getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("email",email);
                        editor.putString("user_id",obj.get("user_id").toString());
                        editor.putString("fname",obj.get("fname").toString());
                        editor.putString("lname",obj.get("lname").toString());
                        editor.putString("phone_no",obj.get("phone_no").toString());
                        editor.commit();
                        progressDialog.hide();
                        signupSucess();
                    }else{
                        progressDialog.hide();
                        TextView t=(TextView)findViewById(R.id.signuperrorMsg);
                        t.setText(obj.get("errorMsg").toString());
                    }
                }catch(Exception exception){

                }

           }

           public void onFailure(int statusCode,Throwable error,String content){
               progressDialog.hide();
               AlertDialog.Builder alBuilder=new AlertDialog.Builder(SignUpActivity.this);
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

    public void signupSucess(){

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

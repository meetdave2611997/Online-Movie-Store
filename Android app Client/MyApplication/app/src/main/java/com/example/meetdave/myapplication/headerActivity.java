package com.example.meetdave.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

public class headerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header);

    }




    public void homeButtonClick(View view){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void cartClick(View view){
        Intent intent=new Intent(this,CartActivity.class);
        startActivity(intent);
    }

    public void signOut(){
        SharedPreferences sharedPreferences=getSharedPreferences("MyPREFERENCES",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.remove("fname");
        editor.remove("user_id");
        editor.remove("lname");
        editor.remove("email");
        editor.remove("phone_no");
        editor.commit();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void startMyOrderActivity(){
        Intent intent=new Intent(this,MyOrderActivity.class);
        startActivity(intent);
    }



    public void loginButtonClick(View view){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);

    }

    public void signupButtonClick(View view){
        Intent intent=new Intent(this,SignUpActivity.class);
        startActivity(intent);

    }

    public void setHeader(int id){
        int flag=1;
        SharedPreferences sharedPreferences=getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("email")){
            flag=2;
        }else{
            flag=1;
        }

        if(flag==1){
            RelativeLayout r1=(RelativeLayout)findViewById(id);
            TextView t=(TextView)findViewById(R.id.textView);

            View child = getLayoutInflater().inflate(R.layout.activity_header, null);

            r1.addView(child);
        }else{
            RelativeLayout r1=(RelativeLayout)findViewById(id);
            LayoutInflater inflater=getLayoutInflater();
            View child=inflater.inflate(R.layout.header2, null);
            r1.addView(child);

            Spinner s=(Spinner)findViewById(R.id.myAccountDropDown);
            s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (id == 0) {

                    } else if (id == 1) {
                        startMyOrderActivity();
                    } else if (id == 2) {

                    } else {
                        signOut();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }



    }
}

package com.mrinalraj.coeroutpass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.net.ssl.HttpsURLConnection;

public class SplashScreen extends AppCompatActivity {

    ImageView splashLogo;
    ProgressBar splashProgress;
    SharedPreferences cred;
    String token;
    String id;
    Boolean loginStatus;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().hide();

        splashLogo = (ImageView)findViewById(R.id.splashLogo);
        splashProgress = (ProgressBar)findViewById(R.id.splashProgress);
        splashProgress.animate();
        cred = getSharedPreferences("IDStore",0);
        token = cred.getString("AccessToken",null);
        loginStatus = cred.getBoolean("isLogged",false);
        id = cred.getString("ID","00000000");
        android.os.Handler handler = new android.os.Handler();

        if(loginStatus==true) {
            new TokenVarify().execute();
        }
        else{
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this,login_main.class));
                    finish();
                }
            },3000);

        }

    }

    class TokenVarify extends AsyncTask<Void ,String, String>{

        URL url;
        HttpURLConnection connection;
        String tS;
        BufferedReader reader;
        JSONObject obj;
        SharedPreferences.Editor cred_edit;

        @Override
        protected String doInBackground(Void... params) {

            try {
                url = new URL("https://coer-backend.herokuapp.com/student/token-validity/"+id);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                connection.setRequestProperty("authkey","SUPERPRIVATE");
                connection.setRequestProperty("deviceID",cred.getString("deviceID",null));
                connection.setRequestProperty("accesstoken",token);
                connection.setDoInput(true);
                connection.connect();


                StringBuffer tokenStatus = new StringBuffer();
                cred_edit = cred.edit();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((tS = reader.readLine())!=null){
                    tokenStatus.append(tS);
                }

                Log.v("Token JSON:",tokenStatus.toString());


                obj = new JSONObject(tokenStatus.toString());
                tS = obj.getString("TOKENValidityStatus");
                name = obj.getString("name");

                return tS;

            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(ProtocolException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }catch(JSONException e){
                e.printStackTrace();
            }
            finally {
                try{
                    if(reader!=null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(connection!=null){
                    connection.disconnect();
                }
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s!=null && s.equals("200OK")){
                Toast.makeText(SplashScreen.this, "Token Validated", Toast.LENGTH_SHORT).show();
                cred_edit.putBoolean("isLogged",true);
                cred_edit.commit();
                Intent i = new Intent(SplashScreen.this, main.class);
                i.putExtra(main.LOGINID,id);
                i.putExtra(main.LOGINNAME,cred.getString("name","COER")); // Name from token validity ..// TODO: 11/9/2016
                startActivity(i);
                finish();
            }
            else{
                    Toast.makeText(SplashScreen.this, "Login Expired", Toast.LENGTH_SHORT).show();
                    cred_edit.putBoolean("isLogged",false);
                    cred_edit.commit();
                    startActivity(new Intent(SplashScreen.this,login_main.class));
                    finish();
            }
        }
    }
}



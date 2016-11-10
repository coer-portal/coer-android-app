package com.mrinalraj.coeroutpass;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.mrinalraj.coeroutpass.main;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;


public class login_main extends AppCompatActivity {


    EditText coerid;
    EditText password;
    Button login;
    SharedPreferences cred;
    SharedPreferences.Editor cred_edit;
    String loginStatus;
    String pwd;
    String accessToken;
    String deviceID;
    String name;

    SharedPreferences idStore;
    SharedPreferences.Editor idStoreEdit;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);


        coerid=(EditText)findViewById(R.id.coerid);
        password=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.button);
        cred=getSharedPreferences("PREFS",0);
        cred_edit=cred.edit();

        idStore = getSharedPreferences("IDStore",0);
        idStoreEdit = idStore.edit();

        deviceID = cred.getString("deviceID",null);

        if(cred.getBoolean("isLogged",false)){
            startActivity(new Intent(login_main.this,main.class));
            finish();
        }

        getSupportActionBar().setElevation(0);
        getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            getWindow().setNavigationBarColor(this.getResources().getColor(R.color.tint_nav));

            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.tint_nav));
        }


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        final ImageView logo=(ImageView) findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.vikashnegizzz.coer");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                }
                else{
                    Snackbar.make(view, "Plugin not found!!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(coerid.getText().toString().trim().equals("") || password.getText().toString().trim().equals("")){
                    Toast.makeText(login_main.this, "All fields are Mandatory", Toast.LENGTH_SHORT).show();
                }
                else{
                    pwd = password.getText().toString().trim();
                    new loginTask().execute(coerid.getText().toString().trim());

                }
            }
        });

        TextView register=(TextView) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login_main.this,register.class));
                finish();
            }
        });

    }

    public void putCred(){
        cred_edit.putString("AccessToken",accessToken);
        cred_edit.putString("deviceID",deviceID);
        cred_edit.commit();
        idStoreEdit.putString("ID",coerid.getText().toString().trim());
        idStoreEdit.putString("name",name);
        idStoreEdit.commit();
    }
    public void loginValidateApp(){
        cred_edit.putBoolean("isLogged",true);
        cred_edit.commit();
    }

    public class loginTask extends AsyncTask<String,Integer,JSONObject> {
        ProgressDialog pd;
        StringBuffer jsonObject;
        String json;
        HttpURLConnection connection;
        BufferedReader reader;
        InputStream iStream;
        OutputStream outStream;

        PrintWriter writer;

        JSONObject loginObject;
        URL url;
        String ID;

        @Override
        protected void onPreExecute() {
            login_main.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd=new ProgressDialog(login_main.this);
                    pd.setTitle("Logging In");
                    pd.setMessage("Please wait...");
                    pd.setCancelable(false);
                    pd.show();
                }
            });
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                url = new URL("https://coer-backend.herokuapp.com/student/login");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("authkey", "SUPERPRIVATE");
                connection.setRequestProperty("deviceID",deviceID);
                connection.setRequestProperty("password", pwd);
                connection.setDoOutput(true);
                connection.connect();

                ID = URLEncoder.encode("ID", "UTF-8")+"="+URLEncoder.encode(strings[0],"UTF-8");

                outStream = connection.getOutputStream();

                writer = new PrintWriter(outStream,true);
                writer.write(ID);
                writer.close();

                iStream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(iStream));
                jsonObject = new StringBuffer();

                while((json = reader.readLine()) !=null){
                    jsonObject.append(json);
                }


                loginObject = new JSONObject(jsonObject.toString());
                loginStatus = loginObject.getString("loginStatus");
                accessToken = loginObject.getString("accesstoken");
                name = loginObject.getString("name");
                deviceID = loginObject.getString("deviceID");

                cred_edit.putString("AccessToken",accessToken);
                cred_edit.commit();

                Log.v("JSON",jsonObject.toString());
                Log.v("CoerId",strings[0]);
                Log.v("devID",cred.getString("deviceID","000000"));
                Log.v("token",cred.getString("AccessToken","abcdef"));


            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }catch(JSONException e){
                e.printStackTrace();
            }
            finally{
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

            return loginObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            login_main.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd.dismiss();
                }
            });
            if (loginStatus != null && loginStatus.equals("200OK")) {
                putCred();
                loginValidateApp();
                Intent i = new Intent(login_main.this, main.class);
                i.putExtra(main.LOGINID,coerid.getText().toString().trim());
                i.putExtra(main.LOGINNAME,name);
                startActivity(i);
                finish();
            }
            else{
                Toast.makeText(login_main.this, "Login failed !", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(jsonObject);
        }

    }
}
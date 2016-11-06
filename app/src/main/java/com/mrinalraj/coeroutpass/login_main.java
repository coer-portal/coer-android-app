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

import java.io.IOException;
import java.io.InputStream;


public class login_main extends AppCompatActivity {


    EditText coerid;
    EditText password;
    Button login;
    SharedPreferences cred;
    SharedPreferences.Editor cred_edit;
    JSONObject jsonObj;
    String name1,password1,coerid1,status;

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
                    //coerid1=coerid.getText().toString().trim();
                    new getAttendance().execute(coerid.getText().toString().trim());

                }
            }
        });
        TextView register=(TextView) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login_main.this,register.class));
            }
        });

    }

    public void putCred(){
        cred_edit.putString("coerid",coerid.getText().toString().trim());
        cred_edit.putString("password",password.getText().toString().trim());
        cred_edit.commit();
    }
    public void loginValidateApp(){
        cred_edit.putBoolean("isLogged",true);
        cred_edit.commit();
    }

    public class getAttendance extends AsyncTask<String,Integer,JSONObject> {
        ProgressDialog pd;
        InputStream inputStream=null;

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
            final JSONObject jsonObject;
            String url="http://coer-backend.herokuapp.com/student/attendance/"+strings[0];
            try {
                JSONCustom jc=new JSONCustom();
                jsonObject = jc.getJSONObjectFromURL(url,"GET");
                if (jsonObject!=null){
                    cred_edit.putString("name",jsonObject.optString("name"));
                    cred_edit.putString("attendance",jsonObject.optString("attendance"));
                    cred_edit.putString("attenLastUpdatedOn",jsonObject.optString("attenLastUpdatedOn"));
                    cred_edit.putString("status",jsonObject.optString("status"));
                    cred_edit.commit();
                    status=jsonObject.optString("status");
                    login_main.this.runOnUiThread(new Runnable() {
                        public void run() {

                        }
                    });
                }
                else{
                    login_main.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(login_main.this, "failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("failed","failed");
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            login_main.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd.dismiss();
                }
            });
            if (status.equals("true")) {
                putCred();
                loginValidateApp();
                startActivity(new Intent(login_main.this, main.class));
                finish();
            }
            else{
                Toast.makeText(login_main.this, "Login failed !\nProbably wrong COERID", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(jsonObject);
        }

    }
}
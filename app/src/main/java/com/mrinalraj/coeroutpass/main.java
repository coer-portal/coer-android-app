package com.mrinalraj.coeroutpass;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mrinalraj.coeroutpass.frags.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
public class main extends AppCompatActivity {
    SharedPreferences cred,IDStore;
    SharedPreferences.Editor cred_edit;
    int pos;

    static final String LOGINNAME = "name";
    static final String LOGINID = "ID";
    private String token;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private Intent intent;

    private String attendance;
    private String attenLastUpdatedOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cred=getSharedPreferences("PREFS",0);
        cred_edit= cred.edit();
        intent = getIntent();

        token = cred.getString("AccessToken",null);

        IDStore = getSharedPreferences("IDStore",0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Clicking here will place a call to your warden. Beware ! ;) " , Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        new GetData().execute(intent.getStringExtra(LOGINID));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            cred_edit.putBoolean("isLogged",false);
            cred_edit.commit();
            startActivity(new Intent(main.this, login_main.class));
            finish();

            return true;
        }
        else if(id == R.id.action_about){
            startActivity(new Intent(getApplicationContext(),about.class));
            return true;
        }
        else if(id == R.id.action_attendance){
            new AlertDialog.Builder(main.this).setTitle(" "+IDStore.getString("name","COER")+" your attendance is").setMessage(attendance+"\n"+"as on "+attenLastUpdatedOn).show();
        }

        return super.onOptionsItemSelected(item);
    }



    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            main.this.pos = position;
            switch (position) {
                case 0:
                    return new outpass();
                case 1:
                    return new leave();
                //case 2:
                 //   return new attendance();
                case 2:
                    return new issued();
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "outpass";
                case 1:
                    return "leave";
                //case 2:
                  //  return "attendance";
                case 2:
                    return "issued";
            }
            return null;
        }
    }

    public class GetData extends AsyncTask<String,Integer,JSONObject> {
        ProgressDialog pd;
        URL url;
        HttpURLConnection connection;

        BufferedReader reader;
        JSONObject parentObject;
        JSONObject academicsObject;
        JSONObject attendanceObject;
        StringBuffer buffer;
        String result;


        @Override
        protected void onPreExecute() {
            main.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd=new ProgressDialog(main.this);
                    pd.setTitle("Welcome "+IDStore.getString("name","COER"));
                    pd.setMessage("Refreshing data... ");
                    pd.setCancelable(false);
                    pd.show();
                }
            });
            super.onPreExecute();

            Log.v("ID FUll:",IDStore.getString("ID","00000000"));
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            try{
               url=new URL("https://coer-backend.herokuapp.com/student/full/"+IDStore.getString("ID","00000000"));
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                connection.setRequestProperty("authkey","testingKEY");
                connection.setRequestProperty("accesstoken",token);
                connection.setRequestProperty("deviceID",cred.getString("deviceID",null));
                connection.setDoInput(true);
                connection.connect();

                buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((result = reader.readLine()) != null){
                    buffer.append(result);
                }

                Log.v("FUll JSON:",buffer.toString());
                Log.v("DEVICE id FULL:",cred.getString("deviceID",null));
                Log.v("access TOKEN FUll:",token);

                parentObject = new JSONObject(buffer.toString());
                academicsObject = new JSONObject(parentObject.getString("academics"));
                attendanceObject = new JSONObject((academicsObject.getString("attendance")));

                attendance = attendanceObject.getString("attendance");
                attenLastUpdatedOn = attendanceObject.getString("attenLastUpdatedOn");

                return parentObject;

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
            main.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd.dismiss();
                }
            });
            super.onPostExecute(jsonObject);
        }

    }
}

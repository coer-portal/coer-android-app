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

import java.io.IOException;
import java.io.InputStream;

public class main extends AppCompatActivity {
    SharedPreferences cred;
    SharedPreferences.Editor cred_edit;
    int pos;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cred=getSharedPreferences("PREFS",0);
        cred_edit= cred.edit();

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
        new getAttendance().execute(cred.getString("coerid", ""));
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
            cred_edit.putString("name","");
            cred_edit.putString("attendance","");
            cred_edit.putString("attenLastUpdatedOn","");
            cred_edit.putString("status","");
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
            new AlertDialog.Builder(main.this).setTitle(" "+cred.getString("name","")+" your attendance is").setMessage(cred.getString("attendance","")+"\n"+"as on "+cred.getString("attenLastUpdatedOn", "")).show();
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

    public class getAttendance extends AsyncTask<String,Integer,JSONObject> {
        ProgressDialog pd;
        InputStream inputStream=null;

        @Override
        protected void onPreExecute() {
            main.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd=new ProgressDialog(main.this);
                    pd.setTitle("Welcome "+cred.getString("name",""));
                    pd.setMessage("Refreshing data... ");
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
                    main.this.runOnUiThread(new Runnable() {
                        public void run() {

                        }
                    });
                }
                else{
                    main.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(main.this, "failed", Toast.LENGTH_SHORT).show();
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

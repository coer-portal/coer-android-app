package com.mrinalraj.coeroutpass;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

import static android.R.attr.id;

public class register extends AppCompatActivity {

    EditText ph;
    RadioGroup statusGroup;
    EditText ID;
    EditText fp;
    EditText dob;
    RadioButton statusBtn;

    String pwd;
    String submitData;
    String id;
    String pno;
    String fPno;
    String db;
    String stStatus;

    String name;
    String registerStatus;

    SharedPreferences sP;
    SharedPreferences.Editor sPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg);
        getSupportActionBar().setElevation(1);

        sP = getSharedPreferences("RegisterData",0);
        sPE = sP.edit();

        ph = (EditText)findViewById(R.id.myPhone);
        statusGroup = (RadioGroup)findViewById(R.id.studentStatus);

        ID=(EditText)findViewById(R.id.id_reg);
        fp=(EditText)findViewById(R.id.fathersphone);
        dob=(EditText)findViewById(R.id.dob);
        ImageButton dp=(ImageButton)findViewById(R.id.imageButton);
        final Calendar c=Calendar.getInstance();
        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int year = c.get(Calendar.YEAR);
                final int month = c.get(Calendar.MONTH);
                final int day = c.get(Calendar.DAY_OF_MONTH);


                final DatePickerDialog dpd=new DatePickerDialog(register.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        dob.setText(i2+"/"+(i1+1)+"/"+i);
                    }
                },year,month,day);
                dpd.show();
            }
        });
        Button validate=(Button)findViewById(R.id.validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input1 = new EditText(register.this);
                final EditText input2 = new EditText(register.this);
                LinearLayout lpl=new LinearLayout(register.this);
                lpl.setOrientation(LinearLayout.VERTICAL);
                input1.setHint("Enter new Password");
                input2.setHint("Re-enter Password");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(30,20,30,0);
                input1.setLayoutParams(lp);
                input1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                input2.setLayoutParams(lp);
                input2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                lpl.addView(input1);
                lpl.addView(input2);
                AlertDialog.Builder passwordDialogue=new AlertDialog.Builder(register.this);
                passwordDialogue.setTitle("Password");
                passwordDialogue.setView(lpl);
                passwordDialogue.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(register.this, input1.getText().toString(), Toast.LENGTH_SHORT).show();
                        pwd = input1.getText().toString();
                    }
                });
                passwordDialogue.show();

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.submitBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RegisterTask().execute("Execute");
            }
        });
    }

    private void setData(){

        id = ID.getText().toString().trim();
        pno = ph.getText().toString().trim();
        fPno = fp.getText().toString().trim();
        db = dob.getText().toString().trim();

        int idS = statusGroup.getCheckedRadioButtonId();
        statusBtn = (RadioButton)findViewById(idS);
        stStatus = statusBtn.getText().toString().trim();

        StringBuilder dateBuilder = new StringBuilder();
        String st[] = db.split("/");
        int dd = Integer.parseInt(st[0]);
        int mm = Integer.parseInt(st[1]);
       if(dd>=1 && dd<=9){
            st[0]="0"+st[0];
        }
        if(mm>=1 && mm<=9){
            st[1]="0"+st[1];
        }
        for(String s:st){
            dateBuilder.append(s);
        }

        if(stStatus.equals("Hosteler")){
            stStatus = "hostel";
        }else{
            if(stStatus.equals("DayScholar")){
                stStatus = "dayscholar";
            }
        }
        try {
            submitData = URLEncoder.encode("ID", "UTF-8")+"="+URLEncoder.encode(id,"UTF-8");
            submitData+="&"+URLEncoder.encode("phoneno", "UTF-8")+"="+URLEncoder.encode(pno,"UTF-8");
            submitData+="&"+URLEncoder.encode("fatherno", "UTF-8")+"="+URLEncoder.encode(fPno,"UTF-8");
            submitData+="&"+URLEncoder.encode("DOB", "UTF-8")+"="+URLEncoder.encode(dateBuilder.toString(),"UTF-8");
            submitData+="&"+URLEncoder.encode("currentStatus", "UTF-8")+"="+URLEncoder.encode(stStatus,"UTF-8");

        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }


    }


    private class RegisterTask extends AsyncTask<String,String,String> {

        URL url;
        HttpURLConnection connection;
        OutputStream outStream;
        InputStream inStream;
        PrintWriter writer;
        BufferedReader reader;

        String result;
        StringBuffer res;

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            setData();
            register.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd=new ProgressDialog(register.this);
                    pd.setTitle("Registering");
                    pd.setMessage("Please wait...");
                    pd.setCancelable(false);
                    pd.show();
                }
            });

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("https://coer-backend.herokuapp.com/student/register");
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                connection.setRequestProperty("authkey","SUPERPRIVATE");
                connection.setRequestProperty("password",pwd);
                connection.setDoOutput(true);
                connection.connect();


                outStream = connection.getOutputStream();

                writer = new PrintWriter(outStream,true);
                writer.write(submitData);
                writer.close();

                inStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inStream));
                res = new StringBuffer();

                while((result = reader.readLine()) != null){
                    res.append(result);
                }

                JSONObject registerObject = new JSONObject(res.toString());
                name = registerObject.getString("name");
               // registerStatus = registerObject.getString("registered");

                return res.toString();

            }catch (MalformedURLException e){
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

            return connection.getRequestMethod();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            register.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd.dismiss();
                    try {
                        setRegisterDialog();
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });

        }

        void setRegisterDialog() throws JSONException{
            registerStatus = new JSONObject(res.toString()).getString("registered");

           if(registerStatus !=null && registerStatus.equals("Already Registered")){
                AlertDialog.Builder registerDialogue=new AlertDialog.Builder(register.this);
                registerDialogue.setTitle("Already Registered");
                registerDialogue.setMessage("Try Logging In...");
               registerDialogue.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       startActivity(new Intent(register.this,login_main.class));
                   }
               });
                registerDialogue.show();
            }

           else {

               if(registerStatus != null && registerStatus.equals("true")) {
                   String msg = "Name = " + name + "\n" + "ID = " + id + "\n" + "D.O.B = " + db + "\n" + "Your Phone Number = " + pno + "\n" + "Father's Phone Number = " + fPno + "\n" + "Current Status = " + stStatus;

                   AlertDialog.Builder registerDialogue = new AlertDialog.Builder(register.this);
                   registerDialogue.setTitle("Registration Successfull");
                   registerDialogue.setMessage(msg);
                   registerDialogue.setPositiveButton("LogIn", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           sPE.putString("Name", name);
                           sPE.putString("ID", id);
                           sPE.putString("DOB", db);
                           sPE.putString("MyPhone", pno);
                           sPE.putString("FatherPhone", fPno);
                           sPE.putString("Status", stStatus);

                           startActivity(new Intent(register.this,login_main.class));

                       }
                   });
                   registerDialogue.show();
               }

               else{

                   AlertDialog.Builder registerDialogue = new AlertDialog.Builder(register.this);
                   registerDialogue.setTitle("Registration Unsuccessfull");
                   registerDialogue.setMessage("Try Again...");
                   registerDialogue.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {

                       }
                   });
                   registerDialogue.show();

               }
           }


        }

    }

}

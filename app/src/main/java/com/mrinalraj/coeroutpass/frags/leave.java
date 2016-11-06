package com.mrinalraj.coeroutpass.frags;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mrinalraj.coeroutpass.R;

import java.util.Calendar;

/**
 * Created by Mrinal on 16-10-2016.
 */
public class leave extends Fragment implements View.OnClickListener {

    View v1;
    String name,room,branch,year,hostel,time_f,time_t,ampm,purpose,address,contact,relationship;
    final String TYPE="leave";

    public leave() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v1=inflater.inflate(R.layout.fragment_leave, container, false);


        //edittexts
        final EditText editName=(EditText)v1.findViewById(R.id.name);
        final EditText editRoom=(EditText)v1.findViewById(R.id.room);
        final EditText editBranch=(EditText)v1.findViewById(R.id.branch);
        final EditText editYear=(EditText)v1.findViewById(R.id.year);
        final EditText editAddress=(EditText)v1.findViewById(R.id.address);
        final EditText editContact=(EditText)v1.findViewById(R.id.contact);
        final EditText editRelationship=(EditText)v1.findViewById(R.id.relationship);
        final EditText editPurpose=(EditText)v1.findViewById(R.id.purpose);

        //spinner
        final Spinner spinnerHostel=(Spinner)v1.findViewById(R.id.hostel_list);

        //text view
        final TextView timeFrom=(TextView) v1.findViewById(R.id.write_time_from);
        final TextView timeTill=(TextView) v1.findViewById(R.id.write_time_till);
        final TextView dateFrom=(TextView) v1.findViewById(R.id.write_date_from);
        final TextView dateTill=(TextView) v1.findViewById(R.id.write_date_till);

        //Buttons
        Button timeFromBtn=(Button)v1.findViewById(R.id.time_from);
        Button timeTillBtn=(Button)v1.findViewById(R.id.time_till);

        Button dateTillBtn=(Button)v1.findViewById(R.id.date_till);
        Button send=(Button)v1.findViewById(R.id.send);

        final Calendar c=Calendar.getInstance();
        int month=(c.get(Calendar.MONTH))+1;
        dateFrom.setText((c.get(Calendar.DAY_OF_MONTH))+1+" - "+month+" - "+c.get(Calendar.YEAR));

        timeFromBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int hour= c.get(Calendar.HOUR_OF_DAY);
                int min= c.get(Calendar.MINUTE);

                TimePickerDialog tpd=new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        if(i>12){
                            i=i-12;
                            ampm="PM";
                        }
                        else {
                            ampm = "AM";
                        }
                        if(i1<10) {
                            time_f = i + " : 0" + i1 + " " + ampm;
                        }
                        else{
                            time_f = i + " : " + i1 + " " + ampm;
                        }
                        timeFrom.setText(time_f);

                    }
                },hour,min,false);
                tpd.show();
            }
        });

        timeTillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int hour= c.get(Calendar.HOUR_OF_DAY);
                int min= c.get(Calendar.MINUTE);

                TimePickerDialog tpd=new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        if(i>12){
                            i=i-12;
                            ampm="PM";
                        }
                        else {
                            ampm = "AM";
                        }
                        if(i1<10) {
                            time_t = i + " : 0" + i1 + " " + ampm;
                        }
                        else {
                            time_t = i + " : " + i1 + " " + ampm;
                        }
                        timeTill.setText(time_t);
                    }
                },hour,min,false);
                tpd.show();
            }
        });

        dateTillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int year = c.get(Calendar.YEAR);
                final int month = c.get(Calendar.MONTH);
                final int day = c.get(Calendar.DAY_OF_MONTH);


                final DatePickerDialog dpd=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        boolean abcd=false;
                        if(i>=year){
                            abcd =true;
                            if(i1>=month){
                                abcd =true;
                                if (i2>day){
                                    abcd =true;
                                }
                            }
                        }
                        if(abcd){
                            dateTill.setText(i2+" - "+i1+" - "+i);
                        }
                        else{
                            Toast.makeText(getContext(), "Same or previous date not allowed !", Toast.LENGTH_SHORT).show();
                        }
                    }
                },year,month,day);
                dpd.show();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=editName.getText().toString();
                branch=editBranch.getText().toString();
                year=editYear.getText().toString();
                hostel=spinnerHostel.getSelectedItem().toString();
                purpose=editPurpose.getText().toString();
                address=editAddress.getText().toString();
                contact=editContact.getText().toString();
                relationship=editRelationship.getText().toString();
                room=editRoom.getText().toString();

                final EditText input = new EditText(getContext());
                input.setHint("Enter your password");
                LinearLayout lpl=new LinearLayout(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(30,20,30,0);
                input.setLayoutParams(lp);
                input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                AlertDialog.Builder passwordDialogue=new AlertDialog.Builder(getContext());
                passwordDialogue.setTitle("Password");
                lpl.addView(input);
                passwordDialogue.setView(lpl);
                passwordDialogue.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), input.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                passwordDialogue.show();
            }
        });


        return v1;
    }


    @Override
    public void onClick(View view) {


    }

}
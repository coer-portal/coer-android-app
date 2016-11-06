package com.mrinalraj.coeroutpass.frags;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrinalraj.coeroutpass.R;

/**
 * Created by Mrinal on 16-10-2016.
 */
public class attendance extends Fragment implements View.OnClickListener {

    View v2;

    public attendance() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v2=inflater.inflate(R.layout.fragment_attendance, container, false);
        SharedPreferences cred= this.getActivity().getSharedPreferences("PREFS",0);

        TextView setName=(TextView) v2.findViewById(R.id.name);
        String heading="Hello! "+cred.getString("name","");
        setName.setText(heading);
        TextView setAttnd=(TextView) v2.findViewById(R.id.att);
        setAttnd.setText("Your attendence is "+ cred.getString("attendance","NA"));
        TextView setUpdate=(TextView) v2.findViewById(R.id.update);
        setUpdate.setText("last updated :" +cred.getString("lastUpdate", "NA"));
        return v2;
    }

    @Override
    public void onClick(View view) {

    }

}
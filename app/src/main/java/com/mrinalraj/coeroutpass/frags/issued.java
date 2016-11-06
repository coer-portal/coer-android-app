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
public class issued extends Fragment implements View.OnClickListener{

    View v3;

    public issued() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v3= inflater.inflate(R.layout.fragment_history, container, false);

        TextView name=(TextView)v3.findViewById(R.id.name);
        TextView id=(TextView)v3.findViewById(R.id.ID);

        SharedPreferences cred=this.getActivity().getSharedPreferences("PREFS",0);
        name.setText(cred.getString("name",""));
        id.setText(cred.getString("coerid",""));

        return v3;
    }
    @Override
    public void onClick(View view) {

    }
}
package com.georgewilliam.speedforce.projectspeedforce;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;


public class FrontFragment extends Fragment implements OnClickListener {

    Button btnLogin;
    Button btnRegister;

    //for testing purposes
    public final int p1 = 53;
    public final int p2 = 57;

    public FrontFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_front, container, false);

        btnLogin = (Button) view.findViewById(R.id.btn_Login_id);
        btnLogin.setOnClickListener(this);
        btnRegister = (Button) view.findViewById(R.id.btn_Register_id);
        btnRegister.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.btn_Login_id:
                intent = new Intent(getActivity(), LoginActivity.class);
                //intent.putExtra("param1", p1);
                //intent.putExtra("param2", p2);
                startActivity(intent);
                break;

            case R.id.btn_Register_id:
                intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
                break;
        }

    }

}

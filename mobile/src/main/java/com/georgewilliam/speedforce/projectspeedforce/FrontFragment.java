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

    Button btnDebugMaps;

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

        btnLogin = (Button) view.findViewById(R.id.front_button_login_id);
        btnLogin.setOnClickListener(this);
        btnRegister = (Button) view.findViewById(R.id.front_button_register_id);
        btnRegister.setOnClickListener(this);

        btnDebugMaps = (Button) view.findViewById(R.id.front_debug_button_maps_id);
        btnDebugMaps.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.front_button_login_id:
                intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.front_button_register_id:
                intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
                break;

            case R.id.front_debug_button_maps_id:
                intent = new Intent(getActivity(), ApiActivity.class);
                //intent.putExtra("param1", p1);
                //intent.putExtra("param2", p2);
                intent.putExtra("username", "WheelKing");
                startActivity(intent);
                break;
        }

    }

}

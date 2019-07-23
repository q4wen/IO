package uwaterloo.ca.patientmobile.measure;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uwaterloo.ca.patientmobile.MainActivity;
import uwaterloo.ca.patientmobile.R;


public class MeasureFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_measure, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        View cvpBtn = view.findViewById(R.id.cvp);
        cvpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MeasureActivity.class);
                startActivity(intent);
            }
        });
    }

}

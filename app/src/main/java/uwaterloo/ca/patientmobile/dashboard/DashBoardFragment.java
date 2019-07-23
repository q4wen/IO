package uwaterloo.ca.patientmobile.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineRadarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import uwaterloo.ca.patientmobile.R;

public class DashBoardFragment extends Fragment {

    ViewPager vp;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ArrayList<LineData> dataList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            dataList.add(generateRandomData());
        }

        vp = view.findViewById(R.id.view_pager);
        vp.setAdapter(new GraphPagerAdapter(getContext(), dataList));

        Button yearBtn = view.findViewById(R.id.yearBtn);
        yearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<LineData> dataList = new ArrayList<>();
                for (int i = 0; i < 3; i++)
                    dataList.add(generateRandomData());
                vp.setAdapter(new GraphPagerAdapter(getContext(), dataList));
            }
        });
        Button mothBtn = view.findViewById(R.id.monthBtn);
        mothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<LineData> dataList = new ArrayList<>();
                for (int i = 0; i < 3; i++)
                    dataList.add(generateRandomData());
                vp.setAdapter(new GraphPagerAdapter(getContext(), dataList));
            }
        });
        Button weekBtn = view.findViewById(R.id.weekBtn);
        weekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<LineData> dataList = new ArrayList<>();
                for (int i = 0; i < 3; i++)
                    dataList.add(generateRandomData());
                vp.setAdapter(new GraphPagerAdapter(getContext(), dataList));
            }
        });
    }
    
    private LineData generateRandomData() {
        int count = 10, range = 100;
        ArrayList<Entry> values = new ArrayList<>();

        for (int j = 0; j < count; j++) {
            float val = (float) (Math.random() * range);
            values.add(new Entry(j, val));
        }

        LineDataSet set = new LineDataSet(values, "");
        set.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        LineData data = new LineData(dataSets);
        return data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dash_board, container, false);
    }
}

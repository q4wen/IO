package uwaterloo.ca.patientmobile.dashboard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Date;

import uwaterloo.ca.patientmobile.R;
import uwaterloo.ca.patientmobile.log.LogAdapter;

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

        /*
        Button btn = view.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 3; i++) {
                    ArrayList<LineData> dataList = new ArrayList<>();
                    dataList.add(generateRandomData());
                    vp.setAdapter(new GraphPagerAdapter(getContext(), dataList));
                }
            }
        });
        */
    }

    private LineData generateRandomData() {
        int count = 10, range = 100;
        ArrayList<Entry> values = new ArrayList<>();

        for (int j = 0; j < count; j++) {

            float val = (float) (Math.random() * range) - 30;
            values.add(new Entry(j, val));
        }

        LineDataSet set;
        set = new LineDataSet(values, "data1");
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

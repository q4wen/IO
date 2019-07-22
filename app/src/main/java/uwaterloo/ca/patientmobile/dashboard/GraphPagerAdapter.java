package uwaterloo.ca.patientmobile.dashboard;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import uwaterloo.ca.patientmobile.R;


public class GraphPagerAdapter extends PagerAdapter {

    private Context mContext;

    private List<LineData> dataList;

    public GraphPagerAdapter(Context context, List<LineData> dataList) {
        mContext = context;
        this.dataList = dataList;
    }

    public void setData(List<LineData> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.dash_board_graph, collection, false);

        LineChart lineChart = layout.findViewById(R.id.chart);
        lineChart.setData(dataList.get(position));

        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Graph";
    }

}



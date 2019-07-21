package uwaterloo.ca.patientmobile.log;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uwaterloo.ca.patientmobile.R;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder>{

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public LogViewHolder(View v) {
            super(v);
        }
    }

    @Override
    public LogAdapter.LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_element, parent, false);
        LogViewHolder vh = new LogViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, int position) {
        //holder.textView.setText(mDataset[position]);

    }

    @Override
    public int getItemCount() {
        return 20;
    }
}

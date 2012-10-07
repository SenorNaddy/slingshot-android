package com.naddysworld.slingshot;

import java.util.ArrayList;

import com.naddysworld.slingshot.R;
 
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
 
public class SlingshotAdapter extends BaseAdapter {
    private static ArrayList<SlingshotUsageItem> searchArrayList;
 
    private LayoutInflater mInflater;
 
    public SlingshotAdapter(Context context, ArrayList<SlingshotUsageItem> results) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
    }
 
    public int getCount() {
        return searchArrayList.size();
    }
 
    public Object getItem(int position) {
        return searchArrayList.get(position);
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_row_view, null);
            holder = new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.name);
            holder.txtValue = (TextView) convertView.findViewById(R.id.value); 
            holder.txtUnit = (TextView) convertView.findViewById(R.id.unit);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
 
        holder.txtName.setText(searchArrayList.get(position).getName());
        holder.txtValue.setText(searchArrayList.get(position).getValue());
        holder.txtUnit.setText(searchArrayList.get(position).getUnit());
        holder.progressBar.setVisibility(searchArrayList.get(position).getVisibilityProgress());
        if(searchArrayList.get(position).getVisibilityProgress() == View.VISIBLE)
        {
        	holder.progressBar.setProgress(Math.round(searchArrayList.get(position).getProgressNum()));
        }
 
        return convertView;
    }
 
    static class ViewHolder {
        TextView txtName;
        TextView txtValue;
        TextView txtUnit;
        ProgressBar progressBar;
    }
}

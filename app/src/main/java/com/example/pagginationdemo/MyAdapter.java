package com.example.pagginationdemo;

//Creating an Adapter Class for Setting Data to Items of Our RecyclerView
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements Filterable {
    //Initialize variable
    private ArrayList<MainData> dataArrayList;
    private ArrayList<MainData> dataArrayListAll;
    private Activity activity;
    //Create Constructor
    public MyAdapter(Activity activity, ArrayList<MainData> dataArrayList){
        this.activity=activity;
        this.dataArrayListAll=dataArrayList;
        this.dataArrayList = new ArrayList<>(dataArrayListAll);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Initialize view
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_main,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Initialize main data
        MainData data=dataArrayList.get(position);
        //set image with image view
        Glide.with(activity).load(data.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
        //set id on text view
        holder.textId.setText(data.getId());
        //set name on TextView
        holder.textView.setText(data.getName());


    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {
        @SuppressLint("SuspiciousIndentation")
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<MainData> filterMainDataList=new ArrayList<>();
            if (constraint == null || constraint.length()==0){
                filterMainDataList.addAll(dataArrayListAll);
           }
            for (MainData mainData: dataArrayListAll){
                if (mainData.name.toLowerCase().contains(constraint.toString().toLowerCase().trim()))
                filterMainDataList.add(mainData);

            }
            FilterResults results=new FilterResults();
            results.values=filterMainDataList;
            results.count=filterMainDataList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataArrayList.clear();
            dataArrayList.addAll((ArrayList)results.values);
            notifyDataSetChanged();

        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Initialization Variable
        ImageView imageView;
        TextView textId;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assign Variable
            textId=itemView.findViewById(R.id.text_id);
            imageView=itemView.findViewById(R.id.image_view);
            textView=itemView.findViewById(R.id.text_view);
        }
    }
}

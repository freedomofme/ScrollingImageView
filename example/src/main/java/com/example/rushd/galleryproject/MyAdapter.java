package com.example.rushd.galleryproject;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<CreateList> galleryList;
    private Context context;

    public MyAdapter(Context context, ArrayList<CreateList> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 0 :
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
                return new ViewHolder(view);
            case 1 :
                View view2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.scroll_img, viewGroup, false);
                return new ViewHolder2(view2);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 1) {
            ((ViewHolder2) holder).img.setImageResource((galleryList.get(position).getImage_ID()));
        } else {
            ((ViewHolder) holder).img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ((ViewHolder) holder).img.setImageResource((galleryList.get(position).getImage_ID()));
            //Picasso.with(context).load(galleryList.get(i).getImage_ID()).resize(240, 120).into(viewHolder.img);
            ((ViewHolder) holder).img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position == 5 ? 1 : 0;
    }


    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.img);
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder{
        private ImageView img;
        public ViewHolder2(View view) {
            super(view);
            img = view.findViewById(R.id.img);
        }
    }

}
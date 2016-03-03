package com.telenav.demoapp;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import com.telenav.expandableviewpager.R;

public class BookGridAdapter extends RecyclerView.Adapter<BookGridAdapter.ViewHolder> {
    private OnItemClickedListener listener;
    private List<Book> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView subtitle;
        public SimpleDraweeView image;
        public ViewGroup container;

        public ViewHolder(RelativeLayout v) {
            super(v);
            container = v;
            title = (TextView) v.findViewById(R.id.cell_text);
            subtitle = (TextView) v.findViewById(R.id.cell_subtitle);
            image = (SimpleDraweeView) v.findViewById(R.id.cell_img);
        }
    }

    public BookGridAdapter(List<Book> dataset) {
        mDataset = dataset;
    }

    @Override
    public BookGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.title.setText(mDataset.get(position).getTitle());
        holder.subtitle.setText(mDataset.get(position).getAuthor());
        Uri uri = Uri.parse(mDataset.get(position).getUrl());
        holder.image.setImageURI(uri);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClicked(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setListener(OnItemClickedListener listener) {
        this.listener = listener;
    }
}
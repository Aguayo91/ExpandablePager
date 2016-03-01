package telenav.com.demoapp;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import telenav.com.expandableviewpager.R;

/**
 * Created by dima on 26/02/16.
 */
public class BookGridAdapter extends RecyclerView.Adapter<BookGridAdapter.ViewHolder> {
    private OnItemClickedListener listener;
    private List<Book> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
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

    // Provide a suitable constructor (depends on the kind of dataset)
    public BookGridAdapter(List<Book> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BookGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setListener(OnItemClickedListener listener) {
        this.listener = listener;
    }
}
package com.telenav.demoapp;

import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.telenav.expandablepager.ExpandablePagerAdapter;
import com.telenav.expandableviewpager.R;

/**
 * Created by dima on 25/02/16.
 */
public class MyAdapter extends ExpandablePagerAdapter<Book> {
    public MyAdapter(List<Book> items) {
        super(items);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ViewGroup rootView = (ViewGroup) LayoutInflater.from(container.getContext()).inflate(R.layout.page, container, false);
        rootView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        ((TextView) rootView.findViewById(R.id.text)).setText(items.get(position).getDescription());
        ((TextView) rootView.findViewById(R.id.header_title)).setText(items.get(position).getTitle());
        ((TextView) rootView.findViewById(R.id.header_subtitle)).setText(items.get(position).getAuthor());
        ((SimpleDraweeView) rootView.findViewById(R.id.header_img)).setImageURI(Uri.parse(items.get(position).getUrl()));
        if (rootView.findViewById(R.id.cell_img) != null)
            ((SimpleDraweeView) rootView.findViewById(R.id.cell_img)).setImageURI(Uri.parse(items.get(position).getUrl()));

        TextView rating = ((TextView) rootView.findViewById(R.id.page_rating));
        setSpan(rating, "\\d\\.\\d / \\d\\.\\d");

        TextView reviews = ((TextView) rootView.findViewById(R.id.page_reviews));
        setSpan(reviews, "\\d+");

        TextView comments = ((TextView) rootView.findViewById(R.id.page_comments));
        setSpan(comments, "\\d+,*\\d+");

        return attach(container, rootView, position);
    }

    private void setSpan(TextView textView, String pattern) {
        float relativeSize = 1.5f;
        Pattern pat = Pattern.compile(pattern);
        Matcher m = pat.matcher(textView.getText());
        if (m.find()) {
            SpannableString span = new SpannableString(textView.getText());
            span.setSpan(new RelativeSizeSpan(relativeSize), 0, m.group(0).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(span);
        }
    }
}

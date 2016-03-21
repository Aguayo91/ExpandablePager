package com.telenav.demoapp.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.telenav.demoapp.Book;
import com.telenav.expandableviewpager.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookFragment extends Fragment {

    private Book myBook;

    public static BookFragment init(Book myBook) {
        BookFragment frag = new BookFragment();
        Bundle args = new Bundle();
        args.putString("title", myBook.getTitle());
        args.putString("author", myBook.getAuthor());
        args.putString("description", myBook.getDescription());
        args.putString("url", myBook.getUrl());
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myBook = new Book(getArguments().getString("title"));
            myBook.setAuthor(getArguments().getString("author"));
            myBook.setDescription(getArguments().getString("description"));
            myBook.setUrl(getArguments().getString("url"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.page, container, false);
        rootView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        ((TextView) rootView.findViewById(R.id.text)).setText(myBook.getDescription());
        ((TextView) rootView.findViewById(R.id.header_title)).setText(myBook.getTitle());
        ((TextView) rootView.findViewById(R.id.header_subtitle)).setText(myBook.getAuthor());
        ((SimpleDraweeView) rootView.findViewById(R.id.header_img)).setImageURI(Uri.parse(myBook.getUrl()));
        if (rootView.findViewById(R.id.cell_img) != null)
            ((SimpleDraweeView) rootView.findViewById(R.id.cell_img)).setImageURI(Uri.parse(myBook.getUrl()));

        TextView rating = ((TextView) rootView.findViewById(R.id.page_rating));
        setSpan(rating, "\\d\\.\\d / \\d\\.\\d");

        TextView reviews = ((TextView) rootView.findViewById(R.id.page_reviews));
        setSpan(reviews, "\\d+");

        TextView comments = ((TextView) rootView.findViewById(R.id.page_comments));
        setSpan(comments, "\\d+,*\\d+");
        return rootView;
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
package com.socketion.studion.autocompletekladr;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 04.10.2017.
 */

public class AddressAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;

    private final Context mContext;

    private List<AddressData> mResults;

    public AddressAdapter(Context context) {
        mContext = context;

        mResults = new ArrayList<AddressData>();
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public AddressData getItem(int index) {
        return mResults.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_address, parent, false);
        }
        AddressData book = getItem(position);
        ((TextView) convertView.findViewById(R.id.text1)).setText(book.value);
        ((TextView) convertView.findViewById(R.id.text2)).setText(book.unrestricted_value);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<AddressData> books = findBooks(mContext, constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = books;
                    filterResults.count = books.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mResults = (List<AddressData>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};

        return filter;
    }

    /**
     * Returns a search result for the given book title.
     */
    private List<AddressData> findBooks(Context mContext, String query) {
        List<AddressData> list=new ArrayList<>();
        new SenderAutoComplete().send(list,query);
        return list;
    }
}

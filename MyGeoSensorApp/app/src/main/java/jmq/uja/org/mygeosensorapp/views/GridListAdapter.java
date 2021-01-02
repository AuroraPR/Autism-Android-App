package jmq.uja.org.mygeosensorapp.views;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import jmq.uja.org.mygeosensorapp.R;

import java.util.ArrayList;

/**
 * Created by sonu on 08/02/17.
 */

public class GridListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> arrayList;
    private ArrayList<String> arrayList2;
    private LayoutInflater inflater;
    private boolean isListView;
    private SparseBooleanArray mSelectedItemsIds;

    public GridListAdapter(Context context, ArrayList<String> arrayList, ArrayList<String> arrayList2, boolean isListView) {
        this.context = context;
        this.arrayList = arrayList;
        this.arrayList2 =arrayList2;
        this.isListView = isListView;
        inflater = LayoutInflater.from(context);
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();

            //inflate the layout on basis of boolean
            if (isListView)
                view = inflater.inflate(R.layout.list_custom_row_layout, viewGroup, false);
            else
                view = inflater.inflate(R.layout.grid_custom_row_layout, viewGroup, false);

            viewHolder.label = (TextView) view.findViewById(R.id.label);
            viewHolder.label1 = (TextView) view.findViewById(R.id.label1);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);

            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();

        viewHolder.label.setText(arrayList.get(i));
        viewHolder.label1.setText(arrayList2.get(i));
        viewHolder.checkBox.setChecked(mSelectedItemsIds.get(i));

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCheckBox(i, !mSelectedItemsIds.get(i));
            }
        });

        viewHolder.label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCheckBox(i, !mSelectedItemsIds.get(i));
            }
        });

        viewHolder.label1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCheckBox(i, !mSelectedItemsIds.get(i));
            }
        });

        return view;
    }

    private class ViewHolder {
        private TextView label;
        private TextView label1;
        private CheckBox checkBox;
    }


    /**
     * Remove all checkbox Selection
     **/
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    /**
     * Check the Checkbox if not checked
     **/
    public void checkCheckBox(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, true);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    /**
     * Return the selected Checkbox IDs
     **/
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

}

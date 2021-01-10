package apr.autismapp.views;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import apr.autismapp.R;
import apr.autismapp.data.AsynRestSensorData;
import apr.autismapp.data.Task;
import retrofit2.Call;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GridListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> arrayList;
    private ArrayList<String> arrayList2;
    private LayoutInflater inflater;
    private boolean isListView;
    private Task[] taskArray;
    private SparseBooleanArray mSelectedItemsIds;
    private DateFormat df=DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);

    public GridListAdapter(Context context, Task[] array, boolean isListView) {
        this.context = context;
        taskArray = array;
        this.isListView = isListView;
        inflater = LayoutInflater.from(context);
        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        mSelectedItemsIds = new SparseBooleanArray();
        int i=0;
        for (Task task : array) {
            arrayList.add(task.name);//Adding items to recycler view
            arrayList2.add(df.format(new Date(task.date)));//Adding items to recycler view
            mSelectedItemsIds.append(i,task.check);
            i=i+1;
        }
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
        Call<Task[]> call=null;
        if (value) {
            mSelectedItemsIds.put(position, true);
            taskArray[position].setCheck(false);
            call = AsynRestSensorData.init().modifyTask(taskArray[position].user,taskArray[position].name,taskArray[position].date,true);

        }
        else {
            mSelectedItemsIds.delete(position);
            taskArray[position].setCheck(false);
            call = AsynRestSensorData.init().modifyTask(taskArray[position].user,taskArray[position].name,taskArray[position].date,false);
        }

        AsynRestSensorData.MyCall<Task[]> tasks=new AsynRestSensorData.MyCall<Task[]>(
                (e)->{notifyDataSetChanged();}
        );
        tasks.execute(call);
    }

    /**
     * Return the selected Checkbox IDs
     **/
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

}

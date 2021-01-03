package jmq.uja.org.mygeosensorapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jmq.uja.org.mygeosensorapp.R;
import jmq.uja.org.mygeosensorapp.data.AsynRestSensorData;
import jmq.uja.org.mygeosensorapp.data.Task;
import jmq.uja.org.mygeosensorapp.views.GridListAdapter;
import retrofit2.Call;

public class TasksFragment extends Fragment {
    private Context context;
    private GridListAdapter adapter;
    private ArrayList<String> arrayList;
    private ArrayList<String> arrayList2;
    private Button selectButton;
    private Locale locale=new Locale("es", "ES");
    private DateFormat df=DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
    private TextView newTaskName;
    private TextView newTaskDate;

    public TasksFragment(TextView newTaskName, TextView newTaskDate) {
        this.newTaskName = newTaskName;
        this.newTaskDate = newTaskDate;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectButton = (Button) view.findViewById(R.id.select_button);

        Call<Task[]> call=null;
        call = AsynRestSensorData.init().getTask("aurora", System.currentTimeMillis());
        AsynRestSensorData.MyCall<Task[]> tasks=new AsynRestSensorData.MyCall<Task[]>(
                (e)->{loadListView(view,e);}
        );
        tasks.execute(call);
        onClickEvent(view);
    }


    private void loadListView(View view, Task[] array) {

        ListView listView = (ListView) view.findViewById(R.id.list_view);
        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        for (Task task : array) {
            arrayList.add(task.name);//Adding items to recycler view

            arrayList2.add(df.format(new Date(task.date)));//Adding items to recycler view
        }

        adapter = new GridListAdapter(context, arrayList, arrayList2, true);
        listView.setAdapter(adapter);
    }

    private void onClickEvent(View view) {
        view.findViewById(R.id.show_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray selectedRows;
                //Check if item is selected or not via size
                StringBuilder stringBuilder = new StringBuilder();
                int remainingTasks=0;
                if(arrayList!=null) {
                    selectedRows = adapter.getSelectedIds();//Get the selected ids from adapter
                    remainingTasks = arrayList.size() - selectedRows.size();
                }
                Toast.makeText(context, "Tareas restantes: " + remainingTasks, Toast.LENGTH_SHORT).show();

            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check the current text of Select Button
                if (selectButton.getText().toString().equals(getResources().getString(R.string.add_task))) {
                    FormDialogFragment form = FormDialogFragment.newInstance(newTaskName.getText().toString(),
                            newTaskDate.getText().toString());
                    form.show(getActivity().getSupportFragmentManager(), FormDialogFragment.TAG);

                }


            }
        });
    }



}
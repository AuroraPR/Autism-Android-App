package apr.autismapp.activities;

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

import java.util.ArrayList;
import java.util.Calendar;

import apr.autismapp.R;
import apr.autismapp.data.AsynRestSensorData;
import apr.autismapp.data.Task;
import apr.autismapp.views.GridListAdapter;
import retrofit2.Call;

public class TasksFragment extends Fragment {
    private Context context;
    private GridListAdapter adapter;
    private Button selectButton;
    private TextView newTaskName;
    private TextView newTaskDate;
    private String username;

    public TasksFragment(TextView newTaskName, TextView newTaskDate, String username) {
        this.newTaskName = newTaskName;
        this.newTaskDate = newTaskDate;
        this.username = username;
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

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Call<Task[]> call=null;
        call = AsynRestSensorData.init().getTask(username, c.getTime().getTime());
        AsynRestSensorData.MyCall<Task[]> tasks=new AsynRestSensorData.MyCall<Task[]>(
                (e)->{loadListView(view,e);}
        );
        tasks.execute(call);
        onClickEvent(view);
    }


    private void loadListView(View view, Task[] array) {

        ListView listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new GridListAdapter(context,array,true);
        listView.setAdapter(adapter);

    }

    private void onClickEvent(View view) {
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
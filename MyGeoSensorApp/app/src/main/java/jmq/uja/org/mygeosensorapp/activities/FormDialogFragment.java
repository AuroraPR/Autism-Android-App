package jmq.uja.org.mygeosensorapp.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import jmq.uja.org.mygeosensorapp.R;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class FormDialogFragment extends AppCompatDialogFragment {

  public static final String TAG = FormDialogFragment.class.getSimpleName();

  private static final String ARG_FIRSTNAME = "ARG_FIRSTNAME";
  private static final String ARG_LASTNAME = "ARG_LASTNAME";

  private TextInputLayout textInputLayoutFirstName;
  private EditText textInputFirstName;
  private FormDialogListener listener;

  private SimpleDateFormat mSimpleDateFormat;
  private Calendar mCalendar;
  private Activity mActivity;
  private Button mDate;


  public static FormDialogFragment newInstance(String firstName, String lastName) {
    Bundle args = new Bundle();
    args.putString(ARG_FIRSTNAME, firstName);
    args.putString(ARG_LASTNAME, lastName);

    FormDialogFragment frag = new FormDialogFragment();
    frag.setArguments(args);

    return frag;
  }

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);
    if (context instanceof FormDialogListener) {
      listener = (FormDialogListener) context;
    } else {
      throw new IllegalArgumentException("context is not FormDialogListener");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    View content = LayoutInflater.from(getContext()).inflate(R.layout.fragment_form, null);

    mActivity = getActivity();
    mSimpleDateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.getDefault());
    mDate = (Button) content.findViewById(R.id.textInputLastName);
    mDate.setText("Selecciona la fecha");
    mDate.setOnClickListener(textListener);

    setupContent(content);

    AlertDialog alertDialog = new MaterialAlertDialogBuilder(getContext())
            .setView(content)
            .setCancelable(true)
            .setNegativeButton(getString(R.string.cancel), null)
            .setTitle(R.string.edit)
            .setPositiveButton(getString(R.string.save), null)
            .create();
    //.setPositiveButton(getString(R.string.save), (dialogInterface, i) -> returnValues());

    //asegura que se muestre el teclado con el diálogo completo
    alertDialog.getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
                    | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    return alertDialog;
  }

  @Override
  public void onStart() {
    super.onStart();
    Button positiveButton = ((AlertDialog) getDialog()).getButton(Dialog.BUTTON_POSITIVE);
    positiveButton.setOnClickListener(view -> {
      if (validate()) {
        returnValues();
        getDialog().dismiss();
      }
    });
  }

  private boolean validate() {
    if (TextUtils.isEmpty(textInputFirstName.getText())) {
      textInputLayoutFirstName.setError(getString(R.string.mandatory));
      textInputLayoutFirstName.setErrorEnabled(true);
      return false;
    }
    return true;
  }

  private void returnValues() {
    listener.update(textInputFirstName.getText().toString(),
            mDate.getText().toString());
  }

  private void setupContent(View content) {
    textInputLayoutFirstName = content.findViewById(R.id.textInputLayoutFirstName);
    textInputFirstName = content.findViewById(R.id.textInputFirstName);
    mDate = content.findViewById(R.id.textInputLastName);
    textInputFirstName.setText(getArguments().getString(ARG_FIRSTNAME));
    textInputFirstName.setSelection(getArguments().getString(ARG_FIRSTNAME).length());
    mDate.setOnEditorActionListener((textView, actionId, keyEvent) -> {
      if (actionId == EditorInfo.IME_ACTION_DONE) {
        returnValues();
        dismiss();
        return true;
      }
      return false;
    });

    textInputFirstName.addTextChangedListener(new TextWatcher() {
      @Override
      public void afterTextChanged(Editable s) {
        if (textInputFirstName.getVisibility() == View.VISIBLE) {
          textInputLayoutFirstName.setError(null);
        }
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //nothing here
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        //nothing here
      }
    });
  }




  /* Define the onClickListener, and start the DatePickerDialog with users current time */
  private final View.OnClickListener textListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      mCalendar = Calendar.getInstance();
      new DatePickerDialog(mActivity, mDateDataSet, mCalendar.get(Calendar.YEAR),
              mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
  };

  /* After user decided on a date, store those in our calendar variable and then start the TimePickerDialog immediately */
  private final DatePickerDialog.OnDateSetListener mDateDataSet = new DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
      mCalendar.set(Calendar.YEAR, year);
      mCalendar.set(Calendar.MONTH, monthOfYear);
      mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
      new TimePickerDialog(mActivity, mTimeDataSet, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
    }
  };

  /* After user decided on a time, save them into our calendar instance, and now parse what our calendar has into the TextView */
  private final TimePickerDialog.OnTimeSetListener mTimeDataSet = new TimePickerDialog.OnTimeSetListener() {
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
      mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
      mCalendar.set(Calendar.MINUTE, minute);
      mDate.setText(mSimpleDateFormat.format(mCalendar.getTime()));
    }
  };

}

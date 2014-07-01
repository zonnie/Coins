package com.moneyifyapp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moneyifyapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateExpenseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateExpenseFragment extends Fragment
{
    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/

    Button mSubmitButton;
    Button mCancelButton;
    EditText mExpenseDescription;
    EditText mExpenseValue;
    private OnFragmentInteractionListener mListener;


    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    /**
     *
     *
     */
    public static CreateExpenseFragment newInstance()
    {
        CreateExpenseFragment fragment = new CreateExpenseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public CreateExpenseFragment()
    {
        // Required empty public constructor
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
        }
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_create_expense_layout, container, false);

        // Save the views
        mSubmitButton = (Button)view.findViewById(R.id.submitButton);
        mCancelButton = (Button)view.findViewById(R.id.cancelAddButton);
        mExpenseDescription = (EditText)view.findViewById(R.id.addExpenseDescription);
        mExpenseValue = (EditText)view.findViewById(R.id.addExpenseSum);

        // Bind listener
        mSubmitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSumbitPressed();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onCancelPressed();
            }
        });

        return view;
    }

    /**
     *
     *
     */
    public void onSumbitPressed()
    {
        if (mListener != null)
        {
            String description = mExpenseDescription.getText().toString();
            String sum = mExpenseValue.getText().toString();

            if(description.isEmpty() || sum.isEmpty())
            {
                Toast.makeText(getActivity(), "Please fill all required info", Toast.LENGTH_SHORT);
            }
            else
            {
                mListener.onAddExpense(description, sum);
            }
        }
    }

    /**
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     *
     */
    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interface for containing activity.
     */
    public interface OnFragmentInteractionListener
    {
        public void onAddExpense(String addDescription, String addSum);

        public void cancel();
    }

    /**
     *
     */
    private void onCancelPressed()
    {
        mListener.cancel();
    }
}

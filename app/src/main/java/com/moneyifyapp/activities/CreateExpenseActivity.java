package com.moneyifyapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.moneyifyapp.R;
import com.moneyifyapp.fragments.CreateExpenseFragment;
import com.moneyifyapp.model.SingleExpense;

public class CreateExpenseActivity extends Activity implements CreateExpenseFragment.OnFragmentInteractionListener
{
    /********************************************************************/
    /**                          Section                               **/
    /********************************************************************/


    /********************************************************************/
    /**                          Section                               **/
    /********************************************************************/


    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_expense_layout);
        if (savedInstanceState == null)
        {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new CreateExpenseFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_expense, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param addDescription
     * @param addSum
     */
    @Override
    public void onAddExpense(String addDescription, String addSum)
    {
        Intent data = getIntent();
        data.putExtra(SingleExpense.EXPENSE_KEY_DESCRIPTION, addDescription);
        data.putExtra(SingleExpense.EXPENSE_KEY_VALUE, addSum);

        setResult(ExpensesActivity.EXPENSE_RESULT_OK, data);

        finish();
    }

    @Override
    public void cancel()
    {
        Intent data = getIntent();
        setResult(ExpensesActivity.EXPENSE_RESULT_CANCELED, null);

        finish();
    }
}

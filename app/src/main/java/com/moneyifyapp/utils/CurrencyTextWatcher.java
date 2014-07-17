package com.moneyifyapp.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;

/**
 *
 */
public class CurrencyTextWatcher implements TextWatcher
{
    private NumberFormat mNumberFormat = NumberFormat.getNumberInstance();
    private EditText mTextField;
    private static final int MAX_REGULAR_DIGITS = 10;
    private static final int MAX_DECIMAL_DIGITS = 1;
    private static final int MAX_LENGTH = MAX_REGULAR_DIGITS + MAX_DECIMAL_DIGITS + 1;

    /**
     */
    public CurrencyTextWatcher(EditText textField)
    {
        this.mTextField = textField;
        mNumberFormat.setMaximumIntegerDigits(MAX_REGULAR_DIGITS);
        mNumberFormat.setMaximumFractionDigits(MAX_DECIMAL_DIGITS);
        mNumberFormat.setGroupingUsed(false);
    }

    /**
     */
    public int countOccurrences(String stringToLookIn, char charToCount)
    {
        int count = 0;
        for (int i = 0; i < stringToLookIn.length(); i++)
            if (stringToLookIn.charAt(i) == charToCount)
                count++;
        return count;
    }

    @Override
    public void afterTextChanged(Editable editTextString)
    {
        // Removed to avoid endless recurrsion
        mTextField.removeTextChangedListener(this);

        String textFieldString = editTextString.toString();
        int len = textFieldString.length();
        int dots = countOccurrences(textFieldString, '.');

        boolean validLength;

        if(dots == 0)
            validLength = len != (MAX_REGULAR_DIGITS + 1);
        else
            validLength = len < (MAX_LENGTH + 1);

        boolean shouldParse = dots <= 1 && validLength;

        if (shouldParse)
        {
            if (len > 1 && textFieldString.lastIndexOf(".") != len - 1)
            {
                try
                {
                    Double d = Double.parseDouble(textFieldString);
                    if (d != null)
                        mTextField.setText(mNumberFormat.format(d));
                }
                catch (NumberFormatException e)
                {
                }
            }
        }

        // Reattach listener
        mTextField.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }
}
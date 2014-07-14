package com.moneyifyapp.model.enums;

/**
 * Created by Zonnie_Work on 30/06/2014.
 */
public enum Months
{
    January(1), Febuary(2), March(3), April(4), May(5),
    June(6), July(7), August(8), September(9), October(10),
    November(11), December(12);

    private int mMonthNumber;

    private Months(int monthNumber)
    {
        mMonthNumber = monthNumber;
    }

    /**
     * @return
     */
    public int getMonthNumber()
    {
        return mMonthNumber;
    }

    /**
     * @param number
     * @return
     */
    public static String getMonthNameByNumber(int number)
    {
        String month = "";

        for (Months curMonth : Months.values())
        {
            if (curMonth.getMonthNumber() == number)
            {
                month = curMonth.toString().toUpperCase();
            }
        }

        return month;
    }
}
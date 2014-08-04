package com.moneyifyapp.model.enums;

import java.util.ArrayList;
import java.util.List;

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
     */
    public int getMonthNumber()
    {
        return mMonthNumber;
    }

    /**
     */
    public static String getMonthNameByNumber(int number)
    {
        String month = "";

        for (Months curMonth : Months.values())
            if (curMonth.getMonthNumber() == number+1)
                month = curMonth.toString();

        return month;
    }

    /**
     */
    public static List<String> getMonthList()
    {
        List<String> list = new ArrayList<String>();

        for(Months curMonth : Months.values())
            list.add(curMonth.toString());

        return list;
    }

    /**
     */
    public static int getMonthByName(String name)
    {
        int month = 0;

        for(Months cur : Months.values())
            if(cur.toString().equals(name))
                month = cur.getMonthNumber();

        return month;
    }
}
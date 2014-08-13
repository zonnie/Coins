package com.moneyifyapp.activities.analytics.intrfaces;

import com.moneyifyapp.model.MonthTransactions;

/**
 */
public interface Sumable
{
    public int sum(MonthTransactions.SubsetType type);
}

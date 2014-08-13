package com.moneyifyapp.activities.analytics.intrfaces;

import com.moneyifyapp.model.MonthTransactions;

/**
 */
public interface Sumable
{
    public double sum(MonthTransactions.SubsetType type);
}

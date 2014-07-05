package com.moneyifyapp.model;


import com.moneyifyapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zonnie_Work on 03/07/2014.
 */
public class Images
{
    /********************************************************************/
    /**                          Members                               **/
    /********************************************************************/

    private static List<Integer> mImages;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    static
    {
        mImages = new ArrayList<Integer>();
        mImages.add(R.drawable.shop);
        mImages.add(R.drawable.ice_cream);
        mImages.add(R.drawable.internet);
        mImages.add(R.drawable.cell);
        mImages.add(R.drawable.gaming);
        mImages.add(R.drawable.camera);
        mImages.add(R.drawable.trip);
        mImages.add(R.drawable.drinks);
        mImages.add(R.drawable.market);

    }

    /**
     *
     * @param position
     * @return
     */
    public static int get(int position)
    {
        return mImages.get(position);
    }

    /**
     *
     * @return
     */
    public static int getCount()
    {
        return mImages.size();
    }

    /**
     *
     * @return
     */
    public static List<Integer> getImages()
    {
        return mImages;
    }
}

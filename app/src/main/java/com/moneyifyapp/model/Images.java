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
    private static List<String> mImageCaptions;

    /********************************************************************/
    /**                          Methods                               **/
    /********************************************************************/

    static
    {
        mImages = new ArrayList<Integer>();
        mImageCaptions = new ArrayList<String>();

        mImages.add(R.drawable.shop);
        mImageCaptions.add("Shopping");

        mImages.add(R.drawable.ice_cream);
        mImageCaptions.add("Ice Cream");

        mImages.add(R.drawable.internet);
        mImageCaptions.add("Internet");

        mImages.add(R.drawable.cell);
        mImageCaptions.add("Cell Phone");

        mImages.add(R.drawable.gaming);
        mImageCaptions.add("Gaming");

        mImages.add(R.drawable.camera);
        mImageCaptions.add("Camera");

        mImages.add(R.drawable.trip);
        mImageCaptions.add("Trips");

        mImages.add(R.drawable.drinks);
        mImageCaptions.add("Drinks");

        mImages.add(R.drawable.market);
        mImageCaptions.add("Groceries");

        mImages.add(R.drawable.transport);
        mImageCaptions.add("Transportation");

        mImages.add(R.drawable.movies);
        mImageCaptions.add("Movies");

        mImages.add(R.drawable.food_outside);
        mImageCaptions.add("Restaurants");

        mImages.add(R.drawable.home);
        mImageCaptions.add("Home");

        mImages.add(R.drawable.music);
        mImageCaptions.add("Music");

        mImages.add(R.drawable.bank);
        mImageCaptions.add("Government");

        mImages.add(R.drawable.savings);
        mImageCaptions.add("Savings");

        mImages.add(R.drawable.tv);
        mImageCaptions.add("TV");

        mImages.add(R.drawable.computers);
        mImageCaptions.add("Tech Stuff");

        mImages.add(R.drawable.cloths);
        mImageCaptions.add("Cloths");

        mImages.add(R.drawable.car);
        mImageCaptions.add("Car");
    }

    /**
     *
     * @param position
     * @return
     */
    public static int getImageByPosition(int position)
    {
        int resource = 0;

        try
        {
            resource = mImages.get(position);
        }
        catch (IndexOutOfBoundsException e)
        {
            resource = mImages.get(0);
        }

        return  resource;
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

    /**
     *
     * @return
     */
    public static List<String> getCaptions()
    {
        return  mImageCaptions;
    }
}

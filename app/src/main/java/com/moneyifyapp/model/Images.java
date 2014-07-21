package com.moneyifyapp.model;


import com.moneyifyapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class Images
{
    private static List<Integer> mImages;
    private static List<String> mImageCaptions;

    static
    {
        mImages = new ArrayList<Integer>();
        mImageCaptions = new ArrayList<String>();

        mImages.add(R.drawable.shop);
        mImageCaptions.add("Shopping");

        mImages.add(R.drawable.ice_cream);
        mImageCaptions.add("Sweets");

        mImages.add(R.drawable.internet);
        mImageCaptions.add("Internet");

        mImages.add(R.drawable.cell);
        mImageCaptions.add("Celluar Charges");

        mImages.add(R.drawable.gaming);
        mImageCaptions.add("Gaming");

        mImages.add(R.drawable.camera);
        mImageCaptions.add("Camera Equipment");

        mImages.add(R.drawable.trip);
        mImageCaptions.add("Holidays");

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
        mImageCaptions.add("Concerts");

        mImages.add(R.drawable.bank);
        mImageCaptions.add("Banking");

        //mImages.add(R.drawable.savings);
        //mImageCaptions.add("Savings");

        mImages.add(R.drawable.tv);
        mImageCaptions.add("TV");

        mImages.add(R.drawable.computers);
        mImageCaptions.add("Computer");

        mImages.add(R.drawable.cloths);
        mImageCaptions.add("Cloths");

        mImages.add(R.drawable.car);
        mImageCaptions.add("Car");

        mImages.add(R.drawable.bike);
        mImageCaptions.add("Bike");

        mImages.add(R.drawable.book);
        mImageCaptions.add("Books");

        mImages.add(R.drawable.buds);
        mImageCaptions.add("Music Downloads");

        //mImages.add(R.drawable.chart);
        //mImageCaptions.add("Invesments");

        mImages.add(R.drawable.gift);
        mImageCaptions.add("Gift");

        mImages.add(R.drawable.insurance);
        mImageCaptions.add("Insurance");

        mImages.add(R.drawable.junk);
        mImageCaptions.add("Junkfood");

        mImages.add(R.drawable.lcd);
        mImageCaptions.add("Electronics");

        //mImages.add(R.drawable.mp3);
        //mImageCaptions.add("Downloaded Music");

        mImages.add(R.drawable.print);
        mImageCaptions.add("Printing");

        mImages.add(R.drawable.safe);
        mImageCaptions.add("Savings");

        mImages.add(R.drawable.saving);
        mImageCaptions.add("Cash");

        mImages.add(R.drawable.world);
        mImageCaptions.add("Flights");

        mImages.add(R.drawable.coffe);
        mImageCaptions.add("Coffe");

        mImages.add(R.drawable.parking);
        mImageCaptions.add("Parking");

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

    /**
     */
    public static String getCaptionByImage(int resourceId)
    {
        String caption = "";

        for(int i = 0; i < Images.getCount(); ++i)
        {
            if(Images.getImages().get(i) == resourceId)
                caption = Images.getCaptions().get(i);
        }

        return caption;
    }
}

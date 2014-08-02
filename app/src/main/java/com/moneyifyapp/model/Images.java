package com.moneyifyapp.model;


import com.moneyifyapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class Images
{
    private static List<ImageWithCaption> mImages;

    static
    {
        mImages = new ArrayList<ImageWithCaption>();

        mImages.add(new ImageWithCaption(R.drawable.shop, "Shopping"));
        mImages.add(new ImageWithCaption(R.drawable.ice_cream, "Sweets"));
        mImages.add(new ImageWithCaption(R.drawable.internet, "Internet"));
        mImages.add(new ImageWithCaption(R.drawable.cell, "Cellphone"));
        mImages.add(new ImageWithCaption(R.drawable.gaming, "Gaming"));
        mImages.add(new ImageWithCaption(R.drawable.camera, "Camera Stuff"));
        mImages.add(new ImageWithCaption(R.drawable.trip, "Holidays"));
        mImages.add(new ImageWithCaption(R.drawable.drinks, "Drinks"));
        mImages.add(new ImageWithCaption(R.drawable.market, "Groceries"));
        mImages.add(new ImageWithCaption(R.drawable.transport, "Transportation"));
        mImages.add(new ImageWithCaption(R.drawable.movies, "Movies"));
        mImages.add(new ImageWithCaption(R.drawable.food_outside, "Restaurants"));
        mImages.add(new ImageWithCaption(R.drawable.home, "Rent"));
        mImages.add(new ImageWithCaption(R.drawable.music, "Concerts"));
        mImages.add(new ImageWithCaption(R.drawable.bank, "Banking"));
        mImages.add(new ImageWithCaption(R.drawable.tv, "TV"));
        mImages.add(new ImageWithCaption(R.drawable.computers, "Computer"));
        mImages.add(new ImageWithCaption(R.drawable.cloths, "Cloths"));
        mImages.add(new ImageWithCaption(R.drawable.car, "Car"));
        mImages.add(new ImageWithCaption(R.drawable.book, "Books"));
        mImages.add(new ImageWithCaption(R.drawable.buds, "Buds"));
        mImages.add(new ImageWithCaption(R.drawable.invesment, "Invesments"));
        mImages.add(new ImageWithCaption(R.drawable.gift, "Gift"));
        mImages.add(new ImageWithCaption(R.drawable.insurance, "Insurance"));
        mImages.add(new ImageWithCaption(R.drawable.junk, "Junkfood"));
        mImages.add(new ImageWithCaption(R.drawable.lcd, "Electronics"));
        mImages.add(new ImageWithCaption(R.drawable.save, "Savings"));
        mImages.add(new ImageWithCaption(R.drawable.saving, "Cash"));
        mImages.add(new ImageWithCaption(R.drawable.coffe, "Coffee"));
        mImages.add(new ImageWithCaption(R.drawable.parking, "Parking"));
        mImages.add(new ImageWithCaption(R.drawable.health, "Health"));
        mImages.add(new ImageWithCaption(R.drawable.loan, "Loan"));
        mImages.add(new ImageWithCaption(R.drawable.sports, "Sports"));
        mImages.add(new ImageWithCaption(R.drawable.dev, "Development"));
        mImages.add(new ImageWithCaption(R.drawable.gym, "Gym"));
        mImages.add(new ImageWithCaption(R.drawable.hobby, "Hobbies"));
        mImages.add(new ImageWithCaption(R.drawable.art, "Art"));
        mImages.add(new ImageWithCaption(R.drawable.salary, "Salary"));
        mImages.add(new ImageWithCaption(R.drawable.in_app, "In-App "));
        mImages.add(new ImageWithCaption(R.drawable.stationery, "Stationary"));
        Collections.sort(mImages);

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
            resource = mImages.get(position).getImage();
        }
        catch (IndexOutOfBoundsException e)
        {
            resource = mImages.get(0).getImage();
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
        List<Integer> resources = new ArrayList<Integer>();

        for(ImageWithCaption cur : mImages)
        {
            resources.add(cur.getImage());
        }

        return resources;
    }

    /**
     *
     * @return
     */
    public static List<String> getCaptions()
    {
        List<String> resources = new ArrayList<String>();

        for(ImageWithCaption cur : mImages)
        {
            resources.add(cur.getCaption());
        }

        return resources;
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

    /**
     *
     */
    public static class ImageWithCaption implements Comparable<ImageWithCaption>
    {
        private Integer mImageResource;
        private String mImageCaption;

        ImageWithCaption(int resource, String caption)
        {
            this.mImageResource = resource;
            this.mImageCaption = caption;
        }

        public Integer getImage()
        {
            return mImageResource;
        }

        public String getCaption()
        {
            return mImageCaption;
        }

        /**
         */
        @Override
        public int compareTo(ImageWithCaption another)
        {
            return mImageCaption.toUpperCase().compareTo(another.getCaption().toUpperCase());
        }
    }

    /**
     */
    public static int getDefaultImage()
    {
        int imageIndex = 0;

        for(int i = 0; i < mImages.size(); ++i)
        {
            if(mImages.get(i).mImageResource == R.drawable.shop)
                imageIndex = i;
        }

        return imageIndex;
    }

    public static List<ImageWithCaption> getImageWithCaptions()
    {
        return mImages;
    }
}

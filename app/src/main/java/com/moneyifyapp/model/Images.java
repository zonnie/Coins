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

        mImages.add(new ImageWithCaption(R.drawable.shop, "Shopping", R.drawable.shop_small));
        mImages.add(new ImageWithCaption(R.drawable.ice_cream, "Sweets", R.drawable.ice_cream_small));
        mImages.add(new ImageWithCaption(R.drawable.internet, "Internet", R.drawable.internet_small));
        mImages.add(new ImageWithCaption(R.drawable.cell, "Cellphone", R.drawable.cell_small));
        mImages.add(new ImageWithCaption(R.drawable.gaming, "Gaming", R.drawable.gaming_small));
        mImages.add(new ImageWithCaption(R.drawable.camera, "Camera Stuff", R.drawable.camera_small));
        mImages.add(new ImageWithCaption(R.drawable.trip, "Holidays", R.drawable.trip_small));
        mImages.add(new ImageWithCaption(R.drawable.drinks, "Drinks", R.drawable.drinks_small));
        mImages.add(new ImageWithCaption(R.drawable.market, "Groceries", R.drawable.market_small));
        mImages.add(new ImageWithCaption(R.drawable.transport, "Transportation", R.drawable.transport_small));
        mImages.add(new ImageWithCaption(R.drawable.movies, "Movies", R.drawable.movies_small));
        mImages.add(new ImageWithCaption(R.drawable.food_outside, "Restaurants", R.drawable.food_outside_small));
        mImages.add(new ImageWithCaption(R.drawable.home, "Rent",R.drawable.home_small));
        mImages.add(new ImageWithCaption(R.drawable.music, "Concerts",R.drawable.music_small));
        mImages.add(new ImageWithCaption(R.drawable.bank, "Banking",R.drawable.bank_small));
        mImages.add(new ImageWithCaption(R.drawable.tv, "TV",R.drawable.tv_small));
        mImages.add(new ImageWithCaption(R.drawable.computers, "Computer", R.drawable.computers_small));
        mImages.add(new ImageWithCaption(R.drawable.cloths, "Cloths",R.drawable.cloths_small));
        mImages.add(new ImageWithCaption(R.drawable.car, "Car",R.drawable.car_small));
        mImages.add(new ImageWithCaption(R.drawable.book, "Books",R.drawable.book_small));
        mImages.add(new ImageWithCaption(R.drawable.buds, "Buds",R.drawable.buds_small));
        mImages.add(new ImageWithCaption(R.drawable.invesment, "Invesments",R.drawable.invesment_small));
        mImages.add(new ImageWithCaption(R.drawable.gift, "Gift",R.drawable.gift_small));
        mImages.add(new ImageWithCaption(R.drawable.insurance, "Insurance",R.drawable.insurance_small));
        mImages.add(new ImageWithCaption(R.drawable.junk, "Junkfood",R.drawable.junk_small));
        mImages.add(new ImageWithCaption(R.drawable.lcd, "Electronics",R.drawable.lcd_small));
        mImages.add(new ImageWithCaption(R.drawable.save, "Savings",R.drawable.save_small));
        mImages.add(new ImageWithCaption(R.drawable.saving, "Cash",R.drawable.saving_small));
        mImages.add(new ImageWithCaption(R.drawable.coffe, "Coffee",R.drawable.coffe_small));
        mImages.add(new ImageWithCaption(R.drawable.parking, "Parking",R.drawable.parking_small));
        mImages.add(new ImageWithCaption(R.drawable.health, "Health",R.drawable.health_small));
        mImages.add(new ImageWithCaption(R.drawable.loan, "Loan",R.drawable.loan_small));
        mImages.add(new ImageWithCaption(R.drawable.sports, "Sports",R.drawable.sports_small));
        mImages.add(new ImageWithCaption(R.drawable.dev, "Development",R.drawable.dev_small));
        mImages.add(new ImageWithCaption(R.drawable.gym, "Gym",R.drawable.gym_small));
        mImages.add(new ImageWithCaption(R.drawable.hobby, "Hobbies",R.drawable.hobby_small));
        mImages.add(new ImageWithCaption(R.drawable.art, "Art",  R.drawable.art_small));
        mImages.add(new ImageWithCaption(R.drawable.salary, "Salary",R.drawable.salary_small));
        mImages.add(new ImageWithCaption(R.drawable.in_app, "In-App",R.drawable.in_app_small));
        mImages.add(new ImageWithCaption(R.drawable.stationery, "Stationary",R.drawable.stationery_small));
        Collections.sort(mImages);

    }

    /**
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
     */
    public static int getSmallImageByPosition(int position)
    {
        int resource = 0;

        try
        {
            resource = mImages.get(position).getImageSmall();
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
        private Integer mImageSmall;
        private String mImageCaption;

        ImageWithCaption(int resource, String caption, int imageSmall)
        {
            this.mImageResource = resource;
            this.mImageCaption = caption;
            this.mImageSmall = imageSmall;
        }

        public Integer getImage()
        {
            return mImageResource;
        }

        public String getCaption()
        {
            return mImageCaption;
        }

        public Integer getImageSmall(){return mImageSmall;}

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

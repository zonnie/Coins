package com.moneyifyapp.model;


import com.moneyifyapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class Images
{
    private static List<ImageWithCaption> mSortedImages;
    private static List<ImageWithCaption> mUnsortedResouceIndexList;
    private static List<ImageWithCaption> mWalletIcons;
    private static List<ImageWithCaption> mWalletSortedIcons;

    public static List<ImageWithCaption> getWalletSorted(){return mWalletSortedIcons;}
    public static List<ImageWithCaption> getSorted(){return mSortedImages;}
    public static List<ImageWithCaption> getWalletUnsorted(){return mWalletIcons;}
    public static List<ImageWithCaption> getUnsorted(){return mUnsortedResouceIndexList;}

    static
    {
        mWalletIcons = new ArrayList<ImageWithCaption>();
        mWalletSortedIcons = new ArrayList<ImageWithCaption>();

        mWalletIcons.add(new ImageWithCaption(R.drawable.wallet, "Private", R.drawable.wallet_small));
        mWalletIcons.add(new ImageWithCaption(R.drawable.bbq, "Event", R.drawable.bbq_small));
        mWalletIcons.add(new ImageWithCaption(R.drawable.invesments, "Invesments", R.drawable.invesments_small));
        mWalletIcons.add(new ImageWithCaption(R.drawable.biz, "Bisnuess", R.drawable.biz_small));
        mWalletIcons.add(new ImageWithCaption(R.drawable.spouse, "Love", R.drawable.spouse_small));

        for(int i = 0; i < mWalletIcons.size(); i++)
            mWalletSortedIcons.add(mWalletIcons.get(i));

        Collections.sort(mWalletSortedIcons);
    }

    static
    {
        mSortedImages = new ArrayList<ImageWithCaption>();
        mUnsortedResouceIndexList = new ArrayList<ImageWithCaption>();

        mSortedImages.add(new ImageWithCaption(R.drawable.shop, "Shopping", R.drawable.shop_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.ice_cream, "Sweets", R.drawable.ice_cream_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.internet, "Internet", R.drawable.internet_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.cell, "Cellphone", R.drawable.cell_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.gaming, "Gaming", R.drawable.gaming_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.camera, "Camera Stuff", R.drawable.camera_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.trip, "Holidays", R.drawable.trip_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.drinks, "Drinks", R.drawable.drinks_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.market, "Groceries", R.drawable.market_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.transport, "Transportation", R.drawable.transport_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.movies, "Movies", R.drawable.movies_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.food_outside, "Restaurants", R.drawable.food_outside_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.home, "Rent",R.drawable.home_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.music, "Concerts",R.drawable.music_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.bank, "Banking",R.drawable.bank_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.tv, "TV",R.drawable.tv_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.computers, "Computer", R.drawable.computers_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.cloths, "Cloths",R.drawable.cloths_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.car, "Car",R.drawable.car_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.book, "Books",R.drawable.book_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.buds, "Buds",R.drawable.buds_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.invesment, "Invesments",R.drawable.invesment_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.gift, "Gift",R.drawable.gift_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.insurance, "Insurance",R.drawable.insurance_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.junk, "Junkfood",R.drawable.junk_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.lcd, "Electronics",R.drawable.lcd_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.save, "Savings",R.drawable.save_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.saving, "Cash",R.drawable.saving_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.coffe, "Coffee",R.drawable.coffe_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.parking, "Parking",R.drawable.parking_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.health, "Health",R.drawable.health_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.loan, "Loan",R.drawable.loan_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.sports, "Sports",R.drawable.sports_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.dev, "Development",R.drawable.dev_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.gym, "Gym",R.drawable.gym_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.hobby, "Hobbies",R.drawable.hobby_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.art, "Art",  R.drawable.art_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.salary, "Salary",R.drawable.salary_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.in_app, "In-App",R.drawable.in_app_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.stationery, "Stationary",R.drawable.stationery_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.pills, "Meds",R.drawable.pills_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.credit, "Credit Card",R.drawable.credit_small));
        mSortedImages.add(new ImageWithCaption(R.drawable.toys, "Toys",R.drawable.toys_small));

        for(int i = 0; i < mSortedImages.size(); i++)
            mUnsortedResouceIndexList.add(mSortedImages.get(i));

        Collections.sort(mSortedImages);

    }

    /**
     */
    public static int getImageByPosition(int position, List<ImageWithCaption> unsorted)
    {
        int resource;

        try
        {
            resource = unsorted.get(position).getImage();
        }
        catch (IndexOutOfBoundsException e)
        {
            resource = unsorted.get(0).getImage();
        }

        return  resource;
    }

    /**
     */
    public static String getCaptioneByPosition(List<ImageWithCaption> unsorted, int position)
    {
        String resource;

        try
        {
            resource = unsorted.get(position).getCaption();
        }
        catch (IndexOutOfBoundsException e)
        {
            resource = unsorted.get(0).getCaption();
        }

        return  resource;
    }

    /**
     */
    public static int getImageIndexBySortedPosition(int position)
    {
        int resourceIndex = 0;

        ImageWithCaption image = mSortedImages.get(position);

        for(int i = 0; i < mSortedImages.size(); i++)
        {
            if(mUnsortedResouceIndexList.get(i).getImage() == image.getImage())
                resourceIndex = i;
        }

        return resourceIndex;
    }

    /**
     */
    public static int getImageIndexByResource(List<ImageWithCaption> imageList, int resource)
    {
        int resourceIndex = 0;

        for(int i = 0; i < imageList.size(); i++)
        {
            if(imageList.get(i).getImage() == resource)
                resourceIndex = i;
        }

        return resourceIndex;
    }

    /**
     */
    public static int getWalletImageIndexBySortedPosition(int position)
    {
        int resourceIndex = 0;

        ImageWithCaption image = mWalletSortedIcons.get(position);

        for(int i = 0; i < mWalletSortedIcons.size(); i++)
        {
            if(mWalletIcons.get(i).getImage() == image.getImage())
                resourceIndex = i;
        }

        return resourceIndex;
    }


    /**
     */
    public static int getSmallImageByPosition(int position, List<ImageWithCaption> unsorted)
    {
        int resource;

        try
        {
            resource = unsorted.get(position).getImageSmall();
        }
        catch (IndexOutOfBoundsException e)
        {
            resource = unsorted.get(0).getImage();
        }

        return  resource;
    }

    /**
     */
    public static int getCount()
    {
        return mSortedImages.size();
    }

    /**
     */
    public static List<Integer> getSortedImages(List<ImageWithCaption> sorted)
    {
        List<Integer> resources = new ArrayList<Integer>();

        for(ImageWithCaption cur : sorted)
            resources.add(cur.getImage());

        return resources;
    }

    /**
     *
     * @return
     */
    public static List<String> getCaptions()
    {
        List<String> resources = new ArrayList<String>();

        for(ImageWithCaption cur : mSortedImages)
            resources.add(cur.getCaption());

        return resources;
    }

    /**
     */
    public static String getCaptionByImage(int resourceId, List<ImageWithCaption> sorted)
    {
        String caption = "";

        for(int i = 0; i < Images.getCount(); ++i)
        {
            if(Images.getSortedImages(sorted).get(i) == resourceId)
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
       return getDefaultImage(mUnsortedResouceIndexList);
    }

    /**
     */
    public static int getDefaultImage(List<ImageWithCaption> unsorted)
    {
        int imageIndex = 0;

        for(int i = 0; i < unsorted.size(); ++i)
            if(unsorted.get(i).mImageResource == R.drawable.shop)
                imageIndex = i;

        return imageIndex;
    }

    public static List<ImageWithCaption> getImageWithCaptions()
    {
        return mSortedImages;
    }

    public static List<ImageWithCaption> getWalletImageWithCaptions()
    {
        return mWalletSortedIcons;
    }
}

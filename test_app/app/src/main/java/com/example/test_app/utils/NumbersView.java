package com.example.test_app.utils;

public class NumbersView {

    // the resource ID for the imageView
    private int ivNumbersImageId;
    private String get_numbers_text;

    // TextView 1
    private String mNumberInDigit;

    // TextView 1
    private String mNumbersInText;

    // create constructor to set the values for all the parameters of the each single view
    public NumbersView(int NumbersImageId, String NumbersInDigit, String NumbersInText,String get_numbers_text2) {
        ivNumbersImageId = NumbersImageId;
        mNumberInDigit = NumbersInDigit;
        mNumbersInText = NumbersInText;
        get_numbers_text = get_numbers_text2;


    }

    // getter method for returning the ID of the imageview
    public int getNumbersImageId() {
        return ivNumbersImageId;
    }
    // getter method for returning the ID of the TextView 1
    public String get_text_2() {
        return get_numbers_text;
    }
    public String getNumberInDigit() {
        return mNumberInDigit;
    }

    // getter method for returning the ID of the TextView 2
    public String getNumbersInText() {
        return mNumbersInText;
    }

}

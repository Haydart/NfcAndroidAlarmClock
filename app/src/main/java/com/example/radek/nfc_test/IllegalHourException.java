package com.example.radek.nfc_test;

import android.widget.Toast;

import java.util.IllegalFormatException;

/**
 * Created by Radek on 2016-05-28.
 */
public class IllegalHourException extends RuntimeException
{
    public IllegalHourException(String message)
    {
        super(message);
    }
}

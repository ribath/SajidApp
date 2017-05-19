package com.example.ribath.sajidapp;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Ribath on 5/6/2017.
 */

@DatabaseTable(tableName = "SentSms")
public class MessageData {

    @DatabaseField
    private String number;

    @DatabaseField
    private String message;

    @DatabaseField
    private String date;

    public MessageData(String number, String message, String date) {
        this.number = number;
        this.message = message;
        this.date = date;
    }

    public MessageData() {
    }

    public String getNumber() {
        return number;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }
}

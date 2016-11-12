package com.company.cpp.lvcaapp;

import android.database.Cursor;
import android.util.Log;

import java.io.Serializable;

class Model implements Serializable {
    private static final String TAG = Model.class.getSimpleName();

    static final String ID = "_id";
    static final String NAME = "NAME";
    static final String SECOND_LINE = "SECOND_LINE";
    static final String HIDDEN = "HIDDEN";
    static final String DESCRIPTION = "DESCRIPTION";

    private static String rawInputString;

    private Long id;
    private String name;
    private String second_line;
    private String hidden;
    private String description;

    Model() {
        setId(0L);
    }

    Model(Cursor cursor) {
        try {
            setName(cursor.getString(cursor.getColumnIndex(NAME)));
            setSecond_line(cursor.getString(cursor.getColumnIndex(SECOND_LINE)));
            setHidden(cursor.getString(cursor.getColumnIndex(HIDDEN)));
            setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
            setId(cursor.getLong(cursor.getColumnIndex(ID)));
        } catch (Throwable t) {
            Log.v(TAG, "Failed to complete model item from database cursor.")    ;
        }
    }

    void load(String string) {
        StringBuilder buf = new StringBuilder(string);
        if (buf.substring(0,2).equals("[{")){
            buf.delete(0,2);
        }
        if (buf.substring(buf.length()-2, buf.length()).equals("}]")) {
            buf.delete(buf.length()-2, buf.length());
        }
        rawInputString = buf.toString();
        parse();
    }

    private void parse() {

        if (rawInputString == null) {
            System.out.println("nothing to parse");
            return;
        }

        rawInputString = rawInputString.replaceAll("\"\\:null,", "\"\\:\"null\",");
        String[] nvpa = rawInputString.split("\",\"");
        for (String nvpString : nvpa) {
            String[] nvpItem = nvpString.split("\":\"");
            if (nvpItem.length < 2) continue;
            String identifier = nvpItem[0].replaceAll("\"", "");
            String content = nvpItem[1].replaceAll("\"", "");

            switch (identifier) {
                case "name":
                    setName(content);
                    break;
                case "second_line":
                    setSecond_line(content);
                    break;
                case "hidden":
                    setHidden(content);
                    break;
                case "description":
                    setDescription(content);
                    break;
                default:
                    System.out.println("nowhere to put [" + nvpItem[0] + "] " + nvpString + " raw: " + rawInputString);
                    break;
            }
        }
    }

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {  this.name = name; }

    void setSecond_line(String second_line) {
        this.second_line = second_line;
    }

    String getSecond_line() {
        return second_line;
    }

    void setHidden(String hidden) {
        this.hidden = hidden;
    }

    String getHidden() {
        return hidden;
    }

    void setDescription(String description) {
        this.description = description;
    }

    String getDescription() {
        return description;
    }

    public String toString() {
        return getId()  + ", " +
                getName() + ", " +
                getSecond_line() + ", " +
                getHidden() + ", " +
                getDescription();
    }

}

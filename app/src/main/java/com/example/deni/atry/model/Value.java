//package model;
package com.example.deni.atry.model;

import java.util.List;

/**
 * Created by deni on 13/06/2018.
 */

public class Value {
    String value;
    String message;
    List<Mahasiswa> result;

    public String getValue(){ // kirim nilai ke values/ API
        return value;
    }

    public String getMessage(){
        return message;
    }
    public List<Mahasiswa> getResult(){
        return result;
    }

}

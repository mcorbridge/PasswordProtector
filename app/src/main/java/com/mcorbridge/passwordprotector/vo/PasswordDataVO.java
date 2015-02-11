package com.mcorbridge.passwordprotector.vo;

import java.io.Serializable;

/**
 * Created by Mike on 12/29/2014.
 * copyright Michael D. Corbridge
 */

public class PasswordDataVO implements Serializable
{


    private Long id;

    private String action;

    private String name;

    private String category;

    private String title;

    private String value;

    //---------------------------------------------

    public Long getId(){
        return id;
    }
    public void setId(Long id){this.id = id;}

    //---------------------------------------------

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }

    //---------------------------------------------


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    //---------------------------------------------

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    //---------------------------------------------

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    //---------------------------------------------

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    //---------------------------------------------



}

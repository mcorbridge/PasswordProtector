package com.mcorbridge.passwordprotector.sql;

/**
 * Created by Mike on 1/23/2015.
 * copyright Michael D. Corbridge
 */
public class Password {

    private long id;
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    private String action;
    public String getAction() {return action;}
    public void setAction(String action) {this.action = action;}

    private int modified;
    public int isModified() {return modified;}
    public void setModified(int modified) {this.modified = modified;}

    private String category;
    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}

    private String value;
    public String getValue() {return value;}
    public void setValue(String value) {this.value = value;}

    private String name;
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    private String title;
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}


}

package com.muruga.t_shirt.model;

public class User
{
    private long id;
    private String name;
    private String phonenumber;
    private String password;

    public User()
    {
    }

    public User(long id, String inputName, String phonenumber, String password)
    {
        this.id=id;
        this.name=inputName;
        this.phonenumber = phonenumber;
        this.password = password;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPhonenumber()
    {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber)
    {
        this.phonenumber = phonenumber;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}

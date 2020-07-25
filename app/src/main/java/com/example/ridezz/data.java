package com.example.ridezz;

import java.util.Map;

public class data  {
    private String first_name;
    private String last_name;
    private String email;
    private String phone_num;
    private String gender;

    data(){

    }

    public data(String first_name, String last_name, String email, String phone_num, String gender) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone_num = phone_num;
        this.gender = gender;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}

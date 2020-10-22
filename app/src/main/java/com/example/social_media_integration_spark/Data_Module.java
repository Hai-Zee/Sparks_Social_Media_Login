package com.example.social_media_integration_spark;

public class Data_Module {
    String createAccountEmail, userName, imageURI;

    Data_Module(String createAccountEmail, String userName, String imageURI){
        this.createAccountEmail = createAccountEmail;
        this.userName = userName;
        this.imageURI = imageURI;
    }
    Data_Module(){

    }

    public String getCreateAccountEmail() {
        return createAccountEmail;
    }

    public void setCreateAccountEmail(String createAccountEmail) {
        this.createAccountEmail = createAccountEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }
}

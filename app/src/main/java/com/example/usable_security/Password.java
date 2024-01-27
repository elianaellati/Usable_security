package com.example.usable_security;

public class Password {

    String password;
    String category;
    int flag=0;
    int sum=0;

    public Password(String password) {

        this.password = password;
    }



    public int checkCapitalLetter(String password) {
        if(password.matches(".*[A-Z].*")){
            sum=sum+1;
            return flag=1;
        }
        return flag;
    }
    public int checkSmallLetter(String password) {
        if (password.matches(".*[a-z].*")) {
            sum=sum+1;
            return flag=2;
        }
        return flag;
    }

    public int checkDigital(String password) {
        if (password.matches(".*\\d.*")) {
            sum=sum+1;
            return flag=3;
        }
        return flag;
    }
    public int checkSpecialCharacter(String password) {
        if (password.matches(".*[^a-zA-Z0-9].*")) {
            sum=sum+1;
            return flag=4;
        }
        return flag;
    }


    public Double CalculateEntropy(String password) {

        return (Math.log(Math.pow(CalculateSize(password), password.length())) / Math.log(2));
    }


    public double calculateVarience() {
        int sum = 0;
        double varience=0;
        int[] values = password.chars().toArray();
        for (int value : values) {
            sum += value;
        }
        double mean=(double) sum / values.length;

        for (int value : values) {
            double difference = value - mean;

            varience += Math.pow(difference, 2);
        }
        return varience/password.length();
    }



    public int CalculateSize(String password) {
        int total=0;
        if(checkCapitalLetter(password)==1) {
            total=total+26;
        }
        if(checkSmallLetter(password)==2) {

            total=total+26;
        }
        if( checkDigital(password)==3) {

            total=total+10;

        }
        if(checkSpecialCharacter(password)==4) {
            total=total+32;
        }
        return total;
    }

}

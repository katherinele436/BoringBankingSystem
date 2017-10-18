package com.company;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Session {
    boolean mode; //true is agent & false is machine
    String[] summary;
    int totalTransfer;
    int totalDeposit;
    int totalWithdraw;

    public boolean validAccNum(int accNum) { //used to test if account number is correctly format and if it already exists  - Back End ?
        return true;
    }

    public boolean validAccName(String accName) { //used to test if account name is valid - Back End ?
        return true;
    }

    public boolean validAccList(int accNum) {
        return true;
    }

}


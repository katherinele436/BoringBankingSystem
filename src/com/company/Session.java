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

    public boolean validAccNum(int accNum){ //used to test if account number is correctly format and if it already exists  - Back End ?
        return true;
    }

    public boolean validAccName(String accName) { //used to test if account name is valid - Back End ?
        return true;
         }

    public String createAccount() throws IOException {
        if (!mode){
            int accNum = getInt("Enter new account number: ");
            if (!validAccNum(accNum)){//if account is not in Valid Account List
                String accName = getStringInput("Enter new account name: ");
                if (validAccName(accName)){
                    System.out.println("account " + accNum + " is created for " + accName);
                    return "NEW" + accNum + "000 0000000" + accName;
                }
            }
        }
        return "";

    }

    public String deleteAccount() throws IOException {
        if (!mode){
            int accNum = getInt("enter account number: ");
            if (validAccNum(accNum)) { //if Account is in valid account list
                System.out.println("account" + accNum + " is deleted");
            }
        }
        return "";
    }

    public  String getStringInput ( String prompt) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String accNumStr;
        accNumStr = br.readLine();
        System.out.println(prompt);
        return accNumStr;
    }

    public int getInt( String prompt) throws IOException {
        try {
            return Integer.parseInt(getStringInput(prompt));
        }
        catch (NumberFormatException e){
        }
        return  0;
    }
}


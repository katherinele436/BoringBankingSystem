package com.company;
import com.company.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class BoringBankingSystem {
    public static Session Account = null;


    public static int[] readValidAccounts(){
        int[] accountsList = null;
        return accountsList;
    }

    public static Session waitForLogin() throws IOException {
        String loginStr = getStringInput("");
        if (loginStr.equals("login")){
            String modeStr = getStringInput("login as agent or machine?");
            if (modeStr.equals("agent")){ Account.mode = true;}
            else if(modeStr.equals("machine")){ Account.mode = false;}
        }

        return null;
    }

    public static String readNextInput(){
        String input = null;
        return input;
    }

    public static void logout(){

    }

    public static void writeSummaryFile(){

    }

    public static String createAccount() throws IOException {
        if (!mode){
            int accNum = getInt("Enter new account number: ");
            if (!validAccNum(accNum) && !validAccList(accNum)){//if account is not in Valid Account List
                String accName = getStringInput("Enter new account name: ");
                if (validAccName(accName)){
                    System.out.println("account " + accNum + " is created for " + accName);
                    return "NEW" + accNum + "000 0000000" + accName;
                }
            }
        }
        return "";

    }

    public static String deleteAccount() throws IOException {
        if (!mode){
            int accNum = getInt("enter account number: ");
            if (validAccList(accNum)) { //if Account is in valid account list
                System.out.println("account" + accNum + " is deleted");
            }
        }
        return "";
    }

    public static boolean validAccList(int accNum) {
        return true;
    }

    public static boolean validAccNum(int accNum){ //used to test if account number is correctly format and if it already exists  - Back End ?
        return true;
    }

    public static boolean validAccName(String accName) { //used to test if account name is valid - Back End ?
        return true;
    }


    public static String getStringInput ( String prompt) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String accNumStr;
        accNumStr = br.readLine();
        System.out.println(prompt);
        return accNumStr;
    }

    public static int getInt( String prompt) throws IOException {
        try {
            return Integer.parseInt(getStringInput(prompt));
        }
        catch (NumberFormatException e){
        }
        return  0;
    }

    public static void Main(String[] args) throws IOException {
        // write your code here
        boolean login = false;
        int[] validAccountsList = readValidAccounts();
        String summaryFile = args[1];
        Session Account = waitForLogin();
        while (login) {
            String input = readNextInput();
            switch (input) {
                case "createacct":
                    createAccount();
                    break;
                case "deleteacct":
                    deleteAccount();
                    break;
                case "withdraw":
                    withdraw();
                    break;
                case "deposit":
                    deposit();
                    break;
                case "transfer":
                    transfer();
                    break;
                case "logout":
                    login = false;
                    break;
                default:
                    System.out.println("invalid transaction");
                    break;
            }
        }
        logout();
        writeSummaryFile();
    }


}



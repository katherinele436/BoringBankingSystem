package com.company;
//import com.company.Session;

public class BoringBankingSystem {
    public static int[] readValidAccounts(){
        int[] accountsList = null;
        return accountsList;
    }

    public static Session waitForLogin(){

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


    public static void Main(String[] args) {
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



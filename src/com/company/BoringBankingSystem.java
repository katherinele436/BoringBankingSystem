package com.company;
import java.util.ArrayList;
import java.io.*
//import com.company.Session;

public class BoringBankingSystem {
    public static Session Account = null;
    public static int[] readValidAccounts(){
        ArrayList<int> accountslist = new ArrayList<int>();
        return accountsList;
    }

    public static boolean checkValidAccounts(String account){
        return true;
    }

    public static Session waitForLogin(){
        return null;
    }

    public static String readNextInput(){
        String input = null;
        return input;
    }

    public static boolean logout(boolean login){
        if(login){
            Account.summary.add("EOS *** *** *** ***\n");
            return true;
        }else{
            System.out.println("erorr: cannot logout if not logged in");
            return false;
        }
    }

    public static void writeSummaryFile(){
        FileWriter writer = new FileWriter("output.txt");
        for(String str: Account.summary) {
            writer.write(str);
        }
        writer.close();
    }
    public static void deposit(){
        System.out.println("enter account number:");
        String input = readNextInput();
        String account_number = null;

        if (checkValidAccounts(input)){
            account_number = input;
        }
        else{
            System.out.println("error: account does not exist, transaction ended");
            return;
        }

        System.out.println("enter amount:");
        input = readNextInput();
        int amount = 0;
        try{
            amount = Integer.parseInt(input);
        }catch (NumberFormatException e){
            System.out.println("error: invalid amount, transaction ended");
            return;
        }

        double dollars = 0;
        if (withinSingleDepositLimit(amount)){
                dollars = amount / 100;
        }else{
            System.out.println("error: single deposit limit exceeded");
            return;
        }

        System.out.printf("Deposited $%0.2f into account %d", dollars, account_number);
        int length = Account.summary.length;
        Account.summary.add("DEP 0000000 " + amount + " " + account_number + " ***\n");


    }

    public static void withdraw(){
        System.out.println("enter account number:");
        String input = readNextInput();
        String account_number = null;

        if (checkValidAccounts(input)){
            account_number = input;
        }
        else{
            System.out.println("error: account does not exist, transaction ended");
            return;
        }

        System.out.println("enter amount:");
        input = readNextInput();
        int amount = 0;
        try{
            amount = Integer.parseInt(input);
        }catch (NumberFormatException e){
            System.out.println("error: invalid amount, transaction ended");
            return;
        }

        double dollars = 0;
        if (withinSingleWithdrawLimit(amount)){
            if (withinTotalWithdrawLimit()) {
                dollars = amount / 100;
            }
            else{
                System.out.println("error: total withdraw limit exceeded");
                return;
            }
        }else{
            System.out.println("error: single withdraw limit exceeded");
            return;
        }

        System.out.printf("Withdrew $%0.2f from account %d", dollars, account_number);
        int length = Account.summary.length;
        Account.summary[length - 1] = "WDR 0000000 " + amount + " " + account_number + " ***\n";
    }

    public static boolean withinSingleDepositLimit(int amount){
        if(amount >= 100000){
            return false;
        } else{
            return true;
        }
    }

    public static boolean withinSingleWithdrawLimit(int amount){
        return true;
    }

    public static boolean withinTotalWithdrawLimit(){
        return true;
    }

    public static void transfer() {    }

    public static void Main(String[] args) {
        // write your code here
        boolean login = false;
        boolean continue = true; //used for while loop until the command quit is given
        int[] validAccountsList = readValidAccounts();
        String summaryFile = args[1];
        Account = waitForLogin();
        while (continue){
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
                    if (logout(login)) {
                        login = false;
                        writeSummaryFile();
                    }
                    break;
                case "quit"://command to exit the while loop
                    continue = false;
                    break;
                default:
                    System.out.println("invalid transaction");
                    break;
            }
        }
    }


}



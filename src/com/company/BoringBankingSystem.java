package com.company;
//import com.company.Session;
import java.io.*;

public class BoringBankingSystem {
    public static Session Account = null;
    public static String summaryFile = null;
    public static String inputFile = null;
    public static int[] validAccountsList;

    public static int[] readValidAccounts(){
        int[] accountsList = null;
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
        if ()
        try{
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            input = bufferedReader.readLine();
            bufferedReader.close();
            return input;
        }catch(FileNotFoundException e){
            System.out.println("Could not find valid accounts list file");
        }
        catch(IOException ex){
            System.out.println("Error reading valid accounts list file");
        }
        return input;
    }

    public static void logout(){

    }

    public static void writeSummaryFile(){

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

    public static boolean withinSingleWithdrawLimit(int amount){
        return true;
    }

    public static boolean withinTotalWithdrawLimit(){
        return true;
    }

    public static void transfer() {
        System.out.println("transfer from account number:");
        String input = readNextInput();
        String account_one, account_two = null;

        if (checkValidAccounts(input)){
            account_one = input;
        }
        else{
            System.out.println("error: account does not exist, transaction ended");
            return;
        }

        System.out.println("transfer to account number:");
        input = readNextInput();

        if (checkValidAccounts(input)){
            account_two = input;
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
        if (withinSingleTransferLimit(amount)){
            dollars = amount / 100;
        }
        else{
            System.out.println("error: single transfer limit exceeded");
            return;
        }

        System.out.printf("Transferred $%0.2f from account %d to account %d", dollars, account_one, account_two);
        int length = Account.summary.length;
        Account.summary[length - 1] = "XFR " + account_one + " " + amount + " " + account_two + " ***\n";
    }

    public static boolean withinSingleTransferLimit(int amount){
        return true;
    }

    public static void Main(String[] args) {
        // write your code here
        boolean login = false;
        validAccountsList = readValidAccounts();
        inputFile = args[0];
        summaryFile = args[1];
        Account = waitForLogin();
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



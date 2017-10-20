package com.company;

import java.io.*;
import java.util.ArrayList;

public class BoringBankingSystem {
    public static Session Account = null;
    public static ArrayList<Integer> validAccountsList = null;
    public static String summaryFile;
    public static String accountsListFile;

    public static ArrayList<Integer> readValidAccounts() throws Exception {
        ArrayList<Integer> accountsList = new ArrayList<>();
        FileReader theList = new FileReader(accountsListFile);
        BufferedReader readList = new BufferedReader(theList);
        String line;
        while ((line = readList.readLine()) != null && !line.equals("0000000")) {
            accountsList.add(Integer.parseInt(line));
        }
        return accountsList;
    }

    public static Session waitForLogin(boolean login) throws IOException {
        String loginStr = getStringInput("");
        if (loginStr.equals("login")){
            String modeStr = getStringInput("log in as agent or machine?");
            if (modeStr.equals("agent")){
                System.out.println("logged in as agent");
                Account.mode = true;
            }
            else if(modeStr.equals("machine")){
                System.out.println("logged in as machine");
                Account.mode = false;
            }
        }
        else if(loginStr.equals("quit")){
            System.exit(0);
        }
        System.out.println("error: invalid login");
        return null;
    }

    public static void createAccount() throws IOException {
        if (!Account.mode){
            int accNum = getInt("Enter new account number: ");
            //if account is not in Valid Account List
            if (!validAccNum(accNum) && !validAccList(accNum)){
                String accName = getStringInput("Enter new account name: ");
                if (validAccName(accName)){
                    System.out.println("account " + accNum + " is created for " + accName);
                    // append summary string to summary list
                    Account.summary.add("NEW" + accNum + "000 0000000" + accName);
                }
            }
        }
        else {
            System.out.println("error: cannot create account in machine mode, transaction ended");
        }
    }

    public static void deleteAccount() throws IOException {
        if (!Account.mode) {
            int accNum = getInt("enter account number: ");
            //if Account is in valid account list
            if (validAccList(accNum)) {
                System.out.println("account" + accNum + " is deleted");
                // append summary string summary list
                Account.summary.add("DEL" + accNum + "000 0000000 ***");
            }
        }
        else {
            System.out.println("error: cannot delete account in machine mode, transaction ended");
        }
    }

    public static void deposit() throws IOException{
        int input = getInt("enter account number:");
        int account_number;
        if (validAccList((input))){
            account_number = input;
        }
        else{
            System.out.println("error: account does not exist, transaction ended");
            return;
        }
        int amount;
        try{
            input = getInt("enter amount");
        }catch (NumberFormatException e){
            System.out.println("error: invalid amount, transaction ended");
            return;
        }
        amount = input;
        double dollars;
        if (withinSingleDepositLimit(amount)){
            dollars = amount / 100;
        }else{
            System.out.println("error: single deposit limit exceeded");
            return;
        }
        System.out.printf("Deposited $%0.2f into account %d", dollars, account_number);
        Account.summary.add("DEP 0000000 " + amount + " " + account_number + " ***\n");
    }

    public static void withdraw()throws IOException{
        int input = getInt("enter account number:");
        int account_number;
        if (validAccList(input)){
            account_number = input;
        }
        else{
            System.out.println("error: account does not exist, transaction ended");
            return;
        }
        int amount;
        try{
            input = getInt("enter amount:");
            amount = input;
        }catch (NumberFormatException e){
            System.out.println("error: invalid amount, transaction ended");
            return;
        }
        double dollars;
        if (withinSingleWithdrawLimit(amount)){
            if (withinTotalWithdrawLimit(amount)) {
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
        Account.summary.add("WDR 0000000 " + amount + " " + account_number + " ***\n");
    }

    public static void transfer() throws IOException{
        int input = getInt("transfer from account number:");
        int account_one, account_two;
        if (validAccList(input)){
            account_one = input;
        }
        else{
            System.out.println("error: account does not exist, transaction ended");
            return;
        }
        input = getInt("transfer to account number:");
        if (validAccList(input)){
            account_two = input;
        }
        else{
            System.out.println("error: account does not exist, transaction ended");
            return;
        }
        int amount;
        try{
            input = getInt("enter amount:");
            amount = input;
        }catch (NumberFormatException e){
            System.out.println("error: invalid amount, transaction ended");
            return;
        }
        double dollars;
        if (withinSingleTransferLimit(amount)){
            dollars = amount / 100;
        }
        else{
            System.out.println("error: single transfer limit exceeded");
            return;
        }
        System.out.printf("Transferred $%0.2f from account %d to account %d", dollars, account_one, account_two);
        Account.summary.add("XFR " + account_one + " " + amount + " " + account_two + " ***\n");
    }

    public static boolean logout(boolean login){
        if(login){
            Account.summary.add("EOS *** *** *** ***\n");
            return true;
        }else{
            System.out.println("error: cannot logout if not logged in");
            return false;
        }
    }

    public static void writeSummaryFile() throws IOException{
        try {
            FileWriter writer = new FileWriter(summaryFile);
            for (String str : Account.summary) {
                writer.write(str);
            }
            writer.close();
        }
        catch (IOException e){
            //summary file error message
        }
    }

    public static String getStringInput ( String prompt) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String accNumStr;
        System.out.println(prompt);
        accNumStr = br.readLine();
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

    public static boolean withinSingleDepositLimit(int amount){
        if(amount >= 100000){
            return false;
        } else{
            Account.totalDeposit += amount;
            return true;
        }
    }

    public static boolean withinSingleWithdrawLimit(int amount){
        if (Account.mode == true){
            if(amount > 99999999){
                return false;
            }else{
                Account.totalWithdraw += amount;
                return true;
            }
        }else{
            if(amount > 100000){
                return false;
            }else{
                Account.totalWithdraw += amount;
                return true;
            }
        }
    }

    public static boolean withinTotalWithdrawLimit(int amount){
        if (Account.mode == false && Account.totalWithdraw > 100000){
            Account.totalWithdraw -= amount;
            return false;
        }else{
            return true;
        }
    }

    public static boolean withinSingleTransferLimit(int amount){
        if (Account.mode == true){
            if(amount > 99999999){
                return false;
            }else{
                Account.totalTransfer += amount;
                return true;
            }
        }else{
            if(amount > 100000){
                return false;
            }else{
                Account.totalTransfer += amount;
                return true;
            }
        }
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

    public static void main(String[] args) throws Exception{
        boolean next = true; //used for while loop until the command quit is given
        boolean login = false;
        validAccountsList = readValidAccounts();
        summaryFile = args[1];
        accountsListFile = args[0];

        while (next){
            if (!login) {
                Account = waitForLogin(login);
            }
            String input = getStringInput("");
            switch (input) {
                case "createacct":
                    createAccount();// append summary string output from create account
                    break;
                case "deleteacct":
                    deleteAccount();// append summary string output from delete account
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
                default:
                    System.out.println("invalid transaction");
                    break;
            }
        }
    }
}



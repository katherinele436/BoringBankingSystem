package com.company;

import java.io.*;
import java.util.ArrayList;

public class BoringBankingSystem {
    public static Session Account = null;
    public static ArrayList<Integer> validAccountsList = null;
    public static String summaryFile;
    public static String accountsListFile;
    public static  BufferedReader br = null;

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

    public static boolean waitForLogin(boolean login) throws IOException {
        String loginStr = br.readLine();
        Account = new Session();
        if (loginStr.equals("login")){
            System.out.println("logged in");
            String modeStr = getStringInput("log in as agent or machine?");
            if (modeStr.equals("agent")){
                System.out.println("logged in as agent");
                Account.mode = true;
                return true;
            }
            else if(modeStr.equals("machine")){
                System.out.println("logged in as machine");
                Account.mode = false;
                return true;
            }
        }
        else if(loginStr.equals("quit")){
            System.exit(0);
        }
        System.out.println("error: invalid login");
        return false;
    }

    public static void createAccount() throws IOException {
        if (Account.mode){
            int accNum = getInt("Enter new account number: ");
            //if account is not in Valid Account List
            if (!validAccNum(accNum) || validAccList(accNum)) {
                return;
            }
            String accName = getStringInput("Enter new account name: ");
            if (!validAccName(accName)) {
                return;
            }
            System.out.println("account " + accNum + " is created for " + accName);
            // append summary string to summary list
            Account.summary.add("NEW " + accNum + "000 0000000 " + accName + "\n");
        }
        else {
            System.out.println("error: cannot create account in machine mode, transaction ended");
        }
    }

    public static void deleteAccount() throws IOException {
        if (Account.mode) {
            int accNum = getInt("enter account number: ");
            //if Account is in valid account list
            if (!validAccList(accNum)) {
                return;
            }
            System.out.println("account" + accNum + " is deleted");
            // append summary string summary list
            Account.summary.add("DEL " + accNum + "000 0000000 ***");
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
            amount = getInt("enter amount");
        }catch (NumberFormatException e){
            System.out.println("error: invalid amount, transaction ended");
            return;
        }
        double dollars;
        if (withinSingleDepositLimit(amount)){
            dollars = amount / 100;
        }else{
            System.out.println("error: single deposit limit exceeded");
            return;
        }
        System.out.printf("Deposited $%.2f into account %d\n", dollars, account_number);
        Account.summary.add("DEP " + account_number + " "+ amount + " " + "0000000" + " ***\n");
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
            amount = getInt("enter amount:");
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
        System.out.printf("Withdrew $%.2f from account %d\n", dollars, account_number);
        Account.summary.add("WDR " + account_number + " "+ amount + " " + "0000000" + " ***\n");
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
            amount = getInt("enter amount:");
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
        System.out.printf("Transferred $%.2f from account %d to account %d\n", dollars, account_one, account_two);
        Account.summary.add("XFR " + account_one + " " + amount + " " + account_two + " ***\n");
    }

    public static boolean logout(boolean login){
        if(login){
            System.out.println("logged out");
            Account.summary.add("EOS 0000000 000 0000000 ***");
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
        if(amount <= 0 || amount >= 100000){
            return false;
        } else{
            Account.totalDeposit += amount;
            return true;
        }
    }

    public static boolean withinSingleWithdrawLimit(int amount){
        if (Account.mode){
            if(amount <= 0 || amount > 99999999){
                return false;
            }else{
                Account.totalWithdraw += amount;
                return true;
            }
        }else{
            if(amount <= 0 || amount > 100000){
                return false;
            }else{
                Account.totalWithdraw += amount;
                return true;
            }
        }
    }

    public static boolean withinTotalWithdrawLimit(int amount){
        if (!Account.mode && Account.totalWithdraw > 100000){
            Account.totalWithdraw -= amount;
            return false;
        }else{
            return true;
        }
    }

    public static boolean withinSingleTransferLimit(int amount){
        if (Account.mode){
            if(amount <= 0 || amount > 99999999){
                return false;
            }else{
                Account.totalTransfer += amount;
                return true;
            }
        }else{
            if(amount <= 0 || amount > 100000){
                return false;
            }else{
                Account.totalTransfer += amount;
                return true;
            }
        }
    }

    public static boolean validAccList(int accNum) {
        if (validAccountsList.contains(accNum)){
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean validAccNum(int accNum){//used to test if account number is correctly format and if it already exists  - Back End ?
        if (accNum < 1000000 || accNum > 10000000) {
            System.out.println("error: account number must be 7 characters long, transaction ended");
            return false;
        }
        return true;
    }

    public static boolean validAccName(String accName) { //used to test if account name is valid - Back End ?

        if (accName.endsWith(" ") ){
            System.out.println("error: account name ends with a space, transaction ended");
            return false;
        }
        else if (accName.startsWith(" ")){
            System.out.println("error: account name begins with a space, transaction ended");
            return false;
        }
        else if (!accName.matches("[^A-Za-z0-9 ]")){
            System.out.println("error: account name has a non-alphanumeric character, transaction ended");
            return false;
        }
        else if (accName.length() > 30 || accName.length() < 3){
            System.out.println("error: account name must be between 3 and 30 characters, transaction ended");
            return  false;
        }

        return true;
    }

    public static void main(String[] args) throws Exception{
        br = new BufferedReader(new InputStreamReader(System.in));
        boolean next = true; //used for while loop until the command quit is given
        boolean login = false;
        summaryFile = args[1];
        accountsListFile = args[0];
        validAccountsList = readValidAccounts();
        while (next){
            while (!login) {
                login = waitForLogin(login);
            }
            String input = br.readLine();
            switch (input) {
                case "login":
                    System.out.println("error: already logged in");
                    break;
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



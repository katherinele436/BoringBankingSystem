package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.*;

public class BoringBankingSystem {
    public static Session Account = null;
    public static ArrayList<String> validAccountsList = null;
    public static String summaryFile;
    public static String accountsListFile;
    public static  BufferedReader br = null;

    // reads the valid accounts file into an array list
    public static ArrayList<String> readValidAccounts() throws Exception {
        ArrayList<String> accountsList = new ArrayList<>();
        FileReader theList = new FileReader(accountsListFile);
        BufferedReader readList = new BufferedReader(theList);
        String line;
        // reads all valid accounts until finding the special end account
        while ((line = readList.readLine()) != null && !line.equals("0000000")) {
            accountsList.add(line);
        }
        return accountsList;
    }

    // handles the login and quit commands, prompts for mode
    public static boolean waitForLogin() throws IOException {
        String loginStr = br.readLine();
        Account = new Session();
        // valid login command condition
        if (loginStr.equals("login")){
            System.out.println("logged in");
            String modeStr = getStringInput("log in as agent or machine?");
            // valid agent mode condition
            if (modeStr.equals("agent")){
                System.out.println("logged in as agent");
                Account.mode = true;
                return true;
            }
            // valid machine mode condition
            else if(modeStr.equals("machine")){
                System.out.println("logged in as machine");
                Account.mode = false;
                return true;
            }
        }
        // valid quit command condition, exits program
        else if(loginStr.equals("quit")){
            System.exit(0);
        }
        // if no login or quit command given, or invalid mode, prints error
        System.out.println("error: invalid login");
        return false;
    }

    // handles inputs after the create account command is inputted
    public static void createAccount() throws IOException {
        // ensures the mode is agent
        if (Account.mode){
            String accNum = getStringInput("enter new account number:");
            // ensures account is on valid accounts list
            if (validAccList(accNum)) {
                System.out.println("error: account number already in use, transaction ended");
                return;
            }
            // ensures account number is valid
            if(!validAccNum(accNum)){
                return;
            }
            String accName = getStringInput("enter new account name:");
            // ensures account name is valid
            if (!validAccName(accName)) {
                return;
            }
            System.out.println("account " + accNum + " is created for " + accName);
            // append summary string to summary list
            Account.summary.add("NEW " + accNum + " 000 0000000 " + accName + "\n");
        }
        // prints error if not in agent mode
        else {
            System.out.println("error: cannot create account in machine mode, transaction ended");
        }
    }

    // handles inputs after delete account command is inputted
    public static void deleteAccount() throws IOException {
        // ensures mode is agent
        if (Account.mode) {
            String accNum = getStringInput("enter account number:");
            // ensures account is on valid accounts list
            if (!validAccList(accNum)) {
                return;
            }
            System.out.println("account " + accNum + " deleted");
            // append summary string to summary list
            Account.summary.add("DEL " + accNum + " 000 0000000 ***\n");
            Account.deletedAccounts.add(accNum);
        }
        // prints error if mode is not agent
        else {
            System.out.println("error: cannot delete account in machine mode, transaction ended");
        }
    }

    // handles inputs after deposit command is inputted
    public static void deposit() throws IOException{
        String input = getStringInput("enter account number:");
        String account_number;
        // ensures account is on valid accounts list
        if (validAccList((input))){
            // ensures account is not deleted
            if (!Account.deletedAccounts.contains(input)) {
                account_number = input;
            }
            else{
                System.out.println("error: account has been deleted, transaction ended");
                return;
            }
        }
        // prints error if account is not on valid accounts list
        else{
            System.out.println("error: account does not exist, transaction ended");
            return;
        }
        int amount;
        // handles input for amount to deposit, catches invalid inputs
        try{
            amount = getInt("enter amount:");
        }catch (NumberFormatException e){
            System.out.println("error: invalid amount, transaction ended");
            return;
        }
        double dollars;
        // ensures amount is within the single deposit limit
        if (withinSingleDepositLimit(amount)){
            dollars = amount / 100;
        }
        // prints error if amount exceeds single deposit limit
        else{
            System.out.println("error: single deposit limit exceeded, transaction ended");
            return;
        }
        System.out.printf("deposited $%.2f into account %s\n", dollars, account_number);
        // appends summary string to summary list
        Account.summary.add("DEP " + account_number + " "+ amount + " " + "0000000" + " ***\n");
    }

    // handles inputs after withdraw command is inputted
    public static void withdraw()throws IOException{
        String input = getStringInput("enter account number:");
        String account_number;
        // ensures account is in valid accounts list
        if (validAccList(input)){
            // ensures account is not deleted
            if (!Account.deletedAccounts.contains(input)) {
                account_number = input;
            }
            else{
                System.out.println("error: account has been deleted, transaction ended");
                return;
            }
        }
        else{
            System.out.println("error: account does not exist, transaction ended");
            return;
        }
        int amount;
        // handles input for amount to withdraw, catches invalid input
        try{
            amount = getInt("enter amount:");
        }catch (NumberFormatException e){
            System.out.println("error: invalid amount, transaction ended");
            return;
        }
        double dollars;
        // ensures amount is within single withdraw limit
        if (withinSingleWithdrawLimit(amount)){
            return;
        }
        // errors if amount is not within single withdraw limit
        else{
            System.out.println("error: single withdraw limit exceeded, transaction ended");
            return;
        }

        if(!Account.mode){//if Account is set to machine mode
            if (Account.withdrawMap.get(account_number) == null){//account has yet to make a withdraw this session
                Account.withdrawMap.put(account_number,amount);
            }else{
                int accountWithdrawn=Account.withdrawMap.get(account_number);
                if(accountWithdrawn+amount < 10000){
                    Account.withdrawMap.get(account_number)+=amount;
                }else{
                    System.out.println("error: total withdraw limit exceeded for account:" + account_number +" , transaction ended");
                    return
                }

            }
        }
        System.out.printf("withdrew $%.2f from account %s\n", dollars, account_number);
        Account.summary.add("WDR " + account_number + " "+ amount + " " + "0000000" + " ***\n");
    }

    // handles inputs after transfer command is inputted
    public static void transfer() throws IOException{
        String input = getStringInput("transfer from account number:");
        String account_one, account_two;
        // ensures account is in valid accounts list
        if (validAccList(input)){
            // ensures account is not deleted
            if (!Account.deletedAccounts.contains(input)) {
                account_one = input;
            }
            else{
                System.out.println("error: account has been deleted, transaction ended");
                return;
            }
        }
        // errors if account is not in valid accounts list
        else{
            System.out.println("error: account does not exist, transaction ended");
            return;
        }
        input = getStringInput("transfer to account number:");
        // ensures account two is in valid accounts list
        if (validAccList(input)){
            if (!Account.deletedAccounts.contains(input)) {
                account_two = input;
            }
            else{
                System.out.println("error: account has been deleted, transaction ended");
                return;
            }
        }
        // errors if account two is not in valid accounts list
        else{
            System.out.println("error: account does not exist, transaction ended");
            return;
        }
        int amount;
        // handles amount to be transferred, catches invalid input
        try{
            amount = getInt("enter amount:");
        }catch (NumberFormatException e){
            System.out.println("error: invalid amount, transaction ended");
            return;
        }
        double dollars;
        // ensures amount is within single transfer limit
        if (withinSingleTransferLimit(amount)){
            dollars = amount / 100;
        }
        // errors if amount exceeds single transfer limit
        else{
            System.out.println("error: single transfer limit exceeded, transaction ended");
            return;
        }
        System.out.printf("transferred $%.2f from account %s to account %s\n", dollars, account_one, account_two);
        // appends summary string to summary list
        Account.summary.add("XFR " + account_one + " " + amount + " " + account_two + " ***\n");
    }

    // handles logout command
    public static boolean logout(boolean login){
        // ensures session is logged in before logging out
        if(login){
            System.out.println("logged out");
            // appends end of session summary string to summary list
            Account.summary.add("EOS 0000000 000 0000000 ***");
            return true;
        }
        // errors if not logged in when logging out
        else{
            System.out.println("error: cannot logout if not logged in");
            return false;
        }
    }

    // writes the summary list to the summary file
    public static void writeSummaryFile() throws IOException{
        // tries to write to summary file, catches file exceptions
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

    // writes prompts to standard output, reads standard input as strings
    public static String getStringInput ( String prompt) throws IOException {
        String accNumStr;
        System.out.println(prompt);
        accNumStr = br.readLine();
        return accNumStr;
    }

    // parses string input for integer, catches invalid integer exceptions
    public static int getInt( String prompt) throws IOException {
        try {
            return Integer.parseInt(getStringInput(prompt));
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    // conditional test for single deposit limits depending on mode
    public static boolean withinSingleDepositLimit(int amount){
        if (Account.mode){
            if(amount <= 0 || amount > 99999999){
                return false;
            }else{
                return true;
            }
        }else{
            if(amount <= 0 || amount > 100000){
                return false;
            }else{
                return true;
            }
        }
    }

    // conditional test for single withdraw limits depending on mode
    public static boolean withinSingleWithdrawLimit(int amount){
        if (Account.mode){
            if(amount <= 0 || amount > 99999999){
                return false;
            }else{
                return true;
            }
        }else{
            if(amount <= 0 || amount > 100000){
                return false;
            }else{
                return true;
            }
        }
    }

    // conditional test for total withdraw limits
    public static boolean withinTotalWithdrawLimit(int amount){
        if (!Account.mode && Account.totalWithdraw > 100000){
            return false;
        }else{
            return true;
        }
    }

    // conditional test for single transfer limits depending on mode
    public static boolean withinSingleTransferLimit(int amount){
        if (Account.mode){
            if(amount <= 0 || amount > 99999999){
                return false;
            }else{
                return true;
            }
        }else{
            if(amount <= 0 || amount > 100000){
                return false;
            }else{
                return true;
            }
        }
    }

    // conditional test for account being within valid accounts list
    public static boolean validAccList(String accNum) {
        if (validAccountsList.contains(accNum)){
            return true;
        }
        else{
            return false;
        }
    }

    //conditional test for correctly formatted account number
    public static boolean validAccNum(String accNum){
        // errors if account number is not 7 characters
        if (accNum.length() != 7) {
            System.out.println("error: account number must be 7 characters long, transaction ended");
            return false;
        }
        // errors if account number starts with zero
        if (accNum.startsWith("0")){
            System.out.println("error: account number cannot start with 0, transaction ended");
            return false;
        }
        return true;
    }

    // conditional test for correctly formatted account name
    public static boolean validAccName(String accName) {
         Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
         Matcher matcher = pattern.matcher(accName);

        // errors if account name ends with space
        if (accName.endsWith(" ") ){
            System.out.println("error: account name ends with a space, transaction ended");
            return false;
        }
        // errors if account name starts with space
        else if (accName.startsWith(" ")){
            System.out.println("error: account name begins with a space, transaction ended");
            return false;
        }
        // errors if account name is too long or too short
        else if (accName.length() > 30 || accName.length() < 3){
            System.out.println("error: account name must be between 3 and 30 characters, transaction ended");
            return  false;
        }
        // errors if account name has non-alphanumeric characters
        else if (!matcher.matches()){
            System.out.println("error: account name has a non-alphanumeric character, transaction ended");
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception{
        // initializes input reader
        br = new BufferedReader(new InputStreamReader(System.in));
        // conditionals for login and next command
        boolean next = true;
        boolean login = false;
        // reads in file names for summary file and valid accounts list file
        summaryFile = args[1];
        accountsListFile = args[0];
        // reads in the valid accounts list
        validAccountsList = readValidAccounts();
        // while there is more input, accept commands
        while (next){
            // while not logged in, wait for login or quit command
            while (!login) {
                login = waitForLogin();
            }
            String input = br.readLine();
            // handles each transaction command, calls related function
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
                // errors if input is not a valid command
                default:
                    System.out.println("error: invalid transaction");
                    break;
            }
        }
    }
}



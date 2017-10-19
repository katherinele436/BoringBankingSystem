package com.company;

import com.company.Session;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class BoringBankingSystem {
    public static Session Account = null;
    public static ArrayList<Integer> validAccountsList = null;
    public static ArrayList<String> summaryString = null;

    public static ArrayList<Integer> readValidAccounts() throws Exception {
        ArrayList<Integer> accountsList = new ArrayList<>();
        FileReader theList = new FileReader("ValidAccountsList.txt");
        BufferedReader readList = new BufferedReader(theList);
        String line;
        while ((line = readList.readLine()) != null && !line.equals("0000000")) {
            accountsList.add(Integer.parseInt(line));
        }

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

    public static boolean logout(boolean login){
        if(login){
            Account.summary.add("EOS *** *** *** ***\n");
            return true;
        }else{
            System.out.println("erorr: cannot logout if not logged in");
            return false;
        }
    }

    public static void writeSummaryFile() throws IOException{

        try {
            FileWriter writer = new FileWriter("output.txt");
            for (String str : Account.summary) {

                writer.write(str);
            }
            writer.close();
        }
        catch (IOException e){
            //summary file error message
        }
    }
    public static void deposit(){
        System.out.println("enter account number:");
        String input = readNextInput();
        String account_number = null;

        if (validAccList(input)){
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
        Account.summary.add("DEP 0000000 " + amount + " " + account_number + " ***\n");

    }

    public static void createAccount( ArrayList<String> sumString) throws IOException {
        if (!Account.mode){
            int accNum = getInt("Enter new account number: ");
            if (!validAccNum(accNum) && !validAccList(accNum)){//if account is not in Valid Account List
                String accName = getStringInput("Enter new account name: ");
                if (validAccName(accName)){
                    System.out.println("account " + accNum + " is created for " + accName);
                    sumString.add("NEW" + accNum + "000 0000000" + accName);// append summary string to summaryString arraylist
                }
            }
        }
    }

    public static void deleteAccount(ArrayList<String> sumString) throws IOException {
        if (!Account.mode) {
            int accNum = getInt("enter account number: ");
            if (validAccList(accNum)) { //if Account is in valid account list
                System.out.println("account" + accNum + " is deleted");
                sumString.add("DEL" + accNum + "000 0000000 ***");// append summary string to summaryString arraylist
            }
        }
    }

    public static boolean withinSingleDepositLimit(int amount){
        if(amount >= 100000){
            return false;
        } else{
            return true;
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

    public static void transfer() {    }

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

    public static void Main(String[] args) throws Exception {
        // write your code here
        boolean next = true; //used for while loop until the command quit is given
        boolean login = false;
        validAccountsList = readValidAccounts();
        summaryString = new ArrayList<>() ;// create a summaryString arrayList to store all the summary strings output
        String summaryFile = args[1];
        Account = waitForLogin();

        while (next){
            String input = readNextInput();
            switch (input) {
                case "createacct":
                    createAccount(summaryString);// append summary string output from create account
                    break;
                case "deleteacct":
                    deleteAccount(summaryString);// append summary string output from delete account
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
                    next = false;
                    break;
                default:
                    System.out.println("invalid transaction");
                    break;
            }
        }
    }
}



package com.company;
import java.util.*;

public class Session {
    // array list for all summary strings to be outputted to the summary file
    ArrayList<String> summary = new ArrayList<>();
    ArrayList<String> deletedAccounts = new ArrayList<>();
    //true is agent & false is machine
    boolean mode;
    // maps account numbers to the total amount withdrawn in a session
    public Map<String,Integer> withdrawMap= new HashMap<>();
    int totalWithdraw;

}


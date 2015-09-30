package com.company;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Helper {

    public static boolean checkMathematicalCondition(int leftOperand, String operator, int rightOperand) {
        /* >|>=|≥|<|<=|=|== */
        if ((operator.equals(">") && leftOperand > rightOperand) ||
                ((operator.equals(">=") || operator.equals("≥")) && leftOperand >= rightOperand) ||
                (operator.equals("<") && leftOperand < rightOperand) ||
                (operator.equals("<=") && leftOperand <= rightOperand) ||
                ((operator.equals("=") || operator.equals("==")) && leftOperand == rightOperand))
            return true;
        else
            return false;
    }

    public static int countSizeOfRule(String rule) {
        return rule.split("(,|==>)").length;
    }

    public static String getRuleSubpart(String rule, String ruleSubpartLabel) {
        if (ruleSubpartLabel.equals("RULE"))
            return rule;
        if (ruleSubpartLabel.equals("BODY"))
            return rule.substring(0, rule.indexOf(" ==> "));
        else if (ruleSubpartLabel.equals("HEAD"))
            return rule.substring(rule.indexOf(" ==> ") + " ==> ".length());
        else
            return null;
    }

    public static List<Set<String>> getSubsetsOfSizeMinusOne(Set<String> theSet) {
        List<Set<String>> subsets = new ArrayList<>();
        List<String> theList = new ArrayList<>();
        theList.addAll(theSet);
        int n = theList.size();

        for (int j = 1; j < n; j++) {
            Set<String> newSet = new HashSet<>();
            newSet.add(theList.get(0));
            for (int x = 1; x < n; x++)
                if (x != j)
                    newSet.add(theList.get(x));
            subsets.add(newSet);
        }

        Set<String> newSet = new HashSet<>();
        newSet.addAll(theList.subList(1, theList.size()));
        subsets.add(newSet);
        return subsets;
    }
}

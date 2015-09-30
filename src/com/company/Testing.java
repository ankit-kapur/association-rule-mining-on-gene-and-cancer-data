package com.company;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Testing {
    public static void main(String[] args) {

        System.out.println("\n" + checkMathematicalCondition(4, ">=", 5));

    }
    private static boolean checkMathematicalCondition(int leftOperand, String operator, int rightOperand) {
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

    private static String getRuleSubpart(String rule, String ruleSubpartLabel) {
        if (ruleSubpartLabel.equals("RULE"))
            return rule;
        if (ruleSubpartLabel.equals("BODY"))
            return rule.substring(0, rule.indexOf(" ==> "));
        else if (ruleSubpartLabel.equals("HEAD"))
            return rule.substring(rule.indexOf(" ==> ") + " ==> ".length());
        else
            return null;
    }

    private static void stuff() {


        String text = "RULE HAS 33 OF (Gene72_UP, Gene1_Down, Gene59_UP) AND something (else)";

        String regexTemplate1 = "(.*) (AND|OR) (.*)";
        Pattern patternTemplate1 = Pattern.compile(regexTemplate1);
        Matcher matcherTemplate1 = patternTemplate1.matcher(text);

        if (matcherTemplate1.find()) {
            System.out.println("found: " + matcherTemplate1.group(1));
            System.out.println("found: " + matcherTemplate1.group(2));
            System.out.println("found: " + matcherTemplate1.group(3));
        } else {

            String regexTemplate2 = "(RULE|BODY|HEAD) HAS (ANY|\\d+|NONE) OF \\((.*?)\\)";
            Pattern patternTemplate2 = Pattern.compile(regexTemplate2);
            Matcher matcherTemplate2 = patternTemplate2.matcher(text);

            if (matcherTemplate2.find()) {
                System.out.println("found: " + matcherTemplate2.group(1));
                System.out.println("found: " + matcherTemplate2.group(2));
                System.out.println("found: " + matcherTemplate2.group(3));
            } else {
                String regexTemplate3 = "SizeOf\\((RULE|BODY|HEAD)\\) (>|>=|≥|<|<=|=|==) (\\d+)";
                Pattern patternTemplate3 = Pattern.compile(regexTemplate3);
                Matcher matcherTemplate3 = patternTemplate3.matcher(text);

                if (matcherTemplate3.find()) {
                    System.out.println("found: " + matcherTemplate3.group(1));
                    System.out.println("found: " + matcherTemplate3.group(2));
                    System.out.println("found: " + matcherTemplate3.group(3));
                } else {
                    System.out.println("No match");
                }
            }
        }
    }
}

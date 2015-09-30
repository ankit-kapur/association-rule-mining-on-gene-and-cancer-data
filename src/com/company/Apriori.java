package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Apriori {

    /* Configurations */
    static final int SUPPORT = 40;
    static final int CONFIDENCE = 70;
    static final String FILE_PATH = "/home/castamere/code/datamining/hw2/association-rule-test-data.txt";

    static final String[] TEMPLATE_TESTS = {
            "BODY HAS 2 OF (G72_UP, G1_Down, G59_UP)",
            "RULE HAS NONE OF (G72_UP)",
            "SizeOf(BODY) ≥ 2"};

    public static void main(String[] args) {

        List<Map<String, Boolean>> data = readData(FILE_PATH);

        Map<Set<String>, Integer> frequentItemset = runApriori(data, SUPPORT);
        List<String> associationRules = generateAssociationRules(frequentItemset, CONFIDENCE);

        System.out.println("------------------Apriori and AR mining ----------------------");
        System.out.println("Frequent itemset ..:::.. Size = " + frequentItemset.size() + " ..:::.. " + frequentItemset);
        System.out.println("Association rules ..:::.. Size = " + associationRules.size() + " ..:::.. " + associationRules);

        /* Parse templates and find association rules that match the conditions */
        System.out.println("----------------- Template tests -----------------------");
        for (String template : TEMPLATE_TESTS) {
            List<String> templateResult = parseTemplate(template, associationRules);
            System.out.println(template + " ..:::.. Size = " + templateResult.size() + " ..:::.. " + templateResult);
        }
    }

    private static List<String> parseTemplate(String text, List<String> associationRules) {
        String regexTemplate1 = "(.*) (AND|OR) (.*)";
        Pattern patternTemplate1 = Pattern.compile(regexTemplate1);
        Matcher matcherTemplate1 = patternTemplate1.matcher(text);

        if (matcherTemplate1.find()) {
            List<String> rulesLeft = parseTemplate(matcherTemplate1.group(1), associationRules);
            List<String> rulesRight = parseTemplate(matcherTemplate1.group(3), associationRules);
            String andOr = matcherTemplate1.group(2);
            if (andOr.equalsIgnoreCase("AND")) {
                rulesLeft.retainAll(rulesRight);
                return rulesLeft;
            } else {
                rulesLeft.addAll(rulesRight);
                return rulesLeft;
            }
        } else {

            String regexTemplate2 = "(RULE|BODY|HEAD) HAS (ANY|\\d+|NONE) OF \\((.*?)\\)";
            Pattern patternTemplate2 = Pattern.compile(regexTemplate2);
            Matcher matcherTemplate2 = patternTemplate2.matcher(text);

            if (matcherTemplate2.find()) {
                String ruleSubpartLabel = matcherTemplate2.group(1);
                String howMany = matcherTemplate2.group(2);
                String featureNamesString = matcherTemplate2.group(3);
                String[] features = featureNamesString.split(",");
                List<String> qualifyingRules = new ArrayList<>();

                for (String rule : associationRules) {
                    String ruleSubpart = getRuleSubpart(rule, ruleSubpartLabel);

                    int count = 0;
                    for (String feature : features)
                        if (ruleSubpart.contains(feature.trim()))
                            count++;

                    if ((howMany.equals("ANY") && count > 0) || (howMany.equals("NONE") && count == 0) || (!howMany.equals("ANY") && !howMany.equals("NONE") && count == Integer.parseInt(howMany)))
                        qualifyingRules.add(rule);
                }
                return qualifyingRules;

            } else {
                String regexTemplate3 = "SizeOf\\((RULE|BODY|HEAD)\\) (>|>=|≥|<|<=|=|==) (\\d+)";
                Pattern patternTemplate3 = Pattern.compile(regexTemplate3);
                Matcher matcherTemplate3 = patternTemplate3.matcher(text);
                List<String> qualifyingRules = new ArrayList<>();

                if (matcherTemplate3.find()) {
                    String ruleSubpartLabel = matcherTemplate3.group(1);
                    String operator = matcherTemplate3.group(2);
                    int sizeCondition = Integer.parseInt(matcherTemplate3.group(3).trim());

                    for (String rule : associationRules) {
                        String ruleSubpart = getRuleSubpart(rule, ruleSubpartLabel);
                        int size = countSizeOfRule(ruleSubpart);

                        if (checkMathematicalCondition(size, operator, sizeCondition))
                            qualifyingRules.add(rule);
                    }
                    return qualifyingRules;

                } else {
                    System.out.println("== No match ==");
                }
            }
        }
        return null;
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

    private static int countSizeOfRule(String rule) {
        return rule.split("(,|==>)").length;
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

    private static List<String> generateAssociationRules(Map<Set<String>, Integer> frequentItemset, int minConfidence) {
        List<String> associationRules = new ArrayList<>();

        for (Set<String> L : frequentItemset.keySet()) {
            for (Set<String> S : generatePowerSet(L)) {
                if (S.size() > 0 && S.size() < L.size() && frequentItemset.containsKey(S) && frequentItemset.get(S) > 0) {
                    double supportCountL = frequentItemset.get(L);
                    double supportCountS = frequentItemset.get(S);
                    double confidence = 100.0 * (supportCountL / supportCountS);
                    if (confidence >= minConfidence) {
                        Set<String> LminusS = new HashSet<>(L);
                        LminusS.removeAll(S);
                        associationRules.add(S + " ==> " + LminusS);
                    }
                }
            }
        }

        return associationRules;
    }

    private static <String> Set<Set<String>> generatePowerSet(Set<String> theGivenSet) {
        Set<Set<String>> thePowerset = new HashSet<>();
        if (theGivenSet.isEmpty()) {
            thePowerset.add(new HashSet<String>());
            return thePowerset;
        }
        List<String> list = new ArrayList<>(theGivenSet);
        String head = list.get(0);
        Set<String> rest = new HashSet<>(list.subList(1, list.size()));
        for (Set<String> thisSet : generatePowerSet(rest)) {
            Set<String> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(thisSet);
            thePowerset.add(newSet);
            thePowerset.add(thisSet);
        }
        return thePowerset;
    }

    private static Map<Set<String>, Integer> runApriori(List<Map<String, Boolean>> data, int minSupport) {

        /* Start timer */
        long start = System.currentTimeMillis();

        /* C = Candidate set
           L = Frequent itemset */
        Map<Set<String>, Integer> resultSet = new HashMap<>();

        /* Make L1 (Frequent itemset with set-size = 1) */
        Map<Set<String>, Integer> L = generateL1(data, minSupport);
        resultSet.putAll(L);
        System.out.println("\nItemset of size = 1 generated.\nL.size() ==> [" + L.size() + "] ===> L = " + L);

        for (int k = 1; !L.isEmpty(); k++) {
            /* k is the size of the itemset being considered currently */

            System.out.println("\nIteration for itemset size = " + (k + 1) + " begins.");
            long iterationStart = System.currentTimeMillis();

            /* Generate candidate sets of size k */
            long t1 = System.currentTimeMillis();
            Map<Set<String>, Integer> C = generateCandidates(L);
            long t2 = System.currentTimeMillis();
            System.out.println(">> generateCandidates time is: " + ((double) (t2 - t1) / 1000) + " seconds.");

            /* Go through dataset and find support values */
            findCandidateSetsSupport(C, data);
            long t3 = System.currentTimeMillis();
            System.out.println(">> findCandidateSetsSupport time is: " + ((double) (t3 - t2) / 1000) + " seconds.");

            /* Get the frequent itemset (for set-size k) */
            L = generateLFromC(C, minSupport);
            long t4 = System.currentTimeMillis();
            System.out.println(">> generateLFromC time is: " + ((double) (t4 - t3) / 1000) + " seconds.");
            resultSet.putAll(L);
            System.out.println("Iteration for itemset size = " + (k + 1) + " complete. L.size() ==> [" + L.size() + "] ===> L = " + L);

            long end = System.currentTimeMillis();
            System.out.println(">>>> Iteration time is: " + ((double) (end - iterationStart) / 1000) + " seconds.");
        }

        long end = System.currentTimeMillis();
        System.out.println("\nTotal execution time is: " + ((double) (end - start) / 1000) + " seconds.");
        return resultSet;
    }

    private static void findCandidateSetsSupport(Map<Set<String>, Integer> C, List<Map<String, Boolean>> data) {

        /* Go through dataset and find support values */
        for (Set<String> set : C.keySet())
            for (Map<String, Boolean> sample : data) {
                boolean allPresent = true;
                for (String key : set)
                    allPresent = allPresent && sample.get(key);
                if (allPresent)
                    C.put(set, (C.containsKey(set) ? C.get(set) + 1 : 1));
            }
    }

    private static Map<Set<String>, Integer> generateL1(List<Map<String, Boolean>> data, int minSupport) {

        /* Generate candidate set C, with itemsets of size=1 for each possible attribute value */
        Map<Set<String>, Integer> C = generateC1(data);

        /* Fill in support values */
        findCandidateSetsSupport(C, data);

        return generateLFromC(C, minSupport);
    }

    private static Map<Set<String>, Integer> generateLFromC(Map<Set<String>, Integer> C, int minSupport) {
        Map<Set<String>, Integer> L = new HashMap<>();

        for (Set<String> key : C.keySet()) {
            int support = C.get(key);
            if (support >= minSupport)
                L.put(key, support);
        }

        return L;
    }

    private static Map<Set<String>, Integer> generateCandidates(Map<Set<String>, Integer> L) {
        /* Do a self-join */
        long t1 = System.currentTimeMillis();
        Map<Set<String>, Integer> selfJoinResult = selfJoin(L);
        long t2 = System.currentTimeMillis();
        System.out.println(">> selfJoin time is: " + ((double) (t2 - t1) / 1000) + " seconds.");

        /* Filter out (prune) those results which are not present in L */
        Map<Set<String>, Integer> prunedResult = prune(selfJoinResult, L);

        long t3 = System.currentTimeMillis();
        System.out.println(">> prune time is: " + ((double) (t3 - t2) / 1000) + " seconds.");

        return prunedResult;
    }

    private static Map<Set<String>, Integer> selfJoin(Map<Set<String>, Integer> L) {
        /* Need to first make a list that has all the sets (which are the keys in L) */
        List<Set<String>> mainset = new ArrayList<>();
        mainset.addAll(L.keySet());

        Map<Set<String>, Integer> result = new HashMap<>();

        for (int i = 0; i < mainset.size(); i++) {
            Set<String> set1 = mainset.get(i);
            for (int j = i + 1; j < mainset.size(); j++) {

                Set<String> set2 = mainset.get(j);

                int count = 0;
                for (String s : set1)
                    if (set2.contains(s))
                        count++;

                if (count == set1.size() - 1) {
                    Set<String> newSet = new HashSet<>();
                    newSet.addAll(set1);
                    newSet.addAll(set2);

                    /* Zero support for each candidate initially */
                    result.put(newSet, 0);
                }
            }
        }
        return result;
    }

    private static Map<Set<String>, Integer> prune(Map<Set<String>, Integer> newL, Map<Set<String>, Integer> oldL) {

        Map<Set<String>, Integer> prunedL = new HashMap<>();

        for (Set<String> setInNewL : newL.keySet()) {
            boolean somethingIsMissing = false;
            List<Set<String>> subsets = getSubsetsOfSizeMinusOne(setInNewL);
            for (Set<String> subset : subsets)
                if (!oldL.containsKey(subset))
                    somethingIsMissing = true;

            if (!somethingIsMissing)
                prunedL.put(setInNewL, newL.get(setInNewL));
        }
        return prunedL;
    }

    private static List<Set<String>> getSubsetsOfSizeMinusOne(Set<String> theSet) {
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

    private static Map<Set<String>, Integer> generateC1(List<Map<String, Boolean>> data) {
        Set<String> allPossibleValuesSet = data.get(0).keySet();

        Map<Set<String>, Integer> C = new HashMap<>();

        for (String value : allPossibleValuesSet) {
            Set<String> newSet = new HashSet<>();
            newSet.add(value);
            C.put(newSet, 0);
        }

        return C;
    }

    private static List<Map<String, Boolean>> readData(String filePath) {
        List<Map<String, Boolean>> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();

            while (line != null) {
                Map<String, Boolean> map = new HashMap<>();
                String tokens[] = line.split("\\t");

                /* Add gene values */
                for (int i = 1; i < tokens.length - 1; i++) {
                    String geneValue = tokens[i];
                    String geneUp = "G" + i + "_UP";
                    String geneDown = "G" + i + "_Down";
                    boolean isGeneUp = false, isGeneDown = false;
                    if (geneValue.equalsIgnoreCase("up"))
                        isGeneUp = true;
                    else
                        isGeneDown = true;
                    map.put(geneUp, isGeneUp);
                    map.put(geneDown, isGeneDown);
                }

                /* Add disease value */
                map.put("ALL", false);
                map.put("AML", false);
                map.put("Breast Cancer", false);
                map.put("Colon Cancer", false);
                map.put(tokens[tokens.length - 1], true);

                list.add(map);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
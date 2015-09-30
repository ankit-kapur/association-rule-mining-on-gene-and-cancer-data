package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Apriori {

    public static void main(String[] args) {

        String filePath = "/home/castamere/code/datamining/hw2/association-rule-test-data.txt";
        List<Map<String, Boolean>> data = getData(filePath);

        List<Set<String>> frequentItemset = runApriori(data, 30);
        System.out.println("\n\nFrequent itemset ===> " + frequentItemset);
    }

    private static List<Set<String>> runApriori(List<Map<String, Boolean>> data, int minSupport) {

        /* Start timer */
        long start = System.currentTimeMillis();

        /* C = Candidate set
           L = Frequent itemset */
        List<Set<String>> resultSet = new ArrayList<>();

        /* Make L1 (Frequent itemset with set-size = 1) */
        List<Set<String>> L = generateL1(data, minSupport);
        System.out.println("L1 ==> " + L);

        for (int k = 1; !L.isEmpty(); k++) {
            /* k is the size of the itemset being considered currently */

            System.out.println("\nIteration " + k + " begins.");
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
            resultSet.addAll(L);
            System.out.println("Iteration " + k + " complete. L size ==> [" + L.size() + "] ... " + L);

            long end = System.currentTimeMillis();
            System.out.println(">>>> Iteration time is: " + ((double) (end - iterationStart) / 1000) + " seconds.");
        }

        long end = System.currentTimeMillis();
        System.out.println("\n\nTotal execution time is: " + ((double) (end - start) / 1000) + " seconds.");
        return L;
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

    private static List<Set<String>> generateL1(List<Map<String, Boolean>> data, int minSupport) {

        /* Generate candidate set C, with itemsets of size=1 for each possible attribute value */
        Map<Set<String>, Integer> C = generateC1(data);

        /* Fill in support values */
        findCandidateSetsSupport(C, data);

        return generateLFromC(C, minSupport);
    }

    private static List<Set<String>> generateLFromC(Map<Set<String>, Integer> C, int minSupport) {
        List<Set<String>> L = new ArrayList<>();

        for (Set<String> key : C.keySet()) {
            int support = C.get(key);
            if (support >= minSupport)
                L.add(key);
        }

        return L;
    }

    private static Map<Set<String>, Integer> generateCandidates(List<Set<String>> L) {
        /* Do a self-join */
        long t1 = System.currentTimeMillis();
        Map<Set<String>, Integer> newCandidateSet = new HashMap<>();
        List<Set<String>> selfJoinResult = selfJoin(L);
        long t2 = System.currentTimeMillis();
        System.out.println(">> selfJoin time is: " + ((double) (t2 - t1) / 1000) + " seconds.");

        /* Filter out (prune) those results which are not present in L */
        List<Set<String>> prunedL = prune(selfJoinResult, L);

        long t3 = System.currentTimeMillis();
        System.out.println(">> prune time is: " + ((double) (t3 - t2) / 1000) + " seconds.");

        /* Zero support for each candidate initially */
        for (Set<String> set : prunedL)
            newCandidateSet.put(set, 0);

        return newCandidateSet;
    }

    private static List<Set<String>> selfJoin(List<Set<String>> mainset) {
        List<Set<String>> result = new ArrayList<>();

        for (int i = 0; i < mainset.size(); i++) {
            Set<String> set1 = mainset.get(i);
            for (int j = i + 1; j < mainset.size(); j++) {

                Set<String> set2 = mainset.get(j);

                int count = 0;
                for (String s: set1)
                    if (set2.contains(s))
                        count++;

                if (count == set1.size()-1) {
                    Set<String> newSet = new HashSet<>();
                    newSet.addAll(set1);
                    newSet.addAll(set2);
                    result.add(newSet);
                }
            }
        }
        return result;
    }

    private static List<Set<String>> prune(List<Set<String>> newL, List<Set<String>> oldL) {
        List<Set<String>> prunedL = new ArrayList<>();
        for (Set<String> setInNewL : newL) {
            boolean somethingIsMissing = false;
            List<Set<String>> subsets = getSubsetsOfSizeMinusOne(setInNewL);
            for (Set<String> subset : subsets)
                if (!oldL.contains(subset))
                    somethingIsMissing = true;

            if (!somethingIsMissing)
                prunedL.add(setInNewL);
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

    private static List<Map<String, Boolean>> getData(String filePath) {
        List<Map<String, Boolean>> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();

            while (line != null) {
                Map<String, Boolean> map = new HashMap<>();
                String tokens[] = line.split("\\t");

                /* Add gene values */
                for (int i = 1; i < tokens.length - 1; i++) {
                    String geneValue = tokens[i];
                    String geneUp = "gene" + i + "_up";
                    String geneDown = "gene" + i + "_down";
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
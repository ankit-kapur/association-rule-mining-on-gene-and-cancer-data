package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Apriori {

    public static void main(String[] args) {

        String filePath = "/home/castamere/code/datamining/hw2/association-rule-test-data.txt";
        List<Set<String>> data = getData(filePath);

        List<Set<String>> frequentItemset = runApriori(data, 30);
        System.out.println("\n\nFrequent itemset ===> " + frequentItemset);
    }

    private static List<Set<String>> runApriori(List<Set<String>> data, int minSupport) {
        /* C = Candidate set
           L = Frequent itemset */
        List<Set<String>> resultSet = new ArrayList<>();

        /* Make L1 (Frequent itemset with set-size = 1) */
        List<Set<String>> L = generateL1(data, minSupport);
        System.out.println("L1 ==> " + L);

        for (int k=1; !L.isEmpty(); k++) {
            /* k is the size of the itemset being considered currently */

            /* Generate candidate sets of size k */
            Map<Set<String>, Integer> C = generateCandidates(L);

            /* Go through dataset and find support values */
            findCandidateSetsSupport(C, data);

            /* Get the frequent itemset (for set-size k) */
            L = generateLFromC(C, minSupport);
//          resultSet.addAll(L);
            System.out.println("Iteration " + k + " complete. L ==> " + L);
        }

        return L;
    }

    private static void findCandidateSetsSupport(Map<Set<String>, Integer> C, List<Set<String>> data) {

        /* Go through dataset and find support values */
        for (Set<String> set: C.keySet())
            for (Set<String> sample: data)
                if (sample.containsAll(set))
                    C.put(set, (C.containsKey(set) ? C.get(set) + 1: 1));
    }

    private static List<Set<String>> generateL1(List<Set<String>> data, int minSupport) {

        Map<Set<String>, Integer> C = new HashMap<>();

        /* Generate candidate set C, with itemsets of size=1 for each possible attribute value */
        List<Set<String>> allPossibleValuesSet = getAllPossibleValuesSet(data);
        for (Set<String> set: allPossibleValuesSet)
            C.put(set, 0);

        /* Fill in support values */
        findCandidateSetsSupport(C, data);

        return generateLFromC(C, minSupport);
    }

    private static List<Set<String>> generateLFromC(Map<Set<String>, Integer> C, int minSupport) {
        List<Set<String>> L = new ArrayList<>();

        for (Set<String> key: C.keySet()) {
            int support = C.get(key);
            if (support >= minSupport)
                L.add(key);
        }

        return L;
    }

    private static Map<Set<String>, Integer> generateCandidates(List<Set<String>> L) {
        /* Do a self-join */
        Map<Set<String>, Integer> newCandidateSet = new HashMap<>();
        List<Set<String>> selfJoinResult = selfJoin(L);

        /* Filter out (prune) those results which are not present in L */
        List<Set<String>> prunedL = prune(selfJoinResult, L);

        /* Zero support for each candidate initially */
        for (Set<String> set: prunedL)
            newCandidateSet.put(set, 0);

        return newCandidateSet;
    }

    private static List<Set<String>> prune(List<Set<String>> newL, List<Set<String>> oldL) {
        List<Set<String>> prunedL = new ArrayList<>();
        for (Set<String> setInNewL: newL) {
            boolean somethingIsMissing = false;
            List<Set<String>> subsets = getSubsetsOfSizeMinusOne(setInNewL);
            for (Set<String> subset: subsets)
                if (!oldL.contains(subset))
                    somethingIsMissing = true;

            if (!somethingIsMissing)
                prunedL.add(setInNewL);
        }
        return prunedL;
    }

    private static List<Set<String>> getSubsetsOfSizeMinusOne(Set<String> set) {
        List<Set<String>> subsets = new ArrayList<>();

        for (String value1: set) {
            Set<String> newSet = new HashSet<>();
            for (String value2: set)
                if (!value1.equals(value2))
                    newSet.add(value2);
            subsets.add(newSet);
        }
        return subsets;
    }

    private static List<Set<String>> selfJoin(List<Set<String>> mainset) {
        List<Set<String>> result = new ArrayList<>();

        for (int i=0; i<mainset.size(); i++) {
            Set<String> set1 = mainset.get(i);
            for (int j=i+1; j<mainset.size(); j++) {
                Set<String> set2 = mainset.get(j);
                Set<String> newSet = new HashSet<>();
                newSet.addAll(set1);
                newSet.addAll(set2);

                boolean isUnique = true;
                for (Set<String> existingSet: result)
                    if (existingSet.containsAll(newSet))
                        isUnique = false;
                if (isUnique)
                    result.add(newSet);
            }
        }
        return result;
    }

    private static List<Set<String>> getAllPossibleValuesSet(List<Set<String>> data) {
        List<Set<String>> list = new ArrayList<>();
        Set<String> allPossibleValuesSet = new HashSet<>();

        for (Set<String> set: data)
            allPossibleValuesSet.addAll(set);

        for (String value: allPossibleValuesSet) {
            Set<String> newSet = new HashSet<>();
            newSet.add(value);
            list.add(newSet);
        }

        return list;
    }

    private static List<Set<String>> getData(String filePath) {
        List<Set<String>> list = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();

            while (line != null) {
                Set<String> set = new LinkedHashSet<>();
                String tokens[] = line.split("\\t");

                for (int i=1; i<tokens.length; i++) {
                    String value = tokens[i];
                    if (!value.equalsIgnoreCase("down")) {
                        if (value.equalsIgnoreCase("up"))
                            value = "C" + i;
                        set.add(value);
                    }
                }

                list.add(set);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
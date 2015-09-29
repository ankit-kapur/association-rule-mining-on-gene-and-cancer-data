package com.company;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Testing {
    public static void main(String[] args) {

        Set<String> set1 = new HashSet<>();
        set1.add("a");
        set1.add("b");
        set1.add("c");
        set1.add("d");

        Set<String> set2 = new HashSet<>();
        set2.add("b");
        set2.add("c");
        set2.add("d");

        Set<String> set3 = new HashSet<>();
        set3.add("a");
        set3.add("b");
        set3.add("d");

        List<Set<String>> mainset = new ArrayList<>();
        mainset.add(set1);
        mainset.add(set2);
        mainset.add(set3);

        System.out.println(mainset);
        List<Set<String>> result = selfJoin(mainset);

        result = getSubsetsOfSizeMinusOne(set1);
        System.out.println(result);
    }

    private static List<Set<String>> getSubsetsOfSizeMinusOne(Set<String> theSet) {
        List<Set<String>> subsets = new ArrayList<>();

        for (String value1: theSet) {
            Set<String> newSet = new HashSet<>();
            for (String value2: theSet)
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
}

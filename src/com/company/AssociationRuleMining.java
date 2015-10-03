package com.company;

import java.util.*;

public class AssociationRuleMining {

    public static Set<String> generateAssociationRules(Map<Set<String>, Integer> frequentItemset, int minConfidence) {
        Set<String> associationRules = new HashSet<>();

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
}

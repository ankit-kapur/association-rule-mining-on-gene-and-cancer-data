package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
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

        Map<Set<String>, Integer> frequentItemset = Apriori.runApriori(data, SUPPORT);
        List<String> associationRules = AssociationRuleMining.generateAssociationRules(frequentItemset, CONFIDENCE);

        System.out.println("------------------Apriori and AR mining ----------------------");
        System.out.println("Frequent itemset ..:::.. Size = " + frequentItemset.size() + " ..:::.. " + frequentItemset);
        System.out.println("Association rules ..:::.. Size = " + associationRules.size() + " ..:::.. " + associationRules);

        /* Parse templates and find association rules that match the conditions */
        System.out.println("\n------------------- Template tests -----------------------");
        for (String template : TEMPLATE_TESTS) {
            List<String> templateResult = TemplateParser.parseTemplate(template, associationRules);
            System.out.println(template + " ..:::.. Size = " + templateResult.size() + " ..:::.. " + templateResult);
        }
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

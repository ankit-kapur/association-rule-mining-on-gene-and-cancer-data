package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    /* ------------------------------ Note ---------------------------------
            To configure support, confidence, filepath, and template queries,
            please open the Configurations class, and change these parameters
       --------------------------------------------------------------------- */

    public static void main(String[] args) {

        List<Map<String, Boolean>> data = readData(Configurations.FILE_PATH);

        Map<Set<String>, Integer> frequentItemset = Apriori.runApriori(data, Configurations.SUPPORT);
        Set<String> associationRules = AssociationRuleMining.generateAssociationRules(frequentItemset, Configurations.CONFIDENCE);

        System.out.println("\n------------------Apriori and AR mining ----------------------");
        System.out.println("Frequent itemset >>>> Size = " + frequentItemset.size() + " >>>> " + frequentItemset);
        System.out.println("Association rules >>>> Size = " + associationRules.size() + " >>>> " + associationRules);

        /* Parse templates and find association rules that match the conditions */
        System.out.println("\n------------------- Template query tests -----------------------");
        for (String template : Configurations.TEMPLATE_TEST_QUERIES) {
            Set<String> templateResult = TemplateParser.parseTemplate(template, associationRules);
            System.out.println(template + " >>>> Size = " + templateResult.size() + " >>>> " + templateResult);
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
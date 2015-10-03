package com.company;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateParser {

    public static Set<String> parseTemplate(String text, Set<String> associationRules) {
        text = text.replaceAll("Gene", "G").replaceAll("\\[", "(").replaceAll("]", ")").replaceAll("'", "");
        String regexTemplate1 = "(.*) (AND|OR) (.*)";
        Pattern patternTemplate1 = Pattern.compile(regexTemplate1);
        Matcher matcherTemplate1 = patternTemplate1.matcher(text);

        if (matcherTemplate1.find()) {
            Set<String> rulesLeft = parseTemplate(matcherTemplate1.group(1), associationRules);
            Set<String> rulesRight = parseTemplate(matcherTemplate1.group(3), associationRules);
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
                Set<String> qualifyingRules = new HashSet<>();

                for (String rule : associationRules) {
                    String ruleSubpart = Helper.getRuleSubpart(rule, ruleSubpartLabel);

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
                Set<String> qualifyingRules = new HashSet<>();

                if (matcherTemplate3.find()) {
                    String ruleSubpartLabel = matcherTemplate3.group(1);
                    String operator = matcherTemplate3.group(2);
                    int sizeCondition = Integer.parseInt(matcherTemplate3.group(3).trim());

                    for (String rule : associationRules) {
                        String ruleSubpart = Helper.getRuleSubpart(rule, ruleSubpartLabel);
                        int size = Helper.countSizeOfRule(ruleSubpart);

                        if (Helper.checkMathematicalCondition(size, operator, sizeCondition))
                            qualifyingRules.add(rule);
                    }
                }

                return qualifyingRules;
            }
        }
    }
}

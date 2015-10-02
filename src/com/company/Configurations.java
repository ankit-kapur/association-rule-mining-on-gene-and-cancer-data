package com.company;

public class Configurations {

    static final int SUPPORT = 50;
    static final int CONFIDENCE = 70;

    static final String FILE_PATH = "/home/castamere/code/datamining/hw2/association-rule-test-data.txt";

    static final String[] TEMPLATE_TEST_QUERIES = {

            /* Template 1 */
            "RULE HAS ANY OF ('G1_UP')",
            "RULE HAS NONE OF ('G1_UP')",
            "RULE HAS 1 OF ('G1_UP','G10_Down')",
            "BODY HAS ANY OF ['G1_UP']",
            "BODY HAS NONE OF ['G1_UP']",
            "BODY HAS 1 OF ('G1_UP','G10_Down')",
            "HEAD HAS ANY OF ['G1_UP']",
            "HEAD HAS NONE OF ['G1_UP']",
            "HEAD HAS 1 OF ('G1_UP','G10_Down')",
            "BODY HAS 2 OF (G72_UP, G1_Down, G59_UP)",
            "RULE HAS NONE OF (G72_UP)",

            /* Template 2 */
            "SizeOf(RULE) ≥ 2",
            "RULE HAS 1 OF ['Gene1_UP', 'Gene10_Down']"};

}

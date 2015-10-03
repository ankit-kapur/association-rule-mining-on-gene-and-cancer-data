package com.company;

public class Configurations {

    static final int SUPPORT = 50;
    static final int CONFIDENCE = 70;

    static final String FILE_PATH = "./association-rule-test-data.txt";

    static final String[] TEMPLATE_TEST_QUERIES = {

            /* Template 1 */
            "RULE HAS 1 OF ['Gene1_UP', 'Gene10_Down']",
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
            "SizeOf(BODY) ≥ 2",
            "SizeOf(HEAD) ≥ 2",

            /* Template 3 */
            "BODY HAS ANY OF ['G1_UP'] OR HEAD HAS 1 OF ('G59_UP')",
            "BODY HAS ANY OF ['G1_UP'] AND HEAD HAS 1 OF ('G59_UP')",
            "BODY HAS ANY OF ['G1_UP'] OR SizeOf(HEAD) ≥ 2",
            "BODY HAS ANY OF ['G1_UP'] AND SizeOf(HEAD) ≥ 2",
            "SizeOf(BODY) ≥ 1 OR SizeOf(HEAD) ≥ 2",
            "SizeOf(BODY) ≥ 1 AND SizeOf(HEAD) ≥ 2"
    };

}
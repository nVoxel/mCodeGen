package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class ExpressionsExample {
    int[] forTypeConversion1 = { 1, 2, 3 };
    List<String> forTypeConversion2 = new ArrayList(12);
    String testNull = null;

    // literal
    String literal = "literal literal";

    // identifier
    String identifier = literal;

    // property access
    ForTest testClass = new ForTest("ForTest ForTest", 1);
    String propertyAccessOne = testClass.forPae;
    String propertyAccessTwo = testClass.forPaeTwo().someVal;
    String propertyAccessThree = ForTest2.ForTest3.someValTwo;
    PrintStream propertyAccessFour = out;

    // method call
    int methodCall = identifier.codePointAt(0);

    // object creation
    ForTest objectCreation = new ForTest(identifier.toLowerCase(), 30 * 8);

    // binary
    int binary = 5 * 2;

    // unary
    boolean unary = !true;

    // assignment
    int assignment = binary += 10;

    // ternary
    Double ternary = binary == 20 ? 1.0 : 2.0;

    // cast
    ForTest2 forCast = new ForTest2();
    ForTest cast = (ForTest) forCast;

    // type check
    boolean typeCheck = forCast instanceof ForTest;

    static class ForTest {
        String forPae = "forPae forPae";

        public ForTest(String test1, int test2) {
            out.println(test1);
            out.println(test2);
        }

        public ForTest2.ForTest3 forPaeTwo() {
            return new ForTest2.ForTest3();
        }
    }

    static class ForTest2 extends ForTest {
        public ForTest2() {
            super("ForTest2 ForTest2", 12);
        }

        static class ForTest3 {
            String someVal = "someVal someVal";
            static String someValTwo = "someValTwo someValTwo";
        }
    }
}
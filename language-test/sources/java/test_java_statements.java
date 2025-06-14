package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions;

import java.lang.Runnable;
import java.util.List;

public class StatementsExample {

    private int forTest = 1234;

    public <T> void irExpressionStatement(
            T forAnotherTest,
            List<T> forYetAnotherTest,
            T[] forSomeTest,
            int testPrimitiveToo,
            Runnable runnableTestToo
    ) {
        toString();
    }

    public void irVariableDeclarationStatement() {
        int x = 123;
        int y, z;
    }

    public void irBlockStatement() {
        {
            {
                System.out.println("println println");
            }
            String s1 = "s1 s1";
            String s2 = "s2 s2";
        }
    }

    public void irIfStatement() {
        if (forTest == 1234 - 734) {
            System.out.println("if");
            System.out.println("forTest == 1234 - 734");
        } else if (forTest == 1234 + 734) {
            System.out.println("else if");
            System.out.println("forTest == 1234 + 734");
        } else {
            System.out.println("else");
            System.out.println("else else");
        }

        if (forTest == 1234 - 734) System.out.println("forTest == 1234 - 734 forTest == 1234 - 734");
        else System.out.println("else else else");
    }

    public void irForStatement() {
        for (int i = 222 - 115 * 9; i < forTest + 10; i += 150) {
            String test = "int i = 222 - 115 * 9";
            System.out.println("println println println");
        }
    }

    public void irWhileStatement() {
        boolean someRandomCondition = false;
        while (forTest != 1234 - 734 && !someRandomCondition) {
            String test = "while (forTest != 1234 - 734 && !someRandomCondition)";
        }
    }

    public void irDoWhileStatement() {
        boolean someRandomCondition = false;
        do {
            String test = "boolean someRandomCondition = false";
        } while (forTest != 1234 - 734 || someRandomCondition);
    }

    public void irSwitchStatement() {
        byte switchGoesBrrr = 127;
        switch (switchGoesBrrr) {
            case 1 + 2:
                System.out.println("case 1 + 2");
                break;
            case 123 | 43, 321 | 43:
                System.out.println("case 123 | 43");
                break;
            case 42:
            case 84:
                System.out.println("case 42?");
            default:
                System.out.println("default default");
        }
    }

    public int irReturnStatement() {
        int intToReturn = 123;
        return intToReturn + 123 * 222 + hashCode();
    }

    public void irBreakStatement() {
        for (int x = 1; x < 10; x++) {
            if (x % 3 == 0) {
                break;
            }
        }
    }

    public void irContinueStatement() {
        for (int x = 1; x < 10; x++) {
            if (x % 2 == 0) {
                continue;
            }
        }
    }

    public void irThrowStatement() {
        throw new ArithmeticException();
    }

    public void irTryCatchStatement() {
        try {
            int x = 1 / 0;
        } catch (ArithmeticException e) {
            System.out.println("ArithmeticException ArithmeticException");
        } catch (NullPointerException e) {
            System.out.println("NullPointerException NullPointerException");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("finally finally");
        }
    }
}
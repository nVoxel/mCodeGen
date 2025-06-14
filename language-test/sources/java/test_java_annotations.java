package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AnnotationsTest {

    @Retention(RetentionPolicy.SOURCE)
    public @interface TestAnnotationWithValueArg {
        float value(); // may not write this name explicitly
    }

    @Retention(RetentionPolicy.CLASS)
    public @interface TestAnnotationWithOneArg {
        int[] someOneValue(); // must write this name explicitly
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotationWithTwoArgs {
        String someValue(); // must write this name explicitly
        int someAnotherValue(); // must write this name explicitly
    }

    public @interface TestAnnotationWithTwoArgsWithDefaultValue {
        String someValue(); // must write this name explicitly
        int someDefaultValue() default 12345; // may not include this arg
    }

    @TestAnnotationWithValueArg(1.0f)
    String someAnnotatedField = "someAnnotatedField";

    @TestAnnotationWithOneArg(someOneValue = {1, 2, 3})
    int someAnnotatedFieldV2 = 10;

    @TestAnnotationWithTwoArgs(someValue = "1someValue", someAnotherValue = (int)123.0)
    private Double anotherAnnotatedField;

    @TestAnnotationWithTwoArgsWithDefaultValue(someValue = true ? "2someValue" : "3someValue")
    protected Double anotherDefaultAnnotatedField;

    @TestAnnotationWithTwoArgsWithDefaultValue(someValue = "4someValue", someDefaultValue = 9_000 * 545)
    public Double anotherNotDefaultAnnotatedField;
}
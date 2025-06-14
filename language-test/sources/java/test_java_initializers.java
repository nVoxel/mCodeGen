package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions;

public class TestInitializers {

    static {
        System.out.println("Static initializer");
    }

    {
        System.out.println("Instance initializer");
    }
}
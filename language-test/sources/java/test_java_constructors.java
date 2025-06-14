package com.voxeldev.mcodegen.dsl.language.java.source.parse.extensions;

class Base {
    int n;

    // designated constructor
    Base(int n) {
        this.n = n;
    }

    class Derived extends Base {

        // (a) delegate upward ── super(...)
        Derived() {
            super(0);
        }
    
        // (b) delegate sideways ── this(...)
        Derived(int n) {
            this();
            System.out.println("n = " + n);
        }
    }
}
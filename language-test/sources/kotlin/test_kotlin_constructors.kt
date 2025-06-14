package com.voxeldev.mcodegen.dsl.language.kotlin

open class Base(val n: Int) {
    // secondary ctors
    class Derived : Base {

        // (a) delegate upward
        constructor() : super(0) {
            println("no-arg")
        }

        // (b) delegate sideways
        constructor(x: Int) : this() {
            println("x = $x")
        }
    }

    // primary ctor
    class Person(
        name: String,
        private val testOne: Int,
        internal var testTwo: Float,
    ) : Base(name.length) {
        // secondary ctor delegates to the primary ctor
        constructor() : this("John Doe", 123, 1.23f)
    }
}

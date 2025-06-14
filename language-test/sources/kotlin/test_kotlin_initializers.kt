package com.voxeldev.mcodegen.dsl.language.kotlin

object SampleObject {
    val test = "Testing objects"

    private fun testObjectFun(): Int = 1001

    internal object SampleNestedObject {
        val testSample = "Testing nested objects"

        private class SampleNestedObjectNestedClass {
            val testSampleClass = "Testing deeply nested classes"
        }
    }

    open private class SampleNestedClass {
        protected open val testClass = 2.123456789

        protected final val testClass2 = NegativeArraySizeException()

        internal companion object {
            public val testingTesting123 = 5_005_302_502.2_729
        }
    }
}

class TestKotlinInitializers {
    init {
        println("Instance initializer")
    }

    companion object {
        init {
            println("Static initializer")
        }
    }
}
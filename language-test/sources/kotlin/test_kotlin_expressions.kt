package com.voxeldev.mcodegen.dsl.language.kotlin

import java.util.ArrayList
import com.voxeldev.mcodegen.dsl.language.kotlin.ForReference.Companion.fr2
import com.voxeldev.mcodegen.dsl.language.kotlin.tests.TEST_FOR_EXPRESSIONS

val alsoTest = 123

class ForReference {
    companion object {
        val fr = "fr fr"
        val fr2 = "fr2 fr2"
    }
}

class ExpressionsExample {
    var forTypeConversion1: IntArray = intArrayOf(1, 2, 3)
    var forTypeConversion2: MutableList<String> = ArrayList(12)
    var testNull: String? = null

    // literal
    var literal: String = "literal literal"

    // identifier
    var identifierOne: String = literal // SimpleNameExpression
    var identifierTwo = ForReference.fr // KtDotQualifiedExpression
    var identifierThree = alsoTest // SimpleNameExpression
    var identifierFour = fr2 // SimpleNameExpression
    var identifierFive = TEST_FOR_EXPRESSIONS // SimpleNameExpression

    // property access
    var testClass: ForTest = ForTest("testClass ForTest", 1)
    var propertyAccessOne: String? = testClass.forPae
    var propertyAccessTwo: String? = testClass.forPaeTwo.someVal

    // method call
    var methodCallOne: Int = identifierOne.codePointAt(0)
    var methodCallTwo: Int = identifierOne.codePointAt(0).and(2)

    // object creation
    var objectCreation: ForTest = ForTest(identifierOne.toLowerCase(), 30 * 8)

    // binary
    var binaryOne: Int = 5 * 2
    var binaryTwo = 10 shr 20

    // unary
    var unary: Boolean = !true

    // assignment
    var assignment: Int = 10.let { binaryOne += it; binaryOne }

    // ternary
    var ternary: Double = if (binaryOne == 20) 1.0 else 2.0

    // cast
    var forCast: ForTest2 = ForTest2()
    var cast: ForTest = forCast as ForTest

    // type check
    var typeCheck: Boolean = forCast is ForTest

    // lambda
    val someLambda: (Int) -> Int = { t ->
        println("someLambda someLambda")
        3+t
    }
    var someLambdaResult = someLambda(5)

    open class ForTest {
        var forPae: String = "forPae forPae"
        var forPaeTwo = ForTest3()

        constructor(test1: String?, test2: Int) {
            println(test1)
            println(test2)
        }
    }

    class ForTest2 : ForTest {
        constructor() : super("ForTest2 constructor", 12)
    }
    
    data class ForTest3(val someVal: String = "someVal someVal")
}
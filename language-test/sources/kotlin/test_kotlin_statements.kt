package com.voxeldev.mcodegen.dsl.language.kotlin

private fun testTopLevelMethod(testArg: String = "testTopLevelMethod testArg") {
    print(testArg)
}

private var testTopLevelVar: String = "testTopLevelVar testTopLevelVar"

private val testTopLevelVal: String = "testTopLevelVal testTopLevelVal"

class TestKotlinStatements {

    private val forTest = 1234

    fun <T> irExpressionStatement(
        forAnotherTest: T,
        forYetAnotherTest: MutableList<T>,
        forSomeTest: Array<T>,
        testPrimitiveToo: Int,
        runnableTestToo: Runnable
    ) {
        toString()
    }

    fun irVariableDeclarationStatement() {
        val x = 123
        var y: Int
        var z: Int
    }

    fun irBlockStatement() {
        run {
            run {
                println("println println")
            }
            val s1 = "s1 s1"
            val s2 = "s2 s2"
        }
    }

    fun irIfStatement() {
        if (forTest == 1234 - 734) {
            println("if")
            println("forTest == 1234 - 734")
        } else if (forTest == 1234 + 734) {
            println("else if")
            println("forTest == 1234 + 734")
        } else {
            println("else")
            println("else else")
        }

        if (forTest == 1234 - 734) println("forTest == 1234 - 734 forTest == 1234 - 734")
        else println("else else else")
    }

    fun irForStatement() {
        var i = 222 - 115 * 9
        while (i < forTest + 10) {
            val test = "while"
            println("i < forTest + 10")
            i += 150
        }
    }

    fun irWhileStatement() {
        val someRandomCondition = false
        while (forTest != 1234 - 734 && !someRandomCondition) {
            val test = "someRandomCondition = false"
        }
    }

    fun irDoWhileStatement() {
        val someRandomCondition = false
        do {
            val test = "someRandomCondition = false someRandomCondition = false"
        } while (forTest != 1234 - 734 || someRandomCondition)
    }

    fun irSwitchStatement(x: Int) {
        when (x) {
            1 -> println("One")
    
            2, 3 -> println("Two or three")
    
            42 -> {
                println("Fourty two")
                println("Wow!")
            }
    
            else -> println("None of the numbers")
        }
    }

    fun irReturnStatement(): Int {
        val intToReturn = 123
        
        intToReturn.let { 
            return@let 12345
        }
        
        return intToReturn + 123 * 222 + hashCode()
    }

    fun irBreakStatement() {
        var x = 1
        while (x < 10) {
            if (x % 3 == 0) {
                break
            }
            x++
        }
    }

    fun irContinueStatement() {
        var x = 1
        while (x < 10) {
            if (x % 2 == 0) {
                continue
            }
            x++
        }
    }

    fun irThrowStatement() {
        throw ArithmeticException()
    }

    fun irTryCatchStatement() {
        try {
            val x = 1 / 0
        } catch (e: ArithmeticException) {
            println("ArithmeticException ArithmeticException")
        } catch (e: NullPointerException) {
            println("NullPointerException NullPointerException")
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            println("finally finally")
        }
    }
}
package com.voxeldev.mcodegen.dsl.language.kotlin

class TestKotlinInheritanceSix<U> : TestKotlinInheritanceFive<U>(10) {
    private var test = mutableListOf<U>()
}

open class TestKotlinInheritanceFive<T>(override val size: Int) : Collection<T> {
    override fun contains(element: T): Boolean {
        TODO("Not yet implemented")
    }
    override fun containsAll(elements: Collection<T>): Boolean {
        TODO("Not yet implemented")
    }
    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }
    override fun iterator(): Iterator<T> {
        TODO("Not yet implemented")
    }
}

class TestKotlinInheritanceFour(override val size: Int) : Collection<TestKotlinInheritanceOne> {
    override fun contains(element: TestKotlinInheritanceOne): Boolean {
        TODO("Not yet implemented")
    }
    override fun containsAll(elements: Collection<TestKotlinInheritanceOne>): Boolean {
        TODO("Not yet implemented")
    }
    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }
    override fun iterator(): Iterator<TestKotlinInheritanceOne> {
        TODO("Not yet implemented")
    }
}

sealed interface ITestKotlinInheritanceOne
interface ITestKotlinInheritanceTwo
interface ITestKotlinInheritanceThree
interface ITestKotlinInheritanceFour : ITestKotlinInheritanceThree {
    interface ITestKotlinInheritanceFive : ITestKotlinInheritanceOne
}

open class TestKotlinInheritanceOne : ITestKotlinInheritanceOne
class TestKotlinInheritanceTwo : TestKotlinInheritanceOne(), ITestKotlinInheritanceTwo, ITestKotlinInheritanceThree
class TestKotlinInheritanceThree : ITestKotlinInheritanceFour.ITestKotlinInheritanceFive
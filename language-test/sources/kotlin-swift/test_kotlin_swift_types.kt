package com.voxeldev.mcodegen.dsl.language.kotlin

class TestReferenceType {
    val test = 123
}

class TestTypes<T>() {
    // references
    val referenceTest = TestReferenceType()
    val stringTest: String = "Lorem ipsum"

    // primitives
    val voidTest: Unit? = null
    val booleanTest: Boolean = true
    val byteTest: Byte = 127
    val shortTest: Short = 32767
    val intTest: Int = 2147483647
    val longTest: Long = 9223372036854775807
    val charTest: Char = 'a'
    val floatTest: Float = 3.4028235E38F
    val doubleTest: Double = 1.7976931348623157E308

    // function
    val noArgFunction: () -> Unit = { }
    val oneArgFunction: (Int) -> Int = { it * 2 }
    val twoArgFunctions: (Int, TestReferenceType) -> Long = { i, t ->
        val multiplier: Long = 10
        i * t.test * multiplier
    }

    // primitive array
    val booleanArray: BooleanArray = booleanArrayOf()
    val byteArray: ByteArray = byteArrayOf()
    val shortArray: ShortArray = shortArrayOf()
    val intArray: IntArray = intArrayOf()
    val longArray: LongArray = longArrayOf()
    val charArray: CharArray = charArrayOf()
    val floatArray: FloatArray = floatArrayOf()
    val doubleArray: DoubleArray = doubleArrayOf()

    // reference array
    val oneDimArray: Array<TestReferenceType> = arrayOf(TestReferenceType(), TestReferenceType())
    val twoDimArray: Array<Array<Int>> = arrayOf(arrayOf(1), arrayOf(2, 3))

    // generic
    var genericProperty: T? = null
    var genericArray: Array<T>? = null
}
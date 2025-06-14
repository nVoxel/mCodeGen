package com.voxeldev.mcodegen.dsl.language.kotlin.tests

const val TEST_FOR_EXPRESSIONS = "123"

fun forImportTest(): Boolean {
    return true
}

class TestKotlinAnnotations {

    @Retention(AnnotationRetention.SOURCE)
    public annotation class TestAnnotationWithValueArg(
        val value: Float
    )

    @Retention(AnnotationRetention.BINARY)
    public annotation class TestAnnotationWithOneArg(
        val someOneValue: IntArray
    )

    @Retention(AnnotationRetention.RUNTIME)
    public annotation class TestAnnotationWithTwoArgs(
        val someValue: String,
        val someAnotherValue: Int
    )

    public annotation class TestAnnotationWithTwoArgsWithDefaultValue(
        val someValue: String,
        val someDefaultValue: Int = 12345
    )

    @TestAnnotationWithValueArg(1.0f)
    var someAnnotatedField = "someAnnotatedField"

    // need to convert this to ArrayInitializerExpression and also ArrayAccessExpression
    @TestAnnotationWithOneArg(intArrayOf(1, 2, 3))
    val someAnnotatedFieldV2 = 10

    @TestAnnotationWithTwoArgs(someValue = "someValue someValue", someAnotherValue = 123 and 522)
    private val anotherAnnotatedField: Double = 0.0

    @TestAnnotationWithTwoArgsWithDefaultValue(someValue = "someValue someValue someValue")
    protected val anotherDefaultAnnotatedField: Double = 0.0

    @TestAnnotationWithTwoArgsWithDefaultValue(someValue = "someValue someValue someValue someValue", someDefaultValue = 9_000 * 545)
    public val anotherNotDefaultAnnotatedField: Double = 0.0

    internal class TestPrimaryCtor(
        @TestAnnotationWithOneArg(intArrayOf(0))
        internal val testPrimaryCtorField: Int,
    )
}
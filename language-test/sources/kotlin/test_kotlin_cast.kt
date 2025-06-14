package com.voxeldev.mcodegen.dsl.language.kotlin

import com.voxeldev.mcodegen.dsl.language.kotlin.tests.forImportTest

class TdApi {
    class PageBlockTable(
        override val variable: TdApi.PageBlockTableCell?,
        override val array: Array<PageBlockTableCell?>?,
        override val matrix: Array<Array<PageBlockTableCell?>?>?
    ) : TdPageBlockTable {
        constructor() : this(null, null, null)
    }

    class PageBlockTableCell()
}

interface TdPageBlock

interface TdPageBlockTableCell

interface TdPageBlockTable : TdPageBlock {
    val variable: TdApi.PageBlockTableCell?
    val array: Array<TdApi.PageBlockTableCell?>?
    val matrix: Array<Array<TdApi.PageBlockTableCell?>?>?

    fun getConstructor(): Int {
        return -942649288
    }

    interface InstanceGetter {
        fun getInstance(
            variable: TdApi.PageBlockTableCell?,
            array: Array<TdPageBlockTableCell?>?,
            matrix: Array<Array<TdPageBlockTableCell?>?>?,
        ): TdPageBlockTable

        fun getInstance(): TdPageBlockTable
    }
}

class TdPageBlockTableInstanceGetterImpl : TdPageBlockTable.InstanceGetter {
    override fun getInstance(
        variable: TdApi.PageBlockTableCell?,
        array: Array<TdPageBlockTableCell?>?,
        matrix: Array<Array<TdPageBlockTableCell?>?>?,
    ): TdPageBlockTable {
        return TdApi.PageBlockTable(
            variable as? TdApi.PageBlockTableCell,
            array?.map { it as? TdApi.PageBlockTableCell }?.toTypedArray(),
            matrix?.map { it?.map { it as? TdApi.PageBlockTableCell }?.toTypedArray() }?.toTypedArray()
        )
    }

    override fun getInstance(): TdPageBlockTable = TdApi.PageBlockTable()
}

class TestCastToGeneric {

    val forAnotherTest = forImportTest()

    inline fun <reified T> forTest(someParam: Any): Boolean {
        return someParam is T
    }

    fun testTypeCastToGeneric() {
        val actualTest = forTest<String>(7415_15_743_7357)
    }
}
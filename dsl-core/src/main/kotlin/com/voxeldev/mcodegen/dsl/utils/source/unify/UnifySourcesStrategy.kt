package com.voxeldev.mcodegen.dsl.utils.source.unify

interface UnifySourcesStrategy<T, R>

interface UnifySourcesListStrategy<T, R> : UnifySourcesStrategy<T, R> {
    fun getUnifiedSourcesList(
        vararg sources: T,
    ): R
}
package com.voxeldev.mcodegen.dsl.utils.source.unify

fun <T, R> unifySources(
    strategy: UnifySourcesStrategy<T, R>,
    vararg sources: T,
    unifyCall: UnifySourcesStrategy<T, R>.() -> R,
) : R = with(strategy) {
    unifyCall()
}

fun <T, R> unifySourcesList(
    strategy: UnifySourcesListStrategy<T, R>,
    vararg sources: T,
) : R = strategy.getUnifiedSourcesList(*sources)

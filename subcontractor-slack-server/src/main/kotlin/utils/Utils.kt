package utils

import java.util.concurrent.CompletableFuture

fun <T, R> CompletableFuture<T>.combine(completable: CompletableFuture<R>): CompletableFuture<Pair<T, R>> {
    return thenCombine(completable) { origin, other ->
        origin to other
    }
}

fun <T, R> CompletableFuture<T>.combineAsync(completable: CompletableFuture<R>): CompletableFuture<Pair<T, R>> {
    return thenCombineAsync(completable) { origin, other ->
        origin to other
    }
}


fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

package com.example.forecast.internal

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

//extend the Task class, with on Success and on Failure we complete the deferred and return it
fun <T> Task<T>.asDeferred(): Deferred<T> {
    //it's up to us to tell when the deferred is completed or not completed or completed exceptionally
    val deferred = CompletableDeferred<T>()

    this.addOnSuccessListener { result ->
        deferred.complete(result)
    }

    this.addOnFailureListener { exception ->
        deferred.completeExceptionally(exception)
    }
    return deferred
}
package com.jonbott.learningrxjava.SimpleExamples

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable

// Singleton
object SimpleRx {

    // A container that we will get a bunch of disposables from
    var bag = CompositeDisposable()

    fun simpleValues() {
        println("~~~~~~simpleValues~~~~~~")

        // Behaviour Relay
        val someInfo = BehaviorRelay.createDefault("1")
        println("🙈 someInfo.value ${ someInfo.value }")

        // Asigning a different variable
        val plainString = someInfo.value
        println("🙈 plainString: $plainString")


        // Change the value
        someInfo.accept("2")
        println("🙈 someInfo.value ${ someInfo.value }")
    }

}
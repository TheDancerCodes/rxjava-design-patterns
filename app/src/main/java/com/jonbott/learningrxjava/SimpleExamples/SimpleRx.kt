package com.jonbott.learningrxjava.SimpleExamples

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.lang.IllegalArgumentException

// Singleton
object SimpleRx {

    // A container that we will get a bunch of disposables from
    var bag = CompositeDisposable()

    fun simpleValues() {
        println("~~~~~~simpleValues~~~~~~")

        // Behaviour Relay: Imperative
        val someInfo = BehaviorRelay.createDefault("1")
        println("🙈 someInfo.value ${ someInfo.value }")

        // Assigning a different variable
        val plainString = someInfo.value
        println("🙈 plainString: $plainString")


        // Change the value
        someInfo.accept("2")
        println("🙈 someInfo.value ${ someInfo.value }")

        // Behaviour Relay: Declarative
        someInfo.subscribe{ newValue ->
            println("🦄 value has changed: $newValue")
        }

        // Change value
        someInfo.accept("3")

        // NOTE: Relays will never receive onError & onComplete events
    }

    // Behaviour Subject
    fun subjects() {
        val behaviorSubject = BehaviorSubject.createDefault(24)

        // Save it into a disposable variable
        val disposable = behaviorSubject.subscribe({ newValue -> //onNext
            println("🕺 behaviorSubject subscription: $newValue")
        }, { error -> //onError
            println("🕺🏾 error: ${ error.localizedMessage }")

        }, { //onCompleted - Triggered when all events are finished & nothing else is coming from this subject
            println("🕺🏾 completed")

        }, { disposable -> //onSubscribed
            println("🕺🏾 subscribed")

        })

        // Push in onNext events to pass in new values
        behaviorSubject.onNext(34)
        behaviorSubject.onNext(47)
        behaviorSubject.onNext(47) // duplicates show as a new events by default.

        // onError
        val someException = IllegalArgumentException("Some fake error")

        // Throw/ push an error into the stream
        behaviorSubject.onError(someException)

        // This will never show because the whole subject will have closed down by the time it gets
        // to this onNext event
        behaviorSubject.onNext(29)

    }
}
package com.jonbott.learningrxjava.SimpleExamples

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jonbott.learningrxjava.Common.disposedBy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

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

        // (1) onError
        // val someException = IllegalArgumentException("Some fake error")

        // Throw/ push an error into the stream
        // behaviorSubject.onError(someException)

        // This will never show because the whole subject will have closed down by the time it gets
        // to this onNext event
        // behaviorSubject.onNext(29)

        // (2) onComplete
        behaviorSubject.onComplete()
        behaviorSubject.onNext(25) // Will never show

    }

    // Basic Observable
    fun basicObservable() {

        // The observable
        val observable = Observable.create<String> { observer ->

            // This lambda is called for every subscriber - by default
            println("🍄 ~~ Observable logic being triggered ~~")

            // Do work on a background thread - Using Coroutines
            launch {
                delay(1000) // artificial delay of 1 second

                // Push results that came from your work
                observer.onNext("some value 29")
                observer.onComplete()
            }
        }

        // Subscribe to the observable & determine what value is coming through
        observable.subscribe { someString ->
            println("🍄 New Value: $someString")

        }.disposedBy(bag)

        // Another observer - different approach to disposing
        val observer = observable.subscribe{ someString ->
            println("🍄 Another Subscriber: $someString")
        }

        observer.disposedBy(bag)



    }

    fun creatingObservables() {

        // Observable of one event
        // val observable = Observable.just(23)

        // A timer
        // val observable = Observable.interval(300, TimeUnit.MILLISECONDS)
        //        .timeInterval(AndroidSchedulers.mainThread())

        // Arrays of values
        // val observable = Observable.fromArray(1,2,3,4,5)

        // val userIds = arrayOf(1, 2, 3, 4, 5, 6, 7)
        // val observable = Observable.fromArray(*userIds) //Using the spread operator
        // val observable = userIds.toObservable()
    }
}
package com.jonbott.learningrxjava.Activities.DatabaseExample

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jonbott.learningrxjava.ModelLayer.ModelLayer
import com.jonbott.learningrxjava.ModelLayer.PersistenceLayer.PhotoDescription
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class DatabaseExamplePresenter {
    val modelLayer = ModelLayer.shared //normally injected

    // Consume changes in the presenter
    val photoDescriptions: BehaviorRelay<List<PhotoDescription>>
        // Use a getter & return the model layers description of this
        get() = modelLayer.photoDescriptions // Bubbling up for the lower layers

    init {
        launch {
            delay(3000) // # seconds delay

            // Model layer loads all PhotoDescriptions from DB
            modelLayer.loadAllPhotoDescriptions()
        }
    }
}
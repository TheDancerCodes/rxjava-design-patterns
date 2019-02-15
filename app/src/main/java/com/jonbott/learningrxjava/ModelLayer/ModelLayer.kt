package com.jonbott.learningrxjava.ModelLayer

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jonbott.learningrxjava.ModelLayer.NetworkLayer.NetworkLayer
import com.jonbott.learningrxjava.ModelLayer.PersistenceLayer.PersistenceLayer
import com.jonbott.learningrxjava.ModelLayer.PersistenceLayer.PhotoDescription

/**
 * Houses all of the data
 *
 * The NetworkLayer & the PersistenceLayer
 */
class ModelLayer {

    companion object {
        val shared = ModelLayer()
    }

    // Method to load all the descriptions
    val photoDescriptions = BehaviorRelay.createDefault(listOf<PhotoDescription>())

    private val networkLayer = NetworkLayer.instance
    private val persistenceLayer = PersistenceLayer.shared

    // Loading Data from the Database
    fun loadAllPhotoDescriptions() {

        // Result may be immediate but use async callbacks. This unifies how we get data across all APIs.
        persistenceLayer.loadAllPhotoDescriptions { photoDescriptions ->

            // Anyone listening to the BehaviorRelay in line 20 will be notified that new values
            // have been pushed in
            this.photoDescriptions.accept(photoDescriptions)
        }

    }

}
package com.jonbott.learningrxjava.ModelLayer

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jonbott.learningrxjava.ModelLayer.Entities.Message
import com.jonbott.learningrxjava.ModelLayer.NetworkLayer.NetworkLayer
import com.jonbott.learningrxjava.ModelLayer.PersistenceLayer.PersistenceLayer
import com.jonbott.learningrxjava.ModelLayer.PersistenceLayer.PhotoDescription
import io.reactivex.Single

/**
 * Houses all of the data
 *
 * The NetworkLayer & the PersistenceLayer
 */
class ModelLayer {

    companion object {
        val shared = ModelLayer()
    }

    // Load all the descriptions
    val photoDescriptions = BehaviorRelay.createDefault(listOf<PhotoDescription>())

    // Load messages
    val messages = BehaviorRelay.createDefault(listOf<Message>())

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

    // Method to get messages from the lower layer
    fun getMessages() {
        return networkLayer.getMessages({ messages ->

            // Accept the new value of messages coming in
            this.messages.accept(messages)
        }, { errorMessage ->
            notifyOfError(errorMessage)

        })
    }

    private fun notifyOfError(errorMessage: String) {

        //TODO: Notify user somehow: Toast or Snackbar
        println("❗️ Error occurred: $errorMessage")

    }

    fun getMessagesRx(): Single<List<Message>> {
        return networkLayer.getMessagesRx()
    }

}
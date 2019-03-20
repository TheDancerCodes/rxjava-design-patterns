package com.jonbott.learningrxjava.ModelLayer.NetworkLayer

import com.github.kittinunf.result.Result
import com.jonbott.datalayerexample.DataLayer.NetworkLayer.EndpointInterfaces.JsonPlaceHolder
import com.jonbott.datalayerexample.DataLayer.NetworkLayer.Helpers.ServiceGenerator
import com.jonbott.learningrxjava.Common.NullBox
import com.jonbott.learningrxjava.Common.StringLambda
import com.jonbott.learningrxjava.Common.VoidLambda
import com.jonbott.learningrxjava.ModelLayer.Entities.Message
import com.jonbott.learningrxjava.ModelLayer.Entities.Person
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.zip
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception


typealias MessageLambda = (Message?)->Unit
typealias MessagesLambda = (List<Message>?)->Unit

class NetworkLayer {
    companion object { val instance = NetworkLayer() }

    private val placeHolderApi: JsonPlaceHolder

    init {
        placeHolderApi = ServiceGenerator.createService(JsonPlaceHolder::class.java)
    }

    //region EndPoint Fully Rx
    fun getMessageRx(articleId: String): Single<Message> {
        return placeHolderApi.getMessageRx(articleId)
    }

    fun getMessagesRx(): Single<List<Message>> {
        return placeHolderApi.getMessagesRx()
    }

    fun postMessageRx(message: Message): Single<Message> {
        return placeHolderApi.postMessageRx(message)
    }
    //endregion


    //region End Point - SemiRx Way

    fun getMessages(success: MessagesLambda, failure: StringLambda) {
        val call = placeHolderApi.getMessages()

        call.enqueue(object: Callback<List<Message>> {
            override fun onResponse(call: Call<List<Message>>?, response: Response<List<Message>>?) {
                val article = parseRespone(response)
                success(article)
            }

            override fun onFailure(call: Call<List<Message>>?, t: Throwable?) {
                println("Failed to GET Message: ${ t?.message }")
                failure(t?.localizedMessage ?: "Unknown error occured")
            }
        })
    }

    fun getMessage(articleId: String, success: MessageLambda, failure: VoidLambda) {
        val call = placeHolderApi.getMessage(articleId)

        call.enqueue(object: Callback<Message> {
            override fun onResponse(call: Call<Message>?, response: Response<Message>?) {
                val article = parseRespone(response)
                success(article)
            }

            override fun onFailure(call: Call<Message>?, t: Throwable?) {
                println("Failed to GET Message: ${ t?.message }")
                failure()
            }
        })
    }

    fun postMessage(message: Message, success: MessageLambda, failure: VoidLambda) {
        val call = placeHolderApi.postMessage(message)

        call.enqueue(object: Callback<Message>{
            override fun onResponse(call: Call<Message>?, response: Response<Message>?) {
                val article = parseRespone(response)
                success(article)
            }

            override fun onFailure(call: Call<Message>?, t: Throwable?) {
                println("Failed to POST Message: ${ t?.message }")
                failure()
            }
        })
    }

    private fun <T> parseRespone(response: Response<T>?): T? {
        val article = response?.body() ?: null

        if (article == null) {
            parseResponeError(response)
        }

        return article
    }

    private fun <T> parseResponeError(response: Response<T>?) {
        if(response == null) return

        val responseBody = response.errorBody()

        if(responseBody != null) {
            try {
                val text = "responseBody = ${ responseBody.string() }"
                println("$text")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            val text = "responseBody == null"
            println("$text")
        }
    }

    //endregion

    //region Task Example

    // Make one Observable for each person on the list
    fun loadInfoFor(people: List<Person>): Observable<List<String>> {
        // For each person, make a network call (Returns list of Observables for us to work with)
        val networkObservables = people.map(::buildGetInfoNetworkCallFor)

        // When all server results have returned zip observables into a single observable
        return networkObservables.zip{ list ->

            // Filter out all box values that are not null
            list.filter { box -> box.value != null }
                    .map { it.value!! }
        }

    }


    // Wrap task in Reactive Observable
    // This pattern is used often for units of work.
    private fun buildGetInfoNetworkCallFor(person: Person): Observable<NullBox<String>> {

        return Observable.create { observer ->
            // Execute Request - Do Actual Work Here
            getInfoFor(person) { result ->

                result.fold({ info ->
                    observer.onNext(info)
                    observer.onComplete() // Not using a single here coz we wil zip these together & we have to have the type of Observable to work with
                }, { error ->
                    // Do something with error, or just pass it on
                    observer.onError(error)


                })

            }

        }

    }


    // Create a network task
    fun getInfoFor(person: Person, finished:(Result<NullBox<String>, Exception>) -> Unit) {

        // Execute on Background Thread
        // Do your task here
        launch {
            println("Start Network Call: $person")

            val randomTime = person.age * 1000 // to milliseconds
            delay(randomTime)

            println("Finished Network Call: $person")

            // Just randomly make odd people null
            val result = Result.of(NullBox(person.toString()))

            // Put result in finished lambda
            finished(result)
        }

    }



    //endregion

}
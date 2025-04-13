package dev.bswanson.hackernews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dev.bswanson.hackernews.model.ID
import dev.bswanson.hackernews.model.Submission
import kotlin.coroutines.suspendCoroutine

class HNViewModel : ViewModel() {

    companion object {
        private const val DATABASE_URL = "https://hacker-news.firebaseio.com"
    }

    private val database = Firebase.database(DATABASE_URL)

    private val _topStories = MutableLiveData<List<ID>>()
    val topStories: LiveData<List<ID>> = _topStories

    private val listeners = mutableListOf<ValueEventListener>()

    init {
        database.setPersistenceEnabled(true)
        database.getReference("v0/topstories").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                @Suppress("UNCHECKED_CAST")
                _topStories.value = snapshot.value as List<ID>
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO error handling/reconnect logic?
                println("cancelled: $error")
            }
        }).also(listeners::add)
    }

    suspend fun getStory(id: Long, refresh: Boolean = false): Submission {
        return suspendCoroutine { continuation ->

            val success = { snapshot: DataSnapshot ->
                val submission = snapshot.getValue(Submission::class.java)
                if (submission == null) {
                    continuation.resumeWith(Result.failure(RuntimeException("Failed to deserialize Submission")))
                } else {
                    continuation.resumeWith(Result.success(submission))
                }
            }

            val failure = { exception: Exception ->
                continuation.resumeWith(Result.failure(exception))
            }

            val ref = database.getReference("v0/item/$id")

            if (!refresh) {
                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        listeners.remove(this)
                        success(snapshot)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listeners.remove(this)
                        failure(error.toException())
                    }
                }

                ref.addListenerForSingleValueEvent(listener)
                listeners.add(listener)
            } else {
                ref.get().addOnSuccessListener(success).addOnFailureListener(failure)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listeners.removeAll { listener ->
            database.reference.removeEventListener(listener)
            true
        }
    }
}
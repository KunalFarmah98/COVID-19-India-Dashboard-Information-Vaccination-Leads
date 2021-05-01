package com.kunalfarmah.covid_19_info_dashboard.viewModel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.kunalfarmah.covid_19_info_dashboard.model.Post

class LeadsViewModel @ViewModelInject
constructor(application: Application) : AndroidViewModel(application) {
    private var postRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Leads")
    private var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private val posts: MutableLiveData<List<Post>> = MutableLiveData()

    val posts_: MutableLiveData<List<Post>>
        get() = posts

    private val userPosts: MutableLiveData<List<Post>> = MutableLiveData()

    val userPosts_: MutableLiveData<List<Post>>
        get() = posts

    fun fetchAllPosts() {
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var posts = ArrayList<Post>()
                for (postSnapshot in snapshot.children) {
                    var post = postSnapshot.getValue(Post::class.java)
                    posts.add(post!!)
                }
                posts_.value = posts.sortedWith(PostComparator())
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun fetchUserPosts() {
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var posts = ArrayList<Post>()
                for (postSnapshot in snapshot.children) {
                    var post = postSnapshot.getValue(Post::class.java)
                    if (post?.userId == user?.uid)
                        posts.add(post!!)
                }
                userPosts_.value = posts.sortedWith(PostComparator())
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun fetchFilteredPosts(filter:String) {
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var posts = ArrayList<Post>()
                for (postSnapshot in snapshot.children) {
                    var post = postSnapshot.getValue(Post::class.java)
                    if (post?.tags?.contains(filter)!!)
                        posts.add(post)
                }
                posts_.value = posts.sortedWith(PostComparator())
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    class PostComparator:Comparator<Post>{
        override fun compare(o1: Post?, o2: Post?): Int {
            return o1?.timeStamp!!.compareTo(o2?.timeStamp!!)
        }

    }
}
package com.kunalfarmah.covid_19_info_dashboard.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.kunalfarmah.covid_19_info_dashboard.listener.LatestListener
import com.kunalfarmah.covid_19_info_dashboard.listener.PostsListener
import com.kunalfarmah.covid_19_info_dashboard.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class LeadsViewModel @Inject
constructor(application: Application) : AndroidViewModel(application) {
    private var postRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Leads")
    private var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    private val posts: MutableLiveData<List<Post>> = MutableLiveData()

    val posts_: MutableLiveData<List<Post>>
        get() = posts

    private val userPosts: MutableLiveData<List<Post>> = MutableLiveData()

    val userPosts_: MutableLiveData<List<Post>>
        get() = userPosts

    fun fetchAllPosts(postListener: PostsListener) {
        postRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var posts = ArrayList<Post>()
                for (postSnapshot in snapshot.children) {
                    var post = postSnapshot.getValue(Post::class.java)
                    posts.add(post!!)
                }
//                posts_.value = posts.sortedWith(PostComparator())
                postListener.setData(posts.sortedWith(PostComparator()))
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun fetchUserPosts(postListener: PostsListener) {
        postRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var posts = ArrayList<Post>()
                for (postSnapshot in snapshot.children) {
                    var post = postSnapshot.getValue(Post::class.java)
                    if (post?.userId == user?.uid)
                        posts.add(post!!)
                }
//                userPosts_.value = posts.sortedWith(PostComparator())
                postListener.setData(posts.sortedWith(PostComparator()))
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun fetchFilteredPosts(filter:String, postListener: PostsListener) {
        postRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var posts = ArrayList<Post>()
                for (postSnapshot in snapshot.children) {
                    var post = postSnapshot.getValue(Post::class.java)
                    if (filter == "All" || post?.tags?.contains(filter)!!)
                        posts.add(post!!)
                }
//                posts_.value = posts.sortedWith(PostComparator())
                postListener.setData(posts.sortedWith(PostComparator()))
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun fetchFilteredUserPosts(filter:String, postListener: PostsListener) {
        postRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var posts = ArrayList<Post>()
                for (postSnapshot in snapshot.children) {
                    var post = postSnapshot.getValue(Post::class.java)
                    if (post?.userId == user?.uid && (filter == "All" || post?.tags?.contains(filter)!!))
                        posts.add(post!!)
                }
//                userPosts_.value = posts.sortedWith(PostComparator())
                postListener.setData(posts.sortedWith(PostComparator()))
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    class PostComparator:Comparator<Post>{
        override fun compare(o1: Post?, o2: Post?): Int {
            var sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            var d1 = sdf.parse(o1?.timeStamp!!)
            var d2 = sdf.parse(o2?.timeStamp!!)
            var res = d2.after(d1)
            return if(res) 1
            else -1
        }

    }
}
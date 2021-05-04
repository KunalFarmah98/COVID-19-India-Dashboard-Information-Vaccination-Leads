package com.kunalfarmah.covid_19_info_dashboard.ui.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.ItemLeadsBinding
import com.kunalfarmah.covid_19_info_dashboard.listener.ImageClickListener
import com.kunalfarmah.covid_19_info_dashboard.listener.PostsListener
import com.kunalfarmah.covid_19_info_dashboard.model.Post
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.WebViewActivity
import java.text.SimpleDateFormat


class PostsAdapter(context_: Context, list_: List<Post>, imageListener_: ImageClickListener, postsListener_: PostsListener) :
    RecyclerView.Adapter<PostsAdapter.PostsVH>() {

    var context = context_
    var list = list_
    var imageClickListener = imageListener_
    var postsListener = postsListener_

    class PostsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemLeadsBinding = ItemLeadsBinding.bind(itemView)

        fun bind(post: Post, context: Context, listener: ImageClickListener, postsListener: PostsListener) {
            binding.title.text = post.title
            binding.user.text = post.userName
            binding.body.text = post.body
            binding.upvoteCnt.text = post.upvotes.size.toString()
            binding.downvoteCnt.text = post.downvotes.size.toString()
            binding.helpfulCnt.isSelected = true
            binding.helpfulCnt.text =
                String.format("%s people were helped by this lead", post.helpful.size.toString())
            binding.date.text = SimpleDateFormat("dd/MM/yyyy hh:mm a").format(
                SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(
                    post.timeStamp
                )
            )
            if (!post.location.isNullOrEmpty()) {
                binding.location.visibility = View.VISIBLE
                binding.location.text = String.format("Location: %s", post.location)
            }
            else{
                binding.location.visibility = View.GONE
            }


            var contacts = post.contacts
            if (null == contacts || contacts.isEmpty()) {
                binding.contactLayout.visibility = View.GONE
            } else if (contacts.size == 1) {
                binding.contact1.setOnClickListener {
                    val call = Intent(Intent.ACTION_DIAL)
                    call.data = Uri.parse("tel:" + contacts[0])
                    context.startActivity(call)
                }
                binding.contactLayout.visibility = View.VISIBLE
                binding.contact2.visibility = View.GONE
                binding.contact1.text = contacts[0]
            } else {
                binding.contact1.setOnClickListener {
                    val call = Intent(Intent.ACTION_DIAL)
                    call.data = Uri.parse("tel:" + contacts[0])
                    context.startActivity(call)
                }
                binding.contact2.setOnClickListener {
                    val call = Intent(Intent.ACTION_DIAL)
                    call.data = Uri.parse("tel:" + contacts[1])
                    context.startActivity(call)
                }
                binding.contactLayout.visibility = View.VISIBLE
                binding.contact2.visibility = View.VISIBLE
                binding.contact1.text = contacts[0]
                binding.contact2.text = contacts[1]
            }

            var links = post.links
            if (null == links || links.isEmpty()) {
                binding.linkLayout.visibility = View.GONE
            } else if (links.size == 1) {
                binding.link1.setOnClickListener {
                    val intent = Intent(context, WebViewActivity::class.java)
                    intent.putExtra("action", "External")
                    intent.putExtra("url", links[0])
                    context.startActivity(intent)
                }
                binding.linkLayout.visibility = View.VISIBLE
                binding.link2.visibility = View.GONE
                binding.link1.text = links[0]
            } else {
                binding.link1.setOnClickListener {
                    val intent = Intent(context, WebViewActivity::class.java)
                    intent.putExtra("action", "External")
                    intent.putExtra("url", links[0])
                    context.startActivity(intent)
                }

                binding.link2.setOnClickListener {
                    val intent = Intent(context, WebViewActivity::class.java)
                    intent.putExtra("action", "External")
                    intent.putExtra("url", links[1])
                    context.startActivity(intent)
                }
                binding.linkLayout.visibility = View.VISIBLE
                binding.link2.visibility = View.VISIBLE
                binding.link1.text = links[0]
                binding.link2.text = links[1]
            }

            if(post.helpful.size==0)
                binding.helpfulCnt.visibility = View.GONE

            var userId = FirebaseAuth.getInstance().currentUser?.uid

            if(post.userId==userId)
                binding.delete.visibility = View.VISIBLE
            else
                binding.delete.visibility = View.GONE


            binding.helpful.setCardBackgroundColor(context.resources.getColor(R.color.white))
            binding.helpfulTv.setTextColor(context.resources.getColor(R.color.black))
            binding.upvoteArrow.setImageDrawable(context.resources.getDrawable(R.drawable.upvote_unselected))
            binding.downvoteArrow.setImageDrawable(context.resources.getDrawable(R.drawable.downvote_unselected))



            if (post.upvotes.contains(userId)) {
                binding.upvoteArrow.setImageDrawable(context.resources.getDrawable(R.drawable.upvote_selected))
            }
            if (post.downvotes.contains(userId)) {
                binding.downvoteArrow.setImageDrawable(context.resources.getDrawable(R.drawable.downvote_selected))
            }
            if (post.helpful.contains(userId)) {
                binding.helpful.setCardBackgroundColor(context.resources.getColor(R.color.purple_700))
                binding.helpfulTv.setTextColor(context.resources.getColor(R.color.white))
            }

            binding.upvoteArrow.setOnClickListener {
                if (post.upvotes.contains(userId)) {
                    post.upvotes.remove(userId)
                   /* binding.upvote.setCardBackgroundColor(context.resources.getColor(R.color.white))
                    binding.upvoteTv.setTextColor(context.resources.getColor(R.color.black))*/
                    binding.upvoteArrow.setImageDrawable(context.resources.getDrawable(R.drawable.upvote_unselected))
                    binding.upvoteCnt.text = post.upvotes.size.toString()
                    binding.downvoteCnt.text = post.downvotes.size.toString()

                } else {
                    post.downvotes.remove(userId)
                    binding.downvoteArrow.setImageDrawable(context.resources.getDrawable(R.drawable.downvote_unselected))
                    post.upvotes.add(userId!!)
                    /*binding.upvote.setCardBackgroundColor(context.resources.getColor(R.color.purple_700))
                    binding.upvoteTv.setTextColor(context.resources.getColor(R.color.white))*/
                    binding.upvoteArrow.setImageDrawable(context.resources.getDrawable(R.drawable.upvote_selected))
                    binding.upvoteCnt.text = post.upvotes.size.toString()
                    binding.downvoteCnt.text = post.downvotes.size.toString()
                }
                FirebaseDatabase.getInstance().getReference("Leads").child(post.id).setValue(post)
            }

            binding.downvoteArrow.setOnClickListener {
                if (post.downvotes.contains(userId)) {
                    post.downvotes.remove(userId)
                   /* binding.downvote.setCardBackgroundColor(context.resources.getColor(R.color.white))
                    binding.downvoteTv.setTextColor(context.resources.getColor(R.color.black))*/
                    binding.downvoteArrow.setImageDrawable(context.resources.getDrawable(R.drawable.downvote_unselected))
                    binding.upvoteCnt.text = post.upvotes.size.toString()
                    binding.downvoteCnt.text = post.downvotes.size.toString()
                } else {
                    post.upvotes.remove(userId)
                    binding.upvoteArrow.setImageDrawable(context.resources.getDrawable(R.drawable.upvote_unselected))
                    post.downvotes.add(userId!!)
                   /* binding.downvote.setCardBackgroundColor(context.resources.getColor(R.color.purple_700))
                    binding.downvoteTv.setTextColor(context.resources.getColor(R.color.white))*/
                    binding.downvoteArrow.setImageDrawable(context.resources.getDrawable(R.drawable.downvote_selected))
                    binding.upvoteCnt.text = post.upvotes.size.toString()
                    binding.downvoteCnt.text = post.downvotes.size.toString()
                }
                FirebaseDatabase.getInstance().getReference("Leads").child(post.id).setValue(post)
            }

            binding.helpful.setOnClickListener {
                var userRef = FirebaseDatabase.getInstance().getReference("Users").child(post.userId)
                if (post.helpful.contains(userId)) {
                    post.helpful.remove(userId)
                    binding.helpful.setCardBackgroundColor(context.resources.getColor(R.color.white))
                    binding.helpfulTv.setTextColor(context.resources.getColor(R.color.black))
                    if(post.helpful.size>0) {
                        binding.helpfulCnt.visibility = View.VISIBLE
                        binding.helpfulCnt.text = String.format(
                            "%s people were helped by this lead",
                            post.helpful.size.toString()
                        )
                    }
                    userRef.child("Helped").child(post.id).removeValue()
                } else {
                    post.helpful.add(userId!!)
                    binding.helpful.setCardBackgroundColor(context.resources.getColor(R.color.purple_700))
                    binding.helpfulTv.setTextColor(context.resources.getColor(R.color.white))
                    if(post.helpful.size>0) {
                        binding.helpfulCnt.visibility = View.VISIBLE
                        binding.helpfulCnt.text = String.format(
                            "%s people were helped by this lead",
                            post.helpful.size.toString()
                        )
                    }
                    userRef.child("Helped").child(post.id).setValue(userId)
                }
                FirebaseDatabase.getInstance().getReference("Leads").child(post.id).setValue(post)


            }



            if (!post.image.isNullOrEmpty()) {
                binding.image.setOnClickListener {
                    listener.openImage(post.title, post.image)
                }
                binding.image.visibility = View.VISIBLE
                Glide.with(context).load(post.image).diskCacheStrategy(DiskCacheStrategy.ALL).into(
                    binding.image
                )
            } else {
                binding.image.visibility = View.GONE
            }

            binding.delete.setOnClickListener {
                showDialog(context, post.id, userId!!, post.image!!, postsListener)
            }

        }

        private fun showDialog(context: Context, id: String, uId: String, url: String, listener: PostsListener){
            var dialog = AlertDialog.Builder(context).setTitle("Are you sure you want to delete this lead?")
                .setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
                    FirebaseDatabase.getInstance().getReference("Leads").child(id).removeValue()
                    FirebaseDatabase.getInstance().getReference("User").child(uId).child("Leads").child(
                        id
                    ).removeValue().addOnSuccessListener {
                        listener.deleted()
                        Toast.makeText(context, "Lead Deleted Successfully!", Toast.LENGTH_SHORT).show()
                    }
                    try {
                        if(!url.isNullOrEmpty()) {
                            var photoRef: StorageReference =
                                FirebaseStorage.getInstance().getReferenceFromUrl(url)
                            photoRef.delete().addOnSuccessListener { // File deleted successfully
                                Log.d("Deletion", "onSuccess: deleted file")
                            }.addOnFailureListener { // Uh-oh, an error occurred!
                                Log.d("Deletion", "onFailure: did not delete file")
                            }
                        }
                    }
                    catch (e:Exception){
                        Log.d("Error",e.message.toString())
                    }

                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }

            dialog.create()
            dialog.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsVH {
        return PostsVH(ItemLeadsBinding.inflate(LayoutInflater.from(context), parent, false).root)
    }

    override fun onBindViewHolder(holder: PostsVH, position: Int) {
        var post = list[position]
        holder.bind(post, context, imageClickListener,postsListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
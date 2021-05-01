package com.kunalfarmah.covid_19_info_dashboard.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.ItemLeadsBinding
import com.kunalfarmah.covid_19_info_dashboard.model.Post
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.WebViewActivity

class PostsAdapter(context_: Context, list_: List<Post>): RecyclerView.Adapter<PostsAdapter.PostsVH>() {

    var context = context_
    var list = list_
    class PostsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemLeadsBinding = ItemLeadsBinding.bind(itemView)

        fun bind(post: Post, context: Context){
            binding.title.text = post.title
            binding.user.text = post.userName
            binding.body.text = post.body
            binding.upvoteCnt.text = post.upvotes?.size.toString()
            binding.downvoteCnt.text = post.downvotes?.size.toString()
            binding.helpfulCnt.text = post.helpful?.size.toString()
            binding.date.text = post.timeStamp
            if(!post.location.isNullOrEmpty()){
                binding.location.visibility = View.VISIBLE
                binding.location.text = post.location
            }

            var contacts = post.contacts
            if(null==contacts || contacts.isEmpty()) {
                binding.contactLayout.visibility = View.GONE
            }
            else if(contacts.size==1){
                binding.contact1.setOnClickListener {
                    val call = Intent(Intent.ACTION_DIAL)
                    call.data = Uri.parse("tel:" + contacts[0])
                    context.startActivity(call)
                }
                binding.contactLayout.visibility = View.VISIBLE
                binding.contact2.visibility = View.GONE
                binding.contact1.text = contacts[0]
            }
            else{
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
            if(null==links || links.isEmpty()) {
                binding.linkLayout.visibility = View.GONE
            }
            else if(links.size==1){
                binding.link1.setOnClickListener {
                    val intent = Intent(context,WebViewActivity::class.java)
                    intent.putExtra("action","External")
                    intent.putExtra("url",links[0])
                    context.startActivity(intent)
                }
                binding.linkLayout.visibility = View.VISIBLE
                binding.link2.visibility = View.GONE
                binding.link1.text = links[0]
            }
            else{
                binding.link1.setOnClickListener {
                    val intent = Intent(context,WebViewActivity::class.java)
                    intent.putExtra("action","External")
                    intent.putExtra("url",links[0])
                    context.startActivity(intent)
                }

                binding.link2.setOnClickListener {
                    val intent = Intent(context,WebViewActivity::class.java)
                    intent.putExtra("action","External")
                    intent.putExtra("url",links[1])
                    context.startActivity(intent)
                }
                binding.linkLayout.visibility = View.VISIBLE
                binding.link2.visibility = View.VISIBLE
                binding.link1.text = links[0]
                binding.link2.text = links[1]
            }

            var userId = FirebaseAuth.getInstance().currentUser?.uid

            if(post.upvotes.contains(userId)) {
                binding.upvote.setCardBackgroundColor(context.resources.getColor(R.color.purple_700))
                binding.upvoteTv.setTextColor(context.resources.getColor(R.color.white))
            }
            else if(post.downvotes.contains(userId))
            {
                binding.downvote.setCardBackgroundColor(context.resources.getColor(R.color.purple_700))
                binding.downvoteTv.setTextColor(context.resources.getColor(R.color.white))
            }
            else if(post.helpful.contains(userId))
            {
                binding.helpful.setCardBackgroundColor(context.resources.getColor(R.color.purple_700))
                binding.helpfulTv.setTextColor(context.resources.getColor(R.color.white))
            }
            else{
                binding.helpful.setCardBackgroundColor(context.resources.getColor(R.color.white))
                binding.helpfulTv.setTextColor(context.resources.getColor(R.color.black))
                binding.downvote.setCardBackgroundColor(context.resources.getColor(R.color.white))
                binding.downvoteTv.setTextColor(context.resources.getColor(R.color.black))
                binding.upvote.setCardBackgroundColor(context.resources.getColor(R.color.white))
                binding.upvoteTv.setTextColor(context.resources.getColor(R.color.black))

            }

            binding.upvote.setOnClickListener {
                if(post.upvotes.contains(userId)){
                    post.upvotes.remove(userId)
                    binding.upvote.setCardBackgroundColor(context.resources.getColor(R.color.white))
                    binding.upvoteTv.setTextColor(context.resources.getColor(R.color.black))
                    binding.upvoteCnt.text = post.upvotes.size.toString()
                }
                else {
                    post.downvotes.remove(userId)
                    post.upvotes.add(userId!!)
                    binding.upvote.setCardBackgroundColor(context.resources.getColor(R.color.purple_700))
                    binding.upvoteTv.setTextColor(context.resources.getColor(R.color.white))
                    binding.upvoteCnt.text = post.upvotes.size.toString()
                }
                FirebaseDatabase.getInstance().getReference("Leads").child(post.id).setValue(post)
            }

             binding.downvote.setOnClickListener {
                if(post.downvotes.contains(userId)){
                    post.downvotes.remove(userId)
                    binding.downvote.setCardBackgroundColor(context.resources.getColor(R.color.white))
                    binding.downvoteTv.setTextColor(context.resources.getColor(R.color.black))
                    binding.downvoteCnt.text = post.downvotes.size.toString()
                }
                else {
                    post.upvotes.remove(userId)
                    post.downvotes.add(userId!!)
                    binding.downvote.setCardBackgroundColor(context.resources.getColor(R.color.purple_700))
                    binding.downvoteTv.setTextColor(context.resources.getColor(R.color.white))
                    binding.downvoteCnt.text = post.downvotes.size.toString()
                }
                FirebaseDatabase.getInstance().getReference("Leads").child(post.id).setValue(post)
            }

             binding.helpful.setOnClickListener {
                if(post.helpful.contains(userId)){
                    post.helpful.remove(userId)
                    binding.helpful.setCardBackgroundColor(context.resources.getColor(R.color.white))
                    binding.helpfulTv.setTextColor(context.resources.getColor(R.color.black))
                    binding.helpfulCnt.text = post.helpful.size.toString()
                }
                else {
                    post.helpful.add(userId!!)
                    binding.helpful.setCardBackgroundColor(context.resources.getColor(R.color.purple_700))
                    binding.helpfulTv.setTextColor(context.resources.getColor(R.color.white))
                    binding.helpfulCnt.text = post.helpful.size.toString()
                }
                FirebaseDatabase.getInstance().getReference("Leads").child(post.id).setValue(post)
            }

            if(!post.image.isNullOrEmpty()){
                binding.image.visibility = View.VISIBLE
                Glide.with(context).load(post.image).diskCacheStrategy(DiskCacheStrategy.ALL).into(
                    binding.image
                )
            }
            else{
                binding.image.visibility = View.GONE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsVH {
        return PostsVH(ItemLeadsBinding.inflate(LayoutInflater.from(context), parent, false).root)
    }

    override fun onBindViewHolder(holder: PostsVH, position: Int) {
        var post = list[position]
        holder.bind(post, context)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
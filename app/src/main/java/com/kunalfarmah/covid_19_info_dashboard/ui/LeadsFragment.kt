package com.kunalfarmah.covid_19_info_dashboard.ui

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.kunalfarmah.covid_19_info_dashboard.util.AppUtil
import com.kunalfarmah.covid_19_info_dashboard.Constants
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.FragmentLeadsBinding
import com.kunalfarmah.covid_19_info_dashboard.listener.ImageClickListener
import com.kunalfarmah.covid_19_info_dashboard.listener.PostsListener
import com.kunalfarmah.covid_19_info_dashboard.model.Post
import com.kunalfarmah.covid_19_info_dashboard.model.User
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.ImageActivity
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.MainActivity
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.PostActivity
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.SignInActivity
import com.kunalfarmah.covid_19_info_dashboard.ui.adapter.PostsAdapter
import com.kunalfarmah.covid_19_info_dashboard.viewModel.LeadsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LeadsFragment() : Fragment(), PostsListener, ImageClickListener {

    private val viewModel: LeadsViewModel by viewModels()
    lateinit var binding: FragmentLeadsBinding
    var user: FirebaseUser? = null
    var user_: User? = null
    var list: List<Post>? = null
    var mAdapter: PostsAdapter? = null
    var filter: String? = "All"
    var sPref: SharedPreferences? = null
    var isPosted: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeadsBinding.inflate(inflater)

        binding.swipeRefresh.isRefreshing = false
        sPref = activity?.getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
        user_ = Gson().fromJson(sPref?.getString(Constants.USER, ""), User::class.java)
        if (AppUtil.isNetworkAvailable(requireContext())) {
            binding.leadsRecycler.visibility = View.GONE
            binding.loading.visibility = View.VISIBLE
            binding.loading.startShimmerAnimation()
            viewModel.fetchAllPosts(this)
        } else {
            setNoNetworkLayout()
        }

/*
        if (AppUtil.isNetworkAvailable(requireContext())) {
            loadData()
        } else {
            setNoNetworkLayout()
        }
*/

        binding.swipeRefresh.setOnRefreshListener {
            if (AppUtil.isNetworkAvailable(requireContext())) {
                binding.swipeRefresh.isRefreshing = true
                viewModel.fetchFilteredPosts(filter!!, this)
            }
        }

        binding.addPost.setOnClickListener {
            addPost()
        }

        setUpFilters()

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                user_ = Gson().fromJson(data?.getStringExtra("user"), User::class.java)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                (activity as MainActivity)?.openDashBoard()
            }
        }
        if (requestCode == Constants.POST_LEAD) {
            if (resultCode == Activity.RESULT_OK) {
                isPosted = true
                viewModel.fetchFilteredPosts(filter!!,this)
            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    fun resetAllFilters() {
        binding.all.setCardBackgroundColor(requireContext().resources.getColor(R.color.white))
        binding.allTv.setTextColor(requireContext().resources.getColor(R.color.black))

        binding.beds.setCardBackgroundColor(requireContext().resources.getColor(R.color.white))
        binding.bedsTv.setTextColor(requireContext().resources.getColor(R.color.black))

        binding.oxi.setCardBackgroundColor(requireContext().resources.getColor(R.color.white))
        binding.oxiTv.setTextColor(requireContext().resources.getColor(R.color.black))

        binding.medicine.setCardBackgroundColor(requireContext().resources.getColor(R.color.white))
        binding.medsTv.setTextColor(requireContext().resources.getColor(R.color.black))

        binding.food.setCardBackgroundColor(requireContext().resources.getColor(R.color.white))
        binding.foodTv.setTextColor(requireContext().resources.getColor(R.color.black))

        binding.others.setCardBackgroundColor(requireContext().resources.getColor(R.color.white))
        binding.othersTv.setTextColor(requireContext().resources.getColor(R.color.black))

        binding.equipment.setCardBackgroundColor(requireContext().resources.getColor(R.color.white))
        binding.equipTv.setTextColor(requireContext().resources.getColor(R.color.black))

        binding.plasma.setCardBackgroundColor(requireContext().resources.getColor(R.color.white))
        binding.plasmaTv.setTextColor(requireContext().resources.getColor(R.color.black))

        binding.ambulance.setCardBackgroundColor(requireContext().resources.getColor(R.color.white))
        binding.ambulanceTv.setTextColor(requireContext().resources.getColor(R.color.black))
    }

    private fun setUpFilters() {
        binding.all.setOnClickListener {
            filter = activity?.resources?.getString(R.string.all)
            resetAllFilters()
            binding.all.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.allTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchAllPosts(this)
        }
        binding.beds.setOnClickListener {
            filter = activity?.resources?.getString(R.string.beds)
            resetAllFilters()
            binding.beds.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.bedsTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!, this)
        }
        binding.oxi.setOnClickListener {
            filter = activity?.resources?.getString(R.string.oxygen)
            resetAllFilters()
            binding.oxi.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.oxiTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!, this)
        }
        binding.medicine.setOnClickListener {
            filter = activity?.resources?.getString(R.string.meds)
            resetAllFilters()
            binding.medicine.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.medsTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!, this)
        }
        binding.equipment.setOnClickListener {
            filter = activity?.resources?.getString(R.string.equipment)
            resetAllFilters()
            binding.equipment.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.equipTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!, this)
        }
        binding.others.setOnClickListener {
            filter = activity?.resources?.getString(R.string.others)
            resetAllFilters()
            binding.others.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.othersTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!, this)
        }
        binding.food.setOnClickListener {
            filter = activity?.resources?.getString(R.string.food)
            resetAllFilters()
            binding.food.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.foodTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!, this)
        }

        binding.plasma.setOnClickListener {
            filter = activity?.resources?.getString(R.string.plasma)
            resetAllFilters()
            binding.plasma.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.plasmaTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!, this)

        }

        binding.ambulance.setOnClickListener {
            filter = activity?.resources?.getString(R.string.ambulance)
            resetAllFilters()
            binding.ambulance.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.ambulanceTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!, this)
        }
    }


    /* private fun loadData() {
         binding.userWelcome.visibility = View.VISIBLE
         binding.userWelcome.text = String.format(
             "Welcome, %s",
             user_?.name
         )
         binding.logout.visibility = View.VISIBLE
         binding.leadsRecycler.visibility = View.GONE
         binding.loading.visibility = View.VISIBLE
         binding.loading.startShimmerAnimation()
         viewModel.posts_.observe(viewLifecycleOwner, {
             if (it.isNotEmpty()) {
                 list = it!!
                 var layoutManager = binding.leadsRecycler.layoutManager
                 if(isPosted || binding.swipeRefresh.isRefreshing || null==mAdapter || (null!=layoutManager && (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()==0))
                     setView(it!!)
             } else {
                 setNoDataLayout()
             }
         })
     }*/

    private fun setView(list: List<Post>) {
        binding.swipeRefresh.isRefreshing = false
        binding.loading.stopShimmerAnimation()
        binding.loading.visibility = View.GONE
        binding.leadsRecycler.visibility = View.VISIBLE
        binding.noPostsLayout.noPostsLayout.visibility = View.GONE
        binding.noNetworkLayout.noNetworkLayout.visibility = View.GONE
        mAdapter = PostsAdapter(requireContext(), list, this,this)
        binding.leadsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.leadsRecycler.adapter = mAdapter
    }

    private fun setNoDataLayout() {
        binding.swipeRefresh.isRefreshing = false
        binding.loading.stopShimmerAnimation()
        binding.loading.visibility = View.GONE
        binding.leadsRecycler.visibility = View.GONE
        binding.noPostsLayout.noPostsLayout.visibility = View.VISIBLE
    }

    private fun setNoNetworkLayout() {
        binding.swipeRefresh.isRefreshing = false
        binding.leadsRecycler.visibility = View.GONE
        binding.loading.stopShimmerAnimation()
        binding.loading.visibility = View.GONE
        binding.noNetworkLayout.noNetworkLayout.visibility = View.VISIBLE
    }

    override fun setData(list: List<Post>) {
        if (list.isNullOrEmpty()) {
            setNoDataLayout()
        } else {
//            if(isPosted || null==mAdapter || binding.swipeRefresh.isRefreshing)
                setView(list)
        }
    }

    private fun addPost() {
        var intent = Intent(activity, PostActivity::class.java)
        startActivityForResult(intent, Constants.POST_LEAD)
    }

    override fun openImage(title: String?, uri: String?) {
        if (uri.isNullOrEmpty())
            return

        var intent = Intent(activity, ImageActivity::class.java)
        intent.putExtra("uri", uri)
        intent.putExtra("title", title)
        startActivity(intent)
    }

    override fun deleted() {
        viewModel.fetchFilteredPosts(filter!!,this)
    }


}
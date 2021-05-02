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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import com.kunalfarmah.covid_19_info_dashboard.util.AppUtil
import com.kunalfarmah.covid_19_info_dashboard.Constants
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.FragmentLeadsBinding
import com.kunalfarmah.covid_19_info_dashboard.model.Post
import com.kunalfarmah.covid_19_info_dashboard.model.User
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.MainActivity
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.PostActivity
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.SignInActivity
import com.kunalfarmah.covid_19_info_dashboard.ui.adapter.PostsAdapter
import com.kunalfarmah.covid_19_info_dashboard.viewModel.LeadsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LeadsFragment(var isUser: Boolean) : Fragment() {

    private val viewModel: LeadsViewModel by viewModels()
    lateinit var binding: FragmentLeadsBinding
    private lateinit var providers: ArrayList<AuthUI.IdpConfig>
    var user: FirebaseUser? = null
    var user_: User? = null
    var usersRef: DatabaseReference? = null
    var list: List<Post>? = null
    var mAdapter: PostsAdapter? = null
    var filter: String? = "All"
    var sPref: SharedPreferences? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeadsBinding.inflate(inflater)

        binding.swipeRefresh.isRefreshing = false
        sPref = activity?.getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
        user_ = Gson().fromJson(sPref?.getString(Constants.USER, ""), User::class.java)
        if (!isUser)
            viewModel.fetchAllPosts()
        else
            viewModel.fetchUserPosts()

        if (AppUtil.isNetworkAvailable(requireContext())) {
            loadData()
        } else {
            setNoNetworkLayout()
        }

        binding.swipeRefresh.setOnRefreshListener {
            if (AppUtil.isNetworkAvailable(requireContext())) {
                binding.swipeRefresh.isRefreshing = true
                viewModel.fetchFilteredPosts(filter!!)
            }
        }

        binding.logout.setOnClickListener {
            AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener(OnCompleteListener {
                    sPref?.edit()?.putString(Constants.USER, "")?.apply()
                    Toast.makeText(requireContext(), "Signed Out successfully", Toast.LENGTH_SHORT)
                        .show()
                    binding.userWelcome.visibility = View.GONE
                    binding.logout.visibility = View.GONE
                    startActivityForResult(
                        Intent(activity, SignInActivity::class.java),
                        Constants.SIGN_IN
                    )
                })
        }

        setUpFilters()

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                user_ = Gson().fromJson(data?.getStringExtra("user"), User::class.java)
                loadData()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                (activity as MainActivity)?.openDashBoard()
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
            filter = "All"
            resetAllFilters()
            binding.all.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.allTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchAllPosts()
        }
        binding.beds.setOnClickListener {
            filter = "Beds"
            resetAllFilters()
            binding.beds.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.bedsTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!)
        }
        binding.oxi.setOnClickListener {
            filter = "Oxygen"
            resetAllFilters()
            binding.oxi.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.oxiTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!)
        }
        binding.medicine.setOnClickListener {
            filter = "Medicines"
            resetAllFilters()
            binding.medicine.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.medsTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!)
        }
        binding.equipment.setOnClickListener {
            filter = "Equipment"
            resetAllFilters()
            binding.equipment.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.equipTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!)
        }
        binding.others.setOnClickListener {
            filter = "Others"
            resetAllFilters()
            binding.others.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.othersTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!)
        }
        binding.food.setOnClickListener {
            filter = "Food"
            resetAllFilters()
            binding.food.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.foodTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!)
        }

        binding.plasma.setOnClickListener {
            filter = "Plasma"
            resetAllFilters()
            binding.plasma.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.plasmaTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!)

        }

        binding.ambulance.setOnClickListener {
            filter = "Ambulance"
            resetAllFilters()
            binding.ambulance.setCardBackgroundColor(requireContext().resources.getColor(R.color.purple_700))
            binding.ambulanceTv.setTextColor(requireContext().resources.getColor(R.color.white))
            viewModel.fetchFilteredPosts(filter!!)
        }
    }


    private fun loadData() {
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
                setView(it!!)
            } else {
                setNoDataLayout()
            }
        })
    }

    private fun setView(list: List<Post>) {
        binding.swipeRefresh.isRefreshing = false
        binding.loading.stopShimmerAnimation()
        binding.loading.visibility = View.GONE
        binding.leadsRecycler.visibility = View.VISIBLE
        binding.noPostsLayout.noPostsLayout.visibility = View.GONE
        binding.noNetworkLayout.noNetworkLayout.visibility = View.GONE
        mAdapter = PostsAdapter(requireContext(), list)
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

}
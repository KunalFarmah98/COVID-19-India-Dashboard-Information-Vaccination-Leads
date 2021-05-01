package com.kunalfarmah.covid_19_info_dashboard.ui

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import com.kunalfarmah.covid_19_info_dashboard.AppUtil
import com.kunalfarmah.covid_19_info_dashboard.Constants
import com.kunalfarmah.covid_19_info_dashboard.Constants.Companion.RC_SIGN_IN
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.FragmentLeadsBinding
import com.kunalfarmah.covid_19_info_dashboard.model.Post
import com.kunalfarmah.covid_19_info_dashboard.model.User
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.PostActivity
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

        sPref = activity?.getSharedPreferences(Constants.PREFS, MODE_PRIVATE)
        if (!isUser)
            viewModel.fetchAllPosts()
        else
            viewModel.fetchUserPosts()


        if (AppUtil.isNetworkAvailable(requireContext())) {
            if (sPref!!.getString(Constants.USER, "").isNullOrEmpty())
                signIn()
            else
                loadData()
        } else {
            setNoNetworkLayout()
        }


        binding.addPost.bringToFront()
        binding.addPost.setOnClickListener {
            startActivity(Intent(activity, PostActivity::class.java))
        }

        setUpFilters()

        return binding.root
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
    }

    private fun signIn() {
        providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.coronavirus)
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().currentUser
                val userId = user?.uid
                val name = user?.displayName
                val phone = user?.phoneNumber
                insertUser(userId, name, phone)
                loadData()
                sPref!!.edit().putString(Constants.USER, Gson().toJson(user)).apply()
            } else {
                Toast.makeText(context, "SignIn Failed, Please Try Again!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun insertUser(userID: String?, name: String?, phone: String?) {
        usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        user_ = User(
            userID,
            name,
            phone,
            null,
            null
        )
        usersRef?.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.child(userID!!).exists()) {
                    usersRef?.child(userID)?.setValue(user_)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        });
    }

    private fun loadData() {
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
        binding.loading.stopShimmerAnimation()
        binding.loading.visibility = View.GONE
        binding.leadsRecycler.visibility = View.GONE
        binding.noPostsLayout.noPostsLayout.visibility = View.VISIBLE
    }

    private fun setNoNetworkLayout() {
        binding.leadsRecycler.visibility = View.GONE
        binding.loading.stopShimmerAnimation()
        binding.loading.visibility = View.GONE
        binding.noNetworkLayout.noNetworkLayout.visibility = View.VISIBLE
    }

}
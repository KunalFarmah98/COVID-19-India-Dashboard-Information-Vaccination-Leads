package com.kunalfarmah.covid_19_info_dashboard.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.kunalfarmah.covid_19_info_dashboard.util.Constants
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.FragmentPostsBinding
import com.kunalfarmah.covid_19_info_dashboard.model.User
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.MainActivity
import com.kunalfarmah.covid_19_info_dashboard.ui.activity.SignInActivity
import com.kunalfarmah.covid_19_info_dashboard.util.AppUtil

class PostsFragment : Fragment() {
    lateinit var binding: FragmentPostsBinding
    var sPref: SharedPreferences? = null
    var user_: User? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostsBinding.inflate(inflater)
        sPref = activity?.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
        user_ = Gson().fromJson(sPref?.getString(Constants.USER, ""), User::class.java)

        checkAuth()

        binding.logout.setOnClickListener {
            AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener(OnCompleteListener {
                    sPref?.edit()?.putString(Constants.USER, "")?.apply()
                    Toast.makeText(requireContext(), "Signed Out successfully", Toast.LENGTH_SHORT)
                        .show()
                    binding.userLayout.visibility = View.GONE
                    startActivityForResult(
                        Intent(activity, SignInActivity::class.java),
                        Constants.SIGN_IN
                    )
                })
        }

        binding.noNetworkLayout.retry.setOnClickListener {
            checkAuth()
        }
        return binding.root
    }

    private fun setNoNetworkLayout() {
        binding.userLayout.visibility = View.GONE
        binding.mainLayout.visibility = View.GONE
        binding.noNetworkLayout.noNetworkLayout.visibility = View.VISIBLE
    }

    fun loadFragment() {
        binding.userLayout.visibility = View.VISIBLE
        binding.userWelcome.text = String.format(
            "Welcome, %s",
            user_?.name
        )
        var pagerAdapter =
            ViewPagerAdapter(childFragmentManager, requireActivity())
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.currentItem = 0
    }

    fun checkAuth() {
        if (AppUtil.isNetworkAvailable(requireContext())) {
            val auth = FirebaseAuth.getInstance()
            if (!sPref?.getString(Constants.USER, "").isNullOrEmpty() || auth.currentUser != null) {
                loadFragment()
            } else {
                var intent = Intent(activity, SignInActivity::class.java)
                startActivityForResult(intent, Constants.SIGN_IN)
            }
        } else {
            setNoNetworkLayout()
        }
    }


    class ViewPagerAdapter(
        fm: FragmentManager,
        activity_: Activity
    ) :
        FragmentStatePagerAdapter(fm) {
        private var leadsFragment: LeadsFragment? = null
        private var leadsFragmentUser: MyLeadsFragment? = null
        private val activity: Activity = activity_
        override fun getItem(position: Int): Fragment {
            if (position == 0) {
                leadsFragment = LeadsFragment()
                return leadsFragment as LeadsFragment
            } else if (position == 1) {
                leadsFragmentUser = MyLeadsFragment()
                return leadsFragmentUser as MyLeadsFragment
            }
            return leadsFragment!!
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            if (position == 0) {
                return activity.resources.getString(R.string.all_posts)
            } else if (position == 1) {
                return activity.resources.getString(R.string.my_posts)
            }
            return ""
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                user_ = Gson().fromJson(data?.getStringExtra("user"), User::class.java)
                loadFragment()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                (activity as MainActivity).openDashBoard()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val navigationView = activity?.findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.menu.findItem(R.id.nav_home).isChecked = false
    }


}
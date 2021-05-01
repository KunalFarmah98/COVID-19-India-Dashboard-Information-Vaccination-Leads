package com.kunalfarmah.covid_19_info_dashboard.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.FragmentPostsBinding

class PostsFragment : Fragment() {
    lateinit var binding: FragmentPostsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostsBinding.inflate(inflater)

        var pagerAdapter =
            ViewPagerAdapter(childFragmentManager, requireActivity())
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.currentItem = 0
        return binding.root
    }


    class ViewPagerAdapter(
        fm: FragmentManager,
        activity_: Activity
    ) :
        FragmentStatePagerAdapter(fm) {
        private var leadsFragment: LeadsFragment? = null
        private var leadsFragmentUser: LeadsFragment? = null
        private val activity: Activity = activity_
        override fun getItem(position: Int): Fragment {
            if (position == 0) {
                leadsFragment = LeadsFragment(false)
                return leadsFragment as LeadsFragment
            } else if (position == 1) {
                leadsFragmentUser = LeadsFragment(true)
                return leadsFragmentUser as LeadsFragment
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


}
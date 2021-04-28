package com.kunalfarmah.covid_19_info_dashboard.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kunalfarmah.covid_19_info_dashboard.R

class ContactsFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_helpline, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        return root
    }
}
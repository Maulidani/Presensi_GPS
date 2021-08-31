package com.skripsi.presensigps.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skripsi.presensigps.ui.fragment.History2Fragment

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val total = 2

    override fun getItemCount(): Int = total

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> History2Fragment("presence")
            1 -> History2Fragment("report")
            else -> Fragment()
        }

    }
}
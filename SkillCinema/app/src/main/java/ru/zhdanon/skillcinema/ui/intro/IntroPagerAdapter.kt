package ru.zhdanon.skillcinema.ui.intro

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class IntroPagerAdapter(
    fm: FragmentManager,
    private val onClick: () -> Unit
) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> IntroFirstFragment()
            1 -> IntroSecondFragment()
            else -> IntroThirdFragment { onClick() }
        }
    }
}
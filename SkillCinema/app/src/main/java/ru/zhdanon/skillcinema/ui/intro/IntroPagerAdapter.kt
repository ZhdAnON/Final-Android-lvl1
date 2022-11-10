package ru.zhdanon.skillcinema.ui.intro

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ru.zhdanon.skillcinema.ui.TAG

class IntroPagerAdapter(
    fm: FragmentManager,
    private val onClick: () -> Unit
) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                Log.d(TAG, "getItem: 1 - FirstFragment")
                IntroFirstFragment()
            }
            1 -> {
                Log.d(TAG, "getItem: 2 - SecondFragment")
                IntroSecondFragment()
            }
            else -> {
                Log.d(TAG, "getItem: 3 - ThirdFragment")
                IntroThirdFragment { onClick() }
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        Log.d(TAG, "getPageTitle: ")
        return "OBJECT ${(position + 1)}"
    }
}
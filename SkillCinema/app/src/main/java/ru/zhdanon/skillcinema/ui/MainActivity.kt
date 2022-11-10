package ru.zhdanon.skillcinema.ui

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import ru.zhdanon.skillcinema.R
import ru.zhdanon.skillcinema.databinding.ActivityMainBinding
import ru.zhdanon.skillcinema.ui.intro.IntroFragment

const val TAG = "AAAAA"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PreferenceManager.getDefaultSharedPreferences(this).apply {
            if (!getBoolean(IntroFragment.PREFERENCES_NAME, false)) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_container, IntroFragment())
                    .commit()

            } else {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_container, MainFragment())
                    .commit()
            }
        }
    }
}
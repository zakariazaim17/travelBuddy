package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.window.SplashScreen
import android.window.SplashScreenView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val mapFragment = MapScreen()
    val feedFragment = FeedScreen()
    val chatFragment = ChatScreen()
    val profileFragment = ProfileScreen()
    val splashFragment = SplashScreen()
    val loginFragment = LoginScreen()
    val registerFragment = RegisterScreen()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        replaceCurrentFragment(splashFragment)

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_map -> replaceCurrentFragment(mapFragment)
                R.id.ic_feed -> replaceCurrentFragment(feedFragment)
                R.id.ic_chat -> replaceCurrentFragment(chatFragment)
                R.id.ic_profile -> replaceCurrentFragment(profileFragment)
            }
            true
        }
    }

    public fun replaceCurrentFragment(fragment: Fragment) {
        if (fragment is com.example.myapplication.SplashScreen || fragment is LoginScreen || fragment is RegisterScreen) {
            bottom_navigation.visibility = View.GONE
        }else {
            bottom_navigation.visibility = View.VISIBLE
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, fragment)
            commit()


            /*if (fragment is com.example.myapplication.SplashScreen) {
                bottom_navigation.visibility = View.GONE
            }else{
            bottom_navigation.visibility = View.VISIBLE
    }
    */
        }

        }
    }

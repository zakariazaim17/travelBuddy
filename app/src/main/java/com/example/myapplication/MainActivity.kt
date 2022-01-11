package com.example.myapplication

import android.content.Context
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.window.SplashScreen
import android.window.SplashScreenView
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.screens.*
import android.widget.EditText

import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager


class MainActivity : AppCompatActivity() {
    val mapFragment = MapScreen()
    val feedFragment = FeedScreen()
    //val chatFragment = ChatScreen()
    val profileFragment = ProfileScreen()
    val splashFragment = SplashScreen()
    val loginFragment = LoginScreen()
    val registerFragment = RegisterScreen()
    val singlePostFragment = SinglePostScreen()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        replaceCurrentFragment(splashFragment)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_map -> replaceCurrentFragment(mapFragment)
                R.id.ic_feed -> replaceCurrentFragment(feedFragment)

                R.id.ic_profile -> replaceCurrentFragment(profileFragment)
            }
            true
        }
    }

    public fun replaceCurrentFragment(fragment: Fragment) {

        controlBottomNavigationVisibility(fragment)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, fragment)
            if (fragment is SinglePostScreen){
                setReorderingAllowed(true)
                addToBackStack("null")

            }
            commit()


        }

        }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun controlBottomNavigationVisibility(fragment: Fragment) {

        when (fragment){
            is LoginScreen -> binding.bottomNavigation.visibility = View.GONE
            is com.example.myapplication.screens.SplashScreen -> binding.bottomNavigation.visibility = View.GONE
            is RegisterScreen -> binding.bottomNavigation.visibility = View.GONE

            else -> {
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }

    }

    public fun goBack(){
        supportFragmentManager.popBackStack()
        binding.bottomNavigation.visibility = View.VISIBLE
    }


    

    public fun MainContext():Context{
        return applicationContext
    }
    }

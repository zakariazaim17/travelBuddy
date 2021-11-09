package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.myapplication.model.LoginUser
import com.example.myapplication.repository.Repository
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.login_btn
import kotlinx.android.synthetic.main.fragment_login.register_textview
import kotlinx.android.synthetic.main.fragment_register.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private lateinit var viewModel: MainViewModel
class LoginScreen : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        view.findViewById<TextView>(R.id.register_textview).setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_firstFragment_to_secondFragment) }
        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        //textView_logo_name.text = "hope"
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor = sharedPreferences.edit();

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        register_textview.setOnClickListener{
            (activity as MainActivity).replaceCurrentFragment(RegisterScreen())

        }

        login_btn.setOnClickListener {

val user = LoginUser(editTextEmail.text.toString(), editTextPassword.text.toString())

            viewModel.login(user)
            viewModel.loginResponse.observe(viewLifecycleOwner, {response ->
                if(response.isSuccessful){
                    if(!response.body()!!.success){
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                    }else{
                        //save token for later use
                            editor.apply {
                                putString("User_token", response.body()?.data?.token.toString())
                            }.apply()
                        Toast.makeText(context, response.body()?.data?.token.toString() , Toast.LENGTH_SHORT).show()
                        (activity as MainActivity).replaceCurrentFragment((MapScreen()))
                    }
                    Log.d("Main1", response.body()?.data?.token.toString())
                    Log.d("Main1", response.code().toString())
                    Log.d("Main1", response.message())




                }else{
                    Log.d("Main1", response.errorBody().toString())

                }

            })
        }




    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirstFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginScreen().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
package com.example.petcare.entry

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.petcare.HomepageActivity
import com.example.petcare.R
import com.example.petcare.databinding.FragmentLoginBinding
import com.example.petcare.model.Profil
import com.example.petcare.service.APICall.APIService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    lateinit var preferences: SharedPreferences
    val userPref = "userPreference"
    val userData = "userData"
    private var isInRequest : Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.errorLoginText.visibility= View.INVISIBLE
        binding.signupText.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.containerEntry,RegisterFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }


        binding.loginButton.setOnClickListener{
            val emailText = binding.nameInputForm.text.toString()
            val passText = binding.passwordInputForm.text.toString()
            if(emailText == ""){
                binding.nameInputForm.setBackgroundResource(R.drawable.shape_textinput_error)
            }else{
                binding.nameInputForm.setBackgroundResource(R.drawable.shape_textinput_normal)
            }
            if(passText == ""){
                binding.passwordInputForm.setBackgroundResource(R.drawable.shape_textinput_error)
            }else{
                binding.passwordInputForm.setBackgroundResource(R.drawable.shape_textinput_normal)
            }
            if(emailText != "" && passText != ""){
                val profil = Profil("","",emailText,passText,"","")
                val json = Gson().toJson(profil)
                val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                if(!isInRequest){
                    isInRequest = true
                    APIService.endpoint.userLogin(requestBody)
                        .enqueue(object : Callback<Profil>{
                            override fun onResponse(call: Call<Profil>, response: Response<Profil>) {
                                if(response.code() == 200){
                                    Log.d("Response", "Response is success")
                                    Log.d("Response", response.body().toString())
                                    preferences = requireActivity().getSharedPreferences(userPref, AppCompatActivity.MODE_PRIVATE)
                                    preferences.edit().putBoolean(userPref,false).apply()
                                    preferences = requireActivity().getSharedPreferences(userData, AppCompatActivity.MODE_PRIVATE)
                                    preferences.edit().putString("id",response.body()?._id.toString()).apply()
                                    preferences.edit().putString("nama", response.body()?.name.toString()).apply()
                                    preferences.edit().putString("email", response.body()?.email.toString()).apply()
                                    preferences.edit().putString("notelp", response.body()?.notelp.toString()).apply()
                                    preferences.edit().putString("foto_profil", response.body()?.foto_profil.toString()).apply()
                                    val intent = Intent(requireContext(), HomepageActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                    binding.errorLoginText.visibility= View.INVISIBLE
                                }
                                else{
                                    binding.errorLoginText.visibility = View.VISIBLE
                                }
                                isInRequest = false
                            }
                            override fun onFailure(call: Call<Profil>, t: Throwable) {
                                Toast.makeText(requireContext(),"Tidak dapat menghibungi ke server, mohon cek koneksi internet",Toast.LENGTH_SHORT).show()
                                isInRequest = false
                            }

                        })
                }

            }


        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume() {
        super.onResume()
        binding.errorLoginText.visibility= View.INVISIBLE
    }

}
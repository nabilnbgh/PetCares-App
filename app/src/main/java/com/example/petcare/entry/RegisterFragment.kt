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
import com.example.petcare.databinding.FragmentRegisterBinding
import com.example.petcare.model.Profil
import com.example.petcare.service.APICall.APIService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterFragment : Fragment() {
    private var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    lateinit var preferences: SharedPreferences
    private val userPref = "userPreference"
    private val userData = "userData"
    private var isInRequest : Boolean = false
    private var call: Call<Profil>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        val view = binding.root

        binding.signinText.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.errorRegisterText.visibility = View.INVISIBLE

        binding.registerButton.setOnClickListener{
            val profilName = binding.usernameInputForm.text.toString()
            val profilEmail = binding.emailInputForm.text.toString()
            val profilPassword = binding.passwordInputForm.text.toString()

            if(profilEmail != "" && profilPassword != ""){
                if(!isInRequest){
                    isInRequest = true
                    val profil = Profil("",profilName,profilEmail,profilPassword,"","")
                    val json = Gson().toJson(profil)
                    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    APIService.endpoint.userRegister(requestBody).enqueue(
                        object : Callback<Profil> {
                            override fun onResponse(call: Call<Profil>, response: Response<Profil>) {
                                if(response.isSuccessful){
                                    Log.d("Response", "Respon API sukses")
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
                                    binding.errorRegisterText.visibility = View.INVISIBLE
                                    requireActivity().finish()
                                    isInRequest = false
                                }else{
                                    binding.errorRegisterText.visibility = View.VISIBLE
                                    isInRequest = false
                                }

                            }
                            override fun onFailure(call: Call<Profil>, t: Throwable) {
                                Toast.makeText(requireContext(),"Tidak dapat menghibungi ke server, mohon cek koneksi internet",
                                    Toast.LENGTH_SHORT).show()
                                isInRequest = false
                            }

                        }
                    )
                }

            }else{
                if(profilEmail == ""){
                    binding.emailInputForm.setBackgroundResource(R.drawable.shape_textinput_error)
                }else{
                    binding.emailInputForm.setBackgroundResource(R.drawable.shape_textinput_normal)
                }
                if(profilPassword == ""){
                    binding.passwordInputForm.setBackgroundResource(R.drawable.shape_textinput_error)
                }else{
                    binding.passwordInputForm.setBackgroundResource(R.drawable.shape_textinput_normal)
                }
            }


        }
        return view
    }

    override fun onResume() {
        super.onResume()
        binding.errorRegisterText.visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
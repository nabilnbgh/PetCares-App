package com.example.petcare.mainpage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcare.databinding.FragmentHomeBinding
import com.example.petcare.groom.GroomActivity
import com.example.petcare.model.News
import com.example.petcare.vaccine.VaccineActivity
import com.example.petcare.volunteer.VolunteerActivity

class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferences : SharedPreferences
    private var newsList = ArrayList<News>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.petGroomButton.setOnClickListener {
            requireContext().startActivity(Intent(requireContext(),GroomActivity::class.java))
        }
        binding.petVolunteerButton.setOnClickListener {
            requireContext().startActivity(Intent(requireContext(),VolunteerActivity::class.java))
        }
        binding.petVaccineButton.setOnClickListener {
            requireContext().startActivity(Intent(requireContext(), VaccineActivity::class.java))
        }
        preferences = requireActivity().getSharedPreferences("userData", AppCompatActivity.MODE_PRIVATE)
        var userName =  preferences.getString("nama","")!!
        if(userName != ""){
            if(userName.length > 15){
                userName = userName.substring(0,15)+ "...."
            }
        }
        binding.welcomeText.text = if(userName != "") "Selamat Datang, ${userName}!" else "Selamat Datang!"
        setNewsRV()
        binding.newsRV.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            val adpt = NewsAdapter(requireContext(),newsList)
            adpt.setOnClickListener(object : NewsAdapter.OnClickListener{
                override fun onClick(position: Int, item: News) {
                    val intent = Intent(requireContext(), NewsDetailActivity::class.java)
                    intent.putExtra("item_detail", item)
                    startActivity(intent)
                }
            })
            adapter = adpt
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setNewsRV(){
        val tempArray = ArrayList<News>()
        tempArray.add(
            News(0,"Gratis Steril Kucing oleh PetLover Community Hingga 30 September 2023!","Kabar baik untuk Anda! PetLovers Community sedang mengadakan event gratis steril kucing, acara ini dalam rangka ulang tahun komunitas yang ke 10, cepat daftar acara ini hanya sampai 30 September 2023. Bawa kucing kamu ke Gedung Serbaguna Arcamanik. Ingat ya Kuota terbatas!")
        )
        tempArray.add(
            News(1,"Gratis Vaksin Rabies untuk Kucing Hingga 30 Agustus 2023!","Bingung harga vaksin mahal? Langsung reservasi di Drh Yusuf Adi Nugroho aja! lagi ada promo gratis vaksin rabies kucing loh! Cuma sampai 30 Agustus 2023. Jangan sampai terlewat ya!")
        )
        tempArray.add(
            News(2,"Diskon 25%! Pet Grooming Anjing, Cepat dapatkan!","Promo untung! Dapatkan Diskon 25% untuk Pet Grooming Anjing di Hammieluvluv Grooming tanpa syarat langsung saja reservasi untuk Grooming Anjing. Jangan lupa promo hanya berlaku sampai tanggal 15 Agustus 2023. Jangan sampai terlewat ya")
        )
        newsList = tempArray
    }
}
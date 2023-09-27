package com.example.petcare.mainpage
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.petcare.R
import com.example.petcare.databinding.ActivityNewsDetailBinding
import com.example.petcare.model.News

class NewsDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNewsDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        val newsData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("item_detail", News::class.java)
        } else {
            intent.getParcelableExtra("item_detail")
        } as News

        binding.newsContentText.text = newsData.konten_berita
        binding.newsTitleDetailText.text = newsData.judul_berita
        val id = when(newsData.foto_berita){
            1 -> R.drawable.news_image_new_2
            2 -> R.drawable.news_image_new_3
            else -> R.drawable.news_image_new_1
        }
        val img =  getDrawable(id)
        binding.newsDetailImage.setImageDrawable(img)


        setContentView(binding.root)
    }
}
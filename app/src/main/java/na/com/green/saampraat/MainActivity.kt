package na.com.green.saampraat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import na.com.green.saampraat.databinding.ActivityMainBinding
import na.com.green.saampraat.ui.HomeActivity

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding?.buttonGetStarted?.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }
}
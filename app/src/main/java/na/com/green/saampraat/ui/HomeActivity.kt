package na.com.green.saampraat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import na.com.green.saampraat.R
import na.com.green.saampraat.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

    }

    private fun setupBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.homeNavHostController) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomBar.onTabSelected = { tab ->
            when (tab.id) {
                R.id.servicesFragment -> {
                    navController.navigate(R.id.servicesFragment)
                }

                R.id.feedbackHubFragment -> {
                    navController.navigate(R.id.feedbackHubFragment)
                }

                R.id.settingsFragment -> {
                    navController.navigate(R.id.settingsFragment)
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateBottomBarSelection(destination.id)
        }
    }

    private fun updateBottomBarSelection(destinationId: Int) {
        val tabIndex = when (destinationId) {
            R.id.servicesFragment -> 0
            R.id.feedbackHubFragment -> 1
            R.id.settingsFragment -> 2
            else -> 0
        }

        // Update bottom bar selection without triggering navigation
        if (binding.bottomBar.selectedIndex != tabIndex) {
            binding.bottomBar.selectTabAt(tabIndex, animate = true)
        }
    }

}
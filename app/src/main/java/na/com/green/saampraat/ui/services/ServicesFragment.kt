package na.com.green.saampraat.ui.services

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import na.com.green.saampraat.R
import na.com.green.saampraat.databinding.FragmentServicesBinding

class ServicesFragment : Fragment() {

    private var binding: FragmentServicesBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServicesBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLottie()
    }

    private fun setupLottie() {

        binding?.apply {
            lottieAnimationViewFingerprintSuccess.repeatCount = 0
            lottieAnimationViewFingerprintSuccess.cancelAnimation()

            lottieAnimationViewFingerprintSuccess.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)


                    textViewStatus.text = "Success!"
                    textViewStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

                    lifecycleScope.launch {
                        delay(1500)
                        findNavController().navigate(R.id.action_servicesFragment_to_allServicesFragment)
                    }
                }

                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    textViewStatus.text = "Processing..."
                }
            })

            lottieAnimationViewFingerprintSuccess.playAnimation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
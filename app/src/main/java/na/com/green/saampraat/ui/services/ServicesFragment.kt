package na.com.green.saampraat.ui.services

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import na.com.green.saampraat.R
import na.com.green.saampraat.databinding.FragmentServicesBinding
import na.com.green.saampraat.ui.services.enum.FingerprintState
import java.util.concurrent.Executor

class ServicesFragment : Fragment() {

    private var binding: FragmentServicesBinding? = null
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor
    private var isProcessing = false
    private var authenticationAttempts = 0
    private val maxAttempts = 3
    private var hasTriedBiometric = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServicesBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupBiometric()
        setupClickListeners()
        startInitialAnimation()
    }

    private fun setupUI() {
        binding?.apply {

            textViewStatus.visibility = View.INVISIBLE

            // Setup entrance animation
            lottieAnimationViewFingerprint.alpha = 0f
            lottieAnimationViewFingerprint.scaleX = 0.8f
            lottieAnimationViewFingerprint.scaleY = 0.8f
        }
    }

    private fun setupBiometric() {
        executor = ContextCompat.getMainExecutor(requireContext())

        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        hasTriedBiometric = true
                        handleAuthenticationError(errString.toString())
                    } else if (errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                        // User canceled, fall back to custom animation
                        hasTriedBiometric = true
                        startCustomAuthentication()
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    handleAuthenticationSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    authenticationAttempts++

                    if (authenticationAttempts >= maxAttempts) {
                        hasTriedBiometric = true
                        handleAuthenticationError(getString(R.string.too_many_attempts))
                    } else {
                        updateStatusText("Try again...")
                    }
                }
            })

        // Fingerprint and face
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_authentication))
            .setSubtitle(getString(R.string.biometric_subtitle))
            .setDescription(getString(R.string.biometric_description))
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
    }

    private fun setupClickListeners() {
        binding?.apply {

            lottieAnimationViewFingerprint.setOnClickListener {
                if (!isProcessing && hasTriedBiometric) {
                    resetState()
                    startAuthentication()
                }
            }
        }
    }

    private fun startInitialAnimation() {
        binding?.apply {

            // Animate Lottie view entrance
            lottieAnimationViewFingerprint.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction {
                    // Start with idle animation
                    setFingerprintAnimation(FingerprintState.IDLE)

                    lifecycleScope.launch {
                        delay(500)

                        if (!hasTriedBiometric) {
                            startAuthentication()
                        }
                    }
                }
                .start()
        }
    }

    private fun startAuthentication() {
        // When biometric prompt appears, switch to scanning state
        setFingerprintAnimation(FingerprintState.SCANNING)

        // Check if biometric authentication is available
        val biometricManager = BiometricManager.from(requireContext())

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Check what types are available and show appropriate message
                updateStatusText(getBiometricAvailabilityMessage())

                lifecycleScope.launch {
                    delay(1000)
                    biometricPrompt.authenticate(promptInfo)
                }
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                updateStatusText("No biometric hardware available")
                hasTriedBiometric = true

                lifecycleScope.launch {
                    delay(2000)
                    startCustomAuthentication()
                }
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                updateStatusText("Biometric hardware unavailable")
                hasTriedBiometric = true

                lifecycleScope.launch {
                    delay(2000)
                    startCustomAuthentication()
                }
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                updateStatusText("No biometrics enrolled")
                hasTriedBiometric = true

                lifecycleScope.launch {
                    delay(2000)
                    startCustomAuthentication()
                }
            }

            else -> {
                hasTriedBiometric = true
                startCustomAuthentication()
            }
        }
    }

    private fun getBiometricAvailabilityMessage(): String {
        val biometricManager = BiometricManager.from(requireContext())

        // Check for strong biometrics (includes face recognition)
        val hasStrongBiometric = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS

        // Check for weak biometrics (fingerprint)
        val hasWeakBiometric = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS

        return when {
            hasStrongBiometric && hasWeakBiometric -> "Use fingerprint or face recognition"
            hasStrongBiometric -> "Use face recognition to authenticate"
            hasWeakBiometric -> "Use fingerprint to authenticate"
            else -> "Preparing authentication..."
        }
    }

    private fun startCustomAuthentication() {
        if (isProcessing) return

        updateStatusText("Touch the screen to authenticate")
        setProcessingState(false)

        binding?.lottieAnimationViewFingerprint?.setOnClickListener {
            if (!isProcessing) {
                setProcessingState(true)
                setupLottie()
            }
        }
    }

    private fun setupLottie() {

        binding?.apply {
            // Reset animation state
            lottieAnimationViewFingerprint.repeatCount = 0
            lottieAnimationViewFingerprint.cancelAnimation()

            lottieAnimationViewFingerprint.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                    val isSuccess = (0..10).random() > 2

                    if (isSuccess) {
                        handleAuthenticationSuccess()
                    } else {
                        authenticationAttempts++

                        if (authenticationAttempts >= maxAttempts) {
                            handleAuthenticationError(getString(R.string.too_many_attempts))
                        } else {
                            handleAuthenticationError(getString(R.string.authentication_failed_try_again))
                        }
                    }
                }

                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    updateStatusText(getString(R.string.processing))
                }
            })

            lottieAnimationViewFingerprint.playAnimation()
        }
    }

    private fun setProcessingState(processing: Boolean) {
        isProcessing = processing

        binding?.apply {
            if (processing) {
                textViewStatus.visibility = View.VISIBLE

                // Add pulsing animation
                val pulseAnimation = ObjectAnimator.ofFloat(
                    lottieAnimationViewFingerprint, "alpha", 1f, 0.7f, 1f
                )

                pulseAnimation.duration = 1500
                pulseAnimation.repeatCount = ObjectAnimator.INFINITE
                pulseAnimation.repeatMode = ObjectAnimator.REVERSE
                pulseAnimation.start()

            } else {
                lottieAnimationViewFingerprint.clearAnimation()
                lottieAnimationViewFingerprint.alpha = 1f
            }
        }
    }

    private fun handleAuthenticationSuccess() {
        setProcessingState(false)

        binding?.apply {
            // Update status
            updateStatusText(
                getString(R.string.authentication_successful),
                ContextCompat.getColor(requireContext(), R.color.green)
            )

            setFingerprintAnimation(FingerprintState.SUCCESS)
        }

        // Announce success for accessibility
        announceForAccessibility(getString(R.string.authentication_successful))
    }

    private fun handleAuthenticationError(errorMessage: String) {
        setProcessingState(false)

        // Switch to error animation (will auto-return to idle)
        setFingerprintAnimation(FingerprintState.ERROR)

        binding?.apply {
            // Update UI for error state
            updateStatusText(
                errorMessage,
                ContextCompat.getColor(requireContext(), R.color.red)
            )

            // Auto-retry after error (except for max attempts)
            if (authenticationAttempts < maxAttempts) {
                lifecycleScope.launch {
                    delay(2000)
                    updateStatusText("Touch the screen to try again")
                }
            }
        }

        // Announce error for accessibility
        announceForAccessibility(errorMessage)
    }

    private fun updateStatusText(text: String, color: Int? = null) {
        binding?.apply {
            textViewStatus.text = text
            textViewStatus.visibility = View.VISIBLE

            color?.let {
                textViewStatus.setTextColor(it)
            } ?: run {
                textViewStatus.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.primary)
                )
            }
        }
    }

    private fun resetState() {
        authenticationAttempts = 0
        hasTriedBiometric = false
        isProcessing = false
    }

    private fun announceForAccessibility(message: String) {
        binding?.textViewStatus?.announceForAccessibility(message)
    }

    private fun setFingerprintAnimation(state: FingerprintState) {
        binding?.lottieAnimationViewFingerprint?.apply {
            when (state) {

                FingerprintState.IDLE -> {
                    setAnimation(R.raw.lottie_idle)
                    repeatCount = LottieDrawable.INFINITE
                    speed = 0.7f
                    playAnimation()
                }

                FingerprintState.SCANNING -> {
                    speed = 1.0f
                }

                FingerprintState.SUCCESS -> {
                    setAnimation(R.raw.lottie_successful)
                    repeatCount = 0
                    speed = 1.0f
                    playAnimation()

                    // Navigate after success animation completes
                    addAnimatorListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            removeAnimatorListener(this)

                            lifecycleScope.launch {
                                delay(300)
                                findNavController().navigate(R.id.action_servicesFragment_to_allServicesFragment)
                            }
                        }
                    })
                }

                FingerprintState.ERROR -> {
                    setAnimation(R.raw.lottie_error)
                    repeatCount = 0
                    speed = 1.2f
                    playAnimation()

                    // Return to idle after error animation completes
                    addAnimatorListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            removeAnimatorListener(this)

                            lifecycleScope.launch {
                                delay(500)
                                setFingerprintAnimation(FingerprintState.IDLE)
                            }
                        }
                    })
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
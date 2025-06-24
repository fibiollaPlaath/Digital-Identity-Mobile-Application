package na.com.green.saampraat.ui.feedbackHub

import android.app.AlertDialog
import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import na.com.green.saampraat.R
import na.com.green.saampraat.databinding.FragmentFeedbackHubBinding
import java.util.Locale

class FeedbackHubFragment : Fragment() {

    private var binding: FragmentFeedbackHubBinding? = null
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent
    private var isRecording = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startRecording()
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedbackHubBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpeechRecognizer()
        setupClickListeners()
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())

        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                binding?.buttonRecordVoiceNote?.text = "Recording... Tap to stop"
                binding?.buttonRecordVoiceNote?.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.red)
                )
            }

            override fun onBeginningOfSpeech() {
                // Speech input has begun
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Voice level changed - you can use this for visual feedback
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Audio buffer received
            }

            override fun onEndOfSpeech() {
                isRecording = false
                binding?.buttonRecordVoiceNote?.text = "Start Recording"
                binding?.buttonRecordVoiceNote?.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.green)
                )
            }

            override fun onError(error: Int) {
                isRecording = false
                binding?.buttonRecordVoiceNote?.text = "Start Recording"
                binding?.buttonRecordVoiceNote?.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.green)
                )

                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech match found"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Unknown error"
                }

                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let { matches ->
                    if (matches.isNotEmpty()) {
                        val recognizedText = matches[0]

                        // Get current text and add new recognized text
                        val currentText = binding?.editTextFeedback?.text.toString()
                        val newText = if (currentText.isEmpty()) {
                            recognizedText
                        } else {
                            "$currentText $recognizedText"
                        }

                        binding?.editTextFeedback?.setText(newText)


                        binding?.editTextFeedback?.setSelection(newText.length) // Move cursor to end
                    }
                }

                isRecording = false
                binding?.buttonRecordVoiceNote?.text = "Start Recording"
                binding?.buttonRecordVoiceNote?.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.green)
                )
            }

            override fun onPartialResults(partialResults: Bundle?) {
                partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let { matches ->
                    if (matches.isNotEmpty()) {
                        print("")
                    }
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Reserved for future use
            }
        })
    }

    private fun setupClickListeners() {
        binding?.buttonRecordVoiceNote?.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                checkPermissionAndRecord()
            }
        }

        binding?.buttonSubmitFeedback?.setOnClickListener {
            showSuccessDialog()
            binding?.editTextFeedback?.setText("")
        }
    }

    private fun checkPermissionAndRecord() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startRecording()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                // Show explanation to user
                AlertDialog.Builder(requireContext())
                    .setTitle("Microphone Permission")
                    .setMessage("This app needs microphone access to record your voice and convert it to text.")
                    .setPositiveButton("Grant") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startRecording() {
        if (!SpeechRecognizer.isRecognitionAvailable(requireContext())) {
            Toast.makeText(context, "Speech recognition not available", Toast.LENGTH_SHORT).show()
            return
        }

        isRecording = true
        speechRecognizer.startListening(speechRecognizerIntent)
    }

    private fun stopRecording() {
        isRecording = false
        speechRecognizer.stopListening()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Success!")
            .setMessage("Successfully submitted!")
            .setIcon(R.drawable.ic_success) // Optional: add a success icon
            .setPositiveButton("Done") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
package lars_lion.dev.o_harid.ui.registration.reg

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.os.postDelayed
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import lars_lion.dev.o_harid.R
import lars_lion.dev.o_harid.base.BaseFragment
import lars_lion.dev.o_harid.databinding.FragmentRegistrationBinding
import lars_lion.dev.o_harid.preferences.PreferencesManager
import lars_lion.dev.o_harid.ui.MainActivity
import lars_lion.dev.o_harid.utils.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationFragment : BaseFragment<FragmentRegistrationBinding>() {
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth

    // [END declare_auth]
    @Inject
    lateinit var prefs: PreferencesManager
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: ForceResendingToken
    private lateinit var callbacks: OnVerificationStateChangedCallbacks
    val viewModel: RegistrationViewModel by viewModels()
    private var isCodeSend = false
    var code = ""
    var number = ""
    override fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegistrationBinding = FragmentRegistrationBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        // [START phone_auth_callbacks]
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                }

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                isCodeSend = true
                storedVerificationId = verificationId
                resendToken = token
            }
        }

        with(binding!!) {

            root.setOnClickListener {
                hideKeyBoard(it)
            }

            loginButton.setOnClickListener {
                loginButton.isClickable = false
                if (etName.text.isNotEmpty() && etPhone.text.toString().isNotEmpty()) {
                    binding!!.progressBar.visible(true)
                    hideKeyBoard(it)
                    if (!isCodeSend) {
                        number = "+998${
                            etPhone.text.toString().substring(1, 3)
                        }${etPhone.text.toString().substring(4, 7)}${
                            etPhone.text.toString().substring(8, 10)
                        }${etPhone.text.toString().substring(11)}"
                        startPhoneNumberVerification(number)

                    } else {
                        val data = JsonObject()
                        data.addProperty("name", et_name.text.toString().trim())
                        data.addProperty("number", number)
                        lifecycleScope.launch {
                            viewModel.fetchRegisterUser(data.toString()).catch { it ->
                                toast(it.message ?: "message == null")
                            }.collectLatest { data ->
                                viewModel.registerUser(data)
                                observeUser()
                            }
                        }
                    }

                } else toast(getString(R.string.toliq_kirit))
            }
        }

        login()
    }

    fun login() {
        val spannable = SpannableString(getString(R.string.login_text))
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF3700B3")),
            29, spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding!!.tvLogin.text = spannable

        binding!!.tvLogin.setOnClickListener {
            findNavController().navigateSafe(R.id.action_registrationFragment_to_loginFragment)
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    binding!!.progressBar.visible(false)
                    binding!!.cvCode.visible(true)
                    binding!!.etParol.setText(credential.smsCode)

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun observeUser() {
        with(binding!!) {
            viewModel.register.observe(viewLifecycleOwner, EventObserver {
                when (it) {
                    UiState.Loading -> progressBar.visible(true)
                    is UiState.Success -> {
                        progressBar.visible(false)
                        prefs.token = it.value.`object`.accessToken
                        prefs.name = etName.text.toString()
                        Handler(Looper.myLooper()!!).postDelayed({
                            prefs.isAuthVerified = true
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        }, 2000)
                    }
                    is UiState.Error -> {
                        toast(it.message)
                        loginButton.isClickable = true
                        progressBar.visible(false)
                    }
                }.exhaustive
            })
        }
    }


    companion object {
        private const val TAG = "PhoneAuthActivity"
    }
}
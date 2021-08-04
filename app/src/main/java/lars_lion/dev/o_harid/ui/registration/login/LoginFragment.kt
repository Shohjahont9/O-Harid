package lars_lion.dev.o_harid.ui.registration.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
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
import lars_lion.dev.o_harid.databinding.FragmentLoginBinding
import lars_lion.dev.o_harid.preferences.PreferencesManager
import lars_lion.dev.o_harid.ui.MainActivity
import lars_lion.dev.o_harid.ui.registration.reg.RegistrationFragment
import lars_lion.dev.o_harid.utils.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var prefs: PreferencesManager
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private lateinit var auth: FirebaseAuth

    private var isCodeSend = false
    var code = ""
    var number = ""

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding =
        FragmentLoginBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        // [START phone_auth_callbacks]
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                Log.d("TAG", "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                Log.w("TAG", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                isCodeSend = true
                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
            }
        }

        login()

        with(binding!!) {

            rooot.setOnClickListener {
                hideKeyBoard(it)
            }

            loginButton.setOnClickListener {
                loginButton.isClickable = false

                if (etPhone.text.toString().isNotEmpty()) {
                    hideKeyBoard(it)
                    number = "+998${etPhone.text.toString().substring(1, 3)}${
                        etPhone.text.toString().substring(4, 7)
                    }${etPhone.text.toString().substring(8, 10)}${
                        etPhone.text.toString().substring(11)
                    }"

                    println("button clicked")
                    if (!isCodeSend) {
                        val body = JsonObject()
                        body.addProperty("number", number)
                        viewModel.loginUser(body.toString())
                        observeUser()
                    } else if (etParol.text.toString() == code) {
                        Handler(Looper.myLooper()!!).postDelayed({
                            prefs.isAuthVerified = true
                            startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }, 2000)
                    }

                } else
                    toast(getString(R.string.toliq_kirit))
            }
        }
    }

    private fun observeUser() {
        with(binding!!) {
            viewModel.login.observe(viewLifecycleOwner, EventObserver {
                println("Observe")
                when (it) {
                    UiState.Loading -> progressBar.visible(true)
                    is UiState.Success -> {
                        prefs.token = it.value.`object`.accessToken
                        prefs.name = it.value.`object`.name
                        binding!!.progressBar.visible(false)
                        startPhoneNumberVerification(number)
                        root.snackbar("Sizga tasdiqlash kodi yuboriladi sabr qiling!")
//                        Handler(Looper.myLooper()!!).postDelayed({
//                            prefs.isAuthVerified = true
//                            startActivity(Intent(requireContext(), MainActivity::class.java))
//                            requireActivity().finish()
//                        }, 2000)
                    }
                    is UiState.Error -> {
                        loginButton.isClickable = true
                        toast(it.message)
                        progressBar.visible(false)
                    }
                }.exhaustive
            })
        }
    }


    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    binding!!.cvCode.visible(true)
                    binding!!.etParol.setText(credential.smsCode)

                    Handler(Looper.myLooper()!!).postDelayed({
                        prefs.isAuthVerified = true
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }, 2000)

                    val user = task.result?.user
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }


    fun login() {
        val spannable = SpannableString("Ro`yhatdan o`tish!")
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF3700B3")),
            0, spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding!!.tvLogin.text = spannable

        binding!!.tvLogin.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}
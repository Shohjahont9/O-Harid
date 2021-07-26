package lars_lion.dev.o_harid.ui.registration.reg

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.fragment.app.viewModels
import com.google.android.gms.tasks.OnCompleteListener
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
                // This callback will be invoked in two situations:
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                Log.w(TAG, "onVerificationFailed", e)

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
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")
//                verifyPhoneNumberWithCode(verificationId, code)
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }

        with(binding!!) {
            loginButton.setOnClickListener {
                if (etName.text.isNotEmpty()) {
                    binding!!.progressBar.visible(true)
                    hideKeyBoard(it)
                    if (!isCodeSend) {
                        number = "+998${
                            etPhone.text.toString().substring(1, 3)
                        }${etPhone.text.toString().substring(4, 7)}${
                            etPhone.text.toString().substring(8, 10)
                        }${etPhone.text.toString().substring(11)}"
                        startPhoneNumberVerification(number)
                        println("number - > $number")
                    } else {
                        println("etParol -> ${etParol.text}  code -> $code")
                        if (etParol.text.toString() == code) {

                            startActivity(Intent(requireContext(), MainActivity::class.java))
                        }
                    }

                } else toast("Ismni kiriting")
            }
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
    // [END resend_verification]

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    binding!!.cvCode.visible(true)
                    binding!!.etParol.setText(credential.smsCode)
                    val data = JsonObject()
                    data.addProperty("name", et_name.text.toString().trim())
                    data.addProperty("number", number)
                    viewModel.registerUser(data.toString())
                    observeUser()
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
                        progressBar.visible(false)
                        toast(it.message)
                    }
                }.exhaustive
            })
        }
    }


    companion object {
        private const val TAG = "PhoneAuthActivity"
    }
}
package com.lrm.kravito.viewModel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lrm.kravito.constants.USER_NUMBER
import com.lrm.kravito.constants.USER_UID
import java.util.regex.Pattern

class LoginViewModel : ViewModel() {

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String>
        get() = _phoneNumber

    private val _verificationId = MutableLiveData<String>()
    val verificationId: LiveData<String>
        get() = _verificationId

    fun setPhoneNumber(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }

    fun setVerificationId(verificationId: String) {
        _verificationId.value = verificationId
    }

    fun isLoginValid(
        context: Context,
        phoneNumber: String,
        termsCheckBox: Boolean
    ): Boolean {
        val mobileNumPattern = Pattern.compile("[6-9][0-9]{9}")

        if (phoneNumber.isNotEmpty()){
            if (phoneNumber.length == 10
                && mobileNumPattern.matcher(phoneNumber).matches()
            ) {
                if (termsCheckBox) {
                    Toast.makeText(context, "Sending OTP", Toast.LENGTH_SHORT).show()
                    return true
                } else {
                    Toast.makeText(context, "Agree to Terms and conditions to proceed", Toast.LENGTH_SHORT).show()
                    return false
                }
            } else {
                Toast.makeText(context, "Please enter valid phone number", Toast.LENGTH_SHORT).show()
                return false
            }
        } else {
            Toast.makeText(context, "Please enter phone number", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun checkUserInFirestore(context: Context, phoneNumber: String, userUid: String) {

        val db = Firebase.firestore

        db.collection("users").document(phoneNumber).get()
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    Toast.makeText(context, "Welcome back...", Toast.LENGTH_SHORT).show()
                } else {
                    val user = hashMapOf(
                        USER_NUMBER to phoneNumber,
                        USER_UID to userUid
                    )

                    db.collection("users").document(phoneNumber).set(user)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Welcome to Kravito...",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
    }
}
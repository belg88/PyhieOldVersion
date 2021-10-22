package gleb.apps.pyhie.intro

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.data.BitmapTeleporter
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import gleb.apps.pyhie.R
import gleb.apps.pyhie.SharedPref
import gleb.apps.pyhie.databinding.FragmentLogInBinding
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.mainHabits.sleepingHabit.SleepingSavedState
import gleb.apps.pyhie.mainHabits.sleepingHabit.cleaningHabit.CleaningSavedState
import gleb.apps.pyhie.mainHabits.sleepingHabit.eatingHabit.EatingSavedState
import gleb.apps.pyhie.util.Keys
import kotlinx.coroutines.launch


class LogInFragment : Fragment() {
    private lateinit var resetDialog: Dialog
    private lateinit var binding: FragmentLogInBinding
    private lateinit var sharedPref: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater,container,false)
        resetDialog = Dialog(requireContext())
        sharedPref = SharedPref(requireContext())
        binding.apply {
            confirmPassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    loginButton.isEnabled = email.text.toString().isNotEmpty() && confirmPassword.text.toString().isNotEmpty()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

            })
            loginButton.setOnClickListener { logIn() }
            forgotPassword.setOnClickListener { resetDialog() }
        }

        return binding.root
    }

    private fun logIn () {
        val auth = FirebaseAuth.getInstance()
        binding.apply {
            auth.signInWithEmailAndPassword(email.text.toString(),confirmPassword.text.toString()).addOnCompleteListener {
                if (it.isSuccessful) {
                    lifecycleScope.launch {
                        val eatingInfo = FirebaseService.getEatingInfo(email.text.toString())
                        val sleepingInfo = FirebaseService.getSleepingPref(email.text.toString())
                        val userInfo = FirebaseService.getUserInfo(email.text.toString())
                        val eatingSaved = EatingSavedState(eatingInfo!!)
                        val gson = Gson()
                        val gsonString = gson.toJson(
                            eatingSaved
                        )
                        SharedPref(requireContext()).saveString(Keys.EAT_SAVED_STATE, gsonString)
                        val sleepSaved = SleepingSavedState(sleepingInfo!!)
                        val gsonSleepString = gson.toJson(
                            sleepSaved
                        )
                        SharedPref(requireContext()).saveString(Keys.SLEEP_SAVED_STATE, gsonSleepString)

                        val savedState = CleaningSavedState()
                        val gsonCleanString = gson.toJson(
                            savedState
                        )
                        SharedPref(requireContext()).saveString(Keys.CLEANING_SAVED_STATE, gsonCleanString)
                        if (userInfo != null) {
                            sharedPref.saveString(Keys.NAME, userInfo.name)
                            sharedPref.saveString(Keys.EMAIL,userInfo.email)
                            sharedPref.saveInt(Keys.CLEAN_BALANCE, userInfo.cleanBalance)
                            sharedPref.saveInt(Keys.EAT_BALANCE, userInfo.eatBalance)
                            sharedPref.saveInt(Keys.SLEEP_BALANCE, userInfo.sleepBalance)
                            sharedPref.saveInt(Keys.PLANNER_BALANCE, userInfo.planningBalance)
                            sharedPref.saveInt(Keys.CURRENT_CHALLENGE, userInfo.currentChallenge)

                        }
                    }.invokeOnCompletion {

                        findNavController().navigate(LogInFragmentDirections.actionLogInFragmentToMainActivity())
                    }

                } else {
                    Toast.makeText(requireContext(), it.exception?.message, Toast.LENGTH_LONG).show()
                }
            }.addOnCanceledListener {
                Toast.makeText(requireContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetDialog() {
        val auth = FirebaseAuth.getInstance()
        resetDialog.setContentView(R.layout.forgot_password_dialog)
        val resetEmail = resetDialog.findViewById<EditText>(R.id.email)
        val resetButton = resetDialog.findViewById<Button>(R.id.reset_button)
        resetDialog.show()

        resetEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                resetButton.isEnabled = resetEmail.text.toString().isNotEmpty()
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        resetButton.setOnClickListener {
            auth.sendPasswordResetEmail(resetEmail.toString()).addOnCompleteListener {
                if (it.isSuccessful) {
                    findNavController().popBackStack()
                    Toast.makeText(requireContext(), "The reset password email has been sent to you.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }.addOnCanceledListener {
                Toast.makeText(requireContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
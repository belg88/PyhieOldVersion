package gleb.apps.pyhie.intro

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import gleb.apps.pyhie.R
import gleb.apps.pyhie.SharedPref
import gleb.apps.pyhie.animation.CustomAnimations
import gleb.apps.pyhie.databinding.FragmentIntroSecondPartBinding
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.pojos.EatingInfo
import gleb.apps.pyhie.pojos.UserInfo
import gleb.apps.pyhie.pojos.UserPoints
import gleb.apps.pyhie.util.Keys
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule


class IntroSecondPart : Fragment() {
    private lateinit var binding: FragmentIntroSecondPartBinding
    private var name = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroSecondPartBinding.inflate(inflater, container, false)
        binding.apply {
            submitButton.setOnClickListener { submitYourName() }
            submitEmailButton.setOnClickListener { submitYourEmail() }
            submitPasswordButton.setOnClickListener { submitYourPassword() }
        }


        introAnim()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.enterName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.submitButton.isEnabled = binding.enterName.text.toString().isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.enterEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.submitEmailButton.isEnabled =
                    binding.enterEmail.text.toString().isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.confirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.submitPasswordButton.isEnabled =
                    binding.enterPassword.text.toString().length > 5 && binding.confirmPassword.length() > 5
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    private fun introAnim() {
        binding.apply {
            CustomAnimations.slideInFromTheLeft(title, 800, 0)
            CustomAnimations.slideInFromTheLeft(title1, 1000, 1200)
            CustomAnimations.appearFromZeroAlpha(enterName, 1800, 700)
            CustomAnimations.appearFromZeroAlpha(submitButton, 1800, 700)
        }
    }

    private fun submitYourName() {
        binding.apply {
            name = enterName.text.toString().trim()
            newTitle.text = getString(R.string.nice_to_meet, name)
            SharedPref(requireContext()).saveString(Keys.NAME,name)
            CustomAnimations.slideUpOffTheScreen(layout, -1900f, 1000, 0)
            CustomAnimations.slideUpToTheScreen(layoutEmail, 1000, 1900f, 500)
        }
    }

    private fun submitYourEmail() {
        binding.apply {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(enterEmail.text.toString().trim())
                    .matches()
            ) {
                SharedPref(requireContext()).saveString(
                    Keys.EMAIL,
                    enterEmail.text.toString().trim()
                )
                CustomAnimations.slideUpOffTheScreen(layoutEmail, -1900f, 1000, 0)
                CustomAnimations.slideUpToTheScreen(layoutPassword, 1000, 1900f, 500)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid email", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun submitYourPassword() {
        val auth = FirebaseAuth.getInstance()
        val email = SharedPref(requireContext()).getString(Keys.EMAIL)!!
        binding.apply {
            if (name.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Something went wrong, please try again.",
                    Toast.LENGTH_SHORT
                ).show()
                layoutPassword.visibility = View.GONE
                layout.translationY = 0f
                layout.visibility = View.VISIBLE

            } else {
                if (confirmPassword.text.toString() == enterPassword.text.toString()) {
                    binding.layoutPassword.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    auth.createUserWithEmailAndPassword(
                        email,
                        confirmPassword.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            findNavController().navigate(IntroSecondPartDirections.actionIntroSecondPartToIntroThirdPart(name))
                            initUser(email)
                        } else {
                            Toast.makeText(
                                requireContext(),it.exception?.message,
                                        Toast.LENGTH_LONG
                            ).show()
                            binding.layoutEmail.visibility = View.VISIBLE
                            binding.layoutEmail.translationY = 0f
                            binding.progressBar.visibility = View.GONE
                        }
                    }.addOnCanceledListener {
                        binding.layoutEmail.visibility = View.VISIBLE
                        binding.layoutEmail.translationY = 0f
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(requireContext(), "Passwords do not match!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun initUser(email: String) {
        lifecycleScope.launch {
            FirebaseService.insertUserPoints(UserPoints(0.0), email)
            FirebaseService.insertUserInfo(UserInfo(name, email), email)
        }
    }
}
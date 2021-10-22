package gleb.apps.pyhie.intro

import android.animation.Animator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import gleb.apps.pyhie.R
import gleb.apps.pyhie.databinding.FragmentIntroFirstPartBinding
import java.util.*
import kotlin.concurrent.schedule


class IntroFirstPart : Fragment() {
    private lateinit var binding: FragmentIntroFirstPartBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroFirstPartBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            Timer().schedule(1000){
                findNavController().navigate(IntroFirstPartDirections.actionIntroFirstPartToMainActivity())
                activity?.finish()
            }

        } else {
            binding.layout.visibility = View.VISIBLE
        }
        binding.noButton.setOnClickListener {
            findNavController().navigate(IntroFirstPartDirections.actionIntroFirstPartToIntroSecondPart())
        }
        binding.yesButton.setOnClickListener {
            findNavController().navigate(IntroFirstPartDirections.actionIntroFirstPartToLogInFragment())
        }

        return binding.root
    }



}
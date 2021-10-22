package gleb.apps.pyhie

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import gleb.apps.pyhie.databinding.FragmentLevelIsCompletedBinding
import gleb.apps.pyhie.util.Keys
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size


class LevelIsCompleted : Fragment() {
   private lateinit var binding: FragmentLevelIsCompletedBinding
    private val args: LevelIsCompletedArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SharedPref(requireContext()).saveBoolean(Keys.NEW_LEVEL_NOT_REACHED,true)
        binding = FragmentLevelIsCompletedBinding.inflate(inflater,container, false).apply {
            konfettiView.build()
                .addColors(Color.BLUE,Color.WHITE,Color.RED)
                .addShapes(Shape.Square)
                .setDirection(0.0,359.0)
                .setSpeed(1f,6f)
                .setTimeToLive(1500)
                .setFadeOutEnabled(true)
                .addSizes(Size(12))
                .setPosition(0f,1000f,-50f,1000f)
                .streamFor(300, 2000)

            title1.text = getString(R.string.you_reached_level,args.level)
            backButton.setOnClickListener { findNavController().popBackStack() }
        }

        return binding.root
    }


}
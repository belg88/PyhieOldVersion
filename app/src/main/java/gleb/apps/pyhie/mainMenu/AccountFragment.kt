package gleb.apps.pyhie.mainMenu

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import gleb.apps.pyhie.R
import gleb.apps.pyhie.SharedPref
import gleb.apps.pyhie.databinding.FragmentAccountBinding
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.intro.IntroActivity
import gleb.apps.pyhie.util.Keys
import kotlinx.coroutines.launch


class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var myDetailsDialog: Dialog
    private var emailString = ""
    private var nameString = ""
    private var notificationsOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationsOn = SharedPref(requireContext()).getBoolean(Keys.NOTIFICATIONS_ON)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater,container,false).apply {
            changeName.setOnClickListener { startMyAccountDialog() }
            switch1.isChecked = notificationsOn
            notifications.setOnClickListener { toggleNotifications() }
            switch1.setOnCheckedChangeListener { buttonView, isChecked ->
                when (isChecked) {
                    false -> {
                        SharedPref(requireContext()).saveBoolean(Keys.NOTIFICATIONS_ON, false)
                    }
                    true -> {
                        SharedPref(requireContext()).saveBoolean(Keys.NOTIFICATIONS_ON, true)
                    }
                }
            }
            logOut.setOnClickListener { logOut() }
        }
        emailString = SharedPref(requireContext()).getString(Keys.EMAIL)!!
        nameString = SharedPref(requireContext()).getString(Keys.NAME)!!
        myDetailsDialog = Dialog(requireContext())


        return binding.root
    }

    private fun startMyAccountDialog() {
        myDetailsDialog.setContentView(R.layout.dialog_my_details)
        val nameEditText = myDetailsDialog.findViewById<EditText>(R.id.name_edittext)
        nameEditText.setText(nameString)
        val email = myDetailsDialog.findViewById<TextView>(R.id.email)
        email.text = emailString
        val saveButton = myDetailsDialog.findViewById<ExtendedFloatingActionButton>(R.id.save_button)
        saveButton.setOnClickListener {
            lifecycleScope.launch {
                val userInfo = FirebaseService.getUserInfo(emailString)
                if (userInfo != null) {
                    userInfo.name = nameEditText.text.toString()
                    FirebaseService.insertUserInfo(userInfo,emailString)
                    SharedPref(requireContext()).saveString(Keys.NAME,userInfo.name)
                }
            }
            myDetailsDialog.dismiss()
        }
        myDetailsDialog.show()
    }

    private fun toggleNotifications() {
        binding.apply {
            when (switch1.isChecked) {
                true -> {
                    switch1.isChecked = false
                    SharedPref(requireContext()).saveBoolean(Keys.NOTIFICATIONS_ON,false)
                }
                false -> {
                    switch1.isChecked = true
                    SharedPref(requireContext()).saveBoolean(Keys.NOTIFICATIONS_ON,true)
                }
            }
        }
    }

    private fun logOut() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        activity?.finish()
        startActivity(Intent(requireContext(),IntroActivity::class.java))
    }
}
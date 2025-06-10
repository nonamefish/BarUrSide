package com.mingyuwu.barurside.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mingyuwu.barurside.MainActivity
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.databinding.FragmentLoginBinding
import com.mingyuwu.barurside.ext.getVmFactory
import com.mingyuwu.barurside.ext.showToast
import com.mingyuwu.barurside.util.Logger

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding
    private val startForSignIn = registerStartForSignIn()
    val viewModel by viewModels<LoginViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        auth = Firebase.auth
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        // set sign in onclick listener
        binding.Signin.setOnClickListener {
            reportUser()
        }

        // after login navigate to start destination
        viewModel.navigateToDetail.observe(
            viewLifecycleOwner, {
                it?.let {
                    findNavController().navigate(
                        MainNavigationDirections.navigateToActivityFragment()
                    )
                    viewModel.onLeft()
                }
            }
        )

        // add app rule
        binding.textRule.setOnClickListener {
            reportUser()
        }

        return binding.root
    }

    private fun firebaseSignInWithPopup() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso)
        val signInIntent = googleSignInClient.signInIntent

        startForSignIn.launch(signInIntent)
    }

    private fun registerStartForSignIn(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK && data != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)

                    account.idToken?.let {
                        firebaseAuthWithGoogle(it, account)
                    }
                    Logger.i("google signInResult successful")
                } catch (e: ApiException) {
                    Logger.e("google signInResult:failed code=" + e.message)
                }
            } else {
                Logger.d("registered cancel")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, google: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    Logger.d("signInWithCredential:success, user:$task")
                    onFirebaseSignInSuccess(google)
                } else {

                    // If sign in fails, display a message to the user.
                    Logger.e("signInWithCredential:failure ${task.exception}")
                    showToast("message: ${task.exception}")
                }
            }
    }

    private fun onFirebaseSignInSuccess(account: GoogleSignInAccount) {
        val user = User(
            account.email ?: "",
            account.displayName ?: "",
            (account.photoUrl ?: "").toString(),
            null,
            0,
            0
        )

        viewModel.addUser(user)
        viewModel.getUserData(account.email!!)
        (requireActivity() as MainActivity).onGetUserDataFinished()
    }

    private fun reportUser() {
        // set alert dialog view
        val alertDialog = AlertDialog.Builder(binding.root.context)
        val mView = LayoutInflater.from(context).inflate(R.layout.dialog_rating_uncompleted, null)
        alertDialog.setView(mView)
        val dialog = alertDialog.create()

        mView.let{
            // set dialog content
            val txtDialog = it.findViewById<TextView>(R.id.dialog_content)
            val titleDialog = it.findViewById<TextView>(R.id.dialog_title)
            titleDialog.text = getString(R.string.report_rule)
            txtDialog.text = getString(R.string.rules_of_use)

            // set button click listener
            val btDialog = it.findViewById<Button>(R.id.button_confirm)
            btDialog.text = getString(R.string.rule_confirm)
            btDialog.setOnClickListener {
                firebaseSignInWithPopup()
                dialog.dismiss()
            }
        }

        dialog.show()

        // set border as transparent
        val layoutParameter = dialog.window?.attributes
        layoutParameter?.width = 900
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}

package com.mingyuwu.barurside.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mingyuwu.barurside.data.User
import com.mingyuwu.barurside.MainNavigationDirections
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.databinding.FragmentLoginBinding
import com.mingyuwu.barurside.ext.getVmFactory

const val TAG = "LoginFragment"
const val RC_SIGN_IN = 102

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding
    val viewModel by viewModels<LoginViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        // set sign in onclick listener
        binding.Signin.setOnClickListener {
            firebaseSignInWithPopup()
        }

        // after login navigate to start destination
        viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d(TAG,"navigateToDetail: $it")
                findNavController().navigate(MainNavigationDirections.navigateToActivityFragment())
                viewModel.onLeft()
            }
        })

        return binding.root
    }

    private fun firebaseSignInWithPopup() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                account.email?.let {
                    UserManager.userToken = account.idToken

                    firebaseAuthWithGoogle(UserManager.userToken!!)
                    val user = User(
                        account.email,
                        account.displayName,
                        account.photoUrl.toString(),
                        null,
                        0,
                        0
                    )
                    viewModel.addUser(user)
                    viewModel.getUserData(account.email!!)
                    Log.d(TAG, "user:$user")
                }

                Log.i(TAG, "signInResult successful")
            } catch (e: ApiException) {
                Log.i(TAG, "signInResult:failed code=" + e.message)
            }
        } else {
            Log.i(TAG, "login fail")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success, user:$task")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

}
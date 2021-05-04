package com.kunalfarmah.covid_19_info_dashboard.ui.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.google.gson.Gson
import com.kunalfarmah.covid_19_info_dashboard.util.Constants
import com.kunalfarmah.covid_19_info_dashboard.util.Constants.Companion.RC_SIGN_IN
import com.kunalfarmah.covid_19_info_dashboard.R
import com.kunalfarmah.covid_19_info_dashboard.databinding.ActivitySigninBinding
import com.kunalfarmah.covid_19_info_dashboard.model.User
import com.kunalfarmah.covid_19_info_dashboard.util.AppUtil
import com.kunalfarmah.covid_19_info_dashboard.util.CustomGoogleSignIn

class SignInActivity : AppCompatActivity() {
    lateinit var binding: ActivitySigninBinding
    var mGoogleSignInClient: GoogleSignInClient? = null
    private var parent_view: View? = null
    private var mAuth: FirebaseAuth? = null
    private var progressDialog: ProgressDialog? = null
    private var sharedPreferences: SharedPreferences? = null
    var usersRef: DatabaseReference? = null
    var isSignin: Boolean = true


    companion object {
        val TAG = "SignInActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Sign In to Continue"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        parent_view = findViewById(android.R.id.content)
        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE)

        binding = ActivitySigninBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage(resources.getString(R.string.please_wait))
        progressDialog?.setCancelable(false)
        mGoogleSignInClient = GoogleSignIn.getClient(
            applicationContext, CustomGoogleSignIn.getInstance().getGso(
                applicationContext
            )
        )

        binding.googleSignIn.setOnClickListener {
            if (AppUtil.isNetworkAvailable(this@SignInActivity)) {
                Log.e(SignInActivity.TAG, "Sign In Google")
                signInWithGoogle()
            } else {
                Snackbar.make(
                    parent_view!!,
                    resources.getString(R.string.no_network),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
        binding.login.setOnClickListener {
            var name = binding.name.text.toString()
            var email = binding.email.text.toString()
            var pass = binding.password.text.toString()

            if(!isSignin && name.isNullOrEmpty()){
                Toast.makeText(this,"Please Provide Your Name",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(email.isNullOrEmpty() || !email.contains('@') || !email.contains(".com")){
                Toast.makeText(this,"Please Provide a valid Email address",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(pass.isNullOrEmpty() || pass.length<6){
                Toast.makeText(this,"Please Provide a password greater than 6 characters",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isSignin) {
                progressDialog?.show()
                mAuth?.createUserWithEmailAndPassword(email, pass)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = mAuth?.currentUser
                            val userId = user?.uid
                            insertUser(userId, name, null)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            progressDialog?.dismiss()
                            Toast.makeText(
                                baseContext, String.format("Authentication failed. %s",task.exception?.message!!),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                progressDialog?.show()
                mAuth?.signInWithEmailAndPassword(email, pass)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = mAuth?.currentUser
                            val userId = user?.uid
                            getUserName(userId!!)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            progressDialog?.dismiss()
                            Toast.makeText(
                                baseContext, String.format("Authentication failed. %s",task.exception?.message!!),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        binding.signUp.setOnClickListener {
            if(isSignin) {
                isSignin = false
                binding.nameLayout.visibility = View.VISIBLE
                binding.login.text = resources.getString(R.string.sign_up)
                binding.signUp.text = Html.fromHtml("<u>Already have an account? SignIn</u>")
            }
            else{
                isSignin = true
                binding.nameLayout.visibility = View.GONE
                binding.login.text = resources.getString(R.string.sign_in)
                binding.signUp.text = Html.fromHtml("<u>No Account? SignUp</u>")
            }
        }
    }


    fun getUserName(uId: String) {
        usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        usersRef?.child(uId)?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var user = snapshot.getValue(User::class.java)
                sendResult(user!!)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun signInWithGoogle() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            progressDialog?.show()
            val account = completedTask.getResult(ApiException::class.java)
            Log.e(TAG, account.toString())
            var credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
            mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        Log.e(TAG, "linkWithCredential:success")
                        var user = task.result!!.user
                        val userId = user?.uid
                        val name = user?.displayName
                        val phone = user?.phoneNumber
                        insertUser(userId, name, phone)


                    } else {
                        progressDialog?.cancel()
                        Log.e(TAG, "linkWithCredential:failure", task.exception)
                        Toast.makeText(
                            this@SignInActivity, getString(R.string.social_auth_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } catch (e: ApiException) {
            progressDialog?.cancel()
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
            Toast.makeText(this, getString(R.string.sign_in_failed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun insertUser(userID: String?, name: String?, phone: String?) {
        usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        var user_ = User(
            userID,
            name,
            phone,
            ArrayList(),
            ArrayList()
        )
        sharedPreferences?.edit()?.putString(Constants.USER, Gson().toJson(user_))
            ?.apply()
        usersRef?.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.child(userID!!).exists()) {
                    usersRef?.child(userID)?.setValue(user_)?.addOnSuccessListener {
                        sendResult(user_)
                    }
                }
                else{
                    sendResult(user_)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun sendResult(user:User){
       /* Toast.makeText(
            this@SignInActivity, getString(R.string.signed_in_success),
            Toast.LENGTH_SHORT
        ).show()*/
        sharedPreferences?.edit()?.putString(Constants.USER, Gson().toJson(user))?.apply()
        progressDialog?.dismiss()
        var intent = Intent()
        intent.putExtra("user", Gson().toJson(user))
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        var intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

}
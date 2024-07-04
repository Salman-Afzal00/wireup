@file:Suppress("DEPRECATION")

package com.mani.wirup

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class Register : AppCompatActivity() {
    private lateinit var mAuth:FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var btnSignup:Button
    private lateinit var btnGoogle:Button
    private lateinit var tvLogin:TextView
    private lateinit var dbuser: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
         mAuth= FirebaseAuth.getInstance()
        supportActionBar?.hide()
        btnSignup=findViewById(R.id.btnSignup)
        btnGoogle=findViewById(R.id.btnGoogle)
        tvLogin=findViewById(R.id.tvLogin)
        btnSignup.setOnClickListener {
            startActivity(Intent(this,Password::class.java))
        }
        tvLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient= GoogleSignIn.getClient(this,gso)

        btnGoogle.setOnClickListener {
            signInGoogle()
        }
    }
    private fun signInGoogle(){
        val signInIntent=googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
        if (result.resultCode== Activity.RESULT_OK){
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account: GoogleSignInAccount?=task.result
            if (account!=null){
                updateUI(account)
            }
        }
        else{
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        var credential= GoogleAuthProvider.getCredential(account.idToken,null)
        mAuth.signInWithCredential(credential).addOnCompleteListener{
            if (it.isSuccessful){
                mAuth.currentUser?.email
                val emai=mAuth.currentUser?.email.toString()
                mAuth.currentUser?.displayName
                val name=mAuth.currentUser?.displayName.toString()
                mAuth.currentUser?.uid.toString()
                addUserToDatabase(emai,name,mAuth.currentUser?.uid!!)
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
            else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

            }
        }
    }
    private fun addUserToDatabase(email: String, name: String, uid: String){
        dbuser= FirebaseDatabase.getInstance().getReference()
        dbuser.child("user").child(uid).setValue(User(name,email,uid))
    }



}
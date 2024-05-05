package com.mani.wirup

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var tvemail: TextView
    private lateinit var tvforgot:TextView
    private lateinit var tvsignup: TextView
    private lateinit var btngoogle: Button
    private lateinit var btnlogin: Button
    private lateinit var edtemaillogin: TextInputEditText
    private lateinit var dbuser:DatabaseReference
    private lateinit var edtpassword:TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        supportActionBar?.hide()
        mAuth= FirebaseAuth.getInstance()
        btngoogle=findViewById(R.id.btngoogle)
        tvsignup=findViewById(R.id.tvsignup)
        tvemail=findViewById(R.id.tvemail)
        tvforgot=findViewById(R.id.tvforgot)
        edtpassword=findViewById(R.id.edtpassword)
        btnlogin=findViewById(R.id.btnlogin)
        edtemaillogin=findViewById(R.id.edtemaillogin)
        edtemaillogin.setOnFocusChangeListener { _, hasFocus ->

        }
        edtpassword.setOnFocusChangeListener { v, hasFocus ->

        }
        tvsignup.setOnClickListener {
            startActivity(Intent(this,Email::class.java))
            finish()
        }
        tvforgot.setOnClickListener {
            startActivity(Intent(this,ForgotPassword::class.java))
            finish()
        }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient= GoogleSignIn.getClient(this,gso)

        btngoogle.setOnClickListener {
            signInGoogle()
        }
        btnlogin.setOnClickListener {
            val email=edtemaillogin.text.toString()
            val password=edtpassword.text.toString()
            login(email,password)
        }
    }
    private fun login(email:String,password:String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if(mAuth.currentUser!!.isEmailVerified()) {

                       startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }
                    else
                    {
                        Toast.makeText(this,"This email is not verified",
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this,"some error occured",Toast.LENGTH_SHORT).show()
                }
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
        dbuser=FirebaseDatabase.getInstance().getReference()
    dbuser.child("user").child(uid).setValue(User(name,email,uid))
    }

}
package com.mani.wirup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class Password : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var tvsignin:TextView
    private lateinit var btnnext2:Button
    private lateinit var edtPass: TextInputEditText
    private lateinit var edtConfirm:TextInputEditText
    private lateinit var dbuser:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        supportActionBar?.hide()
        mAuth= FirebaseAuth.getInstance()

        edtPass=findViewById(R.id.edtPass)
        edtPass.setOnFocusChangeListener { _, hasFocus ->

        }
        edtConfirm=findViewById(R.id.edtConfirm)
        edtConfirm.setOnFocusChangeListener { v, hasFocus ->

        }
        tvsignin=findViewById(R.id.tvsignin)
        tvsignin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
        btnnext2=findViewById(R.id.btnnext2)



            btnnext2.setOnClickListener {
                var password=edtPass.text.toString()
                var confirm=edtConfirm.text.toString()
                if (password==confirm){
                    var email=intent.getStringExtra("EMAIL").toString()
                    var name=intent.getStringExtra("NAME").toString()
                    signup(name,email,password)
                }
                else{
                    Toast.makeText(this, "Password match missing", Toast.LENGTH_SHORT).show()
                }


            }



    }
    private fun signup(name:String,email:String,Password:String){
        mAuth.createUserWithEmailAndPassword(email,Password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    mAuth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener(OnCompleteListener<Void>(){
                                task ->
                            if(task.isSuccessful){

                                addUserToDatabase(name,email,mAuth.currentUser?.uid!!)
                               val intent=Intent(this,signupok::class.java)
                                intent.putExtra("Email",email)
                                startActivity(intent)
                            }
                            else {
                                Toast.makeText(this,task.getException()!!.message, Toast.LENGTH_SHORT).show()
                            }
                        })

                } else {
                    Toast.makeText(this,"This email & password already present", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun addUserToDatabase(name: String,email: String,uid:String){
     dbuser=FirebaseDatabase.getInstance().getReference()
        dbuser.child("user").child(uid).setValue(User(name, email, uid))
    }
}
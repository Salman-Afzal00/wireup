package com.mani.wirup

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


@Suppress("DEPRECATION")
class Password : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var btnnext2:Button
    private lateinit var edtName:EditText
    private lateinit var edtEmail:EditText
    private lateinit var edtPass: EditText
    private lateinit var edtConfirm:EditText
    private lateinit var dbuser:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        supportActionBar?.hide()
        mAuth= FirebaseAuth.getInstance()
        edtName=findViewById(R.id.edtName)
        edtEmail=findViewById(R.id.edtEmail)
        edtPass=findViewById(R.id.edtPass)
        edtConfirm=findViewById(R.id.edtConfirm)


        btnnext2=findViewById(R.id.btnnext2)



            btnnext2.setOnClickListener {
                var name=edtName.text.toString()
                var email=edtEmail.text.toString()
                var password=edtPass.text.toString()
                var confirm=edtConfirm.text.toString()
                if (password==confirm){

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


                                startActivity(Intent(this,signupok::class.java))
                            }
                            else {
                                Toast.makeText(this,task.getException()!!.message, Toast.LENGTH_SHORT).show()
                            }
                        })

                } else {
                    Toast.makeText(this,"This email is already present", Toast.LENGTH_SHORT).show()
                }
            }
    }


}
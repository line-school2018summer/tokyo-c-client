package com.tokyoc.line_client

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult

class SigninActivity: AppCompatActivity() {
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(saveInstanceState: Bundle?) {
        super.onCreate(saveInstanceState)
        setContentView(R.layout.activity_signin)

        val addressEditText: EditText = findViewById<EditText>(R.id.address_edit_text)
        val passwordEditText: EditText = findViewById<EditText>(R.id.password_edit_text)

        val signinButton = findViewById<Button>(R.id.signin_button)

        signinButton.setOnClickListener() {
            val address = addressEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (address.length == 27 && password.length == 11) {
                if (password[0] == 't') {
                    signUp(address, password.toLowerCase())
                } else if (password[0] == 'T' && password[1] == 'h') {
                    signIn(address, password.toLowerCase())
                } else {
                    Toast.makeText(applicationContext, "address or password is invalid", Toast.LENGTH_LONG).show()
                }
            } else {
                val intent = Intent(this, MemberActivity::class.java)
                startActivity(intent)
            }


            //val intent = Intent(this, MemberActivity::class.java)
            //startActivity(intent)
        }
    }

    fun signUp(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        val mUser: FirebaseUser? = mAuth.getCurrentUser()
                        mUser?.getIdToken(true)
                                ?.addOnCompleteListener { tokenTask: Task<GetTokenResult> ->
                                    if (tokenTask.isSuccessful) {
                                        val mToken: String? = tokenTask.getResult().token
                                        if (mToken != null) {
                                            Toast.makeText(applicationContext, "signUp succeeded", Toast.LENGTH_LONG).show()
                                            val intent = Intent(this, MemberActivity::class.java)
                                            intent.putExtra("token", mToken)
                                            startActivity(intent)
                                        } else { //token is null
                                            Toast.makeText(applicationContext, "token is null", Toast.LENGTH_LONG).show()
                                        }
                                    } else { //could register, but could not get token
                                        Toast.makeText(applicationContext, "sign up token error: ${tokenTask.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                    } else { //could not register with email and password
                        Toast.makeText(applicationContext, "sign up error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
    }

    fun signIn(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult> ->
            if(task.isSuccessful){
                val mUser: FirebaseUser? = mAuth.getCurrentUser()
                mUser?.getIdToken(true)
                        ?.addOnCompleteListener { tokenTask: Task<GetTokenResult> ->
                            if (tokenTask.isSuccessful) {
                                val mToken: String? = tokenTask.getResult().token
                                if (mToken != null) {
                                    Toast.makeText(applicationContext, "signIn succeeded", Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, MemberActivity::class.java)
                                    intent.putExtra("token", mToken)
                                    startActivity(intent)
                                } else { //token is null
                                    Toast.makeText(applicationContext, "sign in: token is null", Toast.LENGTH_LONG).show()
                                }
                            } else { //could sign in, but could not get token
                                Toast.makeText(applicationContext, "sign in token error: ${tokenTask.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        } //the end of complete listener of token
            }else{ //could not sign in with email and password
                Toast.makeText(applicationContext, "sign in error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        } //the end of complete listener of
    }

}
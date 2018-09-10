package com.tokyoc.line_client

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.storage.FirebaseStorage


class ProfileActivity : AppCompatActivity() {
    val storage: FirebaseStorage = FirebaseStorage.getInstance()

    override fun onCreate(saveInstanceState: Bundle?) {
        super.onCreate(saveInstanceState)
        setContentView(R.layout.activity_profile)

        val token = intent.getStringExtra("token")

        findViewById<Button>(R.id.change_name_button).setOnClickListener() {
            val intent = Intent(this, ChangeNameActivity::class.java)
            intent.putExtra("token", token)
            startActivity(intent)
        }

        findViewById<Button>(R.id.change_image_button).setOnClickListener() {
            val intent = Intent(this, ChangeImageActivity::class.java)
            intent.putExtra("token", token)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.return_button).setOnClickListener() {
            val intent = Intent(this, MemberActivity::class.java)
            intent.putExtra("token", token)
            startActivity(intent)
        }
    }
}
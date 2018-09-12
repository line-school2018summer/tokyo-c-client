package com.tokyoc.line_client

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.storage.FirebaseStorage
import io.realm.Realm
import io.realm.kotlin.where


class ChangeNameActivity : AppCompatActivity() {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    private lateinit var realm: Realm

    override fun onCreate(saveInstanceState: Bundle?) {
        super.onCreate(saveInstanceState)
        setContentView(R.layout.activity_name_change)

        val token = intent.getStringExtra("token")

        val nameEditText = findViewById<EditText>(R.id.new_name_edit_text)

        realm = Realm.getDefaultInstance()
        val self = realm.where<Member>().equalTo("isFriend", Relation.SELF).findFirst()
        findViewById<TextView>(R.id.name_view).text = self?.name ?: "取得失敗"

        findViewById<Button>(R.id.decide_button).setOnClickListener() {
            val newName = nameEditText.text.toString()
            if (newName.isEmpty()) {
                return@setOnClickListener
            }

            val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build()
            firebaseUser?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener {
                        Log.d("COMM", "update success")
                        self?.name = newName
                        val intent = Intent(this, ProfileActivity::class.java)
                        intent.putExtra("token", token)
                        startActivity(intent)
                    }
                    ?.addOnFailureListener {
                        Log.d("COMM", "update failure: ${it.message}")
                    }
        }


        findViewById<TextView>(R.id.return_button).setOnClickListener() {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("token", token)
            startActivity(intent)
        }
    }
}
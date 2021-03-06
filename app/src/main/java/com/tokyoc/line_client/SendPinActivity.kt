package com.tokyoc.line_client

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.*
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SendPinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_send)

        val token = intent.getStringExtra("token")

        val toolbar = supportActionBar!!
        toolbar.setDisplayHomeAsUpEnabled(true)

        val client = Client.build(token)

        val pinEditText: EditText = findViewById<EditText>(R.id.pin_edit_text)

        findViewById<ImageButton>(R.id.send_pin_button).setOnClickListener {
            val pin = pinEditText.text.toString()
            if (pin.length == 8) {
                client.sendPIN(pin.toInt())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.d("COMM", "post done: ${it.length}")
                            Toast.makeText(applicationContext, "PIN was accepted. Wait for your partner confirm it.", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MemberActivity::class.java)
                            intent.putExtra("token", token)
                            startActivity(intent)
                        }, {
                            Log.d("COMM", "post failed: ${it}")
                        })
            } else {
                Toast.makeText(applicationContext, "PIN length must be 8", Toast.LENGTH_LONG).show()
            }
        }

        findViewById<TextView>(R.id.to_make_pin).setOnClickListener {
            val intent = Intent(this, MakePinActivity::class.java)
            intent.putExtra("token", token)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val token = intent.getStringExtra("token")
        when (item?.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MemberActivity::class.java)
                intent.putExtra("token", token)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
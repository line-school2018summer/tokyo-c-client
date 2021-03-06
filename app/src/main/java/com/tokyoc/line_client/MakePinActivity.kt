package com.tokyoc.line_client

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import com.trello.rxlifecycle.kotlin.bindToLifecycle
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException

class MakePinActivity : RxAppCompatActivity() {
    private lateinit var realm: Realm

    override fun onCreate(saveInstanceState: Bundle?) {
        super.onCreate(saveInstanceState)
        setContentView(R.layout.activity_pin_make)

        realm = Realm.getDefaultInstance()

        val pin_show = findViewById<TextView>(R.id.pin)
        val token = intent.getStringExtra("token")

        val toolbar = supportActionBar!!
        toolbar.setDisplayHomeAsUpEnabled(true)

        val client = Client.build(token)

        client.getPIN()
                .onBackpressureBuffer()
                .flatMap {
                    val source = it.source()

                    rx.Observable.create(rx.Observable.OnSubscribe<PinEvent> {
                        try {
                            while (!source.exhausted()) {
                                it.onNext(Client.gson.fromJson<PinEvent>(source.readUtf8Line(), PinEvent::class.java))
                            }

                            it.onCompleted()
                        } catch (e: IOException) {
                            it.onError(e)
                        }
                    })
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .bindToLifecycle(this)
                .subscribe(
                        {
                            if (it.type == "pin") {
                                pin_show.text = it.pin.toString()
                            } else if (it.type == "request") {
                                val uid: String = it.person
                                Member.lookup(uid, client)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            val realm = Realm.getDefaultInstance()
                                            val member: Member = it
                                            Log.d("COMM", "get person done: ${member.name}")
                                            AlertDialog.Builder(this).apply {
                                                setTitle("Friend Request")
                                                setMessage("Make Friends with ${member.name}?")
                                                setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                                                    client.makeFriends(uid)
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe({
                                                                Log.d("COMM", "make friends succeeded: ${it.size}")
                                                                realm.executeTransaction {
                                                                    member.isFriend = Relation.FRIEND
                                                                    realm.insertOrUpdate(member)
                                                                }
                                                            }, {
                                                                Log.d("COMM", "make friends failed: ${it}")
                                                            })
                                                })
                                                setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
                                                    member.deregister()
                                                })
                                                show()
                                            }
                                        }, {
                                            Log.d("COMM", "get person failed: ${it}")
                                        })
                            }
                        },
                        {
                            Log.d("COMM", "get_pin receive failed: $it")
                            val intent = Intent(this, SendPinActivity::class.java)
                            AlertDialog.Builder(this).apply {
                                setTitle("Error")
                                setMessage("サーバとの通信に失敗しました。\n一旦戻ってから再度PINを発行してください。")
                                setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                                    intent.putExtra("token", token)
                                    startActivity(intent)
                                })
                                setNegativeButton("Cancel", null)
                                show()
                            }
                        })

        findViewById<TextView>(R.id.to_send_pin).setOnClickListener {
            val intent = Intent(this, SendPinActivity::class.java)
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
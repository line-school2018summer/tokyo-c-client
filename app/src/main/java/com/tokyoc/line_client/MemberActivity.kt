package com.tokyoc.line_client

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.content.Intent

class MemberActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TEXTDATA = "com.tokyoc.line_client.TEXTDATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)

        val listAdapter = MemberListAdapter(applicationContext)
        listAdapter.members = listOf(dummyMember("Aさん"),dummyMember("Bさん"),dummyMember("Cさん"), dummyMember("Dさん"), dummyMember("Eさん"))

        val listView: ListView = findViewById(R.id.list_view)
        listView.adapter = listAdapter

        listView.setOnItemClickListener { adapterView, view, position, id ->
            val member = listAdapter.members[position]
            val intent = Intent(this, MessageActivity::class.java)
            intent.putExtra(EXTRA_TEXTDATA,member.name)
            startActivity(intent)
        }
    }
    private fun dummyMember(name: String): Member = Member(name=name)
}

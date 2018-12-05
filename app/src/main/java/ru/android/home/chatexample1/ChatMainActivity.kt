package ru.android.home.chatexample1

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.fasterxml.jackson.databind.JsonNode
import com.scaledrone.lib.*
import java.util.*
import android.view.View
import android.widget.ListView
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper




class ChatMainActivity : AppCompatActivity(), RoomListener {

    private val channelID = "wU6qEw9kSlmslhka"
    private val roomName = "observable-room"
    private var editText: EditText? = null
    private lateinit var scaledrone: Scaledrone

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messagesView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)
        // This is where we write the mesage
        editText = findViewById<EditText>(R.id.editText)
        setupScaledrone()
        messageAdapter = MessageAdapter(this)
        messagesView = findViewById(R.id.messages_view)
        messagesView.adapter = messageAdapter
    }

    private fun setupScaledrone() {
        val data = MemberData(getRandomName(), getRandomColor())

        scaledrone = Scaledrone(channelID, data)
        scaledrone.connect(object : Listener {
            override fun onOpen() {
                println("Scaledrone connection open")
                // Since the MainActivity itself already implement RoomListener we can pass it as a target
                scaledrone.subscribe(roomName, this@ChatMainActivity)
            }

            override fun onOpenFailure(ex: Exception) {
                System.err.println(ex)
            }

            override fun onFailure(ex: Exception) {
                System.err.println(ex)
            }

            override fun onClosed(reason: String) {
                System.err.println(reason)
            }
        })
    }

    // Successfully connected to Scaledrone room
    override fun onOpen(room: Room) {
        println("Conneted to room")
    }

    // Connecting to Scaledrone room failed
    override fun onOpenFailure(room: Room, ex: Exception) {
        System.err.println(ex)
    }

    // Received a message from Scaledrone room
    override fun onMessage(room: Room, json: JsonNode, member: Member) {
        val mapper = ObjectMapper()
        try {
            // member.clientData is a MemberData object, let's parse it as such
            val data = mapper.treeToValue(member.clientData, MemberData::class.java)
            // if the clientID of the message sender is the same as our's it was sent by us
            val belongsToCurrentUser = member.id.equals(scaledrone.clientID)
            // since the message body is a simple string in our case we can use json.asText() to parse it as such
            // if it was instead an object we could use a similar pattern to data parsing
            val message = Message(json.asText(), data, belongsToCurrentUser)
            runOnUiThread {
                messageAdapter.add(message)
                // scroll the ListView to the last added element
                messagesView.setSelection(messagesView.getCount() - 1)
            }
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }

    }


    private fun getRandomName(): String {
        val adjs = arrayOf(
            "autumn",
            "hidden",
            "bitter",
            "misty",
            "silent",
            "empty",
            "dry",
            "dark",
            "summer",
            "icy",
            "delicate",
            "quiet",
            "white",
            "cool",
            "spring",
            "winter",
            "patient",
            "twilight",
            "dawn",
            "crimson",
            "wispy",
            "weathered",
            "blue",
            "billowing",
            "broken",
            "cold",
            "damp",
            "falling",
            "frosty",
            "green",
            "long",
            "late",
            "lingering",
            "bold",
            "little",
            "morning",
            "muddy",
            "old",
            "red",
            "rough",
            "still",
            "small",
            "sparkling",
            "throbbing",
            "shy",
            "wandering",
            "withered",
            "wild",
            "black",
            "young",
            "holy",
            "solitary",
            "fragrant",
            "aged",
            "snowy",
            "proud",
            "floral",
            "restless",
            "divine",
            "polished",
            "ancient",
            "purple",
            "lively",
            "nameless"
        )
        val nouns = arrayOf(
            "waterfall",
            "river",
            "breeze",
            "moon",
            "rain",
            "wind",
            "sea",
            "morning",
            "snow",
            "lake",
            "sunset",
            "pine",
            "shadow",
            "leaf",
            "dawn",
            "glitter",
            "forest",
            "hill",
            "cloud",
            "meadow",
            "sun",
            "glade",
            "bird",
            "brook",
            "butterfly",
            "bush",
            "dew",
            "dust",
            "field",
            "fire",
            "flower",
            "firefly",
            "feather",
            "grass",
            "haze",
            "mountain",
            "night",
            "pond",
            "darkness",
            "snowflake",
            "silence",
            "sound",
            "sky",
            "shape",
            "surf",
            "thunder",
            "violet",
            "water",
            "wildflower",
            "wave",
            "water",
            "resonance",
            "sun",
            "wood",
            "dream",
            "cherry",
            "tree",
            "fog",
            "frost",
            "voice",
            "paper",
            "frog",
            "smoke",
            "star"
        )
        return adjs[Math.floor(Math.random() * adjs.size).toInt()] +
                "_" +
                nouns[Math.floor(Math.random() * nouns.size).toInt()]
    }

    private fun getRandomColor(): String {
        val r = Random()
        val sb = StringBuffer("#")
        while (sb.length < 7) {
            sb.append(Integer.toHexString(r.nextInt()))
        }
        return sb.toString().substring(0, 7)
    }

    ////////////////////
    fun sendMessage(view: View) {
        val message = editText!!.getText().toString()
        if (message.length > 0) {
            scaledrone.publish("observable-room", message)
            editText!!.getText().clear()
        }
    }
}

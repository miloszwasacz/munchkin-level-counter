package com.gmail.miloszwasacz.munchkinlevelcounter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_settings.*

class CreateGameActivity : AppCompatActivity() {
    private var maxLevel = 10
    private var minLevel = 1
    internal lateinit var name: String
    private var gameListSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        maxLevel = resources.getInteger(R.integer.default_rules)
        minLevel = resources.getInteger(R.integer.default_min_level)
        gameListSize = intent.getIntExtra("EXTRA_SIZE", 0)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Nowa Gra"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Zaktualizuj pozycje switch'y
        when (maxLevel) {
            resources.getInteger(R.integer.default_rules) -> {
                switchGamemode.isChecked = false
                switchDungeon.isChecked = false
            }
            resources.getInteger(R.integer.dungeon_rules) -> {
                switchGamemode.isChecked = false
                switchDungeon.isChecked = true
            }
            resources.getInteger(R.integer.epic_rules) -> {
                switchGamemode.isChecked = true
                switchDungeon.isChecked = false
            }
            resources.getInteger(R.integer.epic_dungeon_rules) -> {
                switchGamemode.isChecked = true
                switchDungeon.isChecked = true
            }
        }

        //Ustaw maksymalny poziom gracza
        switchGamemode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (switchDungeon.isChecked)
                    maxLevel = resources.getInteger(R.integer.epic_dungeon_rules)
                else
                    maxLevel = resources.getInteger(R.integer.epic_rules)
            }
            else {
                if (switchDungeon.isChecked)
                    maxLevel = resources.getInteger(R.integer.dungeon_rules)
                else
                    maxLevel = resources.getInteger(R.integer.default_rules)
            }
        }

        //Ustaw maksymalny poziom gracza
        switchDungeon.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (switchGamemode.isChecked)
                    maxLevel = resources.getInteger(R.integer.epic_dungeon_rules)
                else
                    maxLevel = resources.getInteger(R.integer.dungeon_rules)
            } else {
                if (switchGamemode.isChecked)
                    maxLevel = resources.getInteger(R.integer.epic_rules)
                else
                    maxLevel = resources.getInteger(R.integer.default_rules)
            }
        }
    }

    //Tworzenie domyślnej listy graczy
    fun createDefaultPlayerList(): String {
        val playerList = ArrayList<Player>()
        playerList.add(Player("Gracz 1", resources.getInteger(R.integer.default_min_level)))
        playerList.add(Player("Gracz 2", resources.getInteger(R.integer.default_min_level)))
        playerList.add(Player("Gracz 3", resources.getInteger(R.integer.default_min_level)))
        return Gson().toJson(playerList)
    }

    //Tworzenie guzików na app barze
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val mConfirmButton = menu.findItem(R.id.action_confirm)
        mConfirmButton.isVisible = true
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        return super.onCreateOptionsMenu(menu)
    }

    //Guziki na app barze
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        //Potwierdzenie
        R.id.action_confirm -> {
            onBackPressed(Activity.RESULT_OK)
            true
        }
        //Strzałeczka "back"
        android.R.id.home -> {
            onBackPressed(Activity.RESULT_CANCELED)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    //Wyjście z Activity
    fun onBackPressed(result: Int) {
        editTextName.setText(editTextName.text.trim())
        if (editTextName.text.toString() == "")
            editTextName.setText("Gra " + (gameListSize + 1))
        name = editTextName.text.toString()
        val returnIntent = Intent()
        returnIntent.putExtra("resultGame", Gson().toJson(Game(name, createDefaultPlayerList(), maxLevel, minLevel)))
        setResult(result, returnIntent)
        finish()
    }

    //Strać focus podczas przewijania
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if(event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if(editTextName.isFocused) {
                val outRect = Rect()
                editTextName.getGlobalVisibleRect(outRect)
                if(!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    editTextName.clearFocus()
                    //
                    // Hide keyboard
                    //
                    val imm: InputMethodManager = v!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}

package com.gmail.miloszwasacz.munchkinlevelcounter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    internal lateinit var playerList: String
    internal var maxPlayerLevel = 10
    internal var minLevel = 1
    internal var editMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar!!.title = "Ustawienia"
        maxPlayerLevel = resources.getInteger(R.integer.deafult_rules)
        minLevel = resources.getInteger(R.integer.deafult_min_level)

        playerList = intent.getStringExtra("EXTRA_LIST")
        maxPlayerLevel = intent.getIntExtra("EXTRA_MAX_LEVEL", resources.getInteger(R.integer.deafult_rules))
        minLevel = intent.getIntExtra("EXTRA_MIN_LEVEL", resources.getInteger(R.integer.deafult_min_level))
        editMode = intent.getBooleanExtra("EXTRA_EDIT_MODE", true)

        val switchGamemode = findViewById<Switch>(R.id.switchGamemode)
        val switchDungeon = findViewById<Switch>(R.id.switchDungeon)

        //Zaktualizuj pozycje switch'y
        if (maxPlayerLevel == resources.getInteger(R.integer.deafult_rules)) {
            switchGamemode.isChecked = false
            switchDungeon.isChecked = false
        } else if (maxPlayerLevel == resources.getInteger(R.integer.dungeon_rules)) {
            switchGamemode.isChecked = false
            switchDungeon.isChecked = true
        } else if (maxPlayerLevel == resources.getInteger(R.integer.epic_rules)) {
            switchGamemode.isChecked = true
            switchDungeon.isChecked = false
        } else if (maxPlayerLevel == resources.getInteger(R.integer.epic_dungeon_rules)) {
            switchGamemode.isChecked = true
            switchDungeon.isChecked = true
        }

        //Ustaw maksymalny poziom gracza
        switchGamemode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (switchDungeon.isChecked)
                    maxPlayerLevel = resources.getInteger(R.integer.epic_dungeon_rules)
                else
                    maxPlayerLevel = resources.getInteger(R.integer.epic_rules)
            } else {
                if (switchDungeon.isChecked)
                    maxPlayerLevel = resources.getInteger(R.integer.dungeon_rules)
                else
                    maxPlayerLevel = resources.getInteger(R.integer.deafult_rules)
            }
        }
        //Ustaw maksymalny poziom gracza
        switchDungeon.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (switchGamemode.isChecked)
                    maxPlayerLevel = resources.getInteger(R.integer.epic_dungeon_rules)
                else
                    maxPlayerLevel = resources.getInteger(R.integer.dungeon_rules)
            } else {
                if (switchGamemode.isChecked)
                    maxPlayerLevel = resources.getInteger(R.integer.epic_rules)
                else
                    maxPlayerLevel = resources.getInteger(R.integer.deafult_rules)
            }
        }
    }

    //Obsługa strzałeczki w tył
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        } else
            return super.onOptionsItemSelected(item)
    }

    //Wyjście z Activity
    override fun onBackPressed() {
        val returnIntent = Intent()
        returnIntent.putExtra("resultList", playerList)
        returnIntent.putExtra("resultMaxLevel", maxPlayerLevel)
        returnIntent.putExtra("resultMinLevel", minLevel)
        returnIntent.putExtra("resultEditMode", editMode)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}

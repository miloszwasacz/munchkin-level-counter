package com.gmail.miloszwasacz.munchkinlevelcounter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    internal var maxLevel = 10
    internal var minLevel = 1
    internal lateinit var name: String
    internal var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        maxLevel = resources.getInteger(R.integer.default_rules)
        minLevel = resources.getInteger(R.integer.default_min_level)

        //Weź wartości z poprzedniego activity
        maxLevel = intent.getIntExtra("EXTRA_MAX_LEVEL", resources.getInteger(R.integer.default_rules))
        minLevel = intent.getIntExtra("EXTRA_MIN_LEVEL", resources.getInteger(R.integer.default_min_level))
        name = intent.getStringExtra("EXTRA_GAME_NAME")
        position = intent.getIntExtra("EXTRA_POSITION", 0)


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Ustawienia - $name"
        editTextName.setText(name)

        //Zaktualizuj pozycje switch'y
        if (maxLevel == resources.getInteger(R.integer.default_rules)) {
            switchGamemode.isChecked = false
            switchDungeon.isChecked = false
        } else if (maxLevel == resources.getInteger(R.integer.dungeon_rules)) {
            switchGamemode.isChecked = false
            switchDungeon.isChecked = true
        } else if (maxLevel == resources.getInteger(R.integer.epic_rules)) {
            switchGamemode.isChecked = true
            switchDungeon.isChecked = false
        } else if (maxLevel == resources.getInteger(R.integer.epic_dungeon_rules)) {
            switchGamemode.isChecked = true
            switchDungeon.isChecked = true
        }

        //Ustaw maksymalny poziom gracza
        switchGamemode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (switchDungeon.isChecked)
                    maxLevel = resources.getInteger(R.integer.epic_dungeon_rules)
                else
                    maxLevel = resources.getInteger(R.integer.epic_rules)
            } else {
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

    //Obsługa strzałeczki w tył
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            if (item.itemId == android.R.id.home) {
                onBackPressed()
                true
            }
            else
                super.onOptionsItemSelected(item)

    //Wyjście z Activity
    override fun onBackPressed() {
        if (editTextName.text.toString() != "")
            name = editTextName.text.toString()
        val returnIntent = Intent()
        returnIntent.putExtra("resultMaxLevel", maxLevel)
        returnIntent.putExtra("resultMinLevel", minLevel)
        returnIntent.putExtra("resultName", name)
        returnIntent.putExtra("resultPosition", position)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}

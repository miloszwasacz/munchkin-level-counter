package com.gmail.miloszwasacz.munchkinlevelcounter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    private var maxLevel = 10
    private var minLevel = 1
    internal lateinit var name: String
    private var position = 0

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

    //Strzałeczka "back"
    override fun onOptionsItemSelected(item: MenuItem): Boolean = if (item.itemId == android.R.id.home) {
                onBackPressed()
                true
            } else super.onOptionsItemSelected(item)

    //Wyjście z Activity
    override fun onBackPressed() {
        editTextName.setText(editTextName.text.trim())
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

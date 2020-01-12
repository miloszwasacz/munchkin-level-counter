package com.gmail.miloszwasacz.munchkinlevelcounter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.player_dialog.*
import java.util.*

class MainActivity : AppCompatActivity() {

    internal lateinit var adapter: PlayerAdapter
    internal var list = ArrayList<Player>()
    internal var sharedPrefsName = "com.gmail.miloszwasacz.munchkinlevelcounter.prefs"
    internal var maxPlayerLevel: Int = R.integer.deafult_rules
    internal var minLevel: Int = R.integer.deafult_min_level
    internal var gameList: MutableList<Game>? = null
    internal var editMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.title = "Licznik"

        //list = ArrayList()

        //Tworzenie domyślnej listy graczy
        if (savedInstanceState == null)
            createDefaultGame(list)
        //Przywracanie stanu poprzedniego listy graczy
        else {
            maxPlayerLevel = savedInstanceState.getInt("MaksymalnyPoziom", resources.getInteger(R.integer.deafult_rules))
            minLevel = savedInstanceState.getInt("MinimalnyPoziom", resources.getInteger(R.integer.deafult_min_level))
            val json = savedInstanceState.getString("ListaGraczy")
            val listType = object : TypeToken<ArrayList<Player>>() {}.type
            list = Gson().fromJson<ArrayList<Player>>(json, listType)
        }

        setPlayerAdapter()

        //Zmiana trybu guzika: edycja/dodawanie graczy
        floatingActionButton.setOnClickListener {
            if (adapter.editMode) {
                list.add(Player("Nowy gracz", minLevel))
                adapter.notifyItemInserted(list.size - 1)
            } else
                changeEditMode(R.drawable.ic_baseline_add_white_24dp, true, "Edytuj graczy")
        }
    }

    //RecyclerView i PlayerAdapter
    fun setPlayerAdapter() {
        //val recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = PlayerAdapter(list)
        adapter.editMode = editMode

        //Sprawdzenie czy poziomy graczy są w dozwolonym zakresie
        for (element in list) {
            if (element.level > maxPlayerLevel)
                element.level = maxPlayerLevel
            else if (element.level < minLevel)
                element.level = minLevel
        }

        adapter.setOnItemClickListener(object : PlayerAdapter.OnItemClickListener {
            //Edycja poszczególnego gracza
            override fun onItemClick(position: Int) {
                val frameLayout = layoutInflater.inflate(R.layout.player_dialog, null, false) as FrameLayout
                //val editText = frameLayout.findViewById<View>(R.id.editText) as EditText
                editText.setText(list[position].name)

                AlertDialog.Builder(this@MainActivity)
                        .setTitle("Edytuj gracza")
                        .setPositiveButton("Ok") { dialog, which ->
                            list[position].name = editText.text.toString()
                            adapter.notifyItemChanged(position)
                        }
                        .setNegativeButton("Anuluj", null)
                        .setNeutralButton("Usuń") { dialog, which ->
                            list.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                        .setView(frameLayout)
                        .create()
                        .show()
            }

            //Zwiększenie poziomu gracza
            override fun onAddClick(position: Int) {
                if (list[position].level < maxPlayerLevel) {
                    list[position].level++
                    adapter.notifyItemChanged(position)
                }
            }

            //Zmniejszenie poziomu gracza
            override fun onRemoveClick(position: Int) {
                if (list[position].level > minLevel) {
                    list[position].level--
                    adapter.notifyItemChanged(position)
                }
            }

            //Wejście w tryb Kill-O-Meter
            override fun onFightClick(position: Int) {
                val intent = Intent(this@MainActivity, KillOMeterActivity::class.java)
                val playerLevel = list[position].level
                val playerName = list[position].name
                intent.putExtra("EXTRA_LEVEL", playerLevel)
                intent.putExtra("EXTRA_NAME", playerName)
                intent.putExtra("EXTRA_POSITION", position)
                intent.putExtra("EXTRA_LIST", serializePlayerList(list))
                intent.putExtra("EXTRA_MAX_LEVEL", maxPlayerLevel)
                intent.putExtra("EXTRA_MIN_LEVEL", minLevel)
                startActivityForResult(intent, 1)
            }
        })
        recycler_view.adapter = adapter
    }

    //Włączanie/wyłączanie trybu edycji
    fun changeEditMode(icon: Int, enabled: Boolean?, title: String) {
        supportActionBar!!.title = title
        floatingActionButton.setImageResource(icon)
        editMode = enabled!!
        adapter.editMode = enabled
        invalidateOptionsMenu()
        adapter.notifyDataSetChanged()
    }

    //Strzałeczka w tył (tryb edycji)
    override fun onBackPressed() {
        if (adapter.editMode)
            changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik")
        else
            super.onBackPressed()
    }

    //Tworzenie guzików na app barze
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val mSaveButton = menu.findItem(R.id.action_save)
        val mLoadButton = menu.findItem(R.id.action_folder)
        val mClearButton = menu.findItem(R.id.action_clear)
        val mSettingsButton = menu.findItem(R.id.navigation_settings)
        if (adapter.editMode) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            mSaveButton.isVisible = false
            mLoadButton.isVisible = false
            mClearButton.isVisible = false
            mSettingsButton.isVisible = true
        } else {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            mSaveButton.isVisible = true
            mLoadButton.isVisible = true
            mClearButton.isVisible = true
            mSettingsButton.isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }

    //Obsługa guzików na app barze
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val prefs = getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        val listaGier = prefs.getString("ListaGierPrefs", null)
        val listType = object : TypeToken<ArrayList<Game>>() {}.type
        gameList = Gson().fromJson<MutableList<Game>>(listaGier, listType)
        if (gameList!!.isEmpty())
            gameList = null

        when (item.itemId) {
            //Zapisanie rozgrywki
            R.id.action_save -> {
                val frameLayout = layoutInflater.inflate(R.layout.player_dialog, null, false) as FrameLayout
                val editText = frameLayout.findViewById<View>(R.id.editText) as EditText

                AlertDialog.Builder(this@MainActivity)
                        .setTitle("Zapisz rozgrywkę")
                        .setPositiveButton("Ok") { dialog, which ->
                            if (gameList == null)
                                gameList = ArrayList()

                            if (editText.text.toString() == "")
                                editText.setText("Gra " + (gameList!!.size + 1))

                            gameList!!.add(Game(editText.text.toString(), serializePlayerList(list), maxPlayerLevel, minLevel))
                            saveGameListInSharedPreferences(gameList)

                            changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik")
                            Toast.makeText(this@MainActivity, "Zapisano rozgrywkę", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Anuluj", null)
                        .setView(frameLayout)
                        .create()
                        .show()

                return true
            }

            //Wczytanie ostatniej rozgrywki
            R.id.action_folder -> {
                if (gameList == null)
                    gameList = ArrayList()
                val lista = ArrayList<Player>()
                createDefaultGame(lista)
                gameList!!.add(0, Game("default", serializePlayerList(lista), maxPlayerLevel, minLevel))

                val nameArray = arrayOfNulls<String>(gameList!!.size)
                for (i in gameList!!.indices) {
                    nameArray[i] = gameList!![i].name
                }

                AlertDialog.Builder(this@MainActivity)
                        .setTitle("Wczytaj rozgrywkę")
                        .setItems(nameArray) { dialog, which ->
                            maxPlayerLevel = gameList!![which].maxLevel
                            minLevel = gameList!![which].minLevel
                            val jsonGame = gameList!![which].content
                            val listType = object : TypeToken<ArrayList<Player>>() {}.type
                            list = Gson().fromJson<ArrayList<Player>>(jsonGame, listType)
                            setPlayerAdapter()
                            changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik")

                            Toast.makeText(this@MainActivity, "Wczytano rozgrywkę", Toast.LENGTH_SHORT).show()
                        }
                        .create()
                        .show()

                return true
            }

            //Usunięcie gry z listy
            R.id.action_clear -> {
                if (gameList == null)
                    Toast.makeText(this@MainActivity, "Brak zapisanych rozgrywek", Toast.LENGTH_SHORT).show()
                else {
                    val nameArrayDelete = arrayOfNulls<String>(gameList!!.size)

                    for (i in gameList!!.indices) {
                        nameArrayDelete[i] = gameList!![i].name
                    }

                    AlertDialog.Builder(this@MainActivity)
                            .setTitle("Usuń rozgrywkę")
                            .setItems(nameArrayDelete) { dialog, which ->
                                gameList!!.removeAt(which)
                                saveGameListInSharedPreferences(gameList)

                                changeEditMode(R.drawable.ic_baseline_edit_white_24dp, false, "Licznik")
                                Toast.makeText(this@MainActivity, "Usunięto rozgrywkę", Toast.LENGTH_SHORT).show()
                            }
                            .create()
                            .show()
                }
                return true
            }

            //Otwarcie ustawień
            R.id.navigation_settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                intent.putExtra("EXTRA_LIST", serializePlayerList(list))
                intent.putExtra("EXTRA_MAX_LEVEL", maxPlayerLevel)
                intent.putExtra("EXTRA_MIN_LEVEL", minLevel)
                intent.putExtra("EXTRA_EDIT_MODE", adapter.editMode)
                startActivityForResult(intent, 2)
                return true
            }

            //Strzałeczka "back"
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    //Zapisywanie stanu listy graczy
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        val json = Gson().toJson(list)

        savedInstanceState.putString("ListaGraczy", json)
        savedInstanceState.putInt("MaksymalnyPoziom", maxPlayerLevel)
        savedInstanceState.putInt("MinimalnyPoziom", minLevel)
    }

    //Odebranie danych z innych Activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Aktualizacja poziomu z Kill-O-Meter'a
        if (requestCode == 1) {
            maxPlayerLevel = data!!.getIntExtra("resultMaxLevel", resources.getInteger(R.integer.deafult_rules))
            minLevel = data.getIntExtra("resultMinLevel", resources.getInteger(R.integer.deafult_min_level))
            val resultPosition = data.getIntExtra("resultPosition", 0)
            val resultLevel = data.getIntExtra("resultLevel", minLevel)
            val json = data.getStringExtra("resultList")
            val listType = object : TypeToken<ArrayList<Player>>() {}.type
            list = Gson().fromJson<ArrayList<Player>>(json, listType)
            if (resultCode == Activity.RESULT_OK) {
                list[resultPosition].level = resultLevel
                setPlayerAdapter()
            }
        }
        //Sprawdzenie poziomów po aktualizacji maksymalnego poziomu
        else if (requestCode == 2) {
            editMode = data!!.getBooleanExtra("resultEditMode", true)
            maxPlayerLevel = data.getIntExtra("resultMaxLevel", resources.getInteger(R.integer.deafult_rules))
            minLevel = data.getIntExtra("resultMinLevel", resources.getInteger(R.integer.deafult_min_level))
            val json = data.getStringExtra("resultList")
            val listType = object : TypeToken<ArrayList<Player>>() {}.type
            list = Gson().fromJson<ArrayList<Player>>(json, listType)
            if (resultCode == Activity.RESULT_OK)
                setPlayerAdapter()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //Zapisywanie listy gier do SharedPreferences
    fun saveGameListInSharedPreferences(list: List<Game>?) {
        //val gsonGame = Gson()
        val jsonGame = Gson().toJson(list)
        val editor = getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE).edit()
        editor.putString("ListaGierPrefs", jsonGame)
        editor.commit()
    }

    //Zserializuj listę graczy do jsona
    fun serializePlayerList(list: List<Player>) = Gson().toJson(list)

    //Stwórz domyślną grę
    fun createDefaultGame(list: MutableList<Player>) {
        list += Player("Gracz 1", minLevel)
        list += Player("Gracz 2", minLevel)
        list += Player("Gracz 3", minLevel)

        maxPlayerLevel = resources.getInteger(R.integer.deafult_rules)
        minLevel = resources.getInteger(R.integer.deafult_min_level)
    }
}

package com.gmail.miloszwasacz.munchkinlevelcounter

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import com.google.gson.Gson
import java.util.ArrayList
import java.lang.reflect.Type
import com.google.gson.reflect.TypeToken
import android.content.SharedPreferences
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    internal var floatingActionButton: FloatingActionButton
    internal var adapter: PlayerAdapter
    internal var list: MutableList<Player>
    internal var SharedPrefs: String? = null
    internal var MaxPlayerLevel: Int = 0
    internal var MinLevel: Int = 0
    internal var gameList: MutableList<Game>? = null
    internal var editMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setTitle("Licznik")

        list = ArrayList()

        //Tworzenie domyślnej listy graczy
        if (savedInstanceState == null)
            createDefaultGame(list)
        else {
            MaxPlayerLevel = savedInstanceState.getInt("MaksymalnyPoziom", resources.getInteger(R.integer.deafult_rules))
            MinLevel = savedInstanceState.getInt("MinimalnyPoziom", resources.getInteger(R.integer.deafult_min_level))
            val json = savedInstanceState.getString("ListaGraczy")
            val listType = object : TypeToken<ArrayList<Player>>() {

            }.type
            list = Gson().fromJson<List<Player>>(json, listType)
        }//Przywracanie stanu poprzedniego listy graczy

        setPlayerAdapter()

        //Zmiana trybu guzika: edycja/dodawanie graczy
        floatingActionButton = findViewById<View>(R.id.floatingActionButton) as FloatingActionButton
        floatingActionButton.setOnClickListener {
            if (adapter.editMode) {
                list.add(Player("Nowy gracz", MinLevel))
                adapter.notifyItemInserted(list.size - 1)
            } else
                changeEditMode(R.drawable.ic_baseline_add_white_24dp, true, "Edytuj graczy")
        }
    }

    //RecyclerView i PlayerAdapter
    fun setPlayerAdapter() {
        val recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PlayerAdapter(list)
        adapter.editMode = editMode

        //Sprawdzenie czy poziomy graczy są w dozwolonym zakresie
        for (element in list) {
            if (element.level > MaxPlayerLevel)
                element.level = MaxPlayerLevel
            else if (element.level < MinLevel)
                element.level = MinLevel
        }

        adapter.setOnItemClickListener(object : PlayerAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val frameLayout = layoutInflater.inflate(R.layout.player_dialog, null, false) as FrameLayout
                val editText = frameLayout.findViewById<View>(R.id.editText) as EditText
                editText.setText(list[position].name)

                //Okienko do edycji poszczególnego gracza
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
                if (list[position].level < MaxPlayerLevel) {
                    list[position].level++
                    adapter.notifyItemChanged(position)
                }
            }

            //Zmniejszenie poziomu gracza
            override fun onRemoveClick(position: Int) {
                if (list[position].level > MinLevel) {
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
                intent.putExtra("EXTRA_MAX_LEVEL", MaxPlayerLevel)
                intent.putExtra("EXTRA_MIN_LEVEL", MinLevel)
                startActivityForResult(intent, 1)
            }
        })
        recyclerView.adapter = adapter
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
        val prefs = getSharedPreferences(SharedPrefs, Context.MODE_PRIVATE)
        val listaGier = prefs.getString("ListaGierPrefs", null)
        val listType = object : TypeToken<ArrayList<Game>>() {

        }.type
        gameList = Gson().fromJson<List<Game>>(listaGier, listType)
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

                            gameList!!.add(Game(editText.text.toString(), serializePlayerList(list), MaxPlayerLevel, MinLevel))
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
                gameList!!.add(0, Game("default", serializePlayerList(lista), MaxPlayerLevel, MinLevel))

                val nameArray = arrayOfNulls<String>(gameList!!.size)
                for (i in gameList!!.indices) {
                    nameArray[i] = gameList!![i].name
                }

                AlertDialog.Builder(this@MainActivity)
                        .setTitle("Wczytaj rozgrywkę")
                        .setItems(nameArray) { dialog, which ->
                            MaxPlayerLevel = gameList!![which].maxLevel
                            MinLevel = gameList!![which].minLevel
                            val jsonGame = gameList!![which].content
                            val listType = object : TypeToken<ArrayList<Player>>() {

                            }.type
                            list = Gson().fromJson<List<Player>>(jsonGame, listType)
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
                    Toast.makeText(this@MainActivity, "Brak zapisanych rozgrywek", Toast.LENGTH_LONG).show()
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
                intent.putExtra("EXTRA_MAX_LEVEL", MaxPlayerLevel)
                intent.putExtra("EXTRA_MIN_LEVEL", MinLevel)
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
        val gson = Gson()
        val json = gson.toJson(list)

        savedInstanceState.putString("ListaGraczy", json)
        savedInstanceState.putInt("MaksymalnyPoziom", MaxPlayerLevel)
        savedInstanceState.putInt("MinimalnyPoziom", MinLevel)
    }

    //Odebranie danych z innych Activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Aktualizacja poziomu z Kill-O-Meter'a
        if (requestCode == 1) {
            MaxPlayerLevel = data!!.getIntExtra("resultMaxLevel", resources.getInteger(R.integer.deafult_rules))
            MinLevel = data.getIntExtra("resultMinLevel", resources.getInteger(R.integer.deafult_min_level))
            val resultPosition = data.getIntExtra("resultPosition", 0)
            val resultLevel = data.getIntExtra("resultLevel", MinLevel)
            val json = data.getStringExtra("resultList")
            val listType = object : TypeToken<ArrayList<Player>>() {

            }.type
            list = Gson().fromJson<List<Player>>(json, listType)
            if (resultCode == Activity.RESULT_OK) {
                list[resultPosition].level = resultLevel
                setPlayerAdapter()
            }
        } else if (requestCode == 2) {
            editMode = data!!.getBooleanExtra("resultEditMode", true)
            MaxPlayerLevel = data.getIntExtra("resultMaxLevel", resources.getInteger(R.integer.deafult_rules))
            MinLevel = data.getIntExtra("resultMinLevel", resources.getInteger(R.integer.deafult_min_level))
            val json = data.getStringExtra("resultList")
            val listType = object : TypeToken<ArrayList<Player>>() {

            }.type
            list = Gson().fromJson<List<Player>>(json, listType)
            if (resultCode == Activity.RESULT_OK)
                setPlayerAdapter()
        }//Sprawdzenie poziomów po aktualizacji maksymalnego poziomu
        super.onActivityResult(requestCode, resultCode, data)
    }

    //Zapisywanie listy gier do SharedPreferences
    fun saveGameListInSharedPreferences(list: List<Game>?) {
        val gsonGame = Gson()
        val jsonGame = gsonGame.toJson(list)
        val editor = getSharedPreferences(SharedPrefs, Context.MODE_PRIVATE).edit()
        editor.putString("ListaGierPrefs", jsonGame)
        editor.commit()
    }

    //Zserializuj listę graczy do jsona
    fun serializePlayerList(list: List<Player>): String {
        val gsonList = Gson()
        return gsonList.toJson(list)
    }

    //Stwórz domyślną grę
    fun createDefaultGame(list: MutableList<Player>) {
        list.add(Player("Gracz 1", MinLevel))
        list.add(Player("Gracz 2", MinLevel))
        list.add(Player("Gracz 3", MinLevel))

        MaxPlayerLevel = resources.getInteger(R.integer.deafult_rules)
        MinLevel = resources.getInteger(R.integer.deafult_min_level)
    }
}

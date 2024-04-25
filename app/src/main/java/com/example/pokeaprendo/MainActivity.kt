package com.example.pokeaprendo

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View.INVISIBLE
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.pokeaprendo.api.Pokemon
import com.example.pokeaprendo.api.PokemonData
import com.example.pokeaprendo.api.Recorrible
import com.example.pokeaprendo.api.RetrofitClient
import com.example.pokeaprendo.databinding.ActivityMainBinding
import com.example.pokeaprendo.shared_preferences.PokeAprendoApplication
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.coroutineContext


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var binding: ActivityMainBinding
    val fragmetCapturar = CapturarFragment({name:String ->
        with(CoroutineScope(coroutineContext)){
            makeRequestPokemon(name)
        }
    },{lockMode-> setDrawerLayoutLockMode(lockMode)})

    val fragmetVerCapturados = VerCapturados({name:String -> showInfoPokemonFragment(name)},{lockMode-> setDrawerLayoutLockMode(lockMode)})
    val fragmentInfoPokemon = InfoPokemonFragment { name: String ->
        with(CoroutineScope(coroutineContext)) {
            makeRequestPokemon(name)
        }
    }

    val fragmentAjustes = FragmentAjustes(){modeCode, statusDarkMode ->changeTheme(modeCode,statusDarkMode)}



    companion object{

        fun crearColeccion(listaDeObjetos: List<Recorrible>): MutableList<String> {
            var coleccion: MutableList<String> = mutableListOf()
            if(!listaDeObjetos.isNullOrEmpty()) {
                listaDeObjetos.forEach() {
                    var valor = it.getCadenaDeObj()
                    coleccion.add(valor)
                }
            }

            return coleccion
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initMenu()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

        Log.i("RESPPT","NO HACER NADA")

    }

    suspend fun makeRequestPokemon(namePokemon: String){
        Log.i("POKINFO", "makeRequestPokemon")

        val responsePokemon = RetrofitClient.consumirApi.getPokemon(namePokemon)

        if (responsePokemon.isSuccessful) {
            val pokemonData: PokemonData = responsePokemon.body()!!
            val idPokemon = pokemonData.idPokemon

            val specieResponse = RetrofitClient.consumirApi.getSpecies(idPokemon)
            if(specieResponse.isSuccessful)
            {
                pokemonData.setSpecies(specieResponse.body())
                var idEvolutionChain = pokemonData.getIdEvolutionChain()

                val evolutionChainResponse = RetrofitClient.consumirApi.getEvolutionChain(idEvolutionChain)
                if(evolutionChainResponse.isSuccessful)
                {
                    pokemonData.setEnvolveChai(evolutionChainResponse.body())
                    convertDataPokemon(pokemonData)
                }
            }

        } else {
            Log.i("POKINFO", "Mo se ejecuto :(")

        }
    }

    private suspend fun convertDataPokemon(pokemonData: PokemonData) {

        val idPokemon = pokemonData.idPokemon
        val nombre = pokemonData.nombre
        val types = crearColeccion(pokemonData.types)
        val habitat = pokemonData.getHabitat()
        val generation = pokemonData.getGeneration()
        val urlIconPokeon = pokemonData.getUrlIconPokemon()


        var evolutions = extractEvolutions(pokemonData)

        val sourcePokemon = Pokemon(
            idPokemon,
            nombre,
            habitat,
            generation,
            types,
            evolutions,
            urlIconPokeon)

        addEnglishPokemon(sourcePokemon)

        Log.i("RESPPT","Pokemon SIN TRAD: $sourcePokemon")


        var targetPokemon:Pokemon = sourcePokemon


        targetPokemon = TranslatorDataPokemon.validateTranslatePokemonObject(sourcePokemon)


        Log.i("RESPPT","TARGET POKEMON: $targetPokemon")
        addPokemon(targetPokemon)
    }

    private fun addEnglishPokemon(pokemon: Pokemon){
        PokemonList.addEnglishPokemon(pokemon)
    }


    private fun addPokemon(pokemon: Pokemon) {

        PokemonList.addPokemon(pokemon)
    }

    private fun extractEvolutions(pokemonData: PokemonData): MutableList<String>{
        var evolutions = mutableListOf<String>()
        evolutions.add(pokemonData.envolveChain?.getBaseForm()?:"")
        //var evolutions =
        pokemonData.envolveChain?.getListChain()?.forEach{
            evolutions.add(it.getSecondForm())

            it.getListEnolveTo().forEach {
                evolutions.add(it.getFinalForm())
            }
        }
        return evolutions
    }

    private fun initMenu() {
        //Se especifica cual sera la action bar de la activity
        setSupportActionBar(binding.toolbar);

        /*Este objeto hace que se pueda sacar el "NavigationView" haciendo click en un
        boton ubicado en la parte izquierda del "action bar"

        Nota:Si no se hiciera esto no habria boton y unica forma de sacar
        el menu seria deslizando el dedo desde el lado izquierdo como si jalaramos*/
        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //Metodo para contraer el "NavigationView" con el boton de android "hacia atras"
        pressBackButton()
        binding.navigationView.setNavigationItemSelectedListener(this)

        //Se selecciona por defecto el primer item del menu
        val menuItem: MenuItem = binding.navigationView.menu.getItem(0)
        onNavigationItemSelected(menuItem)
        menuItem.isChecked = true
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmenManager = supportFragmentManager
        val fragmentTransaction = fragmenManager.beginTransaction()

        fragmentTransaction.replace(R.id.frModulos, fragment)
        fragmentTransaction.addToBackStack("")

        fragmentTransaction.commit()

        isMenuOpen()
    }

    private fun pressBackButton() {

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (!isMenuOpen()) {
                    /*Si el "NavigationView" no esta desplegado se aplica el comportamiento por defecto
                    del boton hacia atras(navegar hacia atras en la pila de actividades)*/
                    this.isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    this.isEnabled = true
                }
            }
        })
    }

    private fun isMenuOpen(): Boolean {
        var isOpen = false
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            isOpen = true
        }
        return isOpen
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.iCapturar -> {
                Log.i("RESPPT","CLICK EN CAPTURAR")

                replaceFragment(fragmetCapturar)
                setDrawerLayoutLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            R.id.iCapturados -> replaceFragment(fragmetVerCapturados)
            R.id.iAjustes -> replaceFragment(fragmentAjustes)
            R.id.iSalir ->{
                UserFirebase.signOut(this)
                //finish()

                var intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
            }



        }

        return true
    }


    private fun showInfoPokemonFragment(pokemonName:String) {
        putNamePokemonOnFragment(pokemonName)
        replaceFragment(fragmentInfoPokemon)
        setDrawerLayoutLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun setDrawerLayoutLockMode(lockMode:Int)
    {
        binding.drawerLayout.setDrawerLockMode(lockMode);

    }

    private fun putNamePokemonOnFragment(pokemonName:String) {
        fragmentInfoPokemon.setPokemonName(pokemonName)
    }

    private fun changeTheme(modeCode:Int,stateDarkMode:Boolean)
    {
        Log.i("RESPP","changeTheme")
        PokeAprendoApplication.pref.saveDarkModeCode(modeCode)

        delegate.applyDayNight()
        val uiManager = this.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        uiManager.setApplicationNightMode(modeCode)
        recreate()
    }


}
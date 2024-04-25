package com.example.pokeaprendo

import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pokeaprendo.CapturarFragment.Companion.GENERATION
import com.example.pokeaprendo.CapturarFragment.Companion.HABITAT
import com.example.pokeaprendo.CapturarFragment.Companion.NAME_POKEMONS
import com.example.pokeaprendo.CapturarFragment.Companion.NAME_POKEMON_COLLECTION
import com.example.pokeaprendo.CapturarFragment.Companion.TYPES
import com.example.pokeaprendo.api.ListaDeObjetos
import com.example.pokeaprendo.api.PokemonFromFireStore
import com.example.pokeaprendo.api.RetrofitClient
import com.example.pokeaprendo.databinding.FragmentVerCapturadosBinding
import com.example.pokeaprendo.recyclerview_classes.AdapterRvPokemons
import com.example.pokeaprendo.shared_preferences.PokeAprendoApplication
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VerCapturados(var showInfoPokemonFragment: (String) -> Unit, var setDrawerLayoutLockMode: (Int) ->Unit) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var rvAdapter: AdapterRvPokemons
    private lateinit var fireStoreIntance: FirebaseFirestore
    private var allPokemonsFromFireStore = mutableListOf<PokemonFromFireStore>()
    var arrayTypes = mutableListOf<String>()

    private var _binding:FragmentVerCapturadosBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fireStoreIntance = FirebaseFirestore.getInstance()

        // Inflate the layout for this fragment
        _binding = FragmentVerCapturadosBinding.inflate(inflater, container, false)
        return binding.root

    }


    private fun setStringCargaText(idString:Int) {

        activity?.runOnUiThread(){
            binding.tvCargando.text = resources.getString(idString)

        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDrawerLayoutLockMode(DrawerLayout.LOCK_MODE_UNDEFINED)
        //binding.pbCargaMyPokemons.visibility = VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {

                setStringCargaText(R.string.download_resources)

                checkLanguage()
                checkEnumExistenceOfData()
                translateEnumData()

                Log.i("RESPPT", "DATA OF ENUM")
                EnumDataPokemon.enumReference.showMaps()

                setStringCargaText(R.string.looking_pokemons)

                getPokemonsFromFireStore()
                translatePokemons()

                Log.i("RESPPT","POKEMONS ${allPokemonsFromFireStore}")
            }
            catch (e:Exception){
                Log.i("RESPPT", "${e.message}")

                activity?.runOnUiThread()
                {
                    Toast.makeText(context,resources.getString(R.string.no_conection),Toast.LENGTH_LONG).show()

                }
                this.cancel()
            }
        }.invokeOnCompletion {
            Log.i("RESPPT", "invokeOnCompletion")
            Log.i("RESPPT", "${allPokemonsFromFireStore}")

                activity?.runOnUiThread()
                {
                    enableDropDown()
                    initDropDownList()
                    initRecyclerView()
                    setVisibilityViews()
                    setPokemonsRvList(allPokemonsFromFireStore)
                }
            }
    }

    private fun enableDropDown(){
        binding.tiTypes.isEnabled = true

    }





    private suspend fun translateEnumData()
    {
        Log.i("RESPPT","translateEnumData()")

        EnumDataPokemon.enumReference.translateDataOfMaps()
    }

    private suspend fun translatePokemons()
    {
        Log.i("RESPPT","translatePokemons()")
        allPokemonsFromFireStore.forEach {
            var translateTypes = TranslatorDataPokemon.validateTranslateCollection(it.types)
            it.types = translateTypes

            /*if(PokeAprendoApplication.pref.getLanguageCode() == "ja"){
                Log.i("RESPPT","EL LENGUAGE ES JA")

                var name = mutableListOf<String>(it.name)
                var newName = TranslatorDataPokemon.validateTranslateCollection(name)
                it.name = newName.toString()
            }*/

            Log.i("RESPPT","POOKKK")
            Log.i("RESPPT","${it.name}")
            Log.i("RESPPT","${translateTypes}")

        }
    }

    private suspend fun checkLanguage()
    {
        Log.i("RESPPT","checkLanguage()")

        var defaultLanguage = PokeAprendoApplication.pref.getLanguageCode()
        var targetLanguaje = ""


        when (defaultLanguage) {
            "es" -> {
                targetLanguaje= TranslateLanguage.SPANISH
                EnumDataPokemon.enumReference = EnumDataPokemon.SPANISH_DATA
            }
            "ja" -> {
                targetLanguaje= TranslateLanguage.JAPANESE
                EnumDataPokemon.enumReference = EnumDataPokemon.JAPANESE_DATA

            }
            else ->{
                targetLanguaje= TranslateLanguage.ENGLISH
                EnumDataPokemon.enumReference = EnumDataPokemon.ENGLISH_DATA
            }
        }


        configTranslator(targetLanguaje)
    }

    private suspend fun configTranslator(targetLanguaje: String){
        var sourceLanguage = TranslateLanguage.ENGLISH


        if(targetLanguaje != sourceLanguage)
        {

            TranslatorDataPokemon.config(
                TranslateLanguage.ENGLISH,
                targetLanguaje).await()
        }
    }



    private suspend fun checkEnumExistenceOfData() {
        Log.i("RESPPT","checkEnumExistenceOfData()")


        if(!EnumDataPokemon.enumReference.alreadyHaveGeneralData()){
            Log.i("RESPPT","${EnumDataPokemon.enumReference} ENUM NO TIENE DATOS")

            requestGeneralDataPokemon()
        }
        else{
            Log.i("RESPPT","${EnumDataPokemon.enumReference} ENUM TIENE DATOS")


        }
    }

    private suspend fun requestGeneralDataPokemon() {
        Log.i("RESPPT","requestGeneralDataPokemon()")

        setEnumMap(TYPES){ RetrofitClient.consumirApi.getTypes() }
        setEnumMap(GENERATION){RetrofitClient.consumirApi.getGenerations()}
        setEnumMap(HABITAT){RetrofitClient.consumirApi.getHabitat()}
        setEnumMap(NAME_POKEMONS){RetrofitClient.consumirApi.getPokemons()}
    }

    private suspend fun setEnumMap(key:String, resquest:suspend ()->Response<ListaDeObjetos>)
    {

        var response = resquest()

        if(response.isSuccessful)
        {
            var body = response.body()!!.getListOfObjects()
            var list = MainActivity.crearColeccion(body)

            EnumDataPokemon.enumReference.setMap(key, list)
        }

    }

    private fun setVisibilityViews()
    {
        binding.pbCargaMyPokemons.visibility = INVISIBLE
        binding.rvPokemons.visibility = VISIBLE
        binding.tvCargando.visibility = INVISIBLE

    }



    private suspend fun getPokemonsFromFireStore() {
        Log.i("RESPPT","getPokemonsFromFireStore()")


        fireStoreIntance
                .collection("users")
                //.document("NEW_23EQW3QW")
                .document(UserFirebase.getUid()!!)
                .collection(NAME_POKEMON_COLLECTION)
                .get().await().documents.forEach {document->

                    var pokemon = createPokemon(document.data!!)
                    allPokemonsFromFireStore.add(pokemon)

                 }
    }

    private fun createPokemon(pokemonData: MutableMap<String,Any>):PokemonFromFireStore {
        var pokemon:PokemonFromFireStore = PokemonFromFireStore()
        pokemon.id = pokemonData!!["id"].toString()
        pokemon.name = pokemonData!!["name"].toString()
        pokemon.types = pokemonData!!["types"] as MutableList<String>

        return pokemon
    }

    private fun setPokemonsRvList(pokemonsFromFireStore: MutableList<PokemonFromFireStore>) {
        rvAdapter.setPokemonList(pokemonsFromFireStore)
        setVisibilityTvNoPokemons(pokemonsFromFireStore)
    }

    private fun initRecyclerView() {
        binding.rvPokemons.layoutManager = GridLayoutManager(context,3)
        rvAdapter = AdapterRvPokemons(){name:String -> showInfoPokemonFragment(name)}
        binding.rvPokemons.adapter = rvAdapter
    }


    private fun initArrayTypes()
    {
        arrayTypes.add(resources.getString(R.string.all_pokemons))
        arrayTypes.addAll(EnumDataPokemon.enumReference.getTypes())
    }

    private fun initDropDownList() {

        initArrayTypes()

        var adapter = ArrayAdapter(requireContext(),R.layout.view_item_dropdown,arrayTypes)
        binding.acItem.setAdapter(adapter)

        initListenerDropDownList()

        binding.acItem.setAdapterDropDown(requireContext(),arrayTypes[0],arrayTypes)
    }


    private fun initListenerDropDownList(){
        binding.acItem.setOnItemClickListener { adapterView, view, i, l ->
            var newTypeSelected  = adapterView.adapter.getItem(i).toString()
            Log.i("LIZT","SELECT TYPE: ${newTypeSelected}")

            Log.i("LIZT","SELECT TYPE: ${arrayTypes}")

            binding.acItem.setAdapterDropDown(requireContext(),newTypeSelected,arrayTypes)

            if(newTypeSelected == resources.getString(R.string.all_pokemons))
            {
                setPokemonsRvList(allPokemonsFromFireStore)
            }
            else
            {
                var pokemonListByType =  filterPokemons(newTypeSelected)
                setPokemonsRvList(pokemonListByType)
            }

        }
    }

    private fun setVisibilityTvNoPokemons(pokemonList:MutableList<PokemonFromFireStore>)
    {
        if(pokemonList.isEmpty()){
            binding.tvNoPokemons.visibility = VISIBLE
        }
        else{
            binding.tvNoPokemons.visibility = INVISIBLE
        }
    }

    private fun filterPokemons(typeSelected: String): MutableList<PokemonFromFireStore> {
        var pokemonListByType = mutableListOf<PokemonFromFireStore>()

        Log.i("YEP","Lista ${allPokemonsFromFireStore}")
        Log.i("YEP","typeSelected: $typeSelected")

        allPokemonsFromFireStore.forEach {pokemon->
            var isOfSelectedType = pokemon.types.contains(typeSelected)
            if(isOfSelectedType) pokemonListByType.add(pokemon)
        }

        return pokemonListByType
    }

}
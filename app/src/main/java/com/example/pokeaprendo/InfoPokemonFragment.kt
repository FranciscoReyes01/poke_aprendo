package com.example.pokeaprendo

import android.content.Intent
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity.CENTER
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.LinearLayoutCompat.VERTICAL
import com.example.pokeaprendo.CapturarFragment.Companion.getPixelsBasedOnDp
import com.example.pokeaprendo.api.Pokemon
import com.example.pokeaprendo.api.PokemonEvolution
import com.example.pokeaprendo.api.RetrofitClient
import com.example.pokeaprendo.databinding.FragmentInfoPokemonBinding
import com.example.pokeaprendo.reusables_views.InfoDialogView
import com.example.pokeaprendo.reusables_views.InfoPokemonView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class InfoPokemonFragment(var makeRequestPokemon:suspend (String) -> Unit) : Fragment() {

    private var pokemonName : String=""
    private lateinit var pokemon: Pokemon
    private lateinit var evolutions: MutableList<PokemonEvolution>



    private var _binding: FragmentInfoPokemonBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            Log.i("POKINFO","EN ON CREATE ")
            /*LpokemonName = it.getString(POKEMON_KEY_NAME)!!*/
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInfoPokemonBinding.inflate(inflater, container, false)

        //initListenerButtonArrow()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("POKINFO", "ON VIEW CREATED")
        makeRequestInfoPokemon()
    }

    fun setPokemonName(pokemonName:String){
        this.pokemonName = pokemonName
    }

    private fun initUIInfoPokemon() {
        initTexViewNamePokemon()
        initImagePokemon()
        initInfoPokemonView(binding.ipTypes,"${resources.getString(R.string.types)}: " , pokemon.types.joinToString(","))
        initInfoPokemonView(binding.ipHabitat,"${resources.getString(R.string.habitat)}:" , pokemon.habitat)
        initInfoPokemonView(binding.ipGeneration, "${resources.getString(R.string.generation)}: ", pokemon.generation)
        initEvolutions()
        initListeners()




    }

    private fun initTexViewNamePokemon(){
        binding.tvPokemonName.text = pokemonName
    }

    private fun initListeners()
    {
        initListenerButtonEvolutions()
        initListenerBtAtras()
    }

    private fun initListenerBtAtras() {
        binding.ibAtras.setOnClickListener {
            //parentFragmentManager.popBackStack()
            startActivity(Intent(requireContext(),MainActivity::class.java))
        }
    }


    private fun initEvolutions() {

        var contOrden = 1

        if (evolutions.isNotEmpty()) {
            var titleView = createTitleView(resources.getString(R.string.chain_evolution))
            binding.lyEvolutions.addView(titleView)

            evolutions.forEach {

                var imagePokemonView = createImageEvolution(it.idPokemon)
                var namePokemonView = createSimpleTextView(it.name)
                namePokemonView.setPadding(0,0,0, getPixelsBasedOnDp(this.resources.displayMetrics.density,10))

                binding.lyEvolutions.addView(imagePokemonView)
                binding.lyEvolutions.addView(namePokemonView)

                contOrden++
            }
        }
        else {
            var tvNoPokemons= createSimpleTextView(resources.getString(R.string.no_evolutions))
            binding.lyEvolutions.addView(tvNoPokemons)
        }

    }

    private fun createTitleView(ordenEvolution: String):InfoDialogView {
        var titleInfo =InfoDialogView(requireContext())
        titleInfo.setIconById(R.drawable.ic_pokebola)
        titleInfo.setText(ordenEvolution)
        titleInfo.setLayoutsParams()
        return titleInfo
    }

    private fun  createSimpleTextView(nameEnvolve: String):TextView {
        var namePokemonView =TextView(context)
        namePokemonView.text = nameEnvolve
        namePokemonView.textSize = getPixelsBasedOnDp(this.resources.displayMetrics.density,10).toFloat()
        namePokemonView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        return  namePokemonView
    }

    private fun createImageEvolution(idPokemon: String): ImageView {
        var sizeImage = getPixelsBasedOnDp(this.resources.displayMetrics.density,150)

        var ivPokemonEvolution = ImageView(context)
        ivPokemonEvolution.layoutParams = LinearLayout.LayoutParams(sizeImage ,sizeImage)
        Picasso.get().load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${idPokemon}.png").into(ivPokemonEvolution )

        return ivPokemonEvolution
    }

    private fun initImagePokemon() {

        Picasso.get().load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemon.idPokemon}.png").into(binding.ivPokemon)
    }

    private fun initInfoPokemonView(view:InfoPokemonView, title:String, dataString: String?) {
        view.setTitle(title)
        view.setDataInfo(dataString)
    }

    private fun initListenerButtonEvolutions(){
        binding.btEvolutions.setOnClickListener{
            it.visibility = GONE

            binding.lyEvolutions.visibility = VISIBLE
            binding.btEvolutionsCollapse.visibility = VISIBLE
            binding.svContent.post{
                binding.svContent.fullScroll(View.FOCUS_DOWN)
            }
        }


        binding.btEvolutionsCollapse.setOnClickListener{
            it.visibility = GONE
            binding.btEvolutions.visibility = VISIBLE
            binding.lyEvolutions.visibility = GONE
        }
    }





    private fun makeRequestInfoPokemon()
    {
        Log.i("POKINFO","NAME ON FRAGMENT: $pokemonName")
        CoroutineScope(Dispatchers.IO).launch {
            makeRequestPokemon(pokemonName)


        }.invokeOnCompletion {
            Log.i("RESPPT","COMPLETION")

            pokemon = PokemonList.getLastAddedPokemon()

            getPokemonEvolution()
        }
    }

    private fun getPokemonEvolution() {

        var objEvolutions = mutableListOf<PokemonEvolution>()
        var evolutions = pokemon.evolutions


        CoroutineScope(Dispatchers.IO).launch {
            evolutions.forEach {
                if (!it.isNullOrEmpty())
                {
                    var responseEnvolve = RetrofitClient.consumirApi.getPokemonEvolution(it!!)
                    if (responseEnvolve.isSuccessful)
                    {
                        objEvolutions.add(responseEnvolve.body()!!)
                    }
                }
            }
        }.invokeOnCompletion {
            this.evolutions = objEvolutions

            activity?.runOnUiThread {
                binding.pbCargaInfoPokemon.visibility = INVISIBLE
                activeViews()
                initUIInfoPokemon()
            }
        }
    }

    private fun activeViews(){
        binding.svContent.visibility = VISIBLE
        binding.ibAtras.visibility= VISIBLE
    }

}
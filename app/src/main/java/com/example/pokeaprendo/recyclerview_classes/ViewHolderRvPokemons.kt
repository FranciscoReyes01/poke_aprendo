package com.example.pokeaprendo.recyclerview_classes

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeaprendo.api.PokemonFromFireStore
import com.example.pokeaprendo.databinding.ViewItemCardPokemonBinding
import com.squareup.picasso.Picasso

class ViewHolderRvPokemons(view: View,var showInfoPokemonFragment:(String)->Unit): RecyclerView.ViewHolder(view) {

    var binding = ViewItemCardPokemonBinding.bind(view)

    fun bindFromAdapter(pokemon: PokemonFromFireStore)
    {
        Picasso.get().load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemon.id}.png").into(binding.ivPokemonCaptured);
        binding.tvNamePokemonCaptured.text = pokemon.name
    }

    fun initListenerButtonVer(pokemonName:String)
    {
        binding.btVerPokemon.setOnClickListener{
            try {
                showInfoPokemonFragment(pokemonName)
                Log.i("RESPP","CLICK VER DESDE HOLDER")
            }
            catch (e:Exception){Log.i("RESPP","${e.message}")}

        }
    }


}
package com.example.pokeaprendo.recyclerview_classes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pokeaprendo.R
import com.example.pokeaprendo.api.PokemonFromFireStore

class AdapterRvPokemons(var pokemons: List<PokemonFromFireStore> = emptyList(),var showInfoPokemonFragment:(String)->Unit): RecyclerView.Adapter<ViewHolderRvPokemons>()
{

    fun setPokemonList(pokemons: List<PokemonFromFireStore>){
        this.pokemons = pokemons
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRvPokemons {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item_card_pokemon,parent,false)
        return ViewHolderRvPokemons(view){name->showInfoPokemonFragment(name)}
    }

    override fun onBindViewHolder(holder: ViewHolderRvPokemons, position: Int) {
        holder.bindFromAdapter(pokemons[position])
        holder.initListenerButtonVer(pokemons[position].name)
    }

    override fun getItemCount() = pokemons.size
}
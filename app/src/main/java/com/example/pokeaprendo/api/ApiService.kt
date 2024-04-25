package com.example.pokeaprendo.api

import android.graphics.ColorSpace.Model
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {



    @GET("pokemon/{pokemonName}/")
    suspend fun getPokemon(@Path("pokemonName") pokemonName: String): Response<PokemonData>

    @GET("pokemon/{evolutionName}/")
    suspend fun getPokemonEvolution(@Path("evolutionName") evolutionName: String): Response<PokemonEvolution>

    @GET("pokemon-species/{idPokemon}/")
    suspend fun getSpecies(@Path("idPokemon") idPokemon: String) : Response<Specie>

    @GET("evolution-chain/{id}/")
    suspend fun getEvolutionChain(@Path("id") idPokemon:String):Response<EnvolveChainData>

    @GET("type")
    suspend fun getTypes(): Response<ListaDeObjetos>

    @GET("generation")
    suspend fun getGenerations(): Response<ListaDeObjetos>

    @GET("pokemon-habitat")
    suspend fun getHabitat(): Response<ListaDeObjetos>

    @GET("ability")
    suspend fun getAbilities(): Response<ListaDeObjetos>

    @GET("pokemon?limit=100000&offset=0")
    suspend fun getPokemons(): Response<ListaDeObjetos>


}
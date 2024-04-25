package com.example.pokeaprendo.api

import com.example.pokeaprendo.CapturarFragment.Companion.EVOLUTIONS
import com.example.pokeaprendo.CapturarFragment.Companion.GENERATION
import com.example.pokeaprendo.CapturarFragment.Companion.HABITAT
import com.example.pokeaprendo.CapturarFragment.Companion.NAME
import com.example.pokeaprendo.CapturarFragment.Companion.TYPES
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PokemonEvolution(@SerializedName("name")val name:String,@SerializedName("id")val idPokemon: String)

data class Pokemon(
    val idPokemon: String,
    val name: String,
    var habitat: String?,
    val generation: String?,
    val types: MutableList<String>,
    val evolutions: MutableList<String>,
    val urlIconPokemon: String
): Serializable
{
    fun getMapOfData(): Map<String,MutableList<String>>
    {
        return mapOf<String,MutableList<String>>(
            NAME to mutableListOf(name),
            HABITAT to mutableListOf(habitat.orEmpty()),
            GENERATION to mutableListOf(generation.orEmpty()),
            TYPES to types,
            EVOLUTIONS to evolutions
        )
    }

    fun removeActualFormEvolution(){
        evolutions.remove(name)
    }
}

data class PokemonFromFireStore(var id: String="",var name:String="",var types: MutableList<String> = mutableListOf())

data class PokemonData(
    @SerializedName("id") val idPokemon: String,
    @SerializedName("name") val nombre:String,
    @SerializedName("types") val types: List<TypePokemon>,
    @SerializedName("sprites") val sprite: Sprite,
    var specie: Specie?,
    var envolveChain: EnvolveChainData?) {


    fun getUrlIconPokemon() = sprite.getUrl()

   fun setSpecies(specie: Specie?) {
       if(specie != null)
       {
           this.specie = specie
       }
   }

    fun setEnvolveChai(envolveChain: EnvolveChainData?) {
        if(envolveChain != null)
        {
            this.envolveChain = envolveChain
        }
    }

    fun getIdEvolutionChain() = specie?.getIdFromUrl()?:"fail"


    fun getHabitat() = specie?.getHabitat()

    fun getGeneration() = specie?.getGeneration()

}

data class Sprite(@SerializedName("front_default") val urlIconPokemon: String)
{
    fun getUrl() = urlIconPokemon
}


/*data class EnvolveChainData(@SerializedName("chain") val chainData: EnvolveChain)
data class  EnvolveChain(@SerializedName("is_baby") val isBaby: Boolean)*/
data class EnvolveChainData(@SerializedName("chain") val chainData: EnvolveChain)
{
    fun getListChain() = chainData.getListChai()
    fun getBaseForm() = chainData.getBaseForm()
}
data class EnvolveChain(@SerializedName("evolves_to") val listChain: List<EnvolveChainItem>, @SerializedName("species") val baseForm: EnvolveSpecie)
{
    fun getListChai() = listChain

    fun getBaseForm() = baseForm.getName()

}
data class EnvolveChainItem(@SerializedName("evolves_to") val envolveTo: List<EnvolveChainSubItem>, @SerializedName("species") val secondForm: EnvolveSpecie) {

    fun getListEnolveTo() = envolveTo
    fun getSecondForm() = secondForm.getName()


}






data class EnvolveChainSubItem(@SerializedName("species") val finalForm: EnvolveSpecie){
    fun getFinalForm(): String {
        return finalForm.getName()
    }

}



data class EnvolveSpecie(@SerializedName("name") val nombre: String)
{
    fun getName() = nombre
}


data class EvolutionDetail(@SerializedName("trigger") val trigger: Trigger):Recorrible {
    override fun getCadenaDeObj(): String {
       return trigger.getTriggerNamee()
    }
}

data class Trigger(@SerializedName("name")val triggerName: String){
    fun getTriggerNamee() = triggerName
}



data class Specie(@SerializedName("evolution_chain") val endPointEvolutionChain: EndPointEvolutionChain,
                   @SerializedName("habitat") val habitat:Habitat,
                   @SerializedName("generation") val generation:Generation)
{
    fun getHabitat()= habitat?.getHabitat()

    fun getGeneration()=  generation?.getGeneration()

    fun getIdFromUrl() =  endPointEvolutionChain.getIdEvolutionChain()
}


data class EndPointEvolutionChain(@SerializedName("url") val endPoint: String)
{
    fun getIdEvolutionChain(): String
    {

        var id = ""
        var posEnCadena =  endPoint.length-2

        while(true)
        {
            var caracrter =  endPoint[posEnCadena]
            if(caracrter !='/')
            {
                id+=caracrter
                posEnCadena--
            }
            else{
                break
            }
        }
        return id.reversed()
    }
}

data class Habitat(@SerializedName("name") val nameHabitat: String){
    fun getHabitat() = nameHabitat
}
data class Generation(@SerializedName("name") val nameGeneration: String)
{
    fun getGeneration() = nameGeneration
}

data class Move(@SerializedName("move") val move: MoveItem):Recorrible {
    override fun getCadenaDeObj(): String {
        return move.nombre
    }
}

data class MoveItem(@SerializedName("name") val nombre: String)

data class TypePokemon(@SerializedName("type") var typeItem: TypeItem): Recorrible {
    override fun getCadenaDeObj():String {
        return typeItem.name
    }
}
data class TypeItem(@SerializedName("name") var name:String)


data class Ability(@SerializedName("ability") val ability: AbilityItem):Recorrible {
    override fun getCadenaDeObj(): String {
        return ability.nombre
    }
}
data class AbilityItem(@SerializedName("name") val nombre: String)


interface Recorrible
{
    fun getCadenaDeObj():String
}



data class ListaDeObjetos(@SerializedName("results") val objects:List<ObjetoDeLista>)
{
    fun getListOfObjects() = objects
}

data class ObjetoDeLista(@SerializedName("name") val name: String):Recorrible {
    override fun getCadenaDeObj(): String {
        return name
    }
}




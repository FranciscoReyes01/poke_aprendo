package com.example.pokeaprendo

import android.app.UiModeManager
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.fragment.app.Fragment
import com.example.pokeaprendo.CapturarFragment.Companion.getPixelsBasedOnDp
import com.example.pokeaprendo.databinding.FragmentAjustesBinding
import com.example.pokeaprendo.shared_preferences.PokeAprendoApplication
import java.util.Locale

class FragmentAjustes(var changeTheme:(Int,Boolean)->Unit) : Fragment() {


    private var _binding: FragmentAjustesBinding? = null
    private val binding get() = _binding!!
    private var arrayLanguages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAjustesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTitleFragmentView()
        initDropDownLanguages()
        initListenerSwitch()
    }

    private fun initTitleFragmentView(){
        var density = this.resources.displayMetrics.density
        binding.idTitleAjustes.setIconById(R.drawable.ic_pokebola)
        binding.idTitleAjustes.setIconSize(getPixelsBasedOnDp(density ,60))
        binding.idTitleAjustes.setText(getString(R.string.settings))
        binding.idTitleAjustes.setTextSize(getPixelsBasedOnDp(density,30))

    }

    private fun stateSwitch():Boolean{
        var isChecked = false

        if(PokeAprendoApplication.pref.getDarkModeCode() ==2){
            isChecked = true
        }

        return isChecked
    }


    private fun initListenerSwitch()
    {
        binding.swDarkMode.isChecked = stateSwitch()
        binding.swDarkMode.setOnCheckedChangeListener { _, isChecked ->

            Log.i("RESPP","isChecked: $isChecked")
            if(isChecked){
                Log.i("RESPP","TRUE")

                changeTheme(UiModeManager.MODE_NIGHT_YES,true)
            }
            else{
                changeTheme(UiModeManager.MODE_NIGHT_NO,false)
            }
        }
    }

    private fun getArrayLanguages():MutableList<String>{
        var arrayLanguages = mutableListOf<String>()
        var arrayXmlLanguages= resources.getStringArray(R.array.languages)

        arrayXmlLanguages.forEach {
            arrayLanguages.add(it)
        }


        return arrayLanguages
    }

    private fun initDropDownLanguages() {
        arrayLanguages = getArrayLanguages()

        var adapter = ArrayAdapter(requireContext(),R.layout.view_item_dropdown, arrayLanguages)
        binding.acItem.setAdapter(adapter)

        initListenerDropDown()

        binding.acItem.setAdapterDropDown(requireContext(),getStringLanguage(arrayLanguages), arrayLanguages)
    }

    private fun getStringLanguage(arrayLanguages:MutableList<String>):String
    {
        return when(resources.configuration.locale.language){
            "es" -> arrayLanguages[0]
            "ja"-> arrayLanguages[2]
            else->arrayLanguages[1]
        }

    }

    private fun initListenerDropDown()
    {
        binding.acItem.setOnItemClickListener { adapterView, view, i, l ->
            var languageSelected = adapterView.adapter.getItem(i).toString()
            Log.i("RESPPP","languageSelected: $languageSelected")

            when(languageSelected){
                arrayLanguages[0] ->{PokeAprendoApplication.pref.setLocalLanguage("es",requireContext())}
                arrayLanguages[1] ->{PokeAprendoApplication.pref.setLocalLanguage("en",requireContext())}
                arrayLanguages[2] ->{PokeAprendoApplication.pref.setLocalLanguage("ja",requireContext())}
            }
            Toast.makeText(context,"${getString(R.string.language_changed)} ${languageSelected.uppercase()}",Toast.LENGTH_LONG).show()
            clearConfigTranslator()
            startActivity(Intent(context,MainActivity::class.java))



        }
    }

    private fun clearConfigTranslator()
    {
        TranslatorDataPokemon.deleteTranslator()
    }

}
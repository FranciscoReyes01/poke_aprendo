package com.example.pokeaprendo.reusables_views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import com.example.pokeaprendo.CapturarFragment.Companion.getPixelsBasedOnDp
import com.example.pokeaprendo.R
import com.google.android.flexbox.FlexboxLayout

class InfoPokemonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var titleInfo: InfoDialogView
    private var tvDataPokemon: TextView

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_info_pokemon, this, true)

        titleInfo = view.findViewById(R.id.idTitle)
        tvDataPokemon = view.findViewById(R.id.tvDataPokemon)
        setViewProperties()


        var flex = findViewById<FlexboxLayout>(R.id.flContainer)

        flex.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            if(view.height > titleInfo.height)
            {
                tvDataPokemon.setPadding(getPixelsBasedOnDp(resources.displayMetrics.density,40),0,0,0)
            }
        }
    }

    private fun setViewProperties() {
        gravity = Gravity.CENTER
    }

    fun setTitle(title:String) {
        titleInfo.setIconById(R.drawable.ic_pokebola)
        titleInfo.setText(title)


    }

    fun setDataInfo(dataString: String?) {
        tvDataPokemon.text = if(dataString.isNullOrEmpty()) resources.getString(R.string.no_data) else dataString
    }

}
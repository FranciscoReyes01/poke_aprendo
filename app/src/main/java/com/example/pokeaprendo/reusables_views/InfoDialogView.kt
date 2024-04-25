package com.example.pokeaprendo.reusables_views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.Gravity.CENTER
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.pokeaprendo.R
import com.squareup.picasso.Picasso

/*
    Para crear un view personalizada que pueda ser utilizada en varias partes de la app, se crea una clase que
    herede del elemento padre sobre el cual esta contruido el diseño("view_info_dialog"), en este caso un "Linear layout". De
    esta forma esta clase funcionaria como template para crear vistas de este tipo
*/


class InfoDialogView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var ivIcon:ImageView
    private var tvText: TextView

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_info_dialog, this, true)

        ivIcon = view.findViewById(R.id.ivIconDrawable)
        tvText = view.findViewById(R.id.tvItemDescription)
        gravity = Gravity.CENTER_VERTICAL
    }


    fun setIconById(idIcon: Int)
    {
        ivIcon.setImageResource(idIcon)
    }

    fun setIconByUrl(url:String)
    {
        Picasso.get().load(url).into(ivIcon)
    }


    fun setText(newText:String)
    {
        tvText.text = newText
    }

    fun setIconSize(size:Int)
    {
        ivIcon.layoutParams = LayoutParams(size,size)
    }

    fun setTextPaddingLeft(paddingLeft:Int){
        tvText.setPadding(paddingLeft,0,0,0)

    }

    fun setTextSize(size:Int)
    {
        tvText.textSize = size.toFloat()
    }

    fun setPadddingBottom(paddingBottom:Int)
    {
        setPadding(0,0,0,paddingBottom)
    }

    fun setLayoutsParams() {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
    }



}
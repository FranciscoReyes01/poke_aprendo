package com.example.pokeaprendo

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

fun AutoCompleteTextView.setAdapterDropDown(context: Context,itemSelected:String,arrayTypes:MutableList<String>)
{
    var copyList = mutableListOf<String>()
    copyList.addAll(arrayTypes)

    this.setText(itemSelected)

    copyList.remove(itemSelected)
    var adapter = ArrayAdapter(context, R.layout.view_item_dropdown,copyList)

    /*for (i in arrayTypes.indices)
    {
        if(arrayTypes[i][0].equals("#"))
        {
            arrayTypes[i] = arrayTypes[i].drop(0)
        }
    }


    arrayTypes[posSelected] = "#${arrayTypes[posSelected]}"
    newList = arrayTypes

    arrayTypes.removeAt(posSelected)


    var adapter = ArrayAdapter(context, R.layout.view_item_dropdown,arrayTypes)*/

    this.setAdapter(adapter)
}
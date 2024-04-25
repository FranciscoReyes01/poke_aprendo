package com.example.pokeaprendo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pokeaprendo.databinding.FragmentImageTextBinding


class ItemImageText : Fragment() {

    private var textDescription: String? = null
    private var idImageResource: String? = null


    private var _binding: FragmentImageTextBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            textDescription = it.getString(TEXT_DESCRIPTION)
            idImageResource = it.getString(ID_IMAGE_RESOURCE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentImageTextBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvItemDescription.text = "HOLA"
    }

    companion object {


        const val TEXT_DESCRIPTION= "TEXT_DESCRIPTION"
        const val ID_IMAGE_RESOURCE = "ID_IMAGE_RESOURCE"

        fun newInstance(param1: String, param2: String) =
            ItemImageText().apply {
                arguments = Bundle().apply {
                    putString(TEXT_DESCRIPTION, param1)
                    putString(ID_IMAGE_RESOURCE , param2)
                }
            }
    }
}
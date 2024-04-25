package com.example.pokeaprendo

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlin.math.ln

enum class EnumMedia(var soundId:Int) {

    BACKGROUNG_SOUND(R.raw.background_music_app),
    CORRECT_SOUND(R.raw.correct_sound),
    INCORRECT_SOUND(R.raw.iconrrect_sound),
    CAPTURED_SOUND(R.raw.captuted_sound),
    NO_CAPTURED_SOUND(R.raw.no_captured_sound);

    private lateinit var mediaPlayer:MediaPlayer
    val maxVolume = 50.0
    var volume =0f



    fun initMediaPlayer(context: Context){

        mediaPlayer = MediaPlayer.create(context,soundId)
        setLoopingMedia()

    }

    private fun setLoopingMedia(){
        if(this.hashCode() == BACKGROUNG_SOUND.hashCode() ){
            mediaPlayer.isLooping = true
        }
    }

    fun play(){
        mediaPlayer.start()
    }

    fun setVoume(volumeVal:Int){
        volume = (1 - (ln(maxVolume -  volumeVal.toDouble()) / ln(maxVolume))).toFloat()
        mediaPlayer.setVolume(volume,volume)
    }



    fun pause(){
        mediaPlayer.pause()
    }




}
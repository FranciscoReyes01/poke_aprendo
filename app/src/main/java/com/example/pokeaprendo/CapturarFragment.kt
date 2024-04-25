package com.example.pokeaprendo

//import com.mycompany.myappname.R

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.indices
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.pokeaprendo.api.ObjetoDeLista
import com.example.pokeaprendo.api.Pokemon
import com.example.pokeaprendo.api.RetrofitClient
import com.example.pokeaprendo.databinding.FragmentCapturarBinding
import com.example.pokeaprendo.reusables_views.InfoDialogView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class CapturarFragment(var makeRequestPokemon:suspend(String)->Unit,var setDrawerLayoutLockMode: (Int) ->Unit): Fragment(){

    private val keys = arrayOf<String>(
        NAME, HABITAT, GENERATION, TYPES, EVOLUTIONS
    )

    private var keysCopy = mutableListOf<String>()
    private var key = keys[0]
    private var keyActual=""
    private var respCorrects = mutableListOf<String>()

    private var keysIncorrectQuestion = mutableListOf<String>()

    lateinit var mfireStore: FirebaseFirestore

    private lateinit var questions:MutableMap<String,String>

    private lateinit var pokemon: Pokemon
    private lateinit var englishPokemon:Pokemon
    private var numOfQuestions = 0
    private var contCorrectQuestions = 0
    private var pokemonsCaptured = mutableListOf<String>()
    private var pokemonsToFireStore = mutableListOf<Map<String,Any>>()
    private var pokemonsNoCaptured = mutableListOf<String>()
    private var contQuestion = 0
    private var isButtonAdded = false

    private var _binding: FragmentCapturarBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mfireStore = FirebaseFirestore.getInstance()

        if(arguments != null){
        }
        _binding = FragmentCapturarBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initButtonValidateListener()
        initQuestions()
        initMediaPlayerObjects()
        makeRequestPokemons()

        Log.i("RESPP","QUESTIONS: $questions")

    }


    private fun initMediaPlayerObjects(){
        var enumMeidaObjects = EnumMedia.entries
        enumMeidaObjects.forEach {
            it.initMediaPlayer(requireContext())
        }

    }

    private fun initQuestions() {
        questions = mutableMapOf<String, String>(
            NAME to resources.getString(R.string.question_name),
            HABITAT to getString(R.string.question_habitat),
            GENERATION to getString(R.string.question_generation),
            TYPES to getString(R.string.question_types),
            EVOLUTIONS to resources.getString(R.string.question_evolutions)
        )
    }


    private fun makeRequestPokemons() {

        var showStartDialog = true

        if(!PokemonList.isNotEmpy())
        {
             CoroutineScope(Dispatchers.IO).launch {
                 try {
                     binding.tvCargando.text = resources.getString(R.string.looking_capture_pokemons)

                     val shortListPokemons = getShortListOfPokemons(7,EnumDataPokemon.enumReference.getMap(NAME_POKEMONS)!!)
                     Log.i("RESPP", "shortListPokemons: $shortListPokemons")

                     shortListPokemons.forEach(){pokemonName->

                         makeRequestPokemon(pokemonName)
                     }

                 }
                 catch (e:Exception){
                     activity?.runOnUiThread()
                     {
                         Toast.makeText(context,resources.getString(R.string.no_conection),Toast.LENGTH_LONG).show()
                         binding.tvNoPokemonsAvailable.visibility = VISIBLE
                         showStartDialog = false
                         setDrawerLayoutLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

                     }
                     this.cancel()

                 }
            }.invokeOnCompletion {

                requireActivity().runOnUiThread {
                    binding.pbCargaResquestPokemons.visibility = INVISIBLE
                    binding.tvCargando.visibility = INVISIBLE

                   if(showStartDialog) showStartDialog()
                }
            }
        }
        else{
            showStartDialog()
            Log.i("RESPPT", "YA HAY ELEMENTOS EN LA LISTA ")
        }
    }


    private fun getShortListOfPokemons(cantNames: Int, pokemonNames: MutableList<String>): MutableList<String> {
        var newList = mutableListOf<String>()
        for (i in 1..cantNames)
        {
            while (true)
            {
                val randomBasicPokemon = pokemonNames[getRandomNumber(0,pokemonNames.lastIndex)]

                if(!newList.contains(randomBasicPokemon))
                {
                    newList.add(randomBasicPokemon)
                    break
                }
            }
        }
        return newList
    }


    private fun showStartDialog()
    {
        var startDialog = instanceDialog(R.layout.dialog_start_capture)
        setStartDialog(startDialog)
        blockDialog(startDialog)
        startDialog.show()
    }


    private fun initButtonValidateListener() {
        binding.btValidateAnswers.setOnClickListener {
            validateAnswers()
            setEnableButtonSiguente(false)
        }
    }

    private fun instanceDialog(layoutReference:Int):Dialog {
        var dialog = Dialog(requireContext())
        dialog.setContentView(layoutReference)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    private fun setStartDialog(dialog: Dialog) {
        var tvNumPokemons = dialog.findViewById<TextView>(R.id.tvNumPokemons)

        setTitleDialog(dialog,resources.getString(R.string.pokemons_to_capture))
        //setTitleDialog(dialog,"")
        tvNumPokemons.text = PokemonList.getSize().toString()



        initListenersStartDialog(dialog)
    }

    private fun blockDialog(dialog:Dialog){
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
    }

    private fun setTitleDialog(dialog: Dialog,titleText:String) {
        var idTitle = dialog.findViewById<InfoDialogView>(R.id.idTitle)
        idTitle.setText(titleText)
        idTitle.setIconById(R.drawable.ic_pokebola)
    }

    private fun initListenersStartDialog(dialog:Dialog){

        var btComenzar = dialog.findViewById<Button>(R.id.btComenzar)
        var btRegresar = dialog.findViewById<Button>(R.id.btRegresar)

        btComenzar.setOnClickListener(){
            playSound(EnumMedia.BACKGROUNG_SOUND,20)
            startCapture()
            initValuesCapture()
            activeViews()
            dialog.hide()
        }

        btRegresar.setOnClickListener(){
            goToMainScreen(dialog)

        }
    }


    private fun activeViews(){
        binding.btValidateAnswers.visibility = VISIBLE
        binding.vLinea.visibility = VISIBLE
    }

    private fun startCapture() {
        PokemonList.showPokemons()
        PokemonList.showEnglishPokemons()
        cleanContainerUI()
        initValuesToQuestions()
        generateQuestion()
    }

    private fun initValuesCapture()
    {
        pokemonsCaptured = mutableListOf<String>()
        pokemonsToFireStore = mutableListOf<Map<String,Any>>()
        pokemonsNoCaptured = mutableListOf<String>()
    }

    private fun goToMainScreen(dialog: Dialog) {

        startActivity(Intent(requireContext(),MainActivity::class.java))
        //dialog.hide()
    }

    private fun initValuesToQuestions() {
        Log.i("RESPPT", " ")

        var posPokemonRandom = getRandomNumber(0, PokemonList.getSize() - 1)

        pokemon = PokemonList.getPokemon(posPokemonRandom)
        englishPokemon = PokemonList.getEnglishPokemon(posPokemonRandom)
        pokemon.removeActualFormEvolution()
        englishPokemon.removeActualFormEvolution()

        numOfQuestions = getRandomNumber(3, 4)
        keysCopy = keys.toMutableList()
        key = keys[0]
        Log.i("RESPPT", "NUM DE PREGUNTAS: $numOfQuestions")
        contQuestion = 0
        contCorrectQuestions =0
        keysIncorrectQuestion.clear()

        updateImageViewByUrl(binding.ivPokemon)
    }

    private fun generateQuestion() {

        var cantOfAnswers = 0
        var isCheckBoxQuestion = isCheckBoxQuestion()
        var isInitUIQuestion = false
        var limitItCorrecta = 0
        Log.i("RESPPT", " ")
        Log.i("RESPPT", "POKEMON: $pokemon")
        Log.i("RESPPT", "ENGLISH POKEMON: $englishPokemon")
        Log.i("RESPPT", "KEYS COPY: $keysCopy")


        while (!isInitUIQuestion) {
            keyActual = key
            if (isCheckBoxQuestion) {
                cantOfAnswers = getRandomNumber(4, 6)
                limitItCorrecta = cantOfAnswers / 2
                isInitUIQuestion = initUIQuestion(cantOfAnswers, limitItCorrecta)
            }
            else {
                cantOfAnswers = getRandomNumber(3, 5)
                limitItCorrecta = cantOfAnswers
                isInitUIQuestion = initUIQuestion(cantOfAnswers, limitItCorrecta)
            }
            Log.i("RESPP", "isInitUIQuestion: $isInitUIQuestion")

            if(keysCopy.isNotEmpty())
            {
                key = keysCopy[getRandomNumber(0, keysCopy.lastIndex)]
                isCheckBoxQuestion = isCheckBoxQuestion()
            }
            else{

                if(!isInitUIQuestion)
                {
                    respCorrects.clear()
                    validateCapturePokemon()
                    break
                }
            }

        }
    }

    private fun cleanContainerUI() {
        binding.rdRespuestas.removeAllViews()
    }

    private fun initUIQuestion(cantOfAnswers:Int,limitItCorrect:Int): Boolean{

        var isUIInit = false

        Log.i("RESPPT","KEY: $key")
        Log.i("RESPPT","NUM RESPUESTAS: $cantOfAnswers")
        if(isKeyValid(cantOfAnswers))
        {
            updateViewQuestion()
            createAnswers(cantOfAnswers,limitItCorrect)
            contQuestion++
            isUIInit = true
        }
        keysCopy.remove(key)
        Log.i("RESPPT","KEYS COPY REMOVE: $keysCopy")

        return isUIInit
    }

    private fun isKeyValid(cantOfAnswers:Int):Boolean {
        val sizeOfMap = EnumDataPokemon.enumReference.getSizeOfList(key)
        val attPokemon = pokemon.getMapOfData()[key]

        return isAttPokemonValid(attPokemon) && sizeOfMap > cantOfAnswers
    }

    private fun updateImageViewByUrl(imageView: ImageView) {
        Picasso.get().load(pokemon.urlIconPokemon).into(imageView);
    }

    private fun isAttPokemonValid(attPokemon: MutableList<String>?):Boolean{
        var isValid = false

        if(!attPokemon.isNullOrEmpty())
        {
            if(attPokemon[0].isNotEmpty())
            {
                isValid = true
            }
        }
        return isValid
    }

    private fun updateViewQuestion() {
        binding.tvPregunta.text = questions[key]
    }

    private fun isCheckBoxQuestion():Boolean {
        var pokemonAtt = pokemon.getMapOfData()[key]!!

        return pokemonAtt.size > 1
    }

    private fun createAnswers(cantOfAnswers:Int,limitItCorrect: Int){
        var itCorrectAnswer = getRandomNumber(1,limitItCorrect)
        var listOfPokemon = mutableListOf<String>()
        listOfPokemon.addAll(pokemon.getMapOfData()[key]!!)


        for (i in 1 .. cantOfAnswers) {

            val button = if (isCheckBoxQuestion()) CheckBox(context) else RadioButton(context)

            setOnClickListenerAnswerButton(button)

            while(!isButtonAdded)
            {
                if(i != itCorrectAnswer)
                {
                    var randomNumberOfList = getRandomNumber(0,EnumDataPokemon.enumReference.getSizeOfList(key)-1)
                    button.text = EnumDataPokemon.enumReference.getItemString(key,randomNumberOfList)
                    addViewToContainer(button)
                }
                else{
                    if(listOfPokemon!!.size > 0)
                    {
                        var itemRandom = getRandomNumber(0,listOfPokemon.lastIndex)
                        button.text = listOfPokemon[itemRandom]

                        respCorrects.add(listOfPokemon[itemRandom])

                        Log.i("RESPPT","OPS CORRECTA: ${listOfPokemon[itemRandom]}")
                        listOfPokemon.removeAt(itemRandom)
                        val nextIteration = i+1
                        if(nextIteration <=cantOfAnswers)
                        {
                            itCorrectAnswer=getRandomNumber(nextIteration,cantOfAnswers)
                        }
                        addViewToContainer(button)
                    }
                    else
                    {
                        itCorrectAnswer = 0
                    }
                }
            }
            isButtonAdded = false
        }
    }

    private fun setOnClickListenerAnswerButton(button: CompoundButton) {

        button.setOnClickListener {
            setEnableButtonSiguente(isAnyAnswerSelected())
        }

    }

    private fun setEnableButtonSiguente(state:Boolean)
    {
        binding.btValidateAnswers.isEnabled = state
    }

    private fun isAnyAnswerSelected():Boolean{
        var answerSelected = false

        for (posAnswerButton in binding.rdRespuestas.indices){
            var answerButton = binding.rdRespuestas.getChildAt(posAnswerButton) as CompoundButton

            if(answerButton.isChecked){
                answerSelected = true
                break
            }
        }

        return answerSelected
    }

    private fun addViewToContainer(button: Button) {
        if(getButtonRefOnContainer(button.text.toString()) == null)
        {
            binding.rdRespuestas.addView(button)
            isButtonAdded=true
        }
    }

    private fun getButtonRefOnContainer(textButton:String): CompoundButton? {

        var buttonRference:CompoundButton? = null


        if(binding.rdRespuestas.childCount > 0)
        {
            for (i in 0 ..<binding.rdRespuestas.childCount)
            {
                val buttonOfContainer = binding.rdRespuestas[i] as CompoundButton
                if(textButton == buttonOfContainer.text)
                {
                    buttonRference = buttonOfContainer
                    break
                }
            }
        }
        return buttonRference
    }

    private fun validateAnswers(){
        Log.i("RESPP"," ")
        Log.i("RESPP","NEXT QUESTION")

        qualifyQuestion()

        if(contQuestion == numOfQuestions)
        {
            validateCapturePokemon()
        }
        else
        {
            generateNextQuestion()
        }
    }

    private fun generateNextQuestion() {
        cleanContainerUI()
        generateQuestion()
    }

    private fun continueCapture(dialog: Dialog) {
        removePokemon()

        if(PokemonList.isNotEmpy())
        {
            startCapture()
        }
        else
        {
            EnumMedia.BACKGROUNG_SOUND.setVoume(30)

            var dialogTotalCapture = instanceDialog(R.layout.dialog_generic)
            setDialogTotalCapture(dialogTotalCapture)
            blockDialog(dialogTotalCapture)
            dialogTotalCapture.show()

            PokemonList.deletePokemonsEnglish()

            Log.i("RESPPT","CAPTURADOS: ${pokemonsCaptured}")

            Log.i("RESPPT","FIN DEL JUEGO :)")
            Log.i("RESPPT"," ")



            insertPokemonFireStore()
        }

        dialog.hide()
    }

    private fun insertPokemonFireStore() {
        pokemonsToFireStore.forEach{
            mfireStore
                .collection("users")
                .document(UserFirebase.getUid()!!)
                .collection(NAME_POKEMON_COLLECTION)
                .add(it)
                .addOnSuccessListener {doc->
                    Log.i("RESPP", "DATOS INSERTADOS $doc") }
                .addOnFailureListener {doc-> Log.i("RESPP", "DATOS NO INSERTADOS $doc")}
        }
    }

    private fun setDialogTotalCapture(dialog: Dialog) {
        setDialogDynamicContent(
            dialog,
            getString(R.string.end_capture),
            R.drawable.icon_capture,
            "${pokemonsCaptured.size} ${getString(R.string.captured_pokemons)}",
            pokemonsCaptured,
            R.drawable.icon_correct)

        var btAceptar= dialog.findViewById<Button>(R.id.btAcceptItems)

        btAceptar.setOnClickListener {
            dialog.hide()
            var dialogNoCapture = instanceDialog(R.layout.dialog_generic)
            setDialogTotalNoCapture(dialogNoCapture)
            blockDialog(dialogNoCapture)
            dialogNoCapture.show()

        }
    }

    private fun setDialogTotalNoCapture(dialog: Dialog) {
        setDialogDynamicContent(
            dialog,
            getString(R.string.end_capture),
            R.drawable.icon_no_capture,
            "${pokemonsNoCaptured.size} ${getString(R.string.no_captured_pokemons)}",
            pokemonsNoCaptured,
            R.drawable.icon_icorrect)


        var btAceptar= dialog.findViewById<Button>(R.id.btAcceptItems)

        btAceptar.setOnClickListener {
            pauseBackgroundSound()
            goToMainScreen(dialog)
        }
    }

    private fun pauseBackgroundSound(){
        EnumMedia.BACKGROUNG_SOUND.pause()
    }

    private fun validateCapturePokemon() {
        var dialog:Dialog

        var incorrectQuestions = contQuestion-contCorrectQuestions

        if(contCorrectQuestions > incorrectQuestions)
        {
            Log.i("RESPPT","SE CAPTURO POKEMON")
            dialog= instanceDialog(R.layout.dialog_generic)
            setDialogCapture(dialog)
            blockDialog(dialog)
            dialog.show()

            pokemonsCaptured.add(pokemon.name)


            pokemonsToFireStore.add(mapOf(ID_POKEMON to englishPokemon.idPokemon, NAME_POKEMON to englishPokemon.name, TYPES_POKEMON to englishPokemon.types))
            playSound(EnumMedia.CAPTURED_SOUND,25)
            Log.i("RESPPT","${pokemonsToFireStore}")

        }
        else{
            Log.i("RESPPT","NO SE CAPTURO POKEMON")
            dialog= instanceDialog(R.layout.dialog_generic)
            Log.i("RESPPT","${pokemonsToFireStore}")

            setDialogNoCapture(dialog)
            blockDialog(dialog)
            dialog.show()

            pokemonsNoCaptured.add(pokemon.name)
            playSound(EnumMedia.NO_CAPTURED_SOUND,25)

        }
        Log.i("RESPPT"," ")

    }

    private fun playSound(objectSound:EnumMedia,volume:Int){
        objectSound.setVoume(volume)
        objectSound.play()
    }


    private fun setDialogCapture(dialog: Dialog) {
        var ivPokemon = dialog.findViewById<ImageView>(R.id.ivImage)

        setDialogGenericData(dialog, getString(R.string.captured_pokemon),pokemon.name)

        updateImageViewByUrl(ivPokemon)

        setOnClickButtonDialog(dialog)
    }

    private fun setOnClickButtonDialog(dialog: Dialog) {
        var btAceptar= dialog.findViewById<Button>(R.id.btAcceptItems)

        btAceptar.setOnClickListener {
            continueCapture(dialog)
        }
    }

    private fun setDialogGenericData(dialog: Dialog,title:String, description: String){
        setTitleDialog(dialog,title)
        setDescriptionDialog(dialog,description)
    }

    private fun setDescriptionDialog(dialog: Dialog,description:String){
        var tvDescription = dialog.findViewById<TextView>(R.id.tvDescription)
        tvDescription.text = description
    }

    private fun setDialogNoCapture(dialog: Dialog) {
        setDialogDynamicContent(
            dialog,
            getString(R.string.no_captured_pokemon),
            R.drawable.icon_no_capture,
            "${keysIncorrectQuestion.size} ${getString(R.string.wrong_answers)}",
            keysIncorrectQuestion,
            R.drawable.icon_icorrect)

        setOnClickButtonDialog(dialog)

    }

    private fun setDialogDynamicContent(dialog: Dialog, title:String, mainIcon:Int, description: String, itemList: MutableList<String>, iconItemId:Int)     {

        setDialogGenericData(dialog,title,description)

        updateImageViewDialog(dialog,mainIcon)

        addDynamicViewDialog(dialog,itemList,iconItemId)
    }

    private  fun addDynamicViewDialog(dialog: Dialog,itemList: MutableList<String>, iconId:Int){

        var lyDynamicContent = dialog.findViewById<LinearLayout>(R.id.lyDynamicContent)

        itemList.forEach(){

            var infoDialogItem = InfoDialogView(requireContext())
            infoDialogItem.setIconById(iconId)
            infoDialogItem.setIconSize(getPixelsBasedOnDp(this.resources.displayMetrics.density,20))
            infoDialogItem.setText(it)
            infoDialogItem.setTextSize(getPixelsBasedOnDp(this.resources.displayMetrics.density,6))
            infoDialogItem.setTextPaddingLeft(getPixelsBasedOnDp(this.resources.displayMetrics.density,6))
            infoDialogItem.setPadddingBottom(getPixelsBasedOnDp(this.resources.displayMetrics.density,7))

            lyDynamicContent.addView(infoDialogItem)
        }

    }

    private fun updateImageViewDialog(dialog: Dialog,idIcon: Int) {
        var ivDialog = dialog.findViewById<ImageView>(R.id.ivImage)
        ivDialog.setImageResource(idIcon)
    }


    private fun qualifyQuestion() {

        val correctSelected = getCantRespCorrect()
        val correctNoSelected = respCorrects.size - correctSelected

        if(correctSelected >= correctNoSelected){
            Log.i("RESPPT","PREGUNTA BIEN CONTESTADA !!!")
            contCorrectQuestions++
            playSound(EnumMedia.CORRECT_SOUND,25)
        }
        else{
            Log.i("RESPPT","PREGUNTA MAL CONTESTADA !!!")
            keysIncorrectQuestion.add(keyActual)
            playSound(EnumMedia.INCORRECT_SOUND,25)

        }
        respCorrects.clear()
    }

    private fun getCantRespCorrect(): Int {
        var contCorrects =0

        Log.i("RESPP","ENTRASTE AL METODO")
        respCorrects.forEach(){
            Log.i("RESPP","CAD: $it")
            //IT
            var button = getButtonRefOnContainer(it)!!
            Log.i("RESPP","BUTTON TEXT: ${button.text}")
            if(button.isChecked){
                contCorrects++
            }
        }

        return contCorrects
    }

    private fun removePokemon() {
        PokemonList.removePokemon(pokemon)
        PokemonList.removeEnglishPokemon(englishPokemon)
    }

    /*Sintaxis antigua para crear instancia del frgamente en una activity
      Ejemplo de instancia en activity:
      val fragmet = CapturarFragment.newInstance("","")*/

    companion object {
        const val POKEMON_LIST = "POKEMON_LIST"
        const val AGE_BUNDE = "AGE_BUNDE"
        const val NAME="NAME"
        const val HABITAT ="HABITAT"
        const val GENERATION = "GENERATION"
        const val TYPES = "TYPES"
        const val EVOLUTIONS = "EVOLUTIONS"
        const val NAME_POKEMONS = "NAME_POKEMONS"

        const val ID_POKEMON = "id"
        const val NAME_POKEMON = "name"
        const val TYPES_POKEMON = "types"
        const val NAME_POKEMON_COLLECTION = "pokemons"

        fun getRandomNumber(valInicial:Int,valFinal: Int): Int {
            return (valInicial..valFinal).random()
        }

        fun getPixelsBasedOnDp(density:Float, dp:Int):Int {
            val paddingInPx = dp * density + 0.5f

            return paddingInPx.toInt()
        }


    }
}
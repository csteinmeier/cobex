package com.example.cobex

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import com.example.cobex.databinding.FragmentInputKeywordBinding

/**
 *
 * Class which represent the Fragment of InputKeyword
 *
 * If a new Generic Term should be added to the XML, only the following things have to be modified:
 * * [KeywordType]  this enum has to be extended.
 *
 * * [onViewCreated] :
 *      - [counterList] has to be extended with the new [KeywordCounter]
 *      - a New [ButtonGenericKeyword] with the Generic Term has to be added in
 *        [listButtonGenericKeyword]
 * */

class InputKeyword : Fragment(), CompositionArtifact.IArtifact {


    /** Binding */
    private var _binding: FragmentInputKeywordBinding? = null
    private val binding get() = _binding!!

    /** List of Button that stands for the respective keyword*/
    private lateinit var listButtonGenericKeyword: List<ButtonGenericKeyword>

    /**Counter used for the TextView*/
    private lateinit var counterList: List<KeywordCounter>

    /**All existing keyword types are to be added here*/
    enum class KeywordType{
        FEELING,
        ENVIRONMENT
        /**New KeywordType here*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        counterList = listOf(
            KeywordCounter(binding.counterKeywordsFeeling, KeywordType.FEELING),
            KeywordCounter(binding.counterKeywordsEnvironment, KeywordType.ENVIRONMENT)
            /**New Keyword Counter should be initialized here*/
        )

        listButtonGenericKeyword = listOf(
            ButtonGenericKeyword(binding.buttonFeelings, this, binding.tableFeelings, KeywordType.FEELING),
            ButtonGenericKeyword(binding.buttonEnvironment, this, binding.tableEnv, KeywordType.ENVIRONMENT)
            /**New Generic Buttons should be added here*/
        )

        binding.buttonBackCreate.setOnClickListener {
            findNavController().navigate(R.id.action_inputKeyword_to_CreateNew)
        }



        binding.buttonSave.setOnClickListener {
                if(isKeywordButtonPressed()) {
                    saveKeywordList()
                    val recordingmsg = Toast.makeText(activity?.baseContext, "Keyword Set Saved", Toast.LENGTH_LONG) //maybe add index of set
                    recordingmsg.show()
                    listButtonGenericKeyword.forEach { genericButton ->
                        genericButton.listButtonKeywords.forEach { keywordButton ->
                            keywordButton.setDeactivated() }
                    }
                }
        }


    }

    private fun saveKeywordList(){
        val keywordSetSize = getStringSet(requireContext(), this.javaClass)?.size?: 0
        val clickedWords = wrapListSetOfButtons(keywordSetSize)

        synchroniseArtifact(requireContext(), clickedWords, InputKeyword::class.java, true)
    }


    private fun wrapListSetOfButtons(counter: Int) =
        "$counter#${listOfPressedButtonAsString()}#${getTimeStamp(requireContext())};"

    /**
     * @return a sequence of all clicked buttons
     * */
    private fun listOfPressedButtonAsString() =
        listButtonGenericKeyword.flatMap { it.getClickedButtonsAsSequence() }.toString()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInputKeywordBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun updateKeyWordCounter(value: Int, keywordType: KeywordType){
        val counter = counterList.single { keywordType == it.keywordType }
        counter.update(value)
    }


    private class KeywordCounter(var counterField : TextView, val keywordType: KeywordType) {
        var counter : Int = 0

        private fun setVisibility(){
            counterField.visibility = if(counter > 0) View.VISIBLE else View.GONE
        }

        fun update(value: Int){
            counter += value
            counterField.text = Editable.Factory.getInstance().newEditable(counter.toString())
            setVisibility()
        }
    }



    /**********************************************************************************/
    /***************************Parent Button Class************************************/
    /**********************************************************************************/
    private abstract class ButtonStruct(val button: Button,
                                        protected val context: InputKeyword,) :
    View.OnClickListener{

        enum class ButtonState{
            DEACTIVATED,
            ACTIVATED
        }

        var state = ButtonState.DEACTIVATED

        /**Depending on the state, the colors of the buttons are set*/
        protected fun setButtonColour() {
            when(state) {
                ButtonState.ACTIVATED -> button.setBackgroundColor(
                    ContextCompat.getColor( context.requireContext(), R.color.orange))

                ButtonState.DEACTIVATED -> button.setBackgroundColor(
                    ContextCompat.getColor( context.requireContext(), R.color.purple_200))
            }
        }

        open fun setActivated(){
            state = ButtonState.ACTIVATED
            setButtonColour()
        }

        open fun setDeactivated(){
            state = ButtonState.DEACTIVATED
            setButtonColour()
        }
    }

    /**
     *
     *  The class that represents the simple terms in the form of the button
     *
     *  @param button which stands for a term
     *  @param keywordType To determine the number of buttons clicked for the matching generic keyword
     *
     */
    private class ButtonKeyword (button: Button, context: InputKeyword, var keywordType: KeywordType)
        : ButtonStruct(button, context) {

        init {
            button.setOnClickListener (this)
        }

        override fun toString(): String {
            return ("[KeywordType:${keywordType.name}]:[KeywordName:${button.text}]")
        }

        override fun onClick(p0: View?) {
           if(state == ButtonState.DEACTIVATED) setActivated() else setDeactivated()
        }

        override fun setActivated() {
            super.setActivated()
            context.updateKeyWordCounter(1, keywordType)
        }

        override fun setDeactivated() {
            super.setDeactivated()
            context.updateKeyWordCounter(-1, keywordType)
        }
    }

    /**Is set to identify the last clicked button */
    private var lastClick: ButtonGenericKeyword? = null

    /**
     *
     * A class for the buttons representing generic terms
     *
     * @param button which stand for a generic term
     * @param context
     * @param tableLayout Associated layout which opens and closes with the button
     *
     * */
    private class ButtonGenericKeyword(button: Button, context: InputKeyword,
                                       private val tableLayout: TableLayout,
                                       private val keywordType: KeywordType):
        ButtonStruct(button,context) {
        private val table: TableLayout = tableLayout

        var listButtonKeywords : MutableList<ButtonKeyword> = mutableListOf()

        init {
            button.setOnClickListener(this)
            getButtonSequenceFromTable().forEach { listButtonKeywords.add(createButtonKeyword(it)) }
        }


        override fun onClick(p0: View?) {
           if(state == ButtonState.DEACTIVATED){
               setActivated()

               context.lastClick?.setDeactivated()
               context.lastClick = this
            }else{
               setDeactivated()

               context.lastClick = null
            }
        }

        override fun setActivated(){
            super.setActivated()
            setTableVisibility()
        }

        override fun setDeactivated() {
            super.setDeactivated()
            setTableVisibility()
        }


        /**
         * @return true as soon as one of the buttons is clicked
         * */
        fun isButtonPressed() : Boolean =
            listButtonKeywords.any { it.state == ButtonState.ACTIVATED }


        /**
         * @return all buttons that were clicked as sequence
         * */
        fun getClickedButtonsAsSequence() : Sequence<ButtonKeyword> =
            listButtonKeywords.filter { it.state == ButtonState.ACTIVATED }.asSequence()


        private fun setTableVisibility(){
            table.visibility = if(state == ButtonState.ACTIVATED) View.VISIBLE else View.GONE
        }

        private fun createButtonKeyword(button: Button): ButtonKeyword =
            ButtonKeyword(button, context, keywordType)



        private fun getButtonSequenceFromTable(): Sequence<Button> =
            getRowSequenceFromTable().flatMap { it.children.filterIsInstance<Button>()}


        private fun getRowSequenceFromTable(): Sequence<TableRow> =
            tableLayout.children.filterIsInstance<TableRow>()


    }

    private fun isKeywordButtonPressed(): Boolean{
        listButtonGenericKeyword.forEach{
            if(it.isButtonPressed())
                return true
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


}
package com.example.cobex

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.navigation.fragment.findNavController
import com.example.cobex.databinding.FragmentInputKeywordBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * If a new Generic Term should be added to the XML, only the following things have to be modified:
 *
 * A new list must be created for the new terms.
 *
 * @TermType  must be extended.
 *
 * @modifyTermListCounter modifyTermListCounter must be extended
 *
 * @onViewCreated the new previously created list has to be filled like buttonKeyWordFeelingList
 *                and has to be added in "savedList", so that this list is also loaded
 *
 * @onDestroy the new list has to be saved with "saveInstanceOfButtons"
 *
 * */


/**
 * A simple [Fragment] subclass.
 * Use the [InputKeyword.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputKeyword : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var counter = 0

    /** Binding */
    private var _binding: FragmentInputKeywordBinding? = null
    private val binding get() = _binding!!

    /** List of Button that stands for the respective term*/
    private val buttonKeywordFeelingList: MutableList<ButtonKeyword> = mutableListOf()
    private val buttonKeywordEnvironmentList: MutableList<ButtonKeyword> = mutableListOf()

    /**Counter used for the TextView*/
    private var feelingTermListCounter = 0
    private var environmentTermListCounter = 0

    /**All existing term types are to be added here*/
    private enum class TermTyp{
        FEELING,
        ENVIRONMENT
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInputKeywordBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InputKeyword.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InputKeyword().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    /**
     *
     * Change the Counter for the specific TermType
     *
     * @param value value that has to be added, usually 1 or -1
     * @param termTyp The type of term to be incremented or decremented
     *
     *
     * */
    private fun modifyTermListCounter(value: Int, termTyp: TermTyp){
        when(termTyp) {

            TermTyp.FEELING -> {
                feelingTermListCounter += value
                binding.counterKeywordsFeeling.text =
                    Editable.Factory.getInstance().newEditable(feelingTermListCounter.toString())
            }

            TermTyp.ENVIRONMENT -> {
                environmentTermListCounter += value
                binding.counterKeywordsEnvironment.text =
                    Editable.Factory.getInstance().newEditable(environmentTermListCounter.toString())
            }
        }
    }


    /**********************************************************************************/
    /***************************Parent Button Class************************************/
    /**********************************************************************************/
    private abstract class ButtonStruct(val button: Button, context: InputKeyword,) :
    View.OnClickListener{

        enum class ButtonState{
            DEACTIVATED,
            ACTIVATED
        }

        var state = ButtonState.DEACTIVATED
        var context : InputKeyword = context


        /**Depending on the state, the colors of the buttons are set*/
        fun setButtonColour() {
            when(state) {
                ButtonState.ACTIVATED -> button.setBackgroundColor(
                    ContextCompat.getColor( context.requireContext(), R.color.orange))
                ButtonState.DEACTIVATED -> button.setBackgroundColor(
                    ContextCompat.getColor( context.requireContext(), R.color.purple_500))
            }
        }
    }

    /**
     *
     *  The class that represents the simple terms in the form of the button
     *
     *  @param button which stands for a term
     *  @param term To determine the number of buttons clicked for the matching generic term
     *
     */
    private class ButtonKeyword (button: Button, context: InputKeyword, var term: TermTyp)
        : ButtonStruct(button, context) {

        init {
            button.setOnClickListener (this)
        }

        fun modifyState(i: Int){
            if(i == 1){
                state = ButtonState.ACTIVATED
                setButtonColour()
                modifyCounter()
            }
        }

        override fun onClick(p0: View?) {
            if(state == ButtonState.DEACTIVATED) {
                state = ButtonState.ACTIVATED
                setButtonColour()
                modifyCounter()

            } else {
                state = ButtonState.DEACTIVATED
                setButtonColour()
                modifyCounter()
            }
        }

        fun modifyCounter(){
            if(state == ButtonState.ACTIVATED) {
                TermTyp.values().forEach {
                    if (term == it) {
                        context.modifyTermListCounter(1, term)
                    }
                }
            } else {
                TermTyp.values().forEach {
                    if(term == it){
                        context.modifyTermListCounter(-1, term)
                    }
                }
            }
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
    private class ButtonGenericKeyword(button: Button, context: InputKeyword, tableLayout: TableLayout):
        ButtonStruct(button,context) {
        var table: TableLayout = tableLayout

        init {
            button.setOnClickListener(this)
        }

        @SuppressLint("UseRequireInsteadOfGet")
        override fun onClick(p0: View?) {
           if(state == ButtonState.DEACTIVATED){
                state = ButtonState.ACTIVATED
                setButtonColour()

                table.visibility = View.VISIBLE
                context.lastClick?.setOff(context.lastClick!!)
                context.lastClick = this
            }else{
                state = ButtonState.DEACTIVATED
                setButtonColour()

                table.visibility = View.GONE
                context.lastClick = null
            }
        }

        /**To set off the last clicked button*/
        private fun setOff(genericButtonGenericTerm: ButtonGenericKeyword) {
            genericButtonGenericTerm.state = ButtonState.DEACTIVATED
            setButtonColour()
            table.visibility = View.GONE
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


       binding.buttonNext.setOnClickListener {
           findNavController().navigate(R.id.action_inputKeyword_to_inputMelody)
       }

        binding.buttonBackCreate.setOnClickListener {
            findNavController().navigate(R.id.action_inputKeyword_to_CreateNew)
        }


        /**New Generic Buttons should be generate here*/
        ButtonGenericKeyword(binding.buttonFeelings, this, binding.tableFeelings)
        ButtonGenericKeyword(binding.buttonEnvironment, this, binding.tableEnv)

        /**New Tables should be generate here*/
        buttonKeywordEnvironmentList.addAll(
            generateButtonTermFromTable(binding.tableEnv, TermTyp.ENVIRONMENT))
        buttonKeywordFeelingList.addAll(
        generateButtonTermFromTable(binding.tableFeelings, TermTyp.FEELING))



        /** Recreates a saved instance of pressed Button if there is one*/
        val sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val savedList : MutableList<ButtonKeyword> = mutableListOf()

        /**New List has to be added here*/
        savedList.addAll(buttonKeywordEnvironmentList)
        savedList.addAll(buttonKeywordFeelingList)

        sharedPreferences.all?.forEach{
            val buttonKey = it.key.toString().substringAfter(':')
            savedList.forEach{ term->
                recreateSavedInstanceOfButton(term, buttonKey, it.value as Int)
            }
        }
    }

    private fun recreateSavedInstanceOfButton(keyword: ButtonKeyword, btnKeyword: String, value: Int){
        if(keyword.button.text.toString() == btnKeyword) {
            keyword.modifyState(value)
        }
    }

    /**
     *
     *Will iterate the given table layout and generate a ButtonTerm from the existing buttons.
     *
     * @param tableLayout which should be iterate
     * @param term to recognize the category of the button
     *
     * */
    private fun generateButtonTermFromTable(tableLayout: TableLayout, term: TermTyp):
            MutableList<ButtonKeyword> {
        val list  = mutableListOf<ButtonKeyword>()
        tableLayout.children.forEach { child -> if (child is TableRow){
                child.forEach { child_child -> if(child_child is Button) {
                        /**Create a new ButtonTerm*/
                        list.add(ButtonKeyword(child_child, this, term))
                    }
                }
            }
        }
        return list
    }


    override fun onDestroyView() {
        super.onDestroyView()
        val  keywords = activity?.getPreferences(Context.MODE_PRIVATE) ?:return
        with(keywords.edit()){
            clear()

            /**New List has to be added here*/
            saveInstanceOfButtons(buttonKeywordEnvironmentList, this)
            saveInstanceOfButtons(buttonKeywordFeelingList, this)

            commit()

        }
        _binding = null
    }

    private fun saveInstanceOfButtons(list: List<ButtonKeyword>, edit: SharedPreferences.Editor) {
        list.forEach{term -> edit.putInt(
            term.term.ordinal.toString() + ":" + term.button.text.toString(), term.state.ordinal)}
    }

}
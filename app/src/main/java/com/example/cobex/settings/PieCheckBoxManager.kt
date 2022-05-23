package com.example.cobex.settings

import android.view.View
import androidx.cardview.widget.CardView
import com.example.cobex.artifacts.Artifact

class PieCheckBoxManager {

    val map = mutableMapOf<Artifact, CardView>()

    fun toggleCardView(artifact: Artifact){
        map[artifact]?.visibility =
            if(map[artifact]?.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

}
package com.gmail.miloszwasacz.munchkinlevelcounter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.gmail.miloszwasacz.munchkinlevelcounter.PlayerAdapter.MyViewHolder

class PlayerAdapter// Provide a suitable constructor (depends on the kind of dataset)
(var playerList: List<Player>) : RecyclerView.Adapter<PlayerAdapter.MyViewHolder>() {
    var editMode: Boolean = false
    private var listener: OnItemClickListener? = null

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerAdapter.MyViewHolder {
        // create a new view
        val linearLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.player_item, parent, false) as LinearLayout
        return MyViewHolder(linearLayout,
                linearLayout.findViewById<View>(R.id.imageViewFight) as ImageView,
                linearLayout.findViewById<View>(R.id.textViewName) as TextView,
                linearLayout.findViewById<View>(R.id.imageViewAdd) as ImageView,
                linearLayout.findViewById<View>(R.id.textViewLevel) as TextView,
                linearLayout.findViewById<View>(R.id.imageViewRemove) as ImageView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.linearLayout.setOnClickListener(if (editMode)
            View.OnClickListener {
                if (listener != null) {
                    listener!!.onItemClick(holder.adapterPosition)
                }
            }
        else
            null)

        holder.textViewPlayerName.text = playerList[holder.adapterPosition].name
        holder.textViewPlayerLevel.text = Integer.toString(playerList[holder.adapterPosition].level)

        holder.imageViewAdd.setOnClickListener(if (editMode)
            null
        else
            View.OnClickListener {
                if (listener != null) {
                    listener!!.onAddClick(holder.adapterPosition)
                }
            })
        holder.imageViewRemove.setOnClickListener(if (editMode)
            null
        else
            View.OnClickListener {
                if (listener != null) {
                    listener!!.onRemoveClick(holder.adapterPosition)
                }
            })

        holder.imageViewFight.setOnClickListener(if (editMode)
            null
        else
            View.OnClickListener {
                if (listener != null) {
                    listener!!.onFightClick(holder.adapterPosition)
                }
            })
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return playerList.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class MyViewHolder(// each data item is just a string in this case
            var linearLayout: LinearLayout, var imageViewFight: ImageView, var textViewPlayerName: TextView, var imageViewAdd: ImageView, var textViewPlayerLevel: TextView, var imageViewRemove: ImageView) : RecyclerView.ViewHolder(linearLayout)

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onAddClick(position: Int)
        fun onRemoveClick(position: Int)
        fun onFightClick(position: Int)
    }
}


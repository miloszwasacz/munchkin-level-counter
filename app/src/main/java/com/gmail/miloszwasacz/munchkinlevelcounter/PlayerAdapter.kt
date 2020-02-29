package com.gmail.miloszwasacz.munchkinlevelcounter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlayerAdapter// Provide a suitable constructor (depends on the kind of dataset)
(var game: Game) : RecyclerView.Adapter<PlayerAdapter.MyViewHolder>() {
    private var listener: OnItemClickListener? = null
    private val playerListType = object : TypeToken<ArrayList<Player>>() {}.type
    var playerList: ArrayList<Player> = Gson().fromJson<ArrayList<Player>>(game.content, playerListType)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerAdapter.MyViewHolder {
        // create a new view
        val linearLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.player_item, parent, false) as LinearLayout
        return MyViewHolder(linearLayout,
                linearLayout.findViewById<View>(R.id.imageViewEdit) as ImageView,
                linearLayout.findViewById<View>(R.id.textViewName) as TextView,
                linearLayout.findViewById<View>(R.id.imageViewAdd) as ImageView,
                linearLayout.findViewById<View>(R.id.textViewLevel) as TextView,
                linearLayout.findViewById<View>(R.id.imageViewRemove) as ImageView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        playerList = Gson().fromJson<ArrayList<Player>>(game.content, playerListType)
        val level = playerList[holder.adapterPosition].value

        holder.textViewPlayerName.setOnClickListener {
            if (listener != null) {
                listener!!.onFightClick(holder.adapterPosition)
            }
        }

        holder.textViewPlayerName.text = playerList[holder.adapterPosition].name
        holder.textViewPlayerLevel.text = level.toString()

        holder.imageViewAdd.setOnClickListener {
            if (listener != null) {
                listener!!.onAddClick(holder.adapterPosition)
            }
        }
        holder.imageViewRemove.setOnClickListener {
            if (listener != null) {
                listener!!.onRemoveClick(holder.adapterPosition)
            }
        }

        holder.imageViewEdit.setOnClickListener {
            if (listener != null) {
                listener!!.onEditClick(holder.adapterPosition)
            }
        }

        if(level < game.maxLevel)
            holder.imageViewAdd.isEnabled = true
        if(level > game.minLevel)
            holder.imageViewRemove.isEnabled = true
        if(level == game.maxLevel)
            holder.imageViewAdd.isEnabled = false
        if(level == game.minLevel)
            holder.imageViewRemove.isEnabled = false
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
            linearLayout: LinearLayout, var imageViewEdit: ImageView, var textViewPlayerName: TextView, var imageViewAdd: ImageView, var textViewPlayerLevel: TextView, var imageViewRemove: ImageView) : RecyclerView.ViewHolder(linearLayout)

    interface OnItemClickListener {
        fun onEditClick(position: Int)
        fun onAddClick(position: Int)
        fun onRemoveClick(position: Int)
        fun onFightClick(position: Int)
    }
}


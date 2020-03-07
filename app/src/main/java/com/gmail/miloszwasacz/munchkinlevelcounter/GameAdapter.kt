package com.gmail.miloszwasacz.munchkinlevelcounter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GameAdapter
(private var gameList: ArrayList<Game>) : RecyclerView.Adapter<GameAdapter.MyViewHolder>() {
    private var listener: OnItemClickListener? = null

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // create a new view
        val linearLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.game_item, parent, false) as LinearLayout
        return MyViewHolder(linearLayout,
                linearLayout.findViewById<View>(R.id.textViewName) as TextView,
                linearLayout.findViewById<View>(R.id.imageViewSettings) as ImageView,
                linearLayout.findViewById<View>(R.id.imageViewDelete) as ImageView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.linearLayout.setOnClickListener {
            if (listener != null) {
                listener!!.onItemClick(holder.adapterPosition)
            }
        }

        holder.textViewGameName.text = gameList[holder.adapterPosition].name

        holder.imageViewSettings.setOnClickListener {
            if (listener != null) {
                listener!!.onSettingsClick(holder.adapterPosition)
            }
        }
        holder.imageViewDelete.setOnClickListener {
            if (listener != null) {
                listener!!.onDeleteClick(holder.adapterPosition)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return gameList.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class MyViewHolder(// each data item is just a string in this case
            var linearLayout: LinearLayout, var textViewGameName: TextView, var imageViewSettings: ImageView, var imageViewDelete: ImageView) : RecyclerView.ViewHolder(linearLayout)

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onSettingsClick(position: Int)
        fun onDeleteClick(position: Int)
    }
}
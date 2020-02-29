package com.gmail.miloszwasacz.munchkinlevelcounter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class KillOMeterAdapter
(private var fieldList: ArrayList<BaseItem>, private var game: Game) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listener: OnItemClickListener? = null

    private var TYPE_PLAYER = 0
    private var TYPE_MONSTER = 1
    private var TYPE_BONUS = 2

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // create a new view

        val playerLinearLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.kill_o_meter_player_item, parent, false) as LinearLayout
        val monsterLinearLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.kill_o_meter_monster_item, parent, false) as LinearLayout
        val bonusLinearLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.kill_o_meter_bonus_item, parent, false) as LinearLayout
        return when(viewType) {
            TYPE_PLAYER -> PlayerViewHolder(playerLinearLayout,
                    playerLinearLayout.findViewById<View>(R.id.textViewName) as TextView,
                    playerLinearLayout.findViewById<View>(R.id.imageViewRemove) as ImageView,
                    playerLinearLayout.findViewById<View>(R.id.textViewLevel) as TextView,
                    playerLinearLayout.findViewById<View>(R.id.imageViewAdd) as ImageView,
                    playerLinearLayout.findViewById<View>(R.id.imageViewAddBonus) as ImageView,
                    playerLinearLayout.findViewById<View>(R.id.imageViewDelete) as ImageView)
            TYPE_MONSTER -> MonsterViewHolder(monsterLinearLayout,
                    monsterLinearLayout.findViewById<View>(R.id.textViewName) as TextView,
                    monsterLinearLayout.findViewById<View>(R.id.textViewLevel) as TextView,
                    monsterLinearLayout.findViewById<View>(R.id.imageViewAddBonus) as ImageView,
                    monsterLinearLayout.findViewById<View>(R.id.imageViewDelete) as ImageView)
            else -> BonusViewHolder(bonusLinearLayout,
                    bonusLinearLayout.findViewById<View>(R.id.textViewName) as TextView,
                    bonusLinearLayout.findViewById<View>(R.id.textViewValue) as TextView,
                    bonusLinearLayout.findViewById<View>(R.id.imageViewDelete) as ImageView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(fieldList[position]) {
            is Player -> TYPE_PLAYER
            is Monster -> TYPE_MONSTER
            else -> TYPE_BONUS
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        when(holder.itemViewType) {
            TYPE_PLAYER -> {
                val playerViewHolder = holder as PlayerViewHolder
                val item: Player = fieldList[holder.adapterPosition] as Player
                holder.textViewFieldName.text = item.name
                holder.textViewLevel.text = item.value.toString()

                holder.imageViewDelete.isEnabled = holder.adapterPosition != 0

                holder.imageViewAdd.setOnClickListener {
                    if (listener != null) {
                        listener!!.onAddClick(holder.adapterPosition)
                    }
                }

                playerViewHolder.imageViewRemove.setOnClickListener {
                    if (listener != null) {
                        listener!!.onRemoveClick(holder.adapterPosition)
                    }
                }

                holder.imageViewAddBonus.setOnClickListener {
                    if (listener != null) {
                        listener!!.onAddBonusClick(holder.adapterPosition)
                    }
                }

                holder.imageViewDelete.setOnClickListener {
                    if (listener != null) {
                        listener!!.onDeleteClick(holder.adapterPosition)
                    }
                }

                if(item.value < game.maxLevel)
                    holder.imageViewAdd.isEnabled = true
                if(item.value > game.minLevel)
                    holder.imageViewRemove.isEnabled = true
                if(item.value == game.maxLevel)
                    holder.imageViewAdd.isEnabled = false
                if(item.value == game.minLevel)
                    holder.imageViewRemove.isEnabled = false
            }
            TYPE_MONSTER -> {
                val monsterViewHolder = holder as MonsterViewHolder
                val item: Monster = fieldList[holder.adapterPosition] as Monster
                holder.textViewFieldName.text = item.name
                holder.textViewLevel.text = item.value.toString()

                holder.imageViewAddBonus.setOnClickListener {
                    if (listener != null) {
                        listener!!.onAddBonusClick(holder.adapterPosition)
                    }
                }

                holder.imageViewDelete.setOnClickListener {
                    if (listener != null) {
                        listener!!.onDeleteClick(holder.adapterPosition)
                    }
                }
            }
            else -> {
                val bonusViewHolder = holder as BonusViewHolder
                holder.textViewValue.text = fieldList[holder.adapterPosition].value.toString()

                holder.imageViewDelete.setOnClickListener {
                    if (listener != null) {
                        listener!!.onDeleteClick(holder.adapterPosition)
                    }
                }
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return fieldList.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class PlayerViewHolder(// each data item is just a string in this case
            var linearLayout: LinearLayout, var textViewFieldName: TextView, var imageViewRemove: ImageView, var textViewLevel: TextView, var imageViewAdd: ImageView, var imageViewAddBonus: ImageView, var imageViewDelete: ImageView) : RecyclerView.ViewHolder(linearLayout)

    class MonsterViewHolder(// each data item is just a string in this case
            var linearLayout: LinearLayout, var textViewFieldName: TextView, var textViewLevel: TextView, var imageViewAddBonus: ImageView, var imageViewDelete: ImageView) : RecyclerView.ViewHolder(linearLayout)

    class BonusViewHolder(// each data item is just a string in this case
            var linearLayout: LinearLayout, var textViewFieldName: TextView, var textViewValue: TextView, var imageViewDelete: ImageView) : RecyclerView.ViewHolder(linearLayout)

    interface OnItemClickListener {
        fun onAddClick(position: Int)
        fun onRemoveClick(position: Int)
        fun onAddBonusClick(position: Int)
        fun onDeleteClick(position: Int)
    }
}
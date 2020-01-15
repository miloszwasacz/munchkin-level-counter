package com.gmail.miloszwasacz.munchkinlevelcounter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KillOMeterAdapter
(var categoryList: ArrayList<Category>) : RecyclerView.Adapter<KillOMeterAdapter.MyViewHolder>() {
    private var listener: OnItemClickListener? = null

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KillOMeterAdapter.MyViewHolder {
        // create a new view
        val linearLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.kill_o_meter_item, parent, false) as LinearLayout
        return MyViewHolder(linearLayout,
                linearLayout.findViewById<View>(R.id.textViewName) as TextView,
                linearLayout.findViewById<View>(R.id.imageViewAdd) as ImageView,
                linearLayout.findViewById<View>(R.id.textViewValue) as TextView,
                linearLayout.findViewById<View>(R.id.imageViewRemove) as ImageView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val value = categoryList[holder.adapterPosition].value
        val bracket = holder.textViewValue
        val maxValue = categoryList[holder.adapterPosition].maxValue
        val minValue = categoryList[holder.adapterPosition].minValue
        val editable = categoryList[holder.adapterPosition].editable

        holder.textViewName.text = categoryList[holder.adapterPosition].name
        holder.textViewValue.text = value.toString()

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

        /*
        holder.editTextValue.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            KillOMeterActivity().removeLeadingZeros(bracket)

            if (bracket.text.toString() == "" || KillOMeterActivity().tryParse(bracket.text.toString(), maxValue) < minValue)
                bracket.setText(minValue.toString())
            else if (KillOMeterActivity().tryParse(bracket.text.toString(), maxValue) >= maxValue)
                bracket.setText(maxValue.toString())

            //categoryList[categoryList.size - 1].value -= categoryList[position].value

            categoryList[position].value = bracket.text.toString().toInt()

            //categoryList[categoryList.size - 1].value += categoryList[position].value

            if(editable) {
                val value = categoryList[holder.adapterPosition].value
                if(value < maxValue) holder.imageViewAdd.visibility = View.VISIBLE
                if(value > minValue) holder.imageViewRemove.visibility = View.VISIBLE
                if(value == maxValue) holder.imageViewAdd.visibility = View.INVISIBLE
                if(value == minValue) holder.imageViewRemove.visibility = View.INVISIBLE
            } else {
                holder.imageViewAdd.visibility = View.INVISIBLE
                holder.imageViewRemove.visibility = View.INVISIBLE
            }
        }*/

        if(editable) {
            val value = categoryList[holder.adapterPosition].value
            if(value < maxValue) holder.imageViewAdd.visibility = View.VISIBLE
            if(value > minValue) holder.imageViewRemove.visibility = View.VISIBLE
            if(value == maxValue) holder.imageViewAdd.visibility = View.INVISIBLE
            if(value == minValue) holder.imageViewRemove.visibility = View.INVISIBLE
        } else {
            holder.imageViewAdd.visibility = View.INVISIBLE
            holder.imageViewRemove.visibility = View.INVISIBLE
            //holder.textViewValue.isEnabled = false
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return categoryList.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class MyViewHolder(// each data item is just a string in this case
            linearLayout: LinearLayout, var textViewName: TextView, var imageViewAdd: ImageView, var textViewValue: TextView, var imageViewRemove: ImageView) : RecyclerView.ViewHolder(linearLayout)

    interface OnItemClickListener {
        fun onAddClick(position: Int)
        fun onRemoveClick(position: Int)
    }
}
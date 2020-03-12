package com.gmail.miloszwasacz.munchkinlevelcounter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


/*class KillOMeterPagerAdapter(private val viewList: ArrayList<View>, private val titleList: ArrayList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val layout = viewList[position]
        collection.addView(layout)
        return layout
    }

    override fun getItemCount(): Int {
        return viewList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }

}*/
class KillOMeterPagerAdapter(private val layouts: ArrayList<RecyclerView>): RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private var TYPE_PLAYER = 0
    private var TYPE_MONSTER = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        /*val playerRecyclerView = LayoutInflater.from(parent.context)
                .inflate(R.layout.player_kill_o_meter, parent, false) as RecyclerView
        val monsterRecyclerView = LayoutInflater.from(parent.context)
                .inflate(R.layout.monster_kill_o_meter, parent, false) as RecyclerView
        return when(viewType) {
            TYPE_PLAYER -> PagerViewHolder(playerRecyclerView)
            else -> PagerViewHolder(monsterRecyclerView)
        }*/
        return PagerViewHolder(layouts[viewType])
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> TYPE_PLAYER
            else -> TYPE_MONSTER
        }
    }

    override fun getItemCount(): Int {
        return layouts.size
    }

    class PagerViewHolder(recyclerView: RecyclerView): RecyclerView.ViewHolder(recyclerView)
}

    /*
    override fun getItemViewType(position: Int): Int { // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // create a new view
        val playerConstraintLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.player_kill_o_meter, parent, false) as ConstraintLayout
        val monsterConstraintLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.monster_kill_o_meter, parent, false) as ConstraintLayout
        return when(viewType) {
            0 -> playerViewHolder(playerConstraintLayout, playerConstraintLayout.findViewById<View>(R.id.playerRecyclerView) as RecyclerView)
            else -> monsterViewHolder(monsterConstraintLayout, monsterConstraintLayout.findViewById<View>(R.id.monsterRecyclerView) as RecyclerView)
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(holder.itemViewType == 0) {
            val playerViewHolder = holder as playerViewHolder
        }
        else {
            val monsterViewHolder = holder as monsterViewHolder
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return 2
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class playerViewHolder(// each data item is just a string in this case
            var constraintLayout: ConstraintLayout, var recyclerView: RecyclerView) : RecyclerView.ViewHolder(constraintLayout)

    class monsterViewHolder(// each data item is just a string in this case
            var constraintLayout: ConstraintLayout, var recyclerView: RecyclerView) : RecyclerView.ViewHolder(constraintLayout)*/

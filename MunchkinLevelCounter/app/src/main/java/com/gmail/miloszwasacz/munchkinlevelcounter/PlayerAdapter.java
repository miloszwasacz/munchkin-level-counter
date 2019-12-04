package com.gmail.miloszwasacz.munchkinlevelcounter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.MyViewHolder>
{
    public List<Player> playerList;
    public boolean editMode;
    private OnItemClickListener listener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlayerAdapter(List<Player> playerList)
    {
        this.playerList = playerList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PlayerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType)
    {
        // create a new view
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.player_item, parent, false);
        MyViewHolder vh = new MyViewHolder(linearLayout,
                (ImageView) linearLayout.findViewById(R.id.imageViewFight),
                (TextView) linearLayout.findViewById(R.id.textViewPlayerName),
                (ImageView) linearLayout.findViewById(R.id.imageViewAdd),
                (TextView) linearLayout.findViewById(R.id.textViewPlayerLevel),
                (ImageView) linearLayout.findViewById(R.id.imageViewRemove));
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.linearLayout.setOnClickListener(editMode ? new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onItemClick(holder.getAdapterPosition());
                }
            }
        } : null);

        holder.textViewPlayerName.setText(playerList.get(holder.getAdapterPosition()).name);
        holder.textViewPlayerLevel.setText(Integer.toString(playerList.get(holder.getAdapterPosition()).level));

        holder.imageViewAdd.setOnClickListener(editMode ? null : new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onAddClick(holder.getAdapterPosition());
                }
            }
        });
        holder.imageViewRemove.setOnClickListener(editMode ? null : new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onRemoveClick(holder.getAdapterPosition());
                }
            }
        });

        holder.imageViewFight.setOnClickListener(editMode ? null : new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onFightClick(holder.getAdapterPosition());
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return playerList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        public LinearLayout linearLayout;
        public ImageView imageViewFight;
        public TextView textViewPlayerName;
        public ImageView imageViewAdd;
        public TextView textViewPlayerLevel;
        public ImageView imageViewRemove;

        public MyViewHolder(LinearLayout linearLayout,ImageView imageViewFight, TextView textViewPlayerName, ImageView imageViewAdd, TextView textViewPlayerLevel, ImageView imageViewRemove)
        {
            super(linearLayout);
            this.imageViewFight = imageViewFight;
            this.linearLayout = linearLayout;
            this.textViewPlayerName = textViewPlayerName;
            this.imageViewAdd = imageViewAdd;
            this.textViewPlayerLevel = textViewPlayerLevel;
            this.imageViewRemove = imageViewRemove;
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(int position);
        void onAddClick(int position);
        void onRemoveClick(int position);
        void onFightClick(int position);
    }
}


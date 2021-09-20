package com.acn.sgbustimer.menu

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.acn.sgbustimer.R
import com.acn.sgbustimer.model.NavigationItemModel

class NavigationDrawerAdapter(private var items: ArrayList<NavigationItemModel>, private var currentPos: Int) :
    RecyclerView.Adapter<NavigationDrawerAdapter.NavigationItemViewHolder>() {

    private lateinit var context: Context
    private lateinit var navIcon: ImageView
    private lateinit var navText: TextView

    class NavigationItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationItemViewHolder {
        context = parent.context
        val navItem = LayoutInflater.from(parent.context).inflate(R.layout.navigation_drawer_row, parent, false)
        return NavigationItemViewHolder(navItem)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: NavigationItemViewHolder, position: Int) {
        // To highlight the selected item, show different background color
        if (position == currentPos) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

        navIcon = holder.itemView.findViewById<ImageView>(R.id.navigation_icon)
        navText = holder.itemView.findViewById<TextView>(R.id.navigation_title)

        navIcon.setColorFilter(ContextCompat.getColor(context, R.color.iconColor))
        navText.setTextColor(ContextCompat.getColor(context, R.color.mainTextColor))

        navText.text = items[position].title

        navIcon.setImageResource(items[position].icon)
    }

}
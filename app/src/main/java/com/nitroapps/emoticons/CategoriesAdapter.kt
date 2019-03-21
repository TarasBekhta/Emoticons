package com.nitroapps.emoticons

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nitroapps.emoticons.events.CategorySelectedEvent
import kotlinx.android.synthetic.main.emoticon_item.view.*
import org.greenrobot.eventbus.EventBus

class CategoriesAdapter(
    private val context: Context,
    val categories: List<String>
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.emoticon_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currCategory = categories[position]
        holder.emoticonValueTextView.text = currCategory
        holder.isFavoriteImageView.visibility = View.GONE

        holder.itemView.setOnClickListener {
            EventBus.getDefault().post(CategorySelectedEvent(position + 1))
        }
    }

    override fun getItemCount() = categories.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var emoticonValueTextView: TextView = itemView.emoticonTextView
        var isFavoriteImageView: ImageView = itemView.favoriteImageView
        var mainLayout: ConstraintLayout = itemView.mainLayout
    }
}

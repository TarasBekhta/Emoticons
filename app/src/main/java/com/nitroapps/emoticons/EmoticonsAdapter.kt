package com.nitroapps.emoticons

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.nitroapps.emoticons.animation.AnimationUtil
import kotlinx.android.synthetic.main.emoticon_item.view.*


class EmoticonsAdapter(private val context: Context, val emoticons : List<EmoticonEntity>, val listener: FavoriteStatusListener) : RecyclerView.Adapter<EmoticonsAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val v = LayoutInflater.from(context).inflate(R.layout.emoticon_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val currEmoticon = emoticons[position]
        holder.emoticonValueTextView.text = currEmoticon.value
        holder.isFavoriteImageView.setImageResource(
                if(currEmoticon.isFavorite) {
                    R.drawable.ic_favorite_red
                } else {
                    R.drawable.ic_favorite_dark
                })

        holder.isFavoriteImageView.setOnClickListener {
            currEmoticon.isFavorite = !currEmoticon.isFavorite
            AnimationUtil.animateImageViewResourceChange(it as ImageView, if(currEmoticon.isFavorite) R.drawable.ic_favorite_red else R.drawable.ic_favorite_dark, 250)
            listener.favoriteStatusChanged(currEmoticon, position)
        }

        holder.mainLayout.setOnClickListener {
            val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText(currEmoticon.uid.toString(), currEmoticon.value)
            clipboard!!.primaryClip = clip
            Toast.makeText(context, "${currEmoticon.value} was copied to your clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = emoticons.size

    fun getEmoticonPositionByValue(value: String): Int {
        for((i, emoticon) in emoticons.withIndex()) {
            if(emoticon.value == value) {
                return i
            }
        }
        return 0
    }

    interface FavoriteStatusListener {
        fun favoriteStatusChanged(emoticon: EmoticonEntity, position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var emoticonValueTextView: TextView = itemView.emoticonTextView
        var isFavoriteImageView: ImageView = itemView.favoriteImageView
        var mainLayout: ConstraintLayout = itemView.mainLayout
    }

}
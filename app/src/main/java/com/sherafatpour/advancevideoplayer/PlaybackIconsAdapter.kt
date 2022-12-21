package com.sherafatpour.advancevideoplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sherafatpour.advancevideoplayer.IconType.*
import com.sherafatpour.advancevideoplayer.databinding.IconsLayoutBinding

class PlaybackIconsAdapter(private val iconModelList: ArrayList<IconModel>)
    :RecyclerView.Adapter<PlaybackIconsAdapter.PlaybackIconViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaybackIconViewHolder {
        val binding =
            IconsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaybackIconViewHolder(binding)
    }

    override fun getItemCount() = iconModelList.size

    override fun onBindViewHolder(holder: PlaybackIconViewHolder, position: Int) {
        with(holder){
            with(iconModelList[position]) {


                binding.playbackIcon.setImageResource(imageView)
                binding.iconTitle.text = iconModelList[position].iconTitle

           /*     holder.itemView.setOnClickListener {
                    when(iconModelList[position].type){
                        NIGHT -> {
                            Toast.makeText(holder.itemView.context, "NIGHT",
                                Toast.LENGTH_SHORT).show()
                        }
                        MUTE -> {
                            Toast.makeText(holder.itemView.context, "MUTE",
                                Toast.LENGTH_SHORT).show()
                        }
                        ROTATE -> {
                            Toast.makeText(holder.itemView.context, "ROTATE",
                                Toast.LENGTH_SHORT).show()
                        }
                        BACK -> {
                            Toast.makeText(holder.itemView.context, "BACK",
                                Toast.LENGTH_SHORT).show()
                        }
                    }

                }*/
            }
        }
    }



    inner class PlaybackIconViewHolder( val binding: IconsLayoutBinding)
        :RecyclerView.ViewHolder(binding.root)

}
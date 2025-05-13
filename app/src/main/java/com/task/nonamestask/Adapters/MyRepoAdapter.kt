package com.task.nonamestask.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.task.nonamestask.Models.Data
import com.task.nonamestask.R
import java.util.concurrent.CopyOnWriteArrayList

class MyRepoAdapter(var context: Context) :
    RecyclerView.Adapter<MyRepoAdapter.ViewHolder>() {

        var dataList:ArrayList<Data> = ArrayList()


    var inflater: LayoutInflater

    init {

        this.inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.single_item_repo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: Data = dataList[position]


        holder.repoName.text = data.name

        if (data.description!= null) {

            holder.desc.text = data.description.toString()

        } else {

            holder.desc.visibility = View.GONE
        }

        holder.lang.text = data.language

        holder.forkText.setText(data.forksCount.toString())
        holder.starText.setText(data.stargazersCount.toString())








        //Log.d(MyRepoAdapter.class.getName(), "onBindViewHolder: " + myOwnRepo.getLicense().getKey());
//        holder.itemView.setOnClickListener {
//            val intent1 = Intent(context, RepoWebViewActivity::class.java)
//            intent1.putExtra("url", myOwnRepo.getHtmlUrl())
//            context.startActivity(intent1)
//        }

        holder.icon.setOnClickListener { v ->
            val popupMenu = PopupMenu(context, v)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.show()

            val menuItem = popupMenu.menu.getItem(0)
            menuItem.setOnMenuItemClickListener {
                val intent1 = Intent(Intent.ACTION_SEND)
                intent1.putExtra(Intent.EXTRA_TITLE, data.name)
                intent1.putExtra(Intent.EXTRA_TEXT, data.htmlUrl)
                intent1.setType("text/plain")

                context.startActivity(Intent.createChooser(intent1, "Share"))
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    public fun setData(newList:ArrayList<Data>) {

        dataList = newList
        notifyDataSetChanged()


    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView? = null
        var repoName: TextView = itemView.findViewById<View>(R.id.repo_name) as TextView

        var icon: ImageView = itemView.findViewById<View>(R.id.icon) as ImageView

        var desc: TextView = itemView.findViewById<View>(R.id.repo_desc) as TextView

        var lang:TextView = itemView.findViewById(R.id.repo_lang)

        val forkText:TextView = itemView.findViewById(R.id.fork_text)
        val starText:TextView = itemView.findViewById(R.id.star_text)


    }
}

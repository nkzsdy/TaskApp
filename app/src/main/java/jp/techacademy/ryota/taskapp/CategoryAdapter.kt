package jp.techacademy.ryota.taskapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CategoryAdapter(context: Context) : BaseAdapter() {
    private val mLayoutInflater: LayoutInflater
    var categoryList = mutableListOf<Category>()

    init {
        this.mLayoutInflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return categoryList.size
    }

    override fun getItem(position: Int): Any {
        return categoryList[position]
    }

    override fun getItemId(position: Int): Long {
        return categoryList[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: mLayoutInflater.inflate(
            android.R.layout.simple_spinner_dropdown_item,
            null
        )
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = categoryList[position].name

        return view
    }
}
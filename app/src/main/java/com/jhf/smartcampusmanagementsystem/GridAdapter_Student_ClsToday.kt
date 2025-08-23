package com.jhf.smartcampusmanagementsystem
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class GridAdapter_Student_ClsToday(
    private val courseList: List<GridViewModel>,
    private val context: Context
) :
    BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var datetime: TextView
    private lateinit var result: TextView
    private lateinit var button: TextView

    override fun getCount(): Int {
        return courseList.size
    }
    override fun getItem(position: Int): Any? {
        return null
    }
    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView

        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.gridview_item, null)
        }

        title= convertView!!.findViewById(R.id.title)
        description= convertView!!.findViewById(R.id.description)
        datetime= convertView!!.findViewById(R.id.datetime)
        result= convertView!!.findViewById(R.id.result)
        button= convertView!!.findViewById(R.id.button)

        title.setText(courseList.get(position).title)
        description.setText(courseList.get(position).description)
        datetime.setText(courseList.get(position).datetime)
        result.setText(courseList.get(position).result)
        button.setText(courseList.get(position).button)


        return convertView
    }
}

//package com.example.myapplication
//
//import com.example.myapplication.R
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.ImageView
//import android.widget.TextView
//
//
//class SpinnerArrayAdapter(context: Context, resource: Int, objects: MutableList<String>) : ArrayAdapter<String>(context, resource,objects) {
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        return getcustomView(position, convertView, parent)
//    }
//
//    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
//        return getcustomView(position, convertView, parent)
//    }
//     fun getcustomView(position: Int, convertView: View?, parent: ViewGroup?): View {
//         var convertView = convertView
//         if (convertView == null) {
//             val inflater =LayoutInflater.from(context)
//             convertView = inflater.inflate(com.example.myapplication.R.layout.help_page_data, parent, false)
//         }
//         val tv = convertView!!.findViewById<View>(R.id.titlefl) as TextView
//         val vi = convertView!!.findViewById<View>(R.id.image) as View
//         if(position==0) {
//             tv.setText("How to Learn gestures")
//             vi.minimumHeight=0;
//         }
//         else if(position%2==0) {
//             tv.setText("");
//             vi.minimumHeight=200;
//         }
//         else {
//             tv.setText("parahraph 1");
//             vi.minimumHeight = 200
//         }
////        val iv = convertView.findViewById<View>(R.id.icon) as ImageView
////        iv.setImageResource(i.get(position))
//         return convertView
//    }
//}
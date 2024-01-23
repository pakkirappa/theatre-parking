package  com.example.theaterparking.ui.spinners

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.theaterparking.R

class CustomSpinnerAdapter(
    context: Context,
    private val items: List<String>
) : ArrayAdapter<String>(context, R.layout.dropdown_spinner, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        view.findViewById<TextView>(R.id.text1).text = getItem(position)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        view.findViewById<TextView>(R.id.text1).text = getItem(position)
        return view
    }
}
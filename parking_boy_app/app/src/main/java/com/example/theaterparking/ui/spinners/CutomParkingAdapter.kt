package  com.example.theaterparking.ui.spinners

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.theaterparking.R
import com.example.theaterparking.dto.Parking

class CustomParkingAdapter(
    context: Context,
    private val items: List<Parking>
) : ArrayAdapter<Parking>(context, R.layout.parking_list_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.parking_list_item, parent, false)
        }

        val timeView: TextView = view!!.findViewById(R.id.list_item_time)
        timeView.text = getItem(position)!!.time

        val vNumberView: TextView = view.findViewById(R.id.list_item_vNumber)
        vNumberView.text = getItem(position)!!.vehicleNumber

        val amountView: TextView = view.findViewById(R.id.list_item_amount)
        amountView.text = getItem(position)!!.amount

        return view
    }
}
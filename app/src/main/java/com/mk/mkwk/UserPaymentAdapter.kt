package com.mk.mkwk

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot

class UserPaymentAdapter(private val paymentDocuments: List<DocumentSnapshot>) :
    RecyclerView.Adapter<UserPaymentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val spinner: Spinner = view.findViewById(R.id.serviceSpinner)
        val nameText: TextView = view.findViewById(R.id.nameTextView)
        val ibanText: TextView = view.findViewById(R.id.ibanTextView)
        val title: TextView = view.findViewById(R.id.paymentMethodTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_method, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = 1 // Single item in RecyclerView

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = "Payment Method"

        // Extract service names from all documents
        val serviceNames = paymentDocuments.map { doc ->
            doc.getString("services") ?: "Unknown Service"
        }

        // Create spinner adapter
        val adapter = ArrayAdapter(
            holder.itemView.context,
            android.R.layout.simple_spinner_item,
            serviceNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinner.adapter = adapter

        holder.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val selectedDoc = paymentDocuments[pos]
                val name = selectedDoc.getString("name") ?: "-"
                val iban = selectedDoc.getString("iban") ?: "-"

                holder.nameText.text = name
                holder.ibanText.text = iban

                // Setup long press to copy
                setupLongPressCopy(holder.itemView.context, holder.nameText, name, "Name")
                setupLongPressCopy(holder.itemView.context, holder.ibanText, iban, "IBAN")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Select first item by default
        if (paymentDocuments.isNotEmpty()) {
            holder.spinner.setSelection(0)
        }
    }

    private fun setupLongPressCopy(
        context: Context,
        textView: TextView,
        text: String,
        label: String
    ) {
        textView.setOnLongClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
            Toast.makeText(context, "$label copied!", Toast.LENGTH_SHORT).show()
            true
        }
    }
}
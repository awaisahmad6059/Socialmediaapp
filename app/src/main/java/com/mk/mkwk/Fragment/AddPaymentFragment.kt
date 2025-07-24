package com.mk.mkwk.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.mk.mkwk.databinding.FragmentAddPaymentBinding

class AddPaymentFragment : Fragment() {

    private var _binding: FragmentAddPaymentBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPaymentBinding.inflate(inflater, container, false)

        // Add input fields (1 field per service, iban, name)
        addInputField(binding.serviceSection, "Enter Service Name")
        addInputField(binding.ibanSection, "Enter IBAN")
        addInputField(binding.nameSection, "Enter Name")

        binding.saveButton.setOnClickListener {
            val service = extractSingleText(binding.serviceSection)
            val iban = extractSingleText(binding.ibanSection)
            val name = extractSingleText(binding.nameSection)

            if (service.isEmpty() || iban.isEmpty() || name.isEmpty()) {
                Toast.makeText(context, "All fields required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create one document with fields: service, iban, name
            val paymentData = hashMapOf(
                "services" to service,
                "iban" to iban,
                "name" to name
            )

            firestore.collection("paymentMethods")
                .add(paymentData)
                .addOnSuccessListener {
                    Toast.makeText(context, "Saved successfully", Toast.LENGTH_SHORT).show()
                    clearFields(binding.serviceSection)
                    clearFields(binding.ibanSection)
                    clearFields(binding.nameSection)
                    (activity as? TabSwitcher)?.switchToTab(0)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return binding.root
    }

    private fun addInputField(section: LinearLayout, hint: String) {
        val editText = EditText(context)
        editText.hint = hint
        editText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 8, 0, 8)
        }
        editText.setPadding(24, 16, 24, 16)
        section.addView(editText)
    }

    private fun extractSingleText(section: LinearLayout): String {
        for (i in 0 until section.childCount) {
            val view = section.getChildAt(i)
            if (view is EditText) {
                return view.text.toString().trim()
            }
        }
        return ""
    }

    private fun clearFields(section: LinearLayout) {
        for (i in 0 until section.childCount) {
            val view = section.getChildAt(i)
            if (view is EditText) {
                view.text.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface TabSwitcher {
        fun switchToTab(position: Int)
    }
}

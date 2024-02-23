package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject


class RazorpayPayment : AppCompatActivity(), PaymentResultListener {
    // variables for our
    // edit text and button.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.razorpaytest)

        // initializing all our variables.
        var amountEdt = findViewById<EditText>(R.id.idEdtAmount)
        var payBtn = findViewById<Button>(R.id.idBtnPay)

        // adding on click listener to our button.
        payBtn.setOnClickListener(View.OnClickListener {
            // on below line we are getting
            // amount that is entered by user.
            val samount = amountEdt.getText().toString()

            // rounding off the amount.
            val amount = Math.round(samount.toFloat() * 100)

            // initialize Razorpay account.
            val checkout = Checkout()

            // set your id as below
            checkout.setKeyID("rzp_live_yj2a9877Nzg571")

            // set image
            checkout.setImage(R.drawable.small_logo)

            // initialize json object
            val `object` = JSONObject()
            try {
                // to put name
                `object`.put("name", "Let'sTalkSign")

                // put description
                `object`.put("description", "Test payment")

                // to set theme color
                `object`.put("theme.color", "")

                // put the currency
                `object`.put("currency", "INR")

                // put amount
                `object`.put("amount", amount)

                // put mobile number
                `object`.put("prefill.contact", "9087442642")

                // put email
                `object`.put("prefill.email", "arvindsuresh2002@gmail.com")

                // open razorpay to checkout activity
                checkout.open(this@RazorpayPayment, `object`)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        })
    }

    override fun onPaymentSuccess(s: String) {
        // this method is called on payment success.
        Toast.makeText(this, "Payment is successful : $s", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(i: Int, s: String) {
        // on payment failed.
        Toast.makeText(this, "Payment Failed due to error : $s", Toast.LENGTH_SHORT).show()
    }
}
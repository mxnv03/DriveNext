package com.example.drivenext
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.LinearLayout

class Onboarding3 : BaseActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_onboarding3)
		val buttonNext = findViewById<TextView>(R.id.textView)
		buttonNext.setOnClickListener {
			val intent = Intent(this, LoginActivity::class.java)
			startActivity(intent)
		}
		val buttonPass = findViewById<LinearLayout>(R.id.button_layout)
		buttonPass.setOnClickListener {
			val intent = Intent(this, LoginAuthActivity::class.java)
			startActivity(intent)
		}
	}
}
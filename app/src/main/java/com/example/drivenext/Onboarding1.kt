package com.example.drivenext
import android.os.Bundle
import android.content.Intent
import android.widget.LinearLayout
import android.widget.TextView

class Onboarding1 : BaseActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_onboarding1)
		val buttonNext = findViewById<LinearLayout>(R.id.button_layout)
		buttonNext.setOnClickListener {
			val intent = Intent(this, Onboarding2::class.java)
			startActivity(intent)
		}
		val buttonPass = findViewById<TextView>(R.id.textView)
		buttonPass.setOnClickListener {
			val intent = Intent(this, LoginAuthActivity::class.java)
			startActivity(intent)
		}
	}
}
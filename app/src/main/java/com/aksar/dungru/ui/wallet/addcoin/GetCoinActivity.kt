package com.aksar.dungru.ui.wallet.addcoin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.aksar.dungru.R
import com.aksar.dungru.adapters.GetCoinAdapter
import com.aksar.dungru.databinding.ActivityCoinGetBinding
import com.aksar.dungru.models.GetCoinModel

class GetCoinActivity : AppCompatActivity(),OnClickListener{
    lateinit var binding:ActivityCoinGetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinGetBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val layoutManager = LinearLayoutManager(this)
        binding.coinRec.layoutManager = layoutManager

        val data = listOf(
            GetCoinModel("90","98",R.drawable.coin),
            GetCoinModel("149","148",R.drawable.coin),
            GetCoinModel("499","498",R.drawable.coin),
            GetCoinModel("999","998",R.drawable.coin)
        )

        val adapter = GetCoinAdapter(this,data){item->
            /**Api call*/
            startActivity(Intent(this,CoinPaymentMethod::class.java))
        }
        binding.coinRec.adapter = adapter
    }

    override fun onClick(view: View?) {
       when(view){
           binding.btnBack->onBackPressed()
       }
    }


}

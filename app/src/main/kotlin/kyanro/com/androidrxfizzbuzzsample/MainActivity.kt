package kyanro.com.androidrxfizzbuzzsample

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jakewharton.rxbinding.view.RxView
import jp.keita.kagurazaka.rxproperty.ReadOnlyRxProperty
import jp.keita.kagurazaka.rxproperty.RxProperty
import jp.keita.kagurazaka.rxproperty.toRxProperty
import kyanro.com.androidrxfizzbuzzsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModel(RxView.clicks(binding.button))
        binding.viewModel = viewModel
    }

    class ViewModel(buttonTapStream: rx.Observable<Void>) {
        val count: ReadOnlyRxProperty<String>

        val fizzVisibility: ReadOnlyRxProperty<Int>
        val buzzVisibility: ReadOnlyRxProperty<Int>

        init {
            val countStream = buttonTapStream
                    .map({ event -> 1 })
                    .scan({ sum, n -> sum + n })
                    .publish()
                    .refCount()
            count = countStream.map(Int::toString)
                    .toRxProperty("0")

            fizzVisibility = countStream
                    .map({ n -> if (n % 3 == 0) View.VISIBLE else View.INVISIBLE })
                    .toRxProperty(View.VISIBLE)

            buzzVisibility = countStream
                    .map({ n -> if (n % 5 == 0) View.VISIBLE else View.INVISIBLE })
                    .toRxProperty(View.VISIBLE)
        }
    }
}

package kyanro.com.androidrxfizzbuzzsample

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jakewharton.rxbinding.view.RxView
import kyanro.com.androidrxfizzbuzzsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModel(this, RxView.clicks(binding.button))
        binding.viewModel = viewModel
    }

    fun setFizzEnabled(show: Boolean) {
        binding.fizz.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    fun setBuzzEnabled(show: Boolean) {
        binding.buzz.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    class ViewModel(activity: MainActivity, buttonTapStream: rx.Observable<Void>)
        : BaseObservable() {
        @Bindable
        var count: String = "0"
            set(value) {
                field = value
                notifyPropertyChanged(BR.count)
            }

        init {
            val countStream = buttonTapStream
                    .map({ event -> 1 })
                    .scan({ sum, n -> sum + n })
                    .publish()
                    .refCount()
            countStream.subscribe({ n -> count = n.toString() })

            val fizzStream = countStream.map({ n -> n % 3 == 0 })
            fizzStream.subscribe({ isShown -> activity.setFizzEnabled(isShown) })

            val buzzStream = countStream.map({ n -> n % 5 == 0 })
            buzzStream.subscribe({ isShown -> activity.setBuzzEnabled(isShown) })
        }
    }
}

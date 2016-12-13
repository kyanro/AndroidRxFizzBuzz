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
        val viewModel = ViewModel(RxView.clicks(binding.button))
        binding.viewModel = viewModel
    }

    class ViewModel(buttonTapStream: rx.Observable<Void>)
        : BaseObservable() {
        @Bindable
        var count: String = "0"
            set(value) {
                field = value
                notifyPropertyChanged(BR.count)
            }

        @Bindable
        var fizzVisibility = View.VISIBLE
            set(value) {
                field = value
                notifyPropertyChanged(BR.fizzVisibility)
            }

        @Bindable
        var buzzVisibility = View.VISIBLE
            set(value) {
                field = value
                notifyPropertyChanged(BR.buzzVisibility)
            }

        init {
            val countStream = buttonTapStream
                    .map({ event -> 1 })
                    .scan({ sum, n -> sum + n })
                    .publish()
                    .refCount()
            countStream.subscribe({ n -> count = n.toString() })

            val fizzStream = countStream.map({ n -> n % 3 == 0 })
            fizzStream.subscribe({ isShown ->
                fizzVisibility = if (isShown) View.VISIBLE else View.INVISIBLE
            })

            val buzzStream = countStream.map({ n -> n % 5 == 0 })
            buzzStream.subscribe({ isShown ->
                buzzVisibility = if (isShown) View.VISIBLE else View.INVISIBLE
            })
        }
    }
}

package com.example.catchat

import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlin.random.Random

class NyanCatFragment : Fragment(R.layout.fragment_nyan_cat) {

    private lateinit var nyanCatImage: ImageView
    private val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nyan_cat, container, false)
        nyanCatImage = view.findViewById(R.id.nyanCatImage)

        Glide.with(this)
            .load(R.drawable.nyan_cat)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .listener(object : RequestListener<android.graphics.drawable.Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<android.graphics.drawable.Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("NyanCatFragment", "Error loading image: ${e?.localizedMessage ?: "unknown error"}")
                    return false
                }

                override fun onResourceReady(
                    resource: android.graphics.drawable.Drawable?,
                    model: Any?,
                    target: Target<android.graphics.drawable.Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(nyanCatImage)


        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                startMovingCat()
            }
        })

        return view
    }

    private fun startMovingCat() {
        handler.post(object : Runnable {
            override fun run() {
                if (!isAdded) return
                val displayMetrics = DisplayMetrics()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    val windowMetrics = requireActivity().windowManager.currentWindowMetrics
                    displayMetrics.widthPixels = windowMetrics.bounds.width()
                    displayMetrics.heightPixels = windowMetrics.bounds.height()
                } else {
                    @Suppress("DEPRECATION")
                    requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
                }

                val randomX = Random.nextInt(0, displayMetrics.widthPixels - nyanCatImage.width)
                val randomY = Random.nextInt(0, displayMetrics.heightPixels - nyanCatImage.height)

                nyanCatImage.animate()
                    .x(randomX.toFloat())
                    .y(randomY.toFloat())
                    .setDuration(1000)
                    .withEndAction(this)
                    .start()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        nyanCatImage.clearAnimation()
    }
}
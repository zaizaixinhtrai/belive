package com.appster.features.prizeBag

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.appster.R
import com.appster.dialog.NoTitleDialogFragment
import com.appster.extensions.loadImg
import com.apster.common.Utils
import kotlinx.android.synthetic.main.prize_bag_view_dialog.*

class PrizeBagViewDialog : NoTitleDialogFragment() {

    private var item: PrizeBagViewModel? = null

    companion object {
        private const val PRIZE_MODEL = "DAILY_TREAT"
        @JvmStatic
        fun newInstance(item: PrizeBagViewModel): PrizeBagViewDialog {
            val args = Bundle()
            args.putParcelable(PRIZE_MODEL, item)
            val dialog = PrizeBagViewDialog()
            dialog.arguments = args
            return dialog
        }
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null && dialog.window != null) {
            dialog.window?.setLayout(Utils.dpToPx(315f), ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = arguments
        if (b != null) {
            item = b.getParcelable(PRIZE_MODEL)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item?.apply {
            tvName.text = name
            tvEmail.text = email
            tvPrizeTitle.text = prizeTitle
            tvPrizeName.text = prizeName
            imageView.loadImg(image)
        }
        btClose.setOnClickListener { dismissAllowingStateLoss() }
    }

    override fun getRootLayoutResource(): Int {
        return R.layout.prize_bag_view_dialog
    }

    override fun isDimDialog(): Boolean {
        return true
    }

}
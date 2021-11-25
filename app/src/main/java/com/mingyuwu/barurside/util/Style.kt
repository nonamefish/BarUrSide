package com.mingyuwu.barurside.util

import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.util.Util.getString

enum class Style(val chinese: String?) {
    AMERICAN(getString(R.string.style_american)),
    EUROPEAN(getString(R.string.style_european)),
    JAPANESE(getString(R.string.style_japanese)),
    TAIWANESE(getString(R.string.style_taiwanese)),
    KOREAN(getString(R.string.style_korean)),
    BAR(getString(R.string.style_bar)),
    PUB(getString(R.string.style_pub))
}

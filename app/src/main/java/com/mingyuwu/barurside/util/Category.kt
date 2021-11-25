package com.mingyuwu.barurside.util

import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.util.Util.getString

enum class Category(val chinese: String?) {
    COCKTAIL(getString(R.string.category_cocktail)),
    BEER(getString(R.string.category_beer)),
    CHAMPAGNE(getString(R.string.category_champagne)),
    WINE(getString(R.string.category_wine)),
    WHISKEY(getString(R.string.category_whiskey)),
    UMESHU(getString(R.string.category_umeshu)),
    SAKE(getString(R.string.category_sake)),
    OTHER(getString(R.string.category_other))
}

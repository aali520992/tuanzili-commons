package com.jxpanda.common.constants

import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * Created by Panda on 2018/7/31
 */
class DecimalConstant {
    companion object {
        val FORMAT_DOUBLE: DecimalFormat = DecimalFormat("#0.00")
        val FORMAT_FOURTH: DecimalFormat = DecimalFormat("#0.0000")
        val FORMAT_EIGHTH: DecimalFormat = DecimalFormat("#0.00000000")
        val FORMAT_FILL_ZERO_3: DecimalFormat = DecimalFormat("#000")
        val ONE_HUNDRED = BigDecimal(100)
        val FIVE = BigDecimal(5)
    }
}

package ru.myproevent.domain.utils

import android.text.Editable
import android.text.TextWatcher

class PhoneTextWatcher(private val phoneNumberSize: Int = 10) : TextWatcher {

    private var isSelfChange = false

    private var start = 0
    private var before = 0
    private var count = 0

    override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        if (isSelfChange) return
        this.start = start
        this.before = before
        this.count = count
    }

    override fun afterTextChanged(text: Editable) {
        if (isSelfChange) {
            isSelfChange = false
            return
        }

        if (count == 0) {
            if (start == 8) {
                isSelfChange = true
                text.delete(7, 8)
            }
            if (start == 4) {
                isSelfChange = true
                text.delete(3, 4)
            }
            if (start == 11) {
                isSelfChange = true
                text.delete(10, 11)
            }
            return
        }

        val sb = StringBuilder()
        text.filterTo(sb) { it.isDigit() }

        if (sb.length > phoneNumberSize) {
            sb.delete(phoneNumberSize, sb.length)
        }

        if (sb.length > 3) {
            sb.insert(3, " ")
            if (sb.length > 7) {
                sb.insert(7, "-")
                if (sb.length > 10) {
                    sb.insert(10, "-")
                }
            }
        }

        isSelfChange = true
        text.replace(0, text.length, sb)
    }
}
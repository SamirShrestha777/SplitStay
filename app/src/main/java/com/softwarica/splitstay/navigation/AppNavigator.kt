package com.softwarica.splitstay.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.softwarica.splitstay.view.login.LoginUIActivity
import com.softwarica.splitstay.view.login.WelcomeUIActivity
import com.softwarica.splitstay.view.register.RegisterUIActivity
import com.softwarica.splitstay.view.verifyemail.VerifyEmailActivity

object AppNavigator {

    fun toWelcome(context: Context, finishCurrent: Boolean = false) {
        val intent = Intent(context, WelcomeUIActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        context.startActivity(intent)
        if (finishCurrent && context is Activity) {
            context.finish()
        }
    }

    fun toLogin(context: Context, finishCurrent: Boolean = false) {
        open(context, LoginUIActivity::class.java, finishCurrent)
    }

    fun toRegister(context: Context, finishCurrent: Boolean = false) {
        open(context, RegisterUIActivity::class.java, finishCurrent)
    }

    /** Opens the 6-digit code screen for the email that was just registered. */
    fun toVerifyEmail(context: Context, email: String, finishCurrent: Boolean = false) {
        val intent = Intent(context, VerifyEmailActivity::class.java).apply {
            putExtra(VerifyEmailActivity.EXTRA_EMAIL, email)
        }
        context.startActivity(intent)
        if (finishCurrent && context is Activity) {
            context.finish()
        }
    }

    fun toLoginClearingStack(context: Context) {
        val intent = Intent(context, LoginUIActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }

    private fun open(
        context: Context,
        target: Class<out Activity>,
        finishCurrent: Boolean
    ) {
        context.startActivity(Intent(context, target))
        if (finishCurrent && context is Activity) {
            context.finish()
        }
    }
}
package com.softwarica.splitstay.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.softwarica.splitstay.MainActivity
import com.softwarica.splitstay.view.forgotpassword.ForgotPasswordActivity
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
        finishIfNeeded(context, finishCurrent)
    }

    fun toLogin(context: Context, finishCurrent: Boolean = false) {
        context.startActivity(Intent(context, LoginUIActivity::class.java))
        finishIfNeeded(context, finishCurrent)
    }

    fun toRegister(context: Context, finishCurrent: Boolean = false) {
        context.startActivity(Intent(context, RegisterUIActivity::class.java))
        finishIfNeeded(context, finishCurrent)
    }

    fun toForgotPassword(context: Context) {
        context.startActivity(Intent(context, ForgotPasswordActivity::class.java))
    }

    fun toVerifyEmail(context: Context, email: String, finishCurrent: Boolean = false) {
        val intent = Intent(context, VerifyEmailActivity::class.java).apply {
            putExtra(VerifyEmailActivity.EXTRA_EMAIL, email)
        }
        context.startActivity(intent)
        finishIfNeeded(context, finishCurrent)
    }

    /** Opens home and clears the back stack so Back doesn't return to login. */
    fun toHome(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }

    fun toLoginClearingStack(context: Context) {
        val intent = Intent(context, LoginUIActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }

    fun toRegisterClearingStack(context: Context) {
        val intent = Intent(context, RegisterUIActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }

    fun back(activity: Activity) {
        if (activity.isTaskRoot) {
            toWelcome(activity, finishCurrent = true)
        } else {
            activity.finish()
        }
    }

    private fun finishIfNeeded(context: Context, finishCurrent: Boolean) {
        if (finishCurrent && context is Activity) context.finish()
    }
}
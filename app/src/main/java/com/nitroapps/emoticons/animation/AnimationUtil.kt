package com.nitroapps.emoticons.animation

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.util.DisplayMetrics



class AnimationUtil {
    companion object {
        private const val defaultAnimationDuration = 400.toLong()

        /**
         * Animates replacing image resource of an ImageView with another resource scale animation.
         *
         * @param view ImageView that will be changed.
         * @param res resource id of an replacement image resource.
         * @param duration duration of an animation.
         */
        fun animateImageViewResourceChange(view: ImageView, res: Int, duration: Long = defaultAnimationDuration) {
            view.animate()
                    .setDuration(duration / 2)
                    .scaleX(0f)
                    .scaleY(0f)
                    .withEndAction {
                        view.setImageResource(res)
                        view.tag = res
                        view.animate()
                                .setDuration(duration / 2)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setInterpolator(AccelerateDecelerateInterpolator())
                    }
        }

        /**
         * Animates replacing one view with another using fade and scale animation at same time.
         *
         * @param fromView source view. The one that will become invisible after animation
         * @param toView destination view. The one that will become visible after animation
         * @param duration duration of an animation.
         */
        fun animateViewChangeScaleFade(fromView: View, toView: View, viewGone: Boolean = false, duration: Long = defaultAnimationDuration) {
            fromView.animate()
                    .setDuration(duration / 2)
                    .alpha(0f)
                    .scaleX(0f)
                    .scaleY(0f)
                    .withEndAction {
                        fromView.visibility = if(viewGone) View.GONE else View.INVISIBLE
                        toView.alpha = 0f
                        toView.scaleX = 0f
                        toView.scaleY = 0f
                        toView.visibility = View.VISIBLE
                        toView.animate()
                                .setDuration(duration / 2)
                                .alpha(1f)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setInterpolator(AccelerateDecelerateInterpolator())
                    }
        }

        fun viewChangeSlideToLeft(fromView: View, toView: View, screenWidth: Float, duration: Long = defaultAnimationDuration) {
            fromView.animate()
                .setDuration(duration)
                .translationX(-screenWidth)
                .setInterpolator(AccelerateDecelerateInterpolator())
//                .withEndAction {
//                    toView.translationX = screenWidth
//                    toView.animate()
//                        .setDuration(duration / 2)
//                        .translationX(0f)
//                        .setInterpolator(AccelerateDecelerateInterpolator())
//                }
            toView.translationX = screenWidth
            toView.animate()
                .setDuration(duration)
                .translationX(0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
        }

        fun viewChangeSlideToRight(fromView: View, toView: View, screenWidth: Float, duration: Long = defaultAnimationDuration) {
            fromView.animate()
                .setDuration(duration)
                .translationX(screenWidth)
                .setInterpolator(AccelerateDecelerateInterpolator())
//                .withEndAction {
//                    toView.translationX = -screenWidth
//                    toView.animate()
//                        .setDuration(duration / 2)
//                        .translationX(0f)
//                        .setInterpolator(AccelerateDecelerateInterpolator())
//                }
                toView.translationX = -screenWidth
                toView.animate()
                    .setDuration(duration)
                    .translationX(0f)
                    .setInterpolator(AccelerateDecelerateInterpolator())
        }
    }
}
package es.epycus.app.ui.splash;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import es.epycus.app.R;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.ui.MainContainerActivity;
import es.epycus.app.ui.auth.LoginActivity;
import es.epycus.app.util.SessionManager;
import es.epycus.app.util.ThemeManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        ThemeManager.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View logo = findViewById(R.id.ivSplashLogo);
        View tagline = findViewById(R.id.tvSplashTagline);
        View progress = findViewById(R.id.pbSplash);

        logo.setScaleX(0.8f);
        logo.setScaleY(0.8f);
        tagline.setTranslationY(24f);
        tagline.setAlpha(0f);
        progress.setAlpha(0f);

        splashScreen.setKeepOnScreenCondition(() -> true);

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0.8f, 1f);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0.8f, 1f);
        logoScaleX.setDuration(400);
        logoScaleY.setDuration(400);

        ObjectAnimator taglineSlide = ObjectAnimator.ofFloat(tagline, "translationY", 24f, 0f);
        ObjectAnimator taglineFade = ObjectAnimator.ofFloat(tagline, "alpha", 0f, 1f);
        taglineSlide.setDuration(300);
        taglineSlide.setStartDelay(200);
        taglineFade.setDuration(300);
        taglineFade.setStartDelay(200);

        ObjectAnimator progressFade = ObjectAnimator.ofFloat(progress, "alpha", 0f, 1f);
        progressFade.setDuration(200);
        progressFade.setStartDelay(500);

        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(logoScaleX, logoScaleY, taglineSlide, taglineFade, progressFade);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                navegarSiguiente();
            }
        });
        animatorSet.start();
    }

    private void navegarSiguiente() {
        AuthRepository authRepository = new AuthRepository(this);
        SessionManager sessionManager = SessionManager.getInstance(this);

        Intent intent;
        if (authRepository.isLoggedIn() && !sessionManager.isTokenExpired()) {
            intent = new Intent(SplashActivity.this, MainContainerActivity.class);
        } else {
            if (authRepository.isLoggedIn()) {
                sessionManager.logout();
            }
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

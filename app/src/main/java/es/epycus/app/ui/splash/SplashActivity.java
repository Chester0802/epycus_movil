package es.epycus.app.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import es.epycus.app.R;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.ui.MainContainerActivity;
import es.epycus.app.ui.auth.LoginActivity;
import es.epycus.app.util.ThemeManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AuthRepository authRepository = new AuthRepository(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (authRepository.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, MainContainerActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 1500);
    }
}

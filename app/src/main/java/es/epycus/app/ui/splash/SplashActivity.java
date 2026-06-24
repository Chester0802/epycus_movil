package es.epycus.app.ui.splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

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

        splashScreen.setKeepOnScreenCondition(() -> {
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
            return false;
        });
    }
}

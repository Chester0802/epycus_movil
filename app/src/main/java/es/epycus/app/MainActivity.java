package es.epycus.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import es.epycus.app.ui.MainContainerActivity;

/**
 * Código muerto — esta Activity ya no se usa.
 * Launcher real: LoginActivity.
 * Se puede eliminar junto con activity_main.xml y su declaración en AndroidManifest.xml.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MainContainerActivity.class);
        startActivity(intent);
        finish();
    }
}

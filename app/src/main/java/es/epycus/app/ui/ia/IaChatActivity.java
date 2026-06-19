package es.epycus.app.ui.ia;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.ChatRequest;
import es.epycus.app.model.dto.ChatResponse;
import es.epycus.app.ui.adapters.MensajeChatAdapter;

@SuppressLint("SetTextI18n")
public class IaChatActivity extends AppCompatActivity {

    private RecyclerView rvMensajes;
    private EditText etMensaje;
    private ImageButton btnEnviar;
    private ProgressBar loadingView;
    private MensajeChatAdapter adapter;
    private String conversacionId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ia_chat);

        rvMensajes = findViewById(R.id.rvMensajes);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        loadingView = findViewById(R.id.loadingView);

        adapter = new MensajeChatAdapter();
        rvMensajes.setLayoutManager(new LinearLayoutManager(this));
        rvMensajes.setAdapter(adapter);

        adapter.addMensaje(new MensajeChatAdapter.Mensaje(
                "Hola! Soy Edy, tu asistente de bienestar. Como te sientes hoy?", false));

        btnEnviar.setOnClickListener(v -> enviarMensaje());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void enviarMensaje() {
        String texto = etMensaje.getText().toString().trim();
        if (texto.isEmpty()) return;

        etMensaje.setText("");
        adapter.addMensaje(new MensajeChatAdapter.Mensaje(texto, true));
        rvMensajes.smoothScrollToPosition(adapter.getItemCount() - 1);

        loadingView.setVisibility(View.VISIBLE);

        ChatRequest request = new ChatRequest(texto, conversacionId);

        RetrofitClient.getInstance(this).getApiIaService()
                .chat(request).enqueue(new retrofit2.Callback<>() {
                    @Override
                    public void onResponse(@NonNull retrofit2.Call<RespuestaApi<Object>> call,
                                           @NonNull retrofit2.Response<RespuestaApi<Object>> response) {
                        loadingView.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null
                                && response.body().isExito() && response.body().getDatos() != null) {
                            try {
                                com.google.gson.Gson gson = new com.google.gson.Gson();
                                String json = gson.toJson(response.body().getDatos());
                                ChatResponse chatResp = gson.fromJson(json, ChatResponse.class);

                                conversacionId = chatResp.getConversacionId();
                                adapter.addMensaje(new MensajeChatAdapter.Mensaje(
                                        chatResp.getRespuesta(), false));
                                rvMensajes.smoothScrollToPosition(
                                        adapter.getItemCount() - 1);
                            } catch (Exception e) {
                                adapter.addMensaje(new MensajeChatAdapter.Mensaje(
                                        "Lo siento, no pude procesar tu mensaje.", false));
                            }
                        } else {
                            adapter.addMensaje(new MensajeChatAdapter.Mensaje(
                                    "Lo siento, hubo un error. Intenta de nuevo.", false));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull retrofit2.Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                        loadingView.setVisibility(View.GONE);
                        adapter.addMensaje(new MensajeChatAdapter.Mensaje(
                                "Error de conexion. Verifica tu internet.", false));
                    }
                });
    }
}

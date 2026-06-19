package es.epycus.app.ui.ia;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.databinding.ActivityIaChatBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.ChatRequest;
import es.epycus.app.model.dto.ChatResponse;
import es.epycus.app.ui.adapters.MensajeChatAdapter;
import es.epycus.app.util.ThemeManager;

public class IaChatActivity extends AppCompatActivity {

    private static final String TAG = "IaChatActivity";
    private ActivityIaChatBinding binding;
    private MensajeChatAdapter adapter;
    private String conversacionId;
    private retrofit2.Call<RespuestaApi<Object>> activeCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.getInstance(this).applyTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityIaChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new MensajeChatAdapter();
        binding.rvMensajes.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMensajes.setAdapter(adapter);

        adapter.addMensaje(new MensajeChatAdapter.Mensaje(
                getString(R.string.edy_presentacion), false));

        binding.btnEnviar.setOnClickListener(v -> enviarMensaje());

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void enviarMensaje() {
        String texto = binding.etMensaje.getText().toString().trim();
        if (texto.isEmpty()) return;

        if (activeCall != null && !activeCall.isCanceled()) {
            activeCall.cancel();
        }

        binding.etMensaje.setText("");
        adapter.addMensaje(new MensajeChatAdapter.Mensaje(texto, true));
        binding.rvMensajes.smoothScrollToPosition(adapter.getItemCount() - 1);

        binding.loadingView.setVisibility(View.VISIBLE);

        ChatRequest request = new ChatRequest(texto, conversacionId);

        activeCall = RetrofitClient.getInstance(this).getApiIaService()
                .chat(request);
        activeCall.enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<RespuestaApi<Object>> call,
                                   @NonNull retrofit2.Response<RespuestaApi<Object>> response) {
                activeCall = null;
                binding.loadingView.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null
                        && response.body().isExito() && response.body().getDatos() != null) {
                    try {
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        String json = gson.toJson(response.body().getDatos());
                        ChatResponse chatResp = gson.fromJson(json, ChatResponse.class);

                        conversacionId = chatResp.getConversacionId();
                        adapter.addMensaje(new MensajeChatAdapter.Mensaje(
                                chatResp.getRespuesta(), false));
                        binding.rvMensajes.smoothScrollToPosition(
                                adapter.getItemCount() - 1);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing chat response", e);
                        adapter.addMensaje(new MensajeChatAdapter.Mensaje(
                                getString(R.string.error_procesar_mensaje), false));
                    }
                } else {
                    adapter.addMensaje(new MensajeChatAdapter.Mensaje(
                            getString(R.string.error_intentar), false));
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                activeCall = null;
                binding.loadingView.setVisibility(View.GONE);
                int msgRes;
                if (t instanceof SocketTimeoutException) {
                    msgRes = R.string.error_timeout;
                } else if (t instanceof UnknownHostException || t instanceof ConnectException) {
                    msgRes = R.string.error_sin_conexion;
                } else {
                    msgRes = R.string.error_conexion_ia;
                }
                adapter.addMensaje(new MensajeChatAdapter.Mensaje(
                        getString(msgRes), false));
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (activeCall != null && !activeCall.isCanceled()) {
            activeCall.cancel();
        }
        super.onDestroy();
    }
}

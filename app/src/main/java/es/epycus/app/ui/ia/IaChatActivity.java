package es.epycus.app.ui.ia;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.databinding.ActivityIaChatBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.ChatRequest;
import es.epycus.app.model.dto.ChatResponse;
import es.epycus.app.ui.adapters.MensajeChatAdapter;

public class IaChatActivity extends AppCompatActivity {

    private static final String TAG = "IaChatActivity";
    private ActivityIaChatBinding binding;
    private MensajeChatAdapter adapter;
    private String conversacionId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        binding.etMensaje.setText("");
        adapter.addMensaje(new MensajeChatAdapter.Mensaje(texto, true));
        binding.rvMensajes.smoothScrollToPosition(adapter.getItemCount() - 1);

        binding.loadingView.setVisibility(View.VISIBLE);

        ChatRequest request = new ChatRequest(texto, conversacionId);

        RetrofitClient.getInstance(this).getApiIaService()
                .chat(request).enqueue(new retrofit2.Callback<>() {
                    @Override
                    public void onResponse(@NonNull retrofit2.Call<RespuestaApi<Object>> call,
                                           @NonNull retrofit2.Response<RespuestaApi<Object>> response) {
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
                        binding.loadingView.setVisibility(View.GONE);
                        adapter.addMensaje(new MensajeChatAdapter.Mensaje(
                                getString(R.string.error_conexion_ia), false));
                    }
                });
    }
}

package es.epycus.app.ui.ia;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

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
import es.epycus.app.util.NetworkUtils;
import es.epycus.app.util.ThemeManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IaChatActivity extends AppCompatActivity {

    private static final String TAG = "IaChatActivity";
    private ActivityIaChatBinding binding;
    private MensajeChatAdapter adapter;
    private String conversacionId;
    private Call<RespuestaApi<ChatResponse>> activeCall;

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

        binding.etMensaje.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND
                    || (event != null && event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                enviarMensaje();
                return true;
            }
            return false;
        });

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
        activeCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<ChatResponse>> call,
                                   @NonNull Response<RespuestaApi<ChatResponse>> response) {
                activeCall = null;
                binding.loadingView.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null
                        && response.body().isExito() && response.body().getDatos() != null) {
                    ChatResponse chatResp = response.body().getDatos();

                    conversacionId = chatResp.getConversacionId();
                    adapter.addMensaje(new MensajeChatAdapter.Mensaje(
                            chatResp.getRespuesta(), false));
                    binding.rvMensajes.smoothScrollToPosition(
                            adapter.getItemCount() - 1);
                } else {
                    adapter.addMensaje(new MensajeChatAdapter.Mensaje(
                            getString(R.string.error_intentar), false));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<ChatResponse>> call, @NonNull Throwable t) {
                activeCall = null;
                binding.loadingView.setVisibility(View.GONE);
                int msgRes = NetworkUtils.getNetworkErrorResId(t);
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
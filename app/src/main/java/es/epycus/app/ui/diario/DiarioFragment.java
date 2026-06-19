package es.epycus.app.ui.diario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.databinding.FragmentDiarioBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.PreguntaGuiaResponse;
import es.epycus.app.repository.DiarioRepository;
import es.epycus.app.ui.ia.IaChatActivity;
import es.epycus.app.util.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiarioFragment extends Fragment {

    private static final String TAG = "DiarioFragment";
    private static final String CACHE_KEY_PREGUNTA = "pregunta_guia";
    private FragmentDiarioBinding binding;
    private View selectedMood;
    private String selectedMoodText = "";
    private DiarioRepository repository;
    private final List<Call<?>> activeCalls = new ArrayList<>();
    private String entradaHoyTexto = "";
    private String entradaHoyEstado = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDiarioBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        repository = new DiarioRepository(requireContext());

        View.OnClickListener moodListener = v -> {
            if (selectedMood != null) {
                selectedMood.setBackgroundResource(R.drawable.bg_card_rounded);
            }
            selectedMood = v;
            v.setBackgroundResource(R.drawable.bg_accent_gradient);

            if (v.getId() == R.id.moodGenial) selectedMoodText = "Genial";
            else if (v.getId() == R.id.moodBien) selectedMoodText = "Bien";
            else if (v.getId() == R.id.moodNormal) selectedMoodText = "Normal";
            else if (v.getId() == R.id.moodCansado) selectedMoodText = "Cansado";
            else if (v.getId() == R.id.moodEstresado) selectedMoodText = "Estresado";
        };

        binding.moodGenial.setOnClickListener(moodListener);
        binding.moodBien.setOnClickListener(moodListener);
        binding.moodNormal.setOnClickListener(moodListener);
        binding.moodCansado.setOnClickListener(moodListener);
        binding.moodEstresado.setOnClickListener(moodListener);

        binding.btnGuardarAnimo.setOnClickListener(v -> {
            if (selectedMoodText.isEmpty()) {
                Snackbar.make(v, getString(R.string.selecciona_como_te_sientes), Snackbar.LENGTH_SHORT).show();
                return;
            }
            guardarAnimo(selectedMoodText);
        });

        binding.btnChatEdy.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), IaChatActivity.class)));

        binding.swipeRefresh.setOnRefreshListener(this::recargarTodo);
        binding.swipeRefresh.setColorSchemeResources(R.color.light_accent, R.color.light_accent_secondary);

        recargarTodo();

        return view;
    }

    private void recargarTodo() {
        cargarPreguntaGuia();
        cargarEntradaHoy();
    }

    private void guardarAnimo(String estado) {
        JsonObject body = new JsonObject();
        body.addProperty("estado", estado);
        String notas = binding.etNotas.getText().toString().trim();
        if (!notas.isEmpty()) {
            body.addProperty("notas", notas);
        }

        Call<RespuestaApi<Object>> call = RetrofitClient.getInstance(requireContext()).getApiEstadoAnimoService()
                .registrar(body);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                   @NonNull Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful()) {
                    Snackbar.make(requireView(), getString(R.string.estado_animo_guardado),
                            Snackbar.LENGTH_SHORT).show();
                    if (selectedMood != null) {
                        selectedMood.setBackgroundResource(R.drawable.bg_card_rounded);
                        selectedMood = null;
                        selectedMoodText = "";
                    }
                    binding.etNotas.setText("");
                    cargarEntradaHoy();
                } else {
                    String msg = NetworkUtils.getErrorMessage(requireContext(), response);
                    Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                mostrarErrorRed(t);
            }
        });
    }

    private void cargarEntradaHoy() {
        Call<RespuestaApi<Object>> call = repository.hoy();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                   @NonNull Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful() && response.body() != null && response.body().isExito()
                        && response.body().getDatos() != null) {
                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body().getDatos());
                        JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                        if (obj.has("estado")) {
                            entradaHoyEstado = obj.get("estado").getAsString();
                        }
                        if (obj.has("notas")) {
                            entradaHoyTexto = obj.get("notas").getAsString();
                        }

                        String resumen;
                        if (!entradaHoyEstado.isEmpty()) {
                            resumen = getString(R.string.entrada_hoy_formato, entradaHoyEstado);
                            if (!entradaHoyTexto.isEmpty()) {
                                resumen += "\n\"" + entradaHoyTexto + "\"";
                            }
                        } else {
                            resumen = getString(R.string.sin_entrada_hoy);
                        }
                        binding.tvEntradaHoy.setText(resumen);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing entrada hoy", e);
                        binding.tvEntradaHoy.setText(R.string.sin_entrada_hoy);
                    }
                } else {
                    binding.tvEntradaHoy.setText(R.string.sin_entrada_hoy);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                binding.tvEntradaHoy.setText(R.string.sin_entrada_hoy);
            }
        });
    }

    private void cargarPreguntaGuia() {
        binding.loadingView.setVisibility(View.VISIBLE);
        Call<RespuestaApi<PreguntaGuiaResponse>> call = repository.preguntaGuia();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<PreguntaGuiaResponse>> call,
                                   @NonNull Response<RespuestaApi<PreguntaGuiaResponse>> response) {
                activeCalls.remove(call);
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getDatos() != null) {
                    PreguntaGuiaResponse data = response.body().getDatos();
                    Gson gson = new Gson();
                    String json = gson.toJson(data);
                    repository.cacheJson(CACHE_KEY_PREGUNTA, json);
                    binding.tvPreguntaGuia.setText(data.getPregunta());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<PreguntaGuiaResponse>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                cargarPreguntaGuiaDesdeCache();
                mostrarErrorRed(t);
            }
        });
    }

    private void cargarPreguntaGuiaDesdeCache() {
        String json = repository.getCachedJson(CACHE_KEY_PREGUNTA);
        if (json != null) {
            try {
                Gson gson = new Gson();
                PreguntaGuiaResponse data = gson.fromJson(json, PreguntaGuiaResponse.class);
                binding.tvPreguntaGuia.setText(data.getPregunta());
            } catch (Exception e) {
                Log.e(TAG, "Error loading cached pregunta guia", e);
            }
        }
    }

    private void mostrarErrorRed(Throwable t) {
        Snackbar.make(requireView(),
                getString(NetworkUtils.getNetworkErrorResId(t)), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        for (Call<?> call : activeCalls) {
            if (!call.isCanceled()) call.cancel();
        }
        activeCalls.clear();
        super.onDestroyView();
        binding = null;
    }
}

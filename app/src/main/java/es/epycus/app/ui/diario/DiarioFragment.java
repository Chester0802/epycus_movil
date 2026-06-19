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

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.CacheEntity;
import es.epycus.app.databinding.FragmentDiarioBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.ui.ia.IaChatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiarioFragment extends Fragment {

    private static final String TAG = "DiarioFragment";
    private FragmentDiarioBinding binding;
    private View selectedMood;
    private String selectedMoodText = "";
    private AppDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDiarioBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        database = AppDatabase.getInstance(requireContext());

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

        binding.swipeRefresh.setOnRefreshListener(this::cargarPreguntaGuia);
        binding.swipeRefresh.setColorSchemeResources(R.color.light_accent, R.color.light_accent_secondary);

        cargarPreguntaGuia();

        return view;
    }

    private void guardarAnimo(String estado) {
        com.google.gson.JsonObject body = new com.google.gson.JsonObject();
        body.addProperty("estado", estado);

        RetrofitClient.getInstance(requireContext()).getApiEstadoAnimoService()
                .registrar(body).enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                           @NonNull Response<RespuestaApi<Object>> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(requireView(), getString(R.string.estado_animo_guardado),
                                    Snackbar.LENGTH_SHORT).show();
                            if (selectedMood != null) {
                                selectedMood.setBackgroundResource(R.drawable.bg_card_rounded);
                                selectedMood = null;
                                selectedMoodText = "";
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                        Snackbar.make(requireView(), getString(R.string.error_conexion),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarPreguntaGuia() {
        binding.loadingView.setVisibility(View.VISIBLE);
        RetrofitClient.getInstance(requireContext()).getApiDiarioService()
                .preguntaGuia().enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                           @NonNull Response<RespuestaApi<Object>> response) {
                        binding.loadingView.setVisibility(View.GONE);
                        binding.swipeRefresh.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getDatos() != null) {
                            try {
                                Gson gson = new Gson();
                                String json = gson.toJson(response.body().getDatos());
                                database.cacheDao().insert(
                                        new CacheEntity("pregunta_guia", json));
                                JsonObject obj = gson.fromJson(json, JsonObject.class);
                                if (obj.has("pregunta")) {
                                    binding.tvPreguntaGuia.setText(obj.get("pregunta").getAsString());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing pregunta guia", e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                        binding.loadingView.setVisibility(View.GONE);
                        binding.swipeRefresh.setRefreshing(false);
                        cargarPreguntaGuiaDesdeCache();
                        Snackbar.make(requireView(), getString(R.string.error_conexion),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarPreguntaGuiaDesdeCache() {
        String json = database.cacheDao().getValue("pregunta_guia");
        if (json != null) {
            try {
                Gson gson = new Gson();
                JsonObject obj = gson.fromJson(json, JsonObject.class);
                if (obj.has("pregunta")) {
                    binding.tvPreguntaGuia.setText(obj.get("pregunta").getAsString());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading cached pregunta guia", e);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

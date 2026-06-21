package es.epycus.app.ui.diario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.databinding.FragmentDiarioBinding;
import es.epycus.app.util.CacheManager;
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
    private DiarioRepository diarioRepository;
    private final List<Call<?>> activeCalls = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            binding = FragmentDiarioBinding.inflate(inflater, container, false);
            View view = binding.getRoot();

            diarioRepository = new DiarioRepository(requireContext());

            View.OnClickListener moodListener = v -> {
                if (!isAlive()) return;
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
                if (!isAlive()) return;
                if (selectedMoodText.isEmpty()) {
                    Snackbar.make(v, getString(R.string.selecciona_como_te_sientes), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                guardarAnimo(selectedMoodText);
            });

            binding.btnChatEdy.setOnClickListener(v -> {
                if (getActivity() == null) return;
                startActivity(new Intent(getActivity(), IaChatActivity.class));
            });

            binding.swipeRefresh.setOnRefreshListener(this::recargarTodo);
            binding.swipeRefresh.setColorSchemeResources(R.color.light_accent, R.color.light_accent_secondary);

            recargarTodo();

            return view;
        } catch (Exception e) {
            Log.e(TAG, "Error creating DiarioFragment view: " + e.getMessage(), e);
            TextView tv = new TextView(getContext() != null ? getContext() : inflater.getContext());
            tv.setText("Error al cargar diario: " + e.getClass().getSimpleName());
            tv.setPadding(32, 32, 32, 32);
            return tv;
        }
    }

    private void recargarTodo() {
        cargarPreguntaGuia();
        cargarEntradaHoy();
        cargarHistorialAnimo();
    }

    private boolean isAlive() {
        return isAdded() && binding != null;
    }

    private void guardarAnimo(String estado) {
        JsonObject body = new JsonObject();
        body.addProperty("estado", estado);
        String nota = binding.etNotas.getText().toString().trim();
        if (!nota.isEmpty()) {
            body.addProperty("nota", nota);
        }

        Call<RespuestaApi<Object>> call = RetrofitClient.getInstance(requireContext()).getApiEstadoAnimoService()
                .registrar(body);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                   @NonNull Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (!isAlive()) return;
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
                    cargarHistorialAnimo();
                } else {
                    String msg = NetworkUtils.getErrorMessage(requireContext(), response);
                    Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                if (!isAlive()) return;
                mostrarErrorRed(t);
            }
        });
    }

    private void cargarEntradaHoy() {
        Call<RespuestaApi<Object>> call = diarioRepository.hoy();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                   @NonNull Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (!isAlive()) return;
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().isExito()
                            && response.body().getDatos() != null) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body().getDatos());
                        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

                        String estado = "";
                        String nota = "";

                        if (obj.has("entrada") && !obj.get("entrada").isJsonNull()) {
                            JsonObject entrada = obj.getAsJsonObject("entrada");
                            if (entrada.has("estado") && !entrada.get("estado").isJsonNull()) {
                                estado = entrada.get("estado").getAsString();
                            }
                            if (entrada.has("nota") && !entrada.get("nota").isJsonNull()) {
                                nota = entrada.get("nota").getAsString();
                            }
                        }

                        String resumen;
                        if (!estado.isEmpty()) {
                            resumen = getString(R.string.entrada_hoy_formato, estado);
                            if (!nota.isEmpty()) {
                                resumen += "\n\"" + nota + "\"";
                            }
                        } else {
                            resumen = getString(R.string.sin_entrada_hoy);
                        }
                        binding.tvEntradaHoy.setText(resumen);
                    } else {
                        binding.tvEntradaHoy.setText(R.string.sin_entrada_hoy);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing entrada hoy", e);
                    if (isAlive()) binding.tvEntradaHoy.setText(R.string.sin_entrada_hoy);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                if (isAlive()) binding.tvEntradaHoy.setText(R.string.sin_entrada_hoy);
            }
        });
    }

    private void cargarPreguntaGuia() {
        if (!isAlive()) return;
        binding.loadingView.setVisibility(View.VISIBLE);
        Call<RespuestaApi<PreguntaGuiaResponse>> call = diarioRepository.preguntaGuia();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<PreguntaGuiaResponse>> call,
                                   @NonNull Response<RespuestaApi<PreguntaGuiaResponse>> response) {
                activeCalls.remove(call);
                if (!isAlive()) return;
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                try {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().getDatos() != null) {
                        PreguntaGuiaResponse data = response.body().getDatos();
                        Gson gson = new Gson();
                        String json = gson.toJson(data);
                        diarioRepository.cacheJson(CACHE_KEY_PREGUNTA, json, CacheManager.TTL_PREGUNTA_GUIA);
                        binding.tvPreguntaGuia.setText(data.getPregunta());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing pregunta guia", e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<PreguntaGuiaResponse>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                if (!isAlive()) return;
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                cargarPreguntaGuiaDesdeCache();
                mostrarErrorRed(t);
            }
        });
    }

    private void cargarPreguntaGuiaDesdeCache() {
        String json = diarioRepository.getCachedJson(CACHE_KEY_PREGUNTA);
        if (json != null && isAlive()) {
            try {
                Gson gson = new Gson();
                PreguntaGuiaResponse data = gson.fromJson(json, PreguntaGuiaResponse.class);
                binding.tvPreguntaGuia.setText(data.getPregunta());
            } catch (Exception e) {
                Log.e(TAG, "Error loading cached pregunta guia", e);
            }
        }
    }

    private void cargarHistorialAnimo() {
        Call<RespuestaApi<Object>> call = RetrofitClient.getInstance(requireContext()).getApiEstadoAnimoService()
                .historial();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                   @NonNull Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (!isAlive()) return;
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().isExito()
                            && response.body().getDatos() != null) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body().getDatos());
                        JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
                        if (arr.size() == 0) {
                            binding.layoutHistorial.setVisibility(View.GONE);
                            return;
                        }
                        binding.layoutHistorial.setVisibility(View.VISIBLE);
                        List<MoodHistoryItem> items = new ArrayList<>();
                        for (int i = 0; i < arr.size(); i++) {
                            JsonObject obj = arr.get(i).getAsJsonObject();
                            String fecha = obj.has("fecha") && !obj.get("fecha").isJsonNull()
                                    ? obj.get("fecha").getAsString() : "";
                            String estado = obj.has("estado") && !obj.get("estado").isJsonNull()
                                    ? obj.get("estado").getAsString() : "";
                            String nota = obj.has("nota") && !obj.get("nota").isJsonNull()
                                    ? obj.get("nota").getAsString() : "";
                            items.add(new MoodHistoryItem(fecha, estado, nota));
                        }
                        binding.rvHistorialAnimo.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.rvHistorialAnimo.setAdapter(new MoodHistoryAdapter(items));
                    } else {
                        binding.layoutHistorial.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing historial", e);
                    if (isAlive()) binding.layoutHistorial.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                if (isAlive()) binding.layoutHistorial.setVisibility(View.GONE);
            }
        });
    }

    private void mostrarErrorRed(Throwable t) {
        if (!isAlive()) return;
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

    private static class MoodHistoryItem {
        final String fecha;
        final String estado;
        final String nota;

        MoodHistoryItem(String fecha, String estado, String nota) {
            this.fecha = fecha;
            this.estado = estado;
            this.nota = nota;
        }
    }

    private static class MoodHistoryAdapter extends RecyclerView.Adapter<MoodHistoryAdapter.ViewHolder> {
        private final List<MoodHistoryItem> items;

        MoodHistoryAdapter(List<MoodHistoryItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(0, 0, 0, 8);
            return new ViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MoodHistoryItem item = items.get(position);
            String text = item.fecha + " - " + item.estado;
            if (!item.nota.isEmpty()) {
                text += " (\"" + item.nota + "\")";
            }
            holder.textView.setText(text);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView textView;

            ViewHolder(TextView tv) {
                super(tv);
                textView = tv;
            }
        }
    }
}

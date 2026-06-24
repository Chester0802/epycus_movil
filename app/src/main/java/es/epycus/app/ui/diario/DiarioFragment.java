package es.epycus.app.ui.diario;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.entity.DiarioEntradaEntity;
import es.epycus.app.databinding.FragmentDiarioBinding;
import es.epycus.app.util.CacheManager;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.DiarioEntradaResponse;
import es.epycus.app.model.dto.PreguntaGuiaResponse;
import es.epycus.app.repository.DiarioRepository;
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
                    ocultarCheckmark(selectedMood.getId());
                }
                selectedMood = v;
                v.setBackgroundResource(R.drawable.bg_accent_gradient);
                mostrarCheckmark(v.getId());

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
                ((es.epycus.app.ui.MainContainerActivity) getActivity()).navegarAIAChat();
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

    private void ocultarCheckmark(int moodId) {
        ImageView check = null;
        if (moodId == R.id.moodGenial) check = binding.checkGenial;
        else if (moodId == R.id.moodBien) check = binding.checkBien;
        else if (moodId == R.id.moodNormal) check = binding.checkNormal;
        else if (moodId == R.id.moodCansado) check = binding.checkCansado;
        else if (moodId == R.id.moodEstresado) check = binding.checkEstresado;
        if (check != null) check.setVisibility(View.GONE);
    }

    private void mostrarCheckmark(int moodId) {
        ImageView check = null;
        if (moodId == R.id.moodGenial) check = binding.checkGenial;
        else if (moodId == R.id.moodBien) check = binding.checkBien;
        else if (moodId == R.id.moodNormal) check = binding.checkNormal;
        else if (moodId == R.id.moodCansado) check = binding.checkCansado;
        else if (moodId == R.id.moodEstresado) check = binding.checkEstresado;
        if (check != null) check.setVisibility(View.VISIBLE);
    }

    private void guardarAnimo(String estado) {
        int estadoAnimo = mapMoodToInt(estado);
        String diarioTexto = binding.etDiarioTexto.getText().toString().trim();

        JsonObject body = new JsonObject();
        body.addProperty("estadoAnimo", estadoAnimo);
        body.addProperty("nivelEnergia", 2);
        if (!diarioTexto.isEmpty()) {
            body.addProperty("diarioTexto", diarioTexto);
        }

        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Call<RespuestaApi<DiarioEntradaResponse>> call;
        boolean exists = diarioRepository.getCachedEntrada(hoy) != null;
        if (exists) {
            call = diarioRepository.actualizar(hoy, body);
        } else {
            call = diarioRepository.crear(body);
        }

        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<DiarioEntradaResponse>> call,
                                   @NonNull Response<RespuestaApi<DiarioEntradaResponse>> response) {
                activeCalls.remove(call);
                if (!isAlive()) return;
                if (response.isSuccessful()) {
                    Snackbar.make(requireView(), getString(R.string.entrada_diario_guardada),
                            Snackbar.LENGTH_SHORT).show();
                    if (selectedMood != null) {
                        ocultarCheckmark(selectedMood.getId());
                        selectedMood.setBackgroundResource(R.drawable.bg_card_rounded);
                        selectedMood = null;
                        selectedMoodText = "";
                    }
                    binding.etDiarioTexto.setText("");
                    cargarEntradaHoy();
                    cargarHistorialAnimo();
                } else {
                    String msg = NetworkUtils.getErrorMessage(requireContext(), response);
                    Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<DiarioEntradaResponse>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                if (!isAlive()) return;
                mostrarErrorRed(t);
            }
        });
    }

    private int mapMoodToInt(String mood) {
        switch (mood) {
            case "Genial": return 4;
            case "Bien": return 3;
            case "Normal": return 2;
            case "Cansado": return 1;
            case "Estresado": return 0;
            default: return 2;
        }
    }

    private void cargarEntradaHoy() {
        String hoy = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(new java.util.Date());

        DiarioEntradaEntity cached = diarioRepository.getCachedEntrada(hoy);
        if (cached != null) {
            mostrarEntradaHoy(cached);
        }

        Call<RespuestaApi<DiarioEntradaResponse>> call = diarioRepository.hoy();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<DiarioEntradaResponse>> call,
                                   @NonNull Response<RespuestaApi<DiarioEntradaResponse>> response) {
                activeCalls.remove(call);
                if (!isAlive()) return;
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().isExito()
                            && response.body().getDatos() != null) {
                        DiarioEntradaEntity entity = diarioRepository.toEntity(response.body().getDatos());
                        diarioRepository.cacheEntrada(entity);
                        mostrarEntradaHoy(entity);
                    } else if (cached == null) {
                        binding.tvEntradaHoy.setText(R.string.sin_entrada_hoy);
                        binding.layoutEmptyDiario.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing entrada hoy", e);
                    if (isAlive() && cached == null) {
                        binding.tvEntradaHoy.setText(R.string.sin_entrada_hoy);
                        binding.layoutEmptyDiario.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<DiarioEntradaResponse>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                if (isAlive()) {
                    if (cached == null) {
                        binding.tvEntradaHoy.setText(R.string.sin_entrada_hoy);
                        binding.layoutEmptyDiario.setVisibility(View.VISIBLE);
                    }
                    mostrarErrorRed(t);
                }
            }
        });
    }

    private void mostrarEntradaHoy(DiarioEntradaEntity entity) {
        String diarioTexto = entity.getDiarioTexto() != null ? entity.getDiarioTexto() : "";

        if (!diarioTexto.isEmpty()) {
            binding.layoutEmptyDiario.setVisibility(View.GONE);
            binding.tvEntradaHoy.setText(diarioTexto);
            binding.etDiarioTexto.setText(diarioTexto);
        } else {
            binding.tvEntradaHoy.setText(R.string.sin_entrada_hoy);
        }
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
        Call<RespuestaApi<List<es.epycus.app.model.dto.EstadoAnimoEntry>>> call =
                RetrofitClient.getInstance(requireContext()).getApiEstadoAnimoService().historial();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<List<es.epycus.app.model.dto.EstadoAnimoEntry>>> call,
                                   @NonNull Response<RespuestaApi<List<es.epycus.app.model.dto.EstadoAnimoEntry>>> response) {
                activeCalls.remove(call);
                if (!isAlive()) return;
                try {
                    if (response.isSuccessful() && response.body() != null && response.body().isExito()
                            && response.body().getDatos() != null) {
                        List<es.epycus.app.model.dto.EstadoAnimoEntry> entries = response.body().getDatos();
                        if (entries.isEmpty()) {
                            binding.layoutHistorial.setVisibility(View.GONE);
                            return;
                        }
                        binding.layoutHistorial.setVisibility(View.VISIBLE);
                        List<MoodHistoryItem> items = new ArrayList<>();
                        for (es.epycus.app.model.dto.EstadoAnimoEntry entry : entries) {
                            items.add(new MoodHistoryItem(
                                    entry.getFecha() != null ? entry.getFecha() : "",
                                    entry.getEstado() != null ? entry.getEstado() : "",
                                    entry.getNota() != null ? entry.getNota() : ""));
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
            public void onFailure(@NonNull Call<RespuestaApi<List<es.epycus.app.model.dto.EstadoAnimoEntry>>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                if (isAlive()) binding.layoutHistorial.setVisibility(View.GONE);
                mostrarErrorRed(t);
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
        private LayoutInflater inflater;

        MoodHistoryAdapter(List<MoodHistoryItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_historial_animo, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MoodHistoryItem item = items.get(position);
            holder.tvFecha.setText(item.fecha);
            holder.tvEstado.setText(item.estado);
            if (!item.nota.isEmpty()) {
                holder.tvNota.setText("\"" + item.nota + "\"");
                holder.tvNota.setVisibility(View.VISIBLE);
            } else {
                holder.tvNota.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView tvFecha;
            final TextView tvEstado;
            final TextView tvNota;

            ViewHolder(View view) {
                super(view);
                tvFecha = view.findViewById(R.id.tvFecha);
                tvEstado = view.findViewById(R.id.tvEstado);
                tvNota = view.findViewById(R.id.tvNota);
            }
        }
    }
}

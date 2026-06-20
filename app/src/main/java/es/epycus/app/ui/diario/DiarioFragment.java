package es.epycus.app.ui.diario;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.databinding.FragmentDiarioBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.MisionDto;
import es.epycus.app.model.dto.PreguntaGuiaResponse;
import es.epycus.app.repository.DiarioRepository;
import es.epycus.app.repository.MisionesRepository;
import es.epycus.app.ui.adapters.MisionAdapter;
import es.epycus.app.ui.ia.IaChatActivity;
import es.epycus.app.util.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiarioFragment extends Fragment {

    private static final String TAG = "DiarioFragment";
    private static final String CACHE_KEY_PREGUNTA = "pregunta_guia";
    private static final String CACHE_KEY_MISIONES = "misiones";
    private FragmentDiarioBinding binding;
    private View selectedMood;
    private String selectedMoodText = "";
    private DiarioRepository diarioRepository;
    private MisionesRepository misionesRepository;
    private MisionAdapter misionAdapter;
    private final List<Call<?>> activeCalls = new ArrayList<>();
    private String entradaHoyTexto = "";
    private String entradaHoyEstado = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDiarioBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        diarioRepository = new DiarioRepository(requireContext());
        misionesRepository = new MisionesRepository(requireContext());

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

        misionAdapter = new MisionAdapter(id -> completarMision(id));
        binding.rvMisiones.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMisiones.setAdapter(misionAdapter);

        binding.btnNuevaMision.setOnClickListener(v -> mostrarDialogoNuevaMision());

        binding.swipeRefresh.setOnRefreshListener(this::recargarTodo);
        binding.swipeRefresh.setColorSchemeResources(R.color.light_accent, R.color.light_accent_secondary);

        recargarTodo();

        return view;
    }

    private void recargarTodo() {
        cargarPreguntaGuia();
        cargarEntradaHoy();
        cargarMisiones();
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
        Call<RespuestaApi<Object>> call = diarioRepository.hoy();
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
        Call<RespuestaApi<PreguntaGuiaResponse>> call = diarioRepository.preguntaGuia();
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
                    diarioRepository.cacheJson(CACHE_KEY_PREGUNTA, json);
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
        String json = diarioRepository.getCachedJson(CACHE_KEY_PREGUNTA);
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

    private void cargarMisiones() {
        Call<RespuestaApi<List<MisionDto>>> call = misionesRepository.listar();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<List<MisionDto>>> call,
                                   @NonNull Response<RespuestaApi<List<MisionDto>>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful() && response.body() != null
                        && response.body().isExito() && response.body().getDatos() != null) {
                    try {
                        List<MisionDto> misiones = response.body().getDatos();
                        Gson gson = new Gson();
                        String json = gson.toJson(misiones);
                        misionesRepository.cacheJson(CACHE_KEY_MISIONES, json);
                        actualizarVistaMisiones(misiones);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing misiones", e);
                        ocultarMisiones();
                    }
                } else {
                    ocultarMisiones();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<List<MisionDto>>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                String cached = misionesRepository.getCachedJson(CACHE_KEY_MISIONES);
                if (cached != null) {
                    try {
                        Gson gson = new Gson();
                        MisionDto[] arr = gson.fromJson(cached, MisionDto[].class);
                        List<MisionDto> misiones = new ArrayList<>();
                        for (MisionDto m : arr) misiones.add(m);
                        actualizarVistaMisiones(misiones);
                    } catch (Exception e) {
                        ocultarMisiones();
                    }
                } else {
                    ocultarMisiones();
                }
            }
        });
    }

    private void actualizarVistaMisiones(List<MisionDto> misiones) {
        binding.layoutMisiones.setVisibility(View.VISIBLE);
        if (misiones.isEmpty()) {
            binding.rvMisiones.setVisibility(View.GONE);
            binding.layoutMisionesEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.rvMisiones.setVisibility(View.VISIBLE);
            binding.layoutMisionesEmpty.setVisibility(View.GONE);
            misionAdapter.setMisiones(misiones);
        }
    }

    private void ocultarMisiones() {
        binding.layoutMisiones.setVisibility(View.GONE);
    }

    private void completarMision(int id) {
        Call<RespuestaApi<Object>> call = misionesRepository.completar(id);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                   @NonNull Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful()) {
                    Snackbar.make(requireView(), getString(R.string.mision_completada),
                            Snackbar.LENGTH_SHORT).show();
                    cargarMisiones();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                mostrarErrorRed(t);
            }
        });
    }

    private void mostrarDialogoNuevaMision() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.nueva_mision));

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 16, 48, 16);

        EditText etNombre = new EditText(requireContext());
        etNombre.setHint(getString(R.string.mision_nombre_hint));
        layout.addView(etNombre);

        EditText etDescripcion = new EditText(requireContext());
        etDescripcion.setHint(getString(R.string.mision_descripcion_hint));
        layout.addView(etDescripcion);

        Spinner spPrioridad = new Spinner(requireContext());
        ArrayAdapter<String> prioridadAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{getString(R.string.prioridad_alta), getString(R.string.prioridad_media), getString(R.string.prioridad_baja)});
        prioridadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrioridad.setAdapter(prioridadAdapter);
        layout.addView(spPrioridad);

        builder.setView(layout);
        builder.setPositiveButton(getString(R.string.crear), (dialog, which) -> {
            String nombre = etNombre.getText().toString().trim();
            if (nombre.isEmpty()) {
                Snackbar.make(requireView(), getString(R.string.completa_campos), Snackbar.LENGTH_SHORT).show();
                return;
            }
            String descripcion = etDescripcion.getText().toString().trim();
            String prioridad = spPrioridad.getSelectedItem().toString();
            crearMision(nombre, descripcion, prioridad);
        });
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.show();
    }

    private void crearMision(String nombre, String descripcion, String prioridad) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", nombre);
        if (!descripcion.isEmpty()) body.addProperty("descripcion", descripcion);
        body.addProperty("prioridad", prioridad);

        Call<RespuestaApi<Object>> call = misionesRepository.crear(body);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                   @NonNull Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful()) {
                    Snackbar.make(requireView(), getString(R.string.mision_creada),
                            Snackbar.LENGTH_SHORT).show();
                    cargarMisiones();
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

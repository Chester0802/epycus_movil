package es.epycus.app.ui.habitos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.databinding.FragmentHabitosBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.CompletarHabitoResponse;
import es.epycus.app.model.dto.FallarHabitoResponse;
import es.epycus.app.model.dto.HabitoHoyDto;
import es.epycus.app.model.dto.SuccessResponseDto;
import es.epycus.app.repository.HabitosRepository;
import es.epycus.app.util.CacheManager;
import es.epycus.app.ui.adapters.HabitoHoyAdapter;
import es.epycus.app.util.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HabitosFragment extends Fragment {

    private static final String TAG = "HabitosFragment";
    private static final String CACHE_KEY_HABITOS = "habitos_hoy";
    private FragmentHabitosBinding binding;
    private HabitosRepository repository;
    private HabitoHoyAdapter adapter;
    private final List<Call<?>> activeCalls = new ArrayList<>();
    private List<String> categorias = new ArrayList<>();
    private List<HabitoHoyDto> todosHabitos = new ArrayList<>();
    private String categoriaSeleccionada = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHabitosBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        repository = new HabitosRepository(requireContext());

        adapter = new HabitoHoyAdapter(new HabitoHoyAdapter.OnHabitoListener() {
            @Override
            public void onCompletar(int id) {
                completarHabito(id);
            }

            @Override
            public void onFallar(int id) {
                fallarHabito(id);
            }

            @Override
            public void onEditar(int id) {
                mostrarDialogoEditarHabito(id);
            }

            @Override
            public void onEliminar(int id) {
                eliminarHabito(id);
            }
        });

        binding.rvHabitos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHabitos.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new HabitoSwipeCallback(adapter));
        itemTouchHelper.attachToRecyclerView(binding.rvHabitos);

        binding.swipeRefresh.setOnRefreshListener(this::cargarHabitos);
        binding.swipeRefresh.setColorSchemeResources(R.color.light_accent, R.color.light_accent_secondary);

        cargarHabitos();
        cargarCategorias();

        binding.btnNuevoHabito.setOnClickListener(v -> mostrarDialogoNuevoHabito());
        binding.btnEmptyCrearHabito.setOnClickListener(v -> mostrarDialogoNuevoHabito());

        return view;
    }

    private void cargarHabitos() {
        binding.loadingView.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.rvHabitos.setVisibility(View.GONE);

        Call<RespuestaApi<List<HabitoHoyDto>>> call = repository.hoy();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<List<HabitoHoyDto>>> call,
                                   @NonNull Response<RespuestaApi<List<HabitoHoyDto>>> response) {
                activeCalls.remove(call);
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && response.body().isExito()
                        && response.body().getDatos() != null) {
                    try {
                        todosHabitos = response.body().getDatos();
                        Gson gson = new Gson();
                        String json = gson.toJson(todosHabitos);
                        repository.cacheHabitosJson(CACHE_KEY_HABITOS, json, CacheManager.TTL_HABITOS);

                        aplicarFiltro();
                        binding.tvHabitosCount.setText(getString(R.string.habitos_hoy_formato, todosHabitos.size()));
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing habitos", e);
                        mostrarEmpty();
                    }
                } else {
                    mostrarEmpty();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<List<HabitoHoyDto>>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                String cached = repository.getCachedHabitosJson(CACHE_KEY_HABITOS);
                if (cached != null) {
                    try {
                        Gson gson = new Gson();
                        HabitoHoyDto[] habitosArray = gson.fromJson(cached, HabitoHoyDto[].class);
                        todosHabitos = new ArrayList<>(java.util.Arrays.asList(habitosArray));
                        aplicarFiltro();
                    } catch (Exception e) {
                        mostrarEmpty();
                    }
                } else {
                    mostrarEmpty();
                }
                mostrarErrorRed(t);
            }
        });
    }

    private void aplicarFiltro() {
        List<HabitoHoyDto> filtrados;
        if (categoriaSeleccionada != null && !categoriaSeleccionada.equals("Todas")) {
            filtrados = new ArrayList<>();
            for (HabitoHoyDto h : todosHabitos) {
                if (categoriaSeleccionada.equals(h.getCategoria())) {
                    filtrados.add(h);
                }
            }
        } else {
            filtrados = new ArrayList<>(todosHabitos);
        }

        if (filtrados.isEmpty()) {
            mostrarEmpty();
        } else {
            binding.rvHabitos.setVisibility(View.VISIBLE);
            binding.layoutEmpty.setVisibility(View.GONE);
            adapter.setHabitos(filtrados);
        }
    }

    private void mostrarEmpty() {
        binding.rvHabitos.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
        binding.tvEmpty.setText(todosHabitos.isEmpty() ?
                getString(R.string.crea_primer_habito) : getString(R.string.error_conexion_habitos));
    }

    private void cargarCategorias() {
        Call<RespuestaApi<Object>> call = repository.categorias();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RespuestaApi<Object>> call, Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body().getDatos());
                        if (json.startsWith("[")) {
                            if (json.contains("nombre")) {
                                com.google.gson.JsonArray arr = com.google.gson.JsonParser.parseString(json).getAsJsonArray();
                                for (int i = 0; i < arr.size(); i++) {
                                    JsonObject obj = arr.get(i).getAsJsonObject();
                                    if (obj.has("nombre")) {
                                        categorias.add(obj.get("nombre").getAsString());
                                    }
                                }
                            } else {
                                String[] items = gson.fromJson(json, String[].class);
                                for (String item : items) {
                                    categorias.add(item);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing categorias", e);
                    }
                }
                actualizarChips();
            }

            @Override
            public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {
                activeCalls.remove(call);
                actualizarChips();
            }
        });
    }

    private void actualizarChips() {
        if (categorias.isEmpty()) {
            categorias.add(getString(R.string.categoria_general));
            categorias.add(getString(R.string.categoria_salud));
            categorias.add(getString(R.string.categoria_estudio));
            categorias.add(getString(R.string.categoria_trabajo));
            categorias.add(getString(R.string.categoria_personal));
        }

        binding.chipGroupCategorias.removeAllViews();

        Chip chipTodas = new Chip(requireContext());
        chipTodas.setText(getString(R.string.todas));
        chipTodas.setChipBackgroundColorResource(android.R.color.transparent);
        chipTodas.setChipStrokeColorResource(R.color.light_accent);
        chipTodas.setChipStrokeWidth(1f);
        chipTodas.setChipCornerRadius(18f);
        chipTodas.setCheckedIconVisible(false);
        chipTodas.setCheckable(true);
        chipTodas.setChecked(true);
        chipTodas.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                categoriaSeleccionada = null;
                aplicarFiltro();
            }
        });
        binding.chipGroupCategorias.addView(chipTodas);

        for (String cat : categorias) {
            Chip chip = new Chip(requireContext());
            chip.setText(cat);
            chip.setChipBackgroundColorResource(android.R.color.transparent);
            chip.setChipStrokeColorResource(R.color.light_accent);
            chip.setChipStrokeWidth(1f);
            chip.setChipCornerRadius(18f);
            chip.setCheckedIconVisible(false);
            chip.setCheckable(true);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    categoriaSeleccionada = cat;
                    aplicarFiltro();
                }
            });
            binding.chipGroupCategorias.addView(chip);
        }
    }

    private void mostrarDialogoNuevoHabito() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.nuevo_habito));

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nuevo_habito, null);
        android.widget.EditText etNombre = view.findViewById(R.id.etNombreHabito);
        Spinner spCategoria = view.findViewById(R.id.spCategoriaHabito);
        Spinner spFrecuencia = view.findViewById(R.id.spFrecuenciaHabito);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categorias);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(catAdapter);

        String[] frecuencias = {
                getString(R.string.frecuencia_diaria),
                getString(R.string.frecuencia_semanal),
                getString(R.string.frecuencia_personalizada)
        };
        ArrayAdapter<String> freqAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, frecuencias);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrecuencia.setAdapter(freqAdapter);

        builder.setView(view);
        builder.setPositiveButton(getString(R.string.crear), (dialog, which) -> {
            String nombre = etNombre.getText().toString().trim();
            if (nombre.isEmpty()) {
                Snackbar.make(binding.getRoot(), getString(R.string.completa_campos), Snackbar.LENGTH_SHORT).show();
                return;
            }
            String categoria = spCategoria.getSelectedItem().toString();
            String frecuencia = spFrecuencia.getSelectedItem().toString();
            crearHabito(nombre, categoria, frecuencia);
        });
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.show();
    }

    private void crearHabito(String nombre, String categoria, String frecuencia) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", nombre);

        // Mapeo de frecuencia para la API (Indispensable para que el server no de error)
        String frecuenciaApi = frecuencia.toLowerCase().contains("semanal") ? "WEEKLY" : "DAILY";
        body.addProperty("frecuencia", frecuenciaApi);
        body.addProperty("categoria", categoria);

        Call<RespuestaApi<SuccessResponseDto>> call = repository.crear(body);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RespuestaApi<SuccessResponseDto>> call, Response<RespuestaApi<SuccessResponseDto>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful()) {
                    Snackbar.make(binding.getRoot(), getString(R.string.habito_creado), Snackbar.LENGTH_SHORT).show();
                    cargarHabitos(); // Recarga la lista
                } else {
                    String msg = NetworkUtils.getErrorMessage(requireContext(), response);
                    Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<SuccessResponseDto>> call, Throwable t) {
                activeCalls.remove(call);
                mostrarErrorRed(t);
            }
        });
    }


    private void completarHabito(int id) {
        Call<RespuestaApi<CompletarHabitoResponse>> call = repository.completar(id);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<CompletarHabitoResponse>> call,
                                   @NonNull Response<RespuestaApi<CompletarHabitoResponse>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful()) {
                    Snackbar.make(binding.rvHabitos, getString(R.string.habito_completado_xp),
                            Snackbar.LENGTH_SHORT).show();
                    cargarHabitos();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<CompletarHabitoResponse>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                mostrarErrorRed(t);
            }
        });
    }

    private void fallarHabito(int id) {
        Call<RespuestaApi<FallarHabitoResponse>> call = repository.fallar(id);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<FallarHabitoResponse>> call,
                                   @NonNull Response<RespuestaApi<FallarHabitoResponse>> response) {
                activeCalls.remove(call);
                cargarHabitos();
            }

            @Override
            public void onFailure(Call<RespuestaApi<FallarHabitoResponse>> call, Throwable t) {
                activeCalls.remove(call);
                cargarHabitos();
            }
        });
    }

    private void mostrarDialogoEditarHabito(int id) {
        Snackbar.make(binding.getRoot(), getString(R.string.funcionalidad_pronto), Snackbar.LENGTH_SHORT).show();
    }

    private void eliminarHabito(int id) {
        Call<RespuestaApi<SuccessResponseDto>> call = repository.eliminar(id);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<RespuestaApi<SuccessResponseDto>> call, Response<RespuestaApi<SuccessResponseDto>> response) {
                activeCalls.remove(call);
                cargarHabitos();
            }

            @Override
            public void onFailure(Call<RespuestaApi<SuccessResponseDto>> call, Throwable t) {
                activeCalls.remove(call);
                mostrarErrorRed(t);
            }
        });
    }

    private void mostrarErrorRed(Throwable t) {
        Snackbar.make(binding.rvHabitos,
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

    private static class HabitoSwipeCallback extends ItemTouchHelper.SimpleCallback {
        private final HabitoHoyAdapter adapter;

        HabitoSwipeCallback(HabitoHoyAdapter adapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.adapter = adapter;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            HabitoHoyDto habito = adapter.getHabitoAt(position);
            if (direction == ItemTouchHelper.RIGHT) {
                adapter.notifyItemChanged(position);
                adapter.getListener().onCompletar(habito.getId());
            } else if (direction == ItemTouchHelper.LEFT) {
                adapter.notifyItemChanged(position);
                adapter.getListener().onFallar(habito.getId());
            }
        }
    }
}

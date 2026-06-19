package es.epycus.app.ui.habitos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import es.epycus.app.databinding.FragmentHabitosBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.HabitoHoyDto;
import es.epycus.app.repository.HabitosRepository;
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
        });

        binding.rvHabitos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHabitos.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::cargarHabitos);
        binding.swipeRefresh.setColorSchemeResources(R.color.light_accent, R.color.light_accent_secondary);

        cargarHabitos();
        cargarCategorias();

        binding.btnNuevoHabito.setOnClickListener(v -> mostrarDialogoNuevoHabito());

        return view;
    }

    private void cargarHabitos() {
        binding.loadingView.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);
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
                        List<HabitoHoyDto> habitos = response.body().getDatos();
                        Gson gson = new Gson();
                        String json = gson.toJson(habitos);
                        repository.cacheHabitosJson(CACHE_KEY_HABITOS, json);

                        if (habitos.isEmpty()) {
                            binding.tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            binding.rvHabitos.setVisibility(View.VISIBLE);
                            adapter.setHabitos(habitos);
                            binding.tvHabitosCount.setText(getString(R.string.habitos_hoy_formato, habitos.size()));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing habitos", e);
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.tvEmpty.setVisibility(View.VISIBLE);
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
                        List<HabitoHoyDto> habitos = java.util.Arrays.asList(habitosArray);
                        binding.rvHabitos.setVisibility(View.VISIBLE);
                        adapter.setHabitos(habitos);
                    } catch (Exception e) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                        binding.tvEmpty.setText(getString(R.string.error_conexion));
                    }
                } else {
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setText(getString(R.string.error_conexion));
                }
                mostrarErrorRed(t);
            }
        });
    }

    private void cargarCategorias() {
        Call<RespuestaApi<Object>> call = repository.categorias();
        activeCalls.add(call);
        call.enqueue(new Callback<RespuestaApi<Object>>() {
            @Override
            public void onResponse(Call<RespuestaApi<Object>> call, Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body().getDatos());
                        // Parse categories - might be a list of strings or objects with nombre
                        if (json.startsWith("[")) {
                            if (json.contains("nombre")) {
                                // Array of objects with nombre field
                                com.google.gson.JsonArray arr = com.google.gson.JsonParser.parseString(json).getAsJsonArray();
                                for (int i = 0; i < arr.size(); i++) {
                                    JsonObject obj = arr.get(i).getAsJsonObject();
                                    if (obj.has("nombre")) {
                                        categorias.add(obj.get("nombre").getAsString());
                                    }
                                }
                            } else {
                                // Array of plain strings
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
                if (categorias.isEmpty()) {
                    categorias.add("General");
                    categorias.add("Salud");
                    categorias.add("Estudio");
                    categorias.add("Trabajo");
                    categorias.add("Personal");
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {
                activeCalls.remove(call);
                categorias.add("General");
                categorias.add("Salud");
                categorias.add("Estudio");
                categorias.add("Trabajo");
                categorias.add("Personal");
            }
        });
    }

    private void mostrarDialogoNuevoHabito() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.nuevo_habito));

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nuevo_habito, null);
        EditText etNombre = view.findViewById(R.id.etNombreHabito);
        Spinner spCategoria = view.findViewById(R.id.spCategoriaHabito);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapter);

        builder.setView(view);
        builder.setPositiveButton(getString(R.string.crear), (dialog, which) -> {
            String nombre = etNombre.getText().toString().trim();
            if (nombre.isEmpty()) {
                Snackbar.make(binding.getRoot(), getString(R.string.completa_campos), Snackbar.LENGTH_SHORT).show();
                return;
            }
            String categoria = spCategoria.getSelectedItem().toString();
            crearHabito(nombre, categoria);
        });
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.show();
    }

    private void crearHabito(String nombre, String categoria) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", nombre);
        body.addProperty("categoria", categoria);
        body.addProperty("frecuencia", "Diaria");
        body.addProperty("notas", "");

        Call<RespuestaApi<Object>> call = repository.crear(body);
        activeCalls.add(call);
        call.enqueue(new Callback<RespuestaApi<Object>>() {
            @Override
            public void onResponse(Call<RespuestaApi<Object>> call, Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful()) {
                    Snackbar.make(binding.getRoot(), getString(R.string.habito_creado), Snackbar.LENGTH_SHORT).show();
                    cargarHabitos();
                } else {
                    String msg = NetworkUtils.getErrorMessage(requireContext(), response);
                    Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {
                activeCalls.remove(call);
                mostrarErrorRed(t);
            }
        });
    }

    private void completarHabito(int id) {
        Call<RespuestaApi<Object>> call = repository.completar(id);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                   @NonNull Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful()) {
                    Snackbar.make(binding.rvHabitos, getString(R.string.habito_completado_xp),
                            Snackbar.LENGTH_SHORT).show();
                    cargarHabitos();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                mostrarErrorRed(t);
            }
        });
    }

    private void fallarHabito(int id) {
        Call<RespuestaApi<Object>> call = repository.fallar(id);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                   @NonNull Response<RespuestaApi<Object>> response) {
                activeCalls.remove(call);
                cargarHabitos();
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
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
}
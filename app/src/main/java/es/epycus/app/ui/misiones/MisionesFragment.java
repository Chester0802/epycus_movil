package es.epycus.app.ui.misiones;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.data.local.entity.MisionEntity;
import es.epycus.app.databinding.FragmentMisionesBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.CategoriaDto;
import es.epycus.app.model.dto.MisionCompletarResponse;
import es.epycus.app.model.dto.MisionDto;
import es.epycus.app.model.dto.SuccessResponseDto;
import es.epycus.app.repository.MisionesRepository;
import es.epycus.app.ui.adapters.MisionAdapter;
import es.epycus.app.util.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisionesFragment extends Fragment {

    private static final String TAG = "MisionesFragment";
    private FragmentMisionesBinding binding;
    private MisionesRepository repository;
    private MisionAdapter adapter;
    private final List<Call<?>> activeCalls = new ArrayList<>();
    private int defaultCategoriaId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMisionesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        repository = new MisionesRepository(requireContext());

        adapter = new MisionAdapter(id -> completarMision(id));
        binding.rvMisiones.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMisiones.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::cargarMisiones);
        binding.swipeRefresh.setColorSchemeResources(R.color.light_accent, R.color.light_accent_secondary);

        binding.btnNuevaMision.setOnClickListener(v -> mostrarDialogoNuevaMision());

        cargarMisiones();
        cargarCategorias();

        return view;
    }

    private void cargarCategorias() {
        Call<RespuestaApi<List<CategoriaDto>>> call = repository.categorias();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<List<CategoriaDto>>> call,
                                   @NonNull Response<RespuestaApi<List<CategoriaDto>>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful() && response.body() != null
                        && response.body().isExito() && response.body().getDatos() != null) {
                    List<CategoriaDto> cats = response.body().getDatos();
                    if (!cats.isEmpty()) {
                        defaultCategoriaId = cats.get(0).getId();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<List<CategoriaDto>>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
            }
        });
    }

    private void cargarMisiones() {
        mostrarCacheMisiones();

        Call<RespuestaApi<List<MisionDto>>> call = repository.listar();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<List<MisionDto>>> call,
                                   @NonNull Response<RespuestaApi<List<MisionDto>>> response) {
                activeCalls.remove(call);
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().isExito() && response.body().getDatos() != null) {
                    List<MisionDto> misiones = response.body().getDatos();

                    List<MisionEntity> entities = new ArrayList<>();
                    for (MisionDto dto : misiones) {
                        entities.add(repository.toEntity(dto));
                    }
                    repository.cacheMisiones(entities);

                    if (misiones.isEmpty()) {
                        mostrarEmpty();
                    } else {
                        binding.rvMisiones.setVisibility(View.VISIBLE);
                        binding.layoutEmpty.setVisibility(View.GONE);
                        adapter.setMisiones(misiones);
                    }
                } else if (adapter.getItemCount() > 0) {
                    mostrarError(getString(R.string.error_conexion));
                } else {
                    mostrarEmpty();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<List<MisionDto>>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                binding.swipeRefresh.setRefreshing(false);
                if (adapter.getItemCount() == 0) {
                    mostrarEmpty();
                }
                mostrarErrorRed(t);
            }
        });
    }

    private void mostrarCacheMisiones() {
        List<MisionEntity> cached = repository.getCachedMisiones();
        if (cached != null && !cached.isEmpty()) {
            List<MisionDto> misiones = new ArrayList<>();
            for (MisionEntity entity : cached) {
                misiones.add(repository.toDto(entity));
            }
            binding.rvMisiones.setVisibility(View.VISIBLE);
            binding.layoutEmpty.setVisibility(View.GONE);
            adapter.setMisiones(misiones);
        }
    }

    private void mostrarError(String msg) {
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show();
    }

    private void mostrarEmpty() {
        binding.rvMisiones.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
    }

    private void completarMision(int id) {
        Call<RespuestaApi<MisionCompletarResponse>> call = repository.completar(id);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<MisionCompletarResponse>> call,
                                   @NonNull Response<RespuestaApi<MisionCompletarResponse>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful()) {
                    Snackbar.make(requireView(), getString(R.string.mision_completada),
                            Snackbar.LENGTH_SHORT).show();
                    cargarMisiones();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<MisionCompletarResponse>> call, @NonNull Throwable t) {
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
            crearMision(nombre, etDescripcion.getText().toString().trim(), spPrioridad.getSelectedItem().toString());
        });
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.show();
    }

    private void crearMision(String nombre, String descripcion, String prioridad) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", nombre);
        if (!descripcion.isEmpty()) body.addProperty("descripcion", descripcion);
        body.addProperty("prioridad", prioridad);
        if (defaultCategoriaId > 0) {
            body.addProperty("categoriaId", defaultCategoriaId);
        }

        Call<RespuestaApi<SuccessResponseDto>> call = repository.crear(body);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<SuccessResponseDto>> call,
                                   @NonNull Response<RespuestaApi<SuccessResponseDto>> response) {
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
            public void onFailure(@NonNull Call<RespuestaApi<SuccessResponseDto>> call, @NonNull Throwable t) {
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

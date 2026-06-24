package es.epycus.app.ui.misiones;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
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
    private List<CategoriaDto> categorias = new ArrayList<>();
    private int defaultCategoriaId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMisionesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        repository = new MisionesRepository(requireContext());

        adapter = new MisionAdapter(new MisionAdapter.OnMisionListener() {
            @Override
            public void onCompletar(int id) {
                completarMision(id);
            }

            @Override
            public void onEditar(MisionDto mision) {
                mostrarDialogoEditarMision(mision);
            }
        });
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
                    categorias = response.body().getDatos();
                    if (!categorias.isEmpty()) {
                        defaultCategoriaId = categorias.get(0).getId();
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
        mostrarDialogoMision(null);
    }

    private void mostrarDialogoEditarMision(MisionDto mision) {
        mostrarDialogoMision(mision);
    }

    private void mostrarDialogoMision(MisionDto existing) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nueva_mision, null);
        com.google.android.material.textfield.TextInputEditText etNombre = view.findViewById(R.id.etMisionNombre);
        com.google.android.material.textfield.TextInputEditText etDescripcion = view.findViewById(R.id.etMisionDescripcion);
        Spinner spPrioridad = view.findViewById(R.id.spMisionPrioridad);
        Spinner spCategoria = view.findViewById(R.id.spMisionCategoria);
        View layoutFecha = view.findViewById(R.id.layoutFecha);
        TextView tvFecha = view.findViewById(R.id.tvFechaSeleccionada);
        View loadingOverlay = view.findViewById(R.id.loadingOverlay);

        String[] prioridades = {
                getString(R.string.prioridad_alta),
                getString(R.string.prioridad_media),
                getString(R.string.prioridad_baja)
        };
        ArrayAdapter<String> prioridadAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, prioridades);
        prioridadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrioridad.setAdapter(prioridadAdapter);

        List<String> nombresCategoria = new ArrayList<>();
        for (CategoriaDto c : categorias) {
            nombresCategoria.add(c.getNombre());
        }
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, nombresCategoria);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(catAdapter);

        final String[] selectedDate = {""};

        if (existing != null) {
            etNombre.setText(existing.getNombre());
            if (existing.getDescripcion() != null) etDescripcion.setText(existing.getDescripcion());
            for (int i = 0; i < prioridades.length; i++) {
                if (prioridades[i].equals(existing.getPrioridad())) {
                    spPrioridad.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < categorias.size(); i++) {
                if (categorias.get(i).getId() == existing.getCategoriaId()) {
                    spCategoria.setSelection(i);
                    break;
                }
            }
            if (existing.getFechaLimite() != null && !existing.getFechaLimite().isEmpty()) {
                selectedDate[0] = existing.getFechaLimite();
                tvFecha.setText(existing.getFechaLimite());
            }
        }

        layoutFecha.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            if (!selectedDate[0].isEmpty()) {
                try {
                    String[] parts = selectedDate[0].split("-");
                    cal.set(Calendar.YEAR, Integer.parseInt(parts[0]));
                    cal.set(Calendar.MONTH, Integer.parseInt(parts[1]) - 1);
                    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[2]));
                } catch (Exception ignored) {}
            }
            DatePickerDialog dpd = new DatePickerDialog(requireContext(), (view1, year, month, dayOfMonth) -> {
                selectedDate[0] = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                tvFecha.setText(selectedDate[0]);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        boolean isEditing = existing != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(isEditing ? getString(R.string.editar_mision) : getString(R.string.nueva_mision));
        builder.setView(view);
        builder.setPositiveButton(isEditing ? getString(R.string.guardar) : getString(R.string.crear), null);
        builder.setNegativeButton(getString(R.string.cancelar), null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String nombre = etNombre.getText().toString().trim();
                if (nombre.isEmpty()) {
                    Snackbar.make(requireView(), getString(R.string.completa_campos), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                String desc = etDescripcion.getText().toString().trim();
                String prioridad = spPrioridad.getSelectedItem().toString();
                int catId = spCategoria.getSelectedItemPosition() >= 0 && spCategoria.getSelectedItemPosition() < categorias.size()
                        ? categorias.get(spCategoria.getSelectedItemPosition()).getId() : defaultCategoriaId;
                String fecha = selectedDate[0];

                JsonObject body = new JsonObject();
                body.addProperty("nombre", nombre);
                if (!desc.isEmpty()) body.addProperty("descripcion", desc);
                body.addProperty("prioridad", prioridad);
                if (catId > 0) body.addProperty("categoriaId", catId);
                if (!fecha.isEmpty()) body.addProperty("fechaLimite", fecha);

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
                loadingOverlay.setVisibility(View.VISIBLE);

                if (isEditing) {
                    actualizarMision(existing.getId(), body, dialog, loadingOverlay);
                } else {
                    crearMision(body, dialog, loadingOverlay);
                }
            });
        });
        dialog.show();
    }

    private void mostrarCargandoMision(AlertDialog dialog, View loadingOverlay, boolean cargando) {
        if (dialog.isShowing()) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!cargando);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(!cargando);
            loadingOverlay.setVisibility(cargando ? View.VISIBLE : View.GONE);
        }
    }

    private void crearMision(JsonObject body, AlertDialog dialog, View loadingOverlay) {
        Call<RespuestaApi<SuccessResponseDto>> call = repository.crear(body);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<SuccessResponseDto>> call,
                                   @NonNull Response<RespuestaApi<SuccessResponseDto>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    Snackbar.make(requireView(), getString(R.string.mision_creada),
                            Snackbar.LENGTH_SHORT).show();
                    cargarMisiones();
                } else {
                    String msg = NetworkUtils.getErrorMessage(requireContext(), response);
                    Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show();
                    mostrarCargandoMision(dialog, loadingOverlay, false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<SuccessResponseDto>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                mostrarErrorRed(t);
                mostrarCargandoMision(dialog, loadingOverlay, false);
            }
        });
    }

    private void actualizarMision(int id, JsonObject body, AlertDialog dialog, View loadingOverlay) {
        Call<RespuestaApi<SuccessResponseDto>> call = repository.actualizar(id, body);
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<SuccessResponseDto>> call,
                                   @NonNull Response<RespuestaApi<SuccessResponseDto>> response) {
                activeCalls.remove(call);
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    Snackbar.make(requireView(), getString(R.string.mision_actualizada),
                            Snackbar.LENGTH_SHORT).show();
                    cargarMisiones();
                } else {
                    String msg = NetworkUtils.getErrorMessage(requireContext(), response);
                    Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show();
                    mostrarCargandoMision(dialog, loadingOverlay, false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<SuccessResponseDto>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                mostrarErrorRed(t);
                mostrarCargandoMision(dialog, loadingOverlay, false);
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

package es.epycus.app.ui.habitos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.databinding.FragmentHabitosBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.HabitoHoyDto;
import es.epycus.app.repository.HabitosRepository;
import es.epycus.app.ui.adapters.HabitoHoyAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HabitosFragment extends Fragment {

    private static final String TAG = "HabitosFragment";
    private FragmentHabitosBinding binding;
    private HabitosRepository repository;
    private HabitoHoyAdapter adapter;

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

        cargarHabitos();

        binding.btnNuevoHabito.setOnClickListener(v ->
                Snackbar.make(v, getString(R.string.funcionalidad_pronto), Snackbar.LENGTH_SHORT).show());

        return view;
    }

    private void cargarHabitos() {
        binding.loadingView.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);
        binding.rvHabitos.setVisibility(View.GONE);

        repository.hoy().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                   @NonNull Response<RespuestaApi<Object>> response) {
                binding.loadingView.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isExito()
                        && response.body().getDatos() != null) {
                    try {
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        String json = gson.toJson(response.body().getDatos());
                        HabitoHoyDto[] habitosArray = gson.fromJson(json, HabitoHoyDto[].class);
                        List<HabitoHoyDto> habitos = Arrays.asList(habitosArray);

                        if (habitos.isEmpty()) {
                            binding.tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            binding.rvHabitos.setVisibility(View.VISIBLE);
                            adapter.setHabitos(habitos);
                            binding.tvHabitosCount.setText(getString(R.string.habitos_hoy_formato, habitos.size()));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing habitos", e);
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                binding.loadingView.setVisibility(View.GONE);
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.tvEmpty.setText(getString(R.string.error_conexion_habitos));
            }
        });
    }

    private void completarHabito(int id) {
        repository.completar(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                   @NonNull Response<RespuestaApi<Object>> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(binding.rvHabitos, getString(R.string.habito_completado_xp),
                            Snackbar.LENGTH_SHORT).show();
                    cargarHabitos();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), getString(R.string.error_al_completar), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fallarHabito(int id) {
        repository.fallar(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                   @NonNull Response<RespuestaApi<Object>> response) {
                cargarHabitos();
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                Snackbar.make(binding.rvHabitos, getString(R.string.error_conexion),
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

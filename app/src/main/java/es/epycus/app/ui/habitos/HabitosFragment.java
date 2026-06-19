package es.epycus.app.ui.habitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.HabitoHoyDto;
import es.epycus.app.repository.HabitosRepository;
import es.epycus.app.ui.adapters.HabitoHoyAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HabitosFragment extends Fragment {

    private RecyclerView rvHabitos;
    private TextView tvEmpty, tvHabitosCount;
    private View loadingView;
    private HabitosRepository repository;
    private HabitoHoyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habitos, container, false);

        rvHabitos = view.findViewById(R.id.rvHabitos);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvHabitosCount = view.findViewById(R.id.tvHabitosCount);
        loadingView = view.findViewById(R.id.loadingView);
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

        rvHabitos.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHabitos.setAdapter(adapter);

        cargarHabitos();

        view.findViewById(R.id.btnNuevoHabito).setOnClickListener(v -> {
            Snackbar.make(v, "Funcionalidad pronto disponible", Snackbar.LENGTH_SHORT).show();
        });

        return view;
    }

    private void cargarHabitos() {
        loadingView.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        rvHabitos.setVisibility(View.GONE);

        repository.hoy().enqueue(new Callback<RespuestaApi<Object>>() {
            @Override
            public void onResponse(Call<RespuestaApi<Object>> call,
                                   Response<RespuestaApi<Object>> response) {
                loadingView.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isExito()
                        && response.body().getDatos() != null) {
                    try {
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        String json = gson.toJson(response.body().getDatos());
                        HabitoHoyDto[] habitosArray = gson.fromJson(json, HabitoHoyDto[].class);
                        List<HabitoHoyDto> habitos = Arrays.asList(habitosArray);

                        if (habitos.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            rvHabitos.setVisibility(View.VISIBLE);
                            adapter.setHabitos(habitos);
                            tvHabitosCount.setText(habitos.size() + " habitos hoy");
                        }
                    } catch (Exception e) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Error de conexion");
            }
        });
    }

    private void completarHabito(int id) {
        repository.completar(id).enqueue(new Callback<RespuestaApi<Object>>() {
            @Override
            public void onResponse(Call<RespuestaApi<Object>> call,
                                   Response<RespuestaApi<Object>> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(rvHabitos, "Habito completado! +XP",
                            Snackbar.LENGTH_SHORT).show();
                    cargarHabitos();
                }
            }

            @Override
            public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al completar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fallarHabito(int id) {
        repository.fallar(id).enqueue(new Callback<RespuestaApi<Object>>() {
            @Override
            public void onResponse(Call<RespuestaApi<Object>> call,
                                   Response<RespuestaApi<Object>> response) {
                cargarHabitos();
            }

            @Override
            public void onFailure(Call<RespuestaApi<Object>> call, Throwable t) {}
        });
    }
}

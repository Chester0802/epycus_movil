package es.epycus.app.ui.home;

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

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.CacheEntity;
import es.epycus.app.databinding.FragmentInicioBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.DashboardResponse;
import es.epycus.app.model.dto.GamificacionResponse;
import es.epycus.app.util.NetworkUtils;
import es.epycus.app.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioFragment extends Fragment {

    private static final String TAG = "InicioFragment";
    private FragmentInicioBinding binding;
    private SessionManager sessionManager;
    private AppDatabase database;
    private boolean dashboardDataLoaded = false;
    private boolean progresoDataLoaded = false;
    private final List<Call<?>> activeCalls = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInicioBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sessionManager = SessionManager.getInstance(requireContext());
        database = AppDatabase.getInstance(requireContext());

        String nombre = sessionManager.getUserName();
        binding.tvBienvenida.setText(getString(R.string.hola_formato, nombre != null ? nombre : "Epycus"));

        binding.swipeRefresh.setOnRefreshListener(() -> {
            cargarDashboard();
            cargarProgreso();
        });
        binding.swipeRefresh.setColorSchemeResources(R.color.light_accent, R.color.light_accent_secondary);

        cargarDashboard();
        cargarProgreso();

        return view;
    }

    private void cargarDashboard() {
        binding.loadingView.setVisibility(View.VISIBLE);

        Call<RespuestaApi<DashboardResponse>> call = RetrofitClient.getInstance(requireContext()).getApiDashboardService()
                .resumen();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<DashboardResponse>> call,
                                   @NonNull Response<RespuestaApi<DashboardResponse>> response) {
                activeCalls.remove(call);
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getDatos() != null) {
                    try {
                        DashboardResponse data = response.body().getDatos();
                        Gson gson = new Gson();
                        String json = gson.toJson(data);
                        AppDatabase.getWriteExecutor().execute(() ->
                                database.cacheDao().insert(
                                        new CacheEntity("dashboard", json)));
                        dashboardDataLoaded = true;

                        binding.tvHabitosPendientes.setText(
                                String.valueOf(data.getHabitosPendientes()));

                        if (data.getFrase() != null) {
                            binding.tvFrase.setText(data.getFrase().getFrase());
                            binding.tvFraseAutor.setText(getString(R.string.autor_formato, data.getFrase().getAutor()));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing dashboard", e);
                        binding.tvHabitosPendientes.setText(String.valueOf(0));
                    }
                }
                verificarCargaCompleta();
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<DashboardResponse>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                if (cargarDashboardDesdeCache()) {
                    dashboardDataLoaded = true;
                }
                verificarCargaCompleta();
                mostrarErrorRed(t);
            }
        });
    }

    private boolean cargarDashboardDesdeCache() {
        String json = database.cacheDao().getValue("dashboard");
        if (json != null) {
            try {
                Gson gson = new Gson();
                DashboardResponse data = gson.fromJson(json, DashboardResponse.class);
                binding.tvHabitosPendientes.setText(
                        String.valueOf(data.getHabitosPendientes()));
                if (data.getFrase() != null) {
                    binding.tvFrase.setText(data.getFrase().getFrase());
                    binding.tvFraseAutor.setText(getString(R.string.autor_formato, data.getFrase().getAutor()));
                }
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Error loading cached dashboard", e);
            }
        }
        return false;
    }

    private void cargarProgreso() {
        Call<RespuestaApi<GamificacionResponse>> call = RetrofitClient.getInstance(requireContext()).getApiGamificacionService()
                .miProgreso();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<GamificacionResponse>> call,
                                   @NonNull Response<RespuestaApi<GamificacionResponse>> response) {
                activeCalls.remove(call);
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getDatos() != null) {
                    try {
                        GamificacionResponse data = response.body().getDatos();
                        Gson gson = new Gson();
                        String json = gson.toJson(data);
                        AppDatabase.getWriteExecutor().execute(() ->
                                database.cacheDao().insert(
                                        new CacheEntity("progreso", json)));
                        progresoDataLoaded = true;

                        binding.tvRacha.setText(String.valueOf(data.getRachaActual()));
                        binding.tvNivel.setText(getString(R.string.nv_formato, data.getNivel()));
                        binding.pbXp.setProgress((int) data.getPorcentajeProgreso());
                        binding.tvXpText.setText(getString(R.string.xp_formato, data.getXpTotal()));
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing progreso", e);
                    }
                }
                verificarCargaCompleta();
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<GamificacionResponse>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                binding.swipeRefresh.setRefreshing(false);
                if (cargarProgresoDesdeCache()) {
                    progresoDataLoaded = true;
                }
                verificarCargaCompleta();
                mostrarErrorRed(t);
            }
        });
    }

    private boolean cargarProgresoDesdeCache() {
        String json = database.cacheDao().getValue("progreso");
        if (json != null) {
            try {
                Gson gson = new Gson();
                GamificacionResponse data = gson.fromJson(json, GamificacionResponse.class);
                binding.tvRacha.setText(String.valueOf(data.getRachaActual()));
                binding.tvNivel.setText(getString(R.string.nv_formato, data.getNivel()));
                binding.pbXp.setProgress((int) data.getPorcentajeProgreso());
                binding.tvXpText.setText(getString(R.string.xp_formato, data.getXpTotal()));
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Error loading cached progreso", e);
            }
        }
        return false;
    }

    private void verificarCargaCompleta() {
        if (!dashboardDataLoaded && !progresoDataLoaded) {
            binding.emptyView.setVisibility(View.VISIBLE);
        }
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

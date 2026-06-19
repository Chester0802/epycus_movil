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

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.databinding.FragmentInicioBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.DashboardResponse;
import es.epycus.app.model.dto.GamificacionResponse;
import es.epycus.app.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioFragment extends Fragment {

    private static final String TAG = "InicioFragment";
    private FragmentInicioBinding binding;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInicioBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sessionManager = SessionManager.getInstance(requireContext());

        String nombre = sessionManager.getUserName();
        binding.tvBienvenida.setText(getString(R.string.hola_formato, nombre != null ? nombre : "Epycus"));

        cargarDashboard();
        cargarProgreso();

        return view;
    }

    private void cargarDashboard() {
        RetrofitClient.getInstance(requireContext()).getApiDashboardService()
                .resumen().enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                           @NonNull Response<RespuestaApi<Object>> response) {
                        binding.loadingView.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getDatos() != null) {
                            try {
                                com.google.gson.Gson gson = new com.google.gson.Gson();
                                String json = gson.toJson(response.body().getDatos());
                                DashboardResponse data = gson.fromJson(json, DashboardResponse.class);

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
                    }

                    @Override
                    public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                        binding.loadingView.setVisibility(View.GONE);
                        Snackbar.make(requireView(), getString(R.string.error_conexion),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarProgreso() {
        RetrofitClient.getInstance(requireContext()).getApiGamificacionService()
                .miProgreso().enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<RespuestaApi<Object>> call,
                                           @NonNull Response<RespuestaApi<Object>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getDatos() != null) {
                            try {
                                com.google.gson.Gson gson = new com.google.gson.Gson();
                                String json = gson.toJson(response.body().getDatos());
                                GamificacionResponse data = gson.fromJson(json, GamificacionResponse.class);

                                binding.tvRacha.setText(String.valueOf(data.getRachaActual()));
                                binding.tvNivel.setText(getString(R.string.nv_formato, data.getNivel()));
                                binding.pbXp.setProgress((int) data.getPorcentajeProgreso());
                                binding.tvXpText.setText(getString(R.string.xp_formato, data.getXpTotal()));
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing progreso", e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                        Snackbar.make(requireView(), getString(R.string.error_conexion),
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

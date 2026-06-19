package es.epycus.app.ui.perfil;

import android.content.Intent;
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
import es.epycus.app.databinding.FragmentPerfilBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.PerfilResponse;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.ui.auth.LoginActivity;
import es.epycus.app.util.SessionManager;

public class PerfilFragment extends Fragment {

    private static final String TAG = "PerfilFragment";
    private FragmentPerfilBinding binding;
    private SessionManager sessionManager;
    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sessionManager = SessionManager.getInstance(requireContext());
        authRepository = new AuthRepository(requireContext());

        cargarPerfil();

        binding.btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
        binding.btnPersonajes.setOnClickListener(v ->
                Snackbar.make(v, getString(R.string.proximamente), Snackbar.LENGTH_SHORT).show());
        binding.btnLogros.setOnClickListener(v ->
                Snackbar.make(v, getString(R.string.proximamente), Snackbar.LENGTH_SHORT).show());
        binding.btnEstadisticas.setOnClickListener(v ->
                Snackbar.make(v, getString(R.string.proximamente), Snackbar.LENGTH_SHORT).show());
        binding.btnConfiguracion.setOnClickListener(v ->
                Snackbar.make(v, getString(R.string.proximamente), Snackbar.LENGTH_SHORT).show());

        return view;
    }

    private void cargarPerfil() {
        binding.loadingView.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance(requireContext()).getApiPerfilService()
                .obtenerPerfil().enqueue(new retrofit2.Callback<>() {
                    @Override
                    public void onResponse(@NonNull retrofit2.Call<RespuestaApi<Object>> call,
                                           @NonNull retrofit2.Response<RespuestaApi<Object>> response) {
                        binding.loadingView.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null
                                && response.body().isExito() && response.body().getDatos() != null) {
                            try {
                                com.google.gson.Gson gson = new com.google.gson.Gson();
                                String json = gson.toJson(response.body().getDatos());
                                PerfilResponse perfilResp = gson.fromJson(json, PerfilResponse.class);

                                PerfilResponse.Perfil perfil = perfilResp.getPerfil();
                                binding.tvNombre.setText(perfil.getNombre());
                                binding.tvCorreo.setText(perfil.getCorreoElectronico());
                                binding.tvNivel.setText(getString(R.string.nivel_formato, perfil.getNivelActual()));
                                binding.tvRacha.setText(getString(R.string.dias_formato, perfil.getRachaActual()));
                                binding.tvXp.setText(getString(R.string.xp_formato, perfil.getXpTotal()));
                                binding.tvCarrera.setText(perfil.getCarreraNombre() != null ?
                                        perfil.getCarreraNombre() : getString(R.string.sin_carrera));
                                binding.tvMiembroDesde.setText(getString(R.string.miembro_desde_formato,
                                        perfil.getFechaRegistro()));

                                sessionManager.saveSession(
                                        sessionManager.getToken(),
                                        sessionManager.getRefreshToken(),
                                        sessionManager.getUserId(),
                                        perfil.getNombre(),
                                        perfil.getCorreoElectronico()
                                );
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing perfil", e);
                                cargarDatosLocales();
                            }
                        } else {
                            cargarDatosLocales();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull retrofit2.Call<RespuestaApi<Object>> call, @NonNull Throwable t) {
                        binding.loadingView.setVisibility(View.GONE);
                        cargarDatosLocales();
                    }
                });
    }

    private void cargarDatosLocales() {
        binding.tvNombre.setText(sessionManager.getUserName());
        binding.tvCorreo.setText(sessionManager.getUserEmail());
        binding.tvNivel.setText(getString(R.string.nivel_formato, 1));
        binding.tvRacha.setText(getString(R.string.dias_formato, 0));
        binding.tvXp.setText(getString(R.string.xp_formato, 0));
    }

    private void cerrarSesion() {
        authRepository.logout().enqueue(new retrofit2.Callback<RespuestaApi<Void>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<RespuestaApi<Void>> call,
                                   @NonNull retrofit2.Response<RespuestaApi<Void>> response) {
                logoutAndRedirect();
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<RespuestaApi<Void>> call, @NonNull Throwable t) {
                logoutAndRedirect();
            }
        });
    }

    private void logoutAndRedirect() {
        sessionManager.logout();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package es.epycus.app.ui.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.PerfilResponse;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.ui.auth.LoginActivity;
import es.epycus.app.util.SessionManager;

public class PerfilFragment extends Fragment {

    private TextView tvNombre, tvCorreo, tvNivel, tvRacha, tvXp, tvCarrera, tvMiembroDesde;
    private View loadingView;
    private SessionManager sessionManager;
    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        sessionManager = SessionManager.getInstance(requireContext());
        authRepository = new AuthRepository(requireContext());

        tvNombre = view.findViewById(R.id.tvNombre);
        tvCorreo = view.findViewById(R.id.tvCorreo);
        tvNivel = view.findViewById(R.id.tvNivel);
        tvRacha = view.findViewById(R.id.tvRacha);
        tvXp = view.findViewById(R.id.tvXp);
        tvCarrera = view.findViewById(R.id.tvCarrera);
        tvMiembroDesde = view.findViewById(R.id.tvMiembroDesde);
        loadingView = view.findViewById(R.id.loadingView);

        cargarPerfil();

        view.findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> cerrarSesion());
        view.findViewById(R.id.btnPersonajes).setOnClickListener(v ->
                Snackbar.make(v, "Proximamente", Snackbar.LENGTH_SHORT).show());
        view.findViewById(R.id.btnLogros).setOnClickListener(v ->
                Snackbar.make(v, "Proximamente", Snackbar.LENGTH_SHORT).show());
        view.findViewById(R.id.btnEstadisticas).setOnClickListener(v ->
                Snackbar.make(v, "Proximamente", Snackbar.LENGTH_SHORT).show());
        view.findViewById(R.id.btnConfiguracion).setOnClickListener(v ->
                Snackbar.make(v, "Proximamente", Snackbar.LENGTH_SHORT).show());

        return view;
    }

    private void cargarPerfil() {
        loadingView.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance(requireContext()).getApiPerfilService()
                .obtenerPerfil().enqueue(new retrofit2.Callback<RespuestaApi<Object>>() {
                    @Override
                    public void onResponse(retrofit2.Call<RespuestaApi<Object>> call,
                                           retrofit2.Response<RespuestaApi<Object>> response) {
                        loadingView.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null
                                && response.body().isExito() && response.body().getDatos() != null) {
                            try {
                                com.google.gson.Gson gson = new com.google.gson.Gson();
                                String json = gson.toJson(response.body().getDatos());
                                PerfilResponse perfilResp = gson.fromJson(json, PerfilResponse.class);

                                PerfilResponse.Perfil perfil = perfilResp.getPerfil();
                                tvNombre.setText(perfil.getNombre());
                                tvCorreo.setText(perfil.getCorreoElectronico());
                                tvNivel.setText("Nivel " + perfil.getNivelActual());
                                tvRacha.setText(perfil.getRachaActual() + " dias");
                                tvXp.setText(perfil.getXpTotal() + " XP");
                                tvCarrera.setText(perfil.getCarreraNombre() != null ?
                                        perfil.getCarreraNombre() : "Sin carrera");
                                tvMiembroDesde.setText("Miembro desde " +
                                        perfil.getFechaRegistro());

                                sessionManager.saveSession(
                                        sessionManager.getToken(),
                                        sessionManager.getRefreshToken(),
                                        sessionManager.getUserId(),
                                        perfil.getNombre(),
                                        perfil.getCorreoElectronico()
                                );
                            } catch (Exception e) {
                                cargarDatosLocales();
                            }
                        } else {
                            cargarDatosLocales();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<RespuestaApi<Object>> call, Throwable t) {
                        loadingView.setVisibility(View.GONE);
                        cargarDatosLocales();
                    }
                });
    }

    private void cargarDatosLocales() {
        tvNombre.setText(sessionManager.getUserName());
        tvCorreo.setText(sessionManager.getUserEmail());
        tvNivel.setText("Nivel 1");
        tvRacha.setText("0 dias");
        tvXp.setText("0 XP");
    }

    private void cerrarSesion() {
        authRepository.logout().enqueue(new retrofit2.Callback<RespuestaApi<Void>>() {
            @Override
            public void onResponse(retrofit2.Call<RespuestaApi<Void>> call,
                                   retrofit2.Response<RespuestaApi<Void>> response) {
                logoutAndRedirect();
            }

            @Override
            public void onFailure(retrofit2.Call<RespuestaApi<Void>> call, Throwable t) {
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
}

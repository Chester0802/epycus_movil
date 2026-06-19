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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import es.epycus.app.R;
import es.epycus.app.api.RetrofitClient;
import es.epycus.app.data.local.AppDatabase;
import es.epycus.app.data.local.entity.CacheEntity;
import es.epycus.app.data.local.entity.UsuarioEntity;
import es.epycus.app.databinding.FragmentPerfilBinding;
import es.epycus.app.model.RespuestaApi;
import es.epycus.app.model.dto.PerfilResponse;
import es.epycus.app.repository.AuthRepository;
import es.epycus.app.ui.auth.LoginActivity;
import es.epycus.app.util.NetworkUtils;
import es.epycus.app.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;

public class PerfilFragment extends Fragment {

    private static final String TAG = "PerfilFragment";
    private FragmentPerfilBinding binding;
    private SessionManager sessionManager;
    private AuthRepository authRepository;
    private AppDatabase database;
    private final List<Call<?>> activeCalls = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sessionManager = SessionManager.getInstance(requireContext());
        authRepository = new AuthRepository(requireContext());
        database = AppDatabase.getInstance(requireContext());

        binding.swipeRefresh.setOnRefreshListener(this::cargarPerfil);
        binding.swipeRefresh.setColorSchemeResources(R.color.light_accent, R.color.light_accent_secondary);

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

        Call<RespuestaApi<PerfilResponse>> call = RetrofitClient.getInstance(requireContext()).getApiPerfilService()
                .obtenerPerfil();
        activeCalls.add(call);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<PerfilResponse>> call,
                                   @NonNull retrofit2.Response<RespuestaApi<PerfilResponse>> response) {
                activeCalls.remove(call);
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null
                        && response.body().isExito() && response.body().getDatos() != null) {
                    try {
                        PerfilResponse perfilResp = response.body().getDatos();
                        Gson gson = new Gson();
                        String json = gson.toJson(perfilResp);
                        AppDatabase.getWriteExecutor().execute(() ->
                                database.cacheDao().insert(
                                        new CacheEntity("perfil", json)));

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
                        AppDatabase.getWriteExecutor().execute(() ->
                                database.usuarioDao().insert(new UsuarioEntity(
                                        sessionManager.getUserId(),
                                        perfil.getNombre(),
                                        perfil.getCorreoElectronico(),
                                        sessionManager.getToken(),
                                        sessionManager.getRefreshToken(),
                                        perfil.getFechaRegistro()
                                )));
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing perfil", e);
                        cargarDatosLocales();
                    }
                } else {
                    cargarDatosLocales();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<PerfilResponse>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
                binding.loadingView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                if (!cargarPerfilDesdeCache()) {
                    cargarDatosLocales();
                }
                mostrarErrorRed(t);
            }
        });
    }

    private boolean cargarPerfilDesdeCache() {
        String json = database.cacheDao().getValue("perfil");
        if (json == null) return false;

        try {
            Gson gson = new Gson();
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
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error loading cached perfil", e);
            return false;
        }
    }

    private void cargarDatosLocales() {
        if (sessionManager.getUserId() > 0) {
            UsuarioEntity cachedUser = database.usuarioDao().getById(sessionManager.getUserId());
            if (cachedUser != null) {
                binding.tvNombre.setText(cachedUser.getNombre());
                binding.tvCorreo.setText(cachedUser.getCorreoElectronico());
                return;
            }
        }
        binding.tvNombre.setText(sessionManager.getUserName());
        binding.tvCorreo.setText(sessionManager.getUserEmail());
        binding.tvNivel.setText(getString(R.string.nivel_formato, 1));
        binding.tvRacha.setText(getString(R.string.dias_formato, 0));
        binding.tvXp.setText(getString(R.string.xp_formato, 0));
    }

    private void cerrarSesion() {
        Call<RespuestaApi<Void>> call = authRepository.logout();
        activeCalls.add(call);
        call.enqueue(new Callback<RespuestaApi<Void>>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaApi<Void>> call,
                                   @NonNull retrofit2.Response<RespuestaApi<Void>> response) {
                activeCalls.remove(call);
                logoutAndRedirect();
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaApi<Void>> call, @NonNull Throwable t) {
                activeCalls.remove(call);
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

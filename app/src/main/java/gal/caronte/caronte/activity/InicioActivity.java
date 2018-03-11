package gal.caronte.caronte.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.List;

import gal.caronte.caronte.R;
import gal.caronte.caronte.custom.ListaEdificioCustom;
import gal.caronte.caronte.custom.UsuarioEdificioCustom;
import gal.caronte.caronte.custom.sw.ComprobarLoginGoogleCustom;
import gal.caronte.caronte.custom.sw.Conta;
import gal.caronte.caronte.custom.sw.EdificioCustom;
import gal.caronte.caronte.servizo.ComprobarUsuarioGoogle;
import gal.caronte.caronte.servizo.RecuperarConta;
import gal.caronte.caronte.servizo.RecuperarEdificio;
import gal.caronte.caronte.servizo.RecuperarPuntoInteresePercorrido;
import gal.caronte.caronte.util.Constantes;
import gal.caronte.caronte.util.StringUtil;

/**
 * Created by dpan on 25/01/2018.
 */

public class InicioActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = InicioActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 123;

    //Servizos
    private RecuperarConta recuperarConta;
    private RecuperarEdificio recuperarEdificio;

    private GoogleApiClient apiClient;
    private UsuarioEdificioCustom usuarioEdificio;
    private String nomeMostrar;
    private ListaEdificioCustom listaEdificio;

    private SignInButton botonLogin;
    private Button botonLogout;
    private Button botonDesconectar;
    private Button botonAcceder;
    private Spinner spinnerContas;
    private TextView tvGoogle;
    private TextView tvNomeGoogle;

    private ProgressDialog progressDialog;

    private List<Conta> listaContaPublica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.id_token_google_caronte))
                        .requestEmail()
                        .build();

        this.apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        this.botonLogin = findViewById(R.id.boton_login);
        this.botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        this.botonLogout = findViewById(R.id.boton_logout);
        this.botonLogout.setVisibility(View.GONE);
        this.botonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(apiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                actualizarVista(false);
                            }
                        });
            }
        });

        this.botonDesconectar = findViewById(R.id.boton_desconectar);
        this.botonDesconectar.setVisibility(View.GONE);
        this.botonDesconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.revokeAccess(apiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                actualizarVista(false);
                            }
                        });
            }
        });

        this.botonAcceder = findViewById(R.id.boton_acceder);
        this.botonAcceder.setEnabled(false);
        this.botonAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abrimos a nova actividade coa conta seleccionada no spinner
                Conta contaSeleccionada = (Conta) spinnerContas.getSelectedItem();

                if (contaSeleccionada != null) {
                    Log.i(TAG, StringUtil.creaString("Lanzase a actividade MapaActivity coa conta: ", contaSeleccionada));
                    Intent intent = new Intent(InicioActivity.this, MapaActivity.class);

                    //Engadimos a informacion da conta ao intent
                    Bundle b = new Bundle();
                    b.putString(Constantes.NOME_CONTA, contaSeleccionada.getContaUsuario());
                    b.putString(Constantes.CONTRASINAL_CONTA, contaSeleccionada.getContrasinal());
                    b.putParcelable(Constantes.USUARIO_EDIFICIO, InicioActivity.this.usuarioEdificio);
                    b.putParcelable(Constantes.LISTA_EDIFICIO, InicioActivity.this.listaEdificio);
                    intent.putExtras(b);

                    //Iniciamos a actividade do mapa
                    startActivity(intent);
                }
            }
        });

        this.spinnerContas = findViewById(R.id.spinner_contas);
        this.spinnerContas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (InicioActivity.this.listaEdificio != null) {
                    botonAcceder.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //Non se fai nada
            }

        });

        this.tvGoogle = findViewById(R.id.textViewContaGoogle);
        this.tvNomeGoogle = findViewById(R.id.textViewNomeContaGoogle);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            handleSignInResult(result);
        }
    }

    @Override
    protected void onStop() {
        if (this.recuperarConta != null) {
            this.recuperarConta.cancel(true);
        }
        if (this.recuperarEdificio != null) {
            this.recuperarEdificio.cancel(true);
        }
        super.onStop();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            //Usuario logueado
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                String idToken = acct.getIdToken();
                this.nomeMostrar = acct.getDisplayName();
                Log.i(TAG, "Display name: " + acct.getDisplayName());
                Log.i(TAG, "Email: " + acct.getEmail());
                Log.i(TAG, "Family name: " + acct.getFamilyName());
                Log.i(TAG, "Given name: " + acct.getGivenName());

                this.tvGoogle.setText(acct.getEmail());
                this.tvNomeGoogle.setText(this.nomeMostrar);

                ComprobarUsuarioGoogle comprobarUsuarioGoogle = new ComprobarUsuarioGoogle();
                comprobarUsuarioGoogle.setInicioActivity(this);
                comprobarUsuarioGoogle.execute(idToken);

            }
            actualizarVista(true);
        }
        else {
            //Usuario non logueado --> Desconectado
            actualizarVista(false);
            hideProgressDialog();
        }
    }

    private void actualizarVista(boolean loginCorrecto) {
        if (loginCorrecto) {
            this.botonLogin.setVisibility(View.GONE);
            this.botonLogout.setVisibility(View.VISIBLE);
            this.botonDesconectar.setVisibility(View.VISIBLE);
        }
        else {
            this.botonLogin.setVisibility(View.VISIBLE);
            this.botonLogout.setVisibility(View.GONE);
            this.botonDesconectar.setVisibility(View.GONE);

            this.usuarioEdificio = null;
            amosarListaConta(this.listaContaPublica);

            this.tvGoogle.setText(getString(R.string.sen_conta_google_selecionada));
            this.tvNomeGoogle.setText("");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, getString(R.string.erro_conexion), Toast.LENGTH_SHORT).show();
        Log.e(TAG, StringUtil.creaString("OnConnectionFailed: ", connectionResult));
    }

    @Override
    protected void onStart() {
        super.onStart();

        actualizarVista(false);
        recuperarListaConta(null);
        recuperarListaEdificio();

    }

    private void showProgressDialog() {
        if (this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(this);
            this.progressDialog.setMessage("Comprobando credenciais");
            this.progressDialog.setIndeterminate(true);
        }

        this.progressDialog.show();
    }

    private void hideProgressDialog() {
        if (this.progressDialog != null) {
            this.progressDialog.hide();
        }
    }

    private void recuperarListaConta(String idUsuario) {
        this.recuperarConta = new RecuperarConta();
        this.recuperarConta.setInicioActivity(this);
        this.recuperarConta.execute(idUsuario);
    }

    private void recuperarListaEdificio() {
        this.recuperarEdificio = new RecuperarEdificio();
        this.recuperarEdificio.setInicioActivity(this);
        this.recuperarEdificio.execute();
    }

    public void amosarListaConta(List<Conta> listaConta) {

        if (this.listaContaPublica == null
                && this.usuarioEdificio == null) {
            this.listaContaPublica = listaConta;
        }

        if (listaConta != null) {
            this.spinnerContas.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, listaConta));
        }
    }

    public void setUsuarioCorrecto(ComprobarLoginGoogleCustom usuarioCorrecto) {

        hideProgressDialog();
        //Se o obxecto devolto e nulo ou o usuario non e correcto, facemos logout
        if (usuarioCorrecto == null
                || usuarioCorrecto.getIdUsuario() == null) {
            this.usuarioEdificio = null;
            amosarListaConta(this.listaContaPublica);
            this.botonLogout.performClick();
        }
        else {
            this.usuarioEdificio = new UsuarioEdificioCustom(usuarioCorrecto.getIdUsuario(), this.nomeMostrar, usuarioCorrecto.getListaIdEdificioAdministrador());
            recuperarListaConta(usuarioCorrecto.getIdUsuario().toString());
        }
    }

    public void setListaEdificio(List<EdificioCustom> listaEdificio) {
        this.listaEdificio = new ListaEdificioCustom(listaEdificio);
        if (this.spinnerContas.getSelectedItem() != null) {
            this.botonAcceder.setEnabled(true);
        }
    }
}
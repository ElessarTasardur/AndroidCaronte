package gal.caronte.caronte.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import gal.caronte.caronte.custom.sw.ComprobarLoginGoogleCustom;
import gal.caronte.caronte.custom.sw.Conta;
import gal.caronte.caronte.mostrarmapa.MapaActivity;
import gal.caronte.caronte.servizo.ComprobarUsuarioGoogle;
import gal.caronte.caronte.servizo.RecuperarConta;
import gal.caronte.caronte.util.StringUtil;

/**
 * Created by dpan on 25/01/2018.
 */

public class InicioActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = InicioActivity.class.getSimpleName();
    private static final String NOME_CONTA = "nomeConta";
    private static final String CONTRASINAL_CONTA = "contrasinalConta";
    private static final String ID_TOKEN = "idToken";

    private static final int RC_SIGN_IN = 123;

    private RecuperarConta recuperarConta;
    private ComprobarUsuarioGoogle comprobarUsuarioGoogle;

    private GoogleApiClient apiClient;
    private String idToken;
    private ComprobarLoginGoogleCustom usuarioCorrecto;

    private SignInButton botonLogin;
    private Button botonLogout;
    private Button botonDesconectar;
    private Button botonAcceder;

    private Spinner spinnerContas;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("448237493715-f8qnmr9mblorb5bqqaoc836sftm7jl6k.apps.googleusercontent.com")
                        .requestEmail()
                        .build();

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        botonLogin = (SignInButton) findViewById(R.id.boton_login);
//        botonSignIn.setSize(SignInButton.SIZE_STANDARD);
//        botonSignIn.setColorScheme(SignInButton.COLOR_LIGHT);
//        botonSignIn.setScopes(gso.getScopeArray());
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        botonLogout = (Button) findViewById(R.id.boton_logout);
        botonLogout.setVisibility(View.GONE);
        botonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(apiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                updateUI(false);
                            }
                        });
            }
        });

        botonDesconectar = (Button) findViewById(R.id.boton_desconectar);
        botonDesconectar.setVisibility(View.GONE);
        botonDesconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.revokeAccess(apiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                updateUI(false);
                            }
                        });
            }
        });

        botonAcceder = (Button) findViewById(R.id.boton_acceder);
        botonAcceder.setEnabled(false);
        botonAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abrimos a nova actividade coa conta seleccionada no spinner
                Conta contaSeleccionada = (Conta) spinnerContas.getSelectedItem();

                if (contaSeleccionada != null) {
                    Log.i(TAG, StringUtil.creaString("Lanzase a actividade MapaActivity coa conta: ", contaSeleccionada));
                    Intent intent = new Intent(InicioActivity.this, MapaActivity.class);

                    //Engadimos a informacion da conta ao intent
                    Bundle b = new Bundle();
                    b.putString(NOME_CONTA, contaSeleccionada.getContaUsuario());
                    b.putString(CONTRASINAL_CONTA, contaSeleccionada.getContrasinal());
                    b.putString(ID_TOKEN, InicioActivity.this.idToken);
                    intent.putExtras(b);

                    //Iniciamos a actividade do mapa
                    startActivity(intent);
                }
            }
        });
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
        if (recuperarConta != null) {
            this.recuperarConta.cancel(true);
        }
        super.onStop();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            //Usuario logueado
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                this.idToken = acct.getIdToken();
                Log.i(TAG, "Token: " + this.idToken);
                Log.i(TAG, "Id: " + acct.getId());
                Log.i(TAG, "Display name: " + acct.getDisplayName());
                Log.i(TAG, "Email: " + acct.getEmail());
                Log.i(TAG, "Family name: " + acct.getFamilyName());
                Log.i(TAG, "Given name: " + acct.getGivenName());
                Log.i(TAG, "Auth code: " + acct.getServerAuthCode());
                Log.i(TAG, "Photo URL: " + acct.getPhotoUrl());

                Log.i(TAG, "Token: " + acct.getAccount().name);
                Log.i(TAG, "Token: " + acct.getGrantedScopes());

                this.comprobarUsuarioGoogle = new ComprobarUsuarioGoogle();
                this.comprobarUsuarioGoogle.setInicioActivity(this);
                this.comprobarUsuarioGoogle.execute(idToken);

            }
            updateUI(true);
        }
        else {
            //Usuario non logueado --> Desconectado
            updateUI(false);
            hideProgressDialog();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            botonLogin.setVisibility(View.GONE);
            botonLogout.setVisibility(View.VISIBLE);
            botonDesconectar.setVisibility(View.VISIBLE);
        }
        else {
            botonLogin.setVisibility(View.VISIBLE);
            botonLogout.setVisibility(View.GONE);
            botonDesconectar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Error de conexion", Toast.LENGTH_SHORT).show();
        Log.e(TAG, StringUtil.creaString("OnConnectionFailed: ", connectionResult));

        //TODO engadir callbacks de conexion? http://www.sgoliver.net/blog/google-sign-in-android-1/
    }

    @Override
    protected void onStart() {
        super.onStart();

        recuperarListaConta();

//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(apiClient);
//        if (opr.isDone()) {
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        }
//        else {
//            showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    Log.i(TAG, "onResult");
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Comprobando credenciais");
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    private void recuperarListaConta() {
        this.recuperarConta = new RecuperarConta();
        this.recuperarConta.setInicioActivity(this);
        this.recuperarConta.execute();
    }

    public void mostrarListaConta(List<Conta> listaConta) {

        spinnerContas = findViewById(R.id.spinner_contas);
        spinnerContas.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, listaConta));

        spinnerContas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                botonAcceder.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //Non se fai nada
            }

        });

    }

    public void setUsuarioCorrecto(ComprobarLoginGoogleCustom usuarioCorrecto) {

        hideProgressDialog();
        //Se o obxecto devolto e nulo ou o usuario non e correcto, facemos logout
        if (usuarioCorrecto == null
                || !usuarioCorrecto.isLoginCorrecto()) {
            this.botonLogout.performClick();
        }
        else {
            this.usuarioCorrecto = usuarioCorrecto;
        }
    }
}
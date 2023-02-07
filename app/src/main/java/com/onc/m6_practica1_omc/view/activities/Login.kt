package com.onc.m6_practica1_omc.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.onc.m6_practica1_omc.R
import com.onc.m6_practica1_omc.databinding.ActivityLoginBinding


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    //Para Firebase
    private lateinit var firebaseAuth: FirebaseAuth

    //Para lo que ingresa el usuario
    private var email = ""
    private var password = ""

    //Manejo del boton crear user
    private var enableButtonCreateUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Instanciar FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        //Mamnejo del evento para el boton Login
        binding.btnLogin.setOnClickListener {
            if (!validarCampos()) return@setOnClickListener

            binding.pbLogin.visibility = View.VISIBLE
            autenticarUsuario(email, password)
            hideKeyboard()
        }

        //Mamnejo del evento para el boton Registrase
        binding.btnNewUser.setOnClickListener {
            enableButtonCreateUser = !enableButtonCreateUser
            setButtonsVisibility()
        }

        binding.btnCrearUser.setOnClickListener {

            if (!validarCampos()) return@setOnClickListener

            binding.pbLogin.visibility = View.VISIBLE
            registrarUsuario(email, password)
            hideKeyboard()
            enableButtonCreateUser = false
            setButtonsVisibility()

        }

        //MAnejo de eventos para el botón reset password
        binding.btnResetPassword.setOnClickListener {
            enableButtonCreateUser = false
            setButtonsVisibility()
            sendMailRecoveryPasswrd(it.context)
            hideKeyboard()

        }

        binding.textInputLayPasswd.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus)
                binding.textInputLayCorreo.error = null
        }

        binding.textInputLayPasswd.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus)
                binding.textInputLayPasswd.error = null
        }

        enableButtonCreateUser = false
        setButtonsVisibility()

    }


    fun setButtonsVisibility() {
        if (enableButtonCreateUser){
            binding.btnLogin.visibility = View.GONE
            binding.btnCrearUser.visibility = View.VISIBLE
            binding.btnNewUser.text = getString(R.string.activity_login_new_user_login)
        } else {
            binding.btnLogin.visibility = View.VISIBLE
            binding.btnCrearUser.visibility = View.GONE
            binding.btnNewUser.text = getString(R.string.activity_login_new_user)
        }
    }

    fun hideKeyboard() {
        val inputMethodManager: InputMethodManager =
            this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Busca la vista con el foco actual
        var view = this.currentFocus
        //Si la vista actual no tiene el foco, crea uno nuevo
        if (view == null) {
            view = View(this)
        }
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /*
      android:id="@+id/textInputLayCorreo"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        style="?attr/textInputOutlinedStyle"
        app:boxStrokeColor="@color/blue_sky"
        app:startIconContentDescription="@string/activity_login_email"
        app:endIconMode="clear_text"
        app:shapeAppearance="@style/ShapeAppearance.Material3.Corner.Medium"
        android:hint="@string/activity_login_email"
        android:layout_margin="20dp" >
     */


    private fun sendMailRecoveryPasswrd(context: Context){

        val textInputLayout = TextInputLayout(context)
        textInputLayout.hint = getString(R.string.activity_login_email)
        textInputLayout.boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
        textInputLayout.setBoxCornerRadii(5f,5f,5f,5f)
        textInputLayout.boxStrokeColor = getColor(R.color.blue_night)

        val editText = TextInputEditText(textInputLayout.context)
        editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        textInputLayout.addView(editText)



        val etMail = editText //EditText(context)



        val passwdResetDialog = AlertDialog.Builder(context)
            .setTitle(getString(R.string.activity_login_recovery_passwd_dialog_title))
            .setMessage(getString(R.string.activity_login_recovery_passwd_dialog_Message))
            .setView(textInputLayout)
            .setPositiveButton(R.string.activity_login_recovery_passwd_dialog_Enviar) { dialog, which ->
                val userMail = etMail.text.toString().trim()
                if (userMail.isEmpty()) {
                    Toast.makeText(
                        context, getString(R.string.activity_login_recovery_passwd_dialog_Empty),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                //SE envía el mensaje para reestablecer el correo
                firebaseAuth.sendPasswordResetEmail(userMail).addOnSuccessListener {
                    Toast.makeText(
                        this,
                        getString(R.string.activity_login_register_send_mail_recovery),
                        Toast.LENGTH_LONG
                    ).show()
                }?.addOnFailureListener {
                    Toast.makeText(
                        this,
                        getString(R.string.activity_login_register_send_mail_recovery_fail) +
                                " (${it.message})",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


        passwdResetDialog.create().show()
}


    private fun registrarUsuario(user: String, passwd: String) {
        firebaseAuth.createUserWithEmailAndPassword(user, passwd).addOnCompleteListener { authResultTask ->
            if (authResultTask.isSuccessful) { //El registro el usuario
                //Envia correo para verificar email
                var userFirebase = firebaseAuth.currentUser
                userFirebase?.sendEmailVerification()?.addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.activity_login_register_send_mail_verification),
                        Toast.LENGTH_LONG).show()
                }?.addOnFailureListener {
                    Toast.makeText(this, getString(R.string.activity_login_register_send_mail_verification_fail) +
                            " (${it.message})",
                        Toast.LENGTH_LONG).show()

                }

                Toast.makeText(this, getString(R.string.activity_login_register_new_user_success),
                    Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("passwd", passwd)
                startActivity(intent)
                finish()

            } else { //No se pudo registrar
                binding.pbLogin.visibility = View.GONE
                manejoErroresFirebaseAth(authResultTask)

            }
        }
    }



    private fun autenticarUsuario(user: String, passwd: String) {
        firebaseAuth.signInWithEmailAndPassword(user, passwd)
            .addOnCompleteListener { authResultTask ->
                if (authResultTask.isSuccessful) { //Autenticación  exitosa
                    //Envia
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("passwd", passwd)
                    startActivity(intent)
                    finish()

                } else { //No se pudo registrar
                    binding.pbLogin.visibility = View.GONE
                    manejoErroresFirebaseAth(authResultTask)

                }
            }
    }




    private fun validarCampos(): Boolean{
        var resultEmail : Boolean = false
        var resultPasswd : Boolean = false

        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()){
            binding.textInputLayCorreo.error = getString(R.string.activity_login_error_email_empty)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayCorreo.error = getString(R.string.activity_login_error_email_mismatch)
        } else {
            resultEmail = true
        }

        if (password.isEmpty()) {
            binding.textInputLayPasswd.error = getString(R.string.activity_login_error_passwd_empty)
        } else if (password.length < 6) {
            binding.textInputLayPasswd.error = getString(R.string.activity_login_error_passwd_short)
        } else {
            resultPasswd = true
        }

        return resultEmail && resultPasswd
    }




    //Manejo de errores de FirebaseAuth
    private fun manejoErroresFirebaseAth(task : Task<AuthResult>) {
        var codigoError = ""
        try {
            codigoError = (task.exception as FirebaseAuthException).errorCode
        } catch (e: Exception) {
            codigoError = "NOT_NETWORK"
        }

        when(codigoError) {
            "ERROR_INVALID_EMAIL" -> {
                Toast.makeText(this, getString(R.string.activity_login_error_email_mismatch_toast),
                    Toast.LENGTH_LONG
                ).show();
                binding.etEmail.error = getString(R.string.activity_login_error_email_mismatch)
            }
            "ERROR_WRONG_PASSWORD" -> {
                Toast.makeText(this, getString(R.string.activity_login_error_passwd_wrong_toast),
                    Toast.LENGTH_LONG
                ).show();
                binding.etPassword.error = getString(R.string.activity_login_error_passwd_wrong)
            }
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                Toast.makeText(this, getString(R.string.activity_login_error_account_exists_other_credentials),
                    Toast.LENGTH_LONG
                ).show();

            }
            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                Toast.makeText(this, getString(R.string.activity_login_error_email_already_in_use),
                    Toast.LENGTH_LONG
                ).show();
            }
            "ERROR_USER_DISABLED" -> {
                Toast.makeText(this, getString(R.string.activity_login_error_user_disable),
                    Toast.LENGTH_LONG
                ).show();
            }

            "ERROR_USER_TOKEN_EXPIRED" -> {
                Toast.makeText(this, getString(R.string.activity_login_error_token_expired),
                    Toast.LENGTH_LONG
                ).show();
            }

            "ERROR_USER_NOT_FOUND" -> {
                Toast.makeText(this, getString(R.string.activity_login_error_usuario_not_found),
                    Toast.LENGTH_LONG
                ).show();
            }

            "ERROR_INVALID_USER_TOKEN" -> {
                Toast.makeText(this, getString(R.string.activity_login_error_invalid_user_token),
                    Toast.LENGTH_LONG
                ).show();
            }

            "ERROR_WEAK_PASSWORD" -> {
                Toast.makeText(this, getString(R.string.activity_login_error_passwd_short_Toast),
                    Toast.LENGTH_LONG
                ).show();
                binding.etPassword.error = getString(R.string.activity_login_error_passwd_short)
            }

            "NOT_NETWORK" -> {
                Toast.makeText(this, getString(R.string.activity_login_error_not_network),
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {
                Toast.makeText(this, getString(R.string.activity_login_error_other),
                    Toast.LENGTH_LONG
                ).show()
            }

        }

    }
}
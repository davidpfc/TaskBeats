package com.comunidadedevspace.taskbeats

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

class TaskDetailActivity : AppCompatActivity() {

        private var task: Task? = null
        private lateinit var btnDone: Button
        //VAriável só para mostrar uma msg, aula floating action button

    companion object{
        val TASK_DETAIL_EXTRA = "task.title.extra.detail"

            // Aula pegando resultado de uma activity.
        fun start(context: Context, task: Task?): Intent{
            val intent = Intent(context, TaskDetailActivity::class.java)
                .apply {
                    putExtra(TASK_DETAIL_EXTRA, task)
                }
            return intent
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)
        //AULA toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        /*Recuperar string da tela anterior
        val title: String = intent.getStringExtra(TASK_DETAIL_EXTRA) ?: ""*/

        //Recuperar tudo, Aula pegando resultado
        task = intent.getSerializableExtra(TASK_DETAIL_EXTRA) as Task?

        //Aula add new task
        val edtTitle = findViewById<EditText>(R.id.edt_task_title)
        val edtDesc = findViewById<EditText>(R.id.edt_task_desc)
        btnDone = findViewById<Button>(R.id.btn_done)

        if(task != null){
            edtTitle.setText(task!!.title)
            edtDesc.setText(task!!.description)
        }

        btnDone.setOnClickListener{
            val title = edtTitle.text.toString()
            val desc = edtDesc.text.toString()

            if(title.isNotEmpty() && desc.isNotEmpty()){
                if(task == null){
                    //Criar tarefa nova
                    addOrUpdateTask(0 ,title, desc, ActionType.CREATE)
                }else{
                    //editar tarefa existente
                    addOrUpdateTask(task!!.id,title, desc, ActionType.UPDATE)
                }
            } else{
                showMessage(it, "Fields are required")
            }
        }

        //Recuperar campo XML
        //tvTitle = findViewById<TextView>(R.id.tv_task_title_detail)
        //Setar um novo texto na tela
        //tvTitle.text = task?.title ?: "Adicione uma tarefa"
    }

    //Aula add new task e edit task
    private fun addOrUpdateTask(id: Int,title: String, description: String, actionType: ActionType){
        val task = Task(id, title, description)
        returnAction(task, actionType)
    }


    //Cria o botão de delete
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_task_detail, menu)
        return true
    }

    // Da a função ao botão
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_task -> {

                if(task!= null){
                    returnAction(task!!, ActionType.DELETE)
                } else{
                    showMessage(btnDone, "Item not found")
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Function for set in before view - AULA Add new task
    private fun returnAction(task: Task, actionType: ActionType){
        val intent = Intent()
            .apply {
                val taskAction = TaskAction(task, actionType.name)
                putExtra(TASK_ACTION_RESULT, taskAction)
            }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    private fun showMessage(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }

}


package com.comunidadedevspace.taskbeats.presentation

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import java.io.Serializable
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.comunidadedevspace.taskbeats.R
import com.comunidadedevspace.taskbeats.TaskBeatsApplication
import com.comunidadedevspace.taskbeats.data.AppDataBase
import com.comunidadedevspace.taskbeats.data.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    //Recuperar img de lista vazia. AULA estado vazio
    private lateinit var ctnContent: LinearLayout

    //Adapter
    private val adapter: TaskListAdapter by lazy{
        TaskListAdapter(::onListItemClicked)
    }

    //Aula Criando uma tabela de dados

    private val viewModel: TaskListViewModel by lazy{
        TaskListViewModel.create(application)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK){
            // you will get result here in  result.data & O resultado após apertar o delete cairá dentro desse if
            val data  = result.data
            val taskAction = data?.getSerializableExtra(TASK_ACTION_RESULT) as TaskAction

            viewModel.execute(taskAction)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

       ctnContent = findViewById(R.id.ctn_content)


        //RecyclerView
        val rvTasks: RecyclerView = findViewById(R.id.rv_task_list)
        rvTasks.adapter = adapter

        //float action bar
        val fab = findViewById<FloatingActionButton>(R.id.fab_add)

        fab.setOnClickListener {
            openTaskListDetail(null)
        }

    }

    override fun onStart(){
        super.onStart()
        listFromDataBase()
    }

    private fun deleteAll(){
        val taskAction = TaskAction(null, ActionType.DELETE_ALL.name)
        viewModel.execute(taskAction)
    }

    //function para fazer a lista na data base
    private fun listFromDataBase(){

            val listObserver = Observer<List<Task>>{listTask ->
                if(listTask.isEmpty()){
                    ctnContent.visibility = View.VISIBLE
                }else{
                    ctnContent.visibility = View.GONE
                }
                adapter.submitList(listTask)
            }
            viewModel.taskListLiveData.observe(this@MainActivity, listObserver)
    }

    //function for show a message in float action bar
    private fun showMessage(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }

    //Abrir uma nova tela de detalhes
    private fun onListItemClicked(task: Task){
        // AULA Pegando Resultado
        openTaskListDetail(task)
    }

    //AULA Floating Action Button
    private fun openTaskListDetail(task: Task?){
        val intent = TaskDetailActivity.start(this, task)
        startForResult.launch(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_task_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_task -> {
                deleteAll()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

//Crud
//AULA Ação de Deletar
enum class ActionType{
    DELETE,
    DELETE_ALL,
    UPDATE,
    CREATE
}

data class TaskAction(
    val task: Task?,
    val actionType: String
) : Serializable

const val TASK_ACTION_RESULT = "TASK_ACTION_RESULT"
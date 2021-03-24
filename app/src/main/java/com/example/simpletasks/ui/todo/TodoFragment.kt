package com.example.simpletasks.ui.todo
//
//import android.os.Bundle
//import android.view.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.lifecycle.ViewModelProvider
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import androidx.recyclerview.widget.ItemTouchHelper
//import com.example.simpletasks.R
//import com.example.simpletasks.data.preference.PreferenceViewModel
//import com.example.simpletasks.data.preference.PreferenceViewModelFactory
//import com.example.simpletasks.data.task.TaskViewModel
//import com.example.simpletasks.data.task.TaskViewModelFactory
//import com.example.simpletasks.data.todo.TodoViewModel
//import com.example.simpletasks.data.user.UserViewModel
//import com.example.simpletasks.databinding.FragmentTodoBinding
//import com.example.simpletasks.ui.theme.SimpleTasksTheme
//import com.example.simpletasks.util.DragManager
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//
//@ExperimentalCoroutinesApi
//class TodoFragment : Fragment() {
//
//    private var _binding: FragmentTodoBinding? = null
//    private val binding get() = _binding!!
//    private val args by navArgs<TodoFragmentArgs>()
//
//    private val todoViewModel by activityViewModels<TodoViewModel>()
//    private val userViewModel by activityViewModels<UserViewModel>()
//
//    private val taskViewModel: TaskViewModel by lazy {
//        ViewModelProvider(
//            this,
//            TaskViewModelFactory(todoViewModel, args.todo)
//        ).get(TaskViewModel::class.java)
//    }
//
//    private lateinit var uncompletedTaskAdapter: UncompletedTaskAdapter
//    private lateinit var completedTaskAdapter: CompletedTaskAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentTodoBinding.inflate(inflater, container, false)
//        uncompletedTaskAdapter = UncompletedTaskAdapter(todoViewModel, args.todo)
//        completedTaskAdapter = CompletedTaskAdapter(args.todo)
//
//        (requireActivity() as AppCompatActivity).supportActionBar?.title = args.todo.name
//        setHasOptionsMenu(true)
//
//        binding.apply {
//            lifecycleOwner = viewLifecycleOwner
//            todoFragment = this@TodoFragment
//
//            todoFab.setContent {
//                TodoFAB(todo = args.todo) {}
//            }
//
//            completedIndicator.setContent {
//                val user = userViewModel.readUser().value
//
//                val completedTasks =
//                    taskViewModel.completedTasks.observeAsState(initial = listOf())
////                SimpleTasksTheme {
////                    CompletedIndicator(
////                        isExpanded = preferenceViewModel.isExpanded,
////                        onExpandChange = preferenceViewModel::onExpandChange,
////                        completedAmount = completedTasks.value.size
////                    )
////                }
//            }
//        }
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            uncompletedTaskRecyclerview.adapter = uncompletedTaskAdapter
//            completedTaskRecyclerview.adapter = completedTaskAdapter
//        }
//        val dragManager = DragManager(
//            dragDirs = ItemTouchHelper.UP or ItemTouchHelper.DOWN,
//            swipeDirs = 0
//        )
//        val helper = ItemTouchHelper(dragManager)
//        helper.attachToRecyclerView(binding.uncompletedTaskRecyclerview)
//
//        taskViewModel.completedTasks.observe(viewLifecycleOwner) {
//            completedTaskAdapter.submitList(it)
//        }
//
//        val uncompletedTasks = args.todo.tasks.filter { !it.completed }
//        uncompletedTaskAdapter.apply {
//            submitList(uncompletedTasks)
//            updatedList(uncompletedTasks)
//        }
//
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.todo_menu, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_rename_list -> {
//                true
//            }
//            R.id.action_change_label -> {
//                true
//            }
//            R.id.action_delete_completed_tasks -> {
//                taskViewModel.onCompletedTasksDelete(args.todo)
//                true
//            }
//            R.id.action_delete_list -> {
//                goToHomeScreen()
//                todoViewModel.deleteTodo(args.todo)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//    private fun setCompletedTasksVisibility(isExpanded: Boolean) {
//        binding.completedTaskRecyclerview.visibility = if (isExpanded) View.VISIBLE else View.GONE
//    }
//
//    private fun goToHomeScreen() {
//        findNavController().navigateUp()
//    }
//}
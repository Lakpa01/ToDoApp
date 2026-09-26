package com.example.todo.presentation.compose

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.todo.R
import com.example.todo.domain.model.ToDo
import com.example.todo.presentation.uistate.Screen
import com.example.todo.presentation.viewmodel.AuthViewModel
import com.example.todo.presentation.viewmodel.TodoListViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoListScreen(
    modifier: Modifier = Modifier,
    viewModel: TodoListViewModel,
    authViewModel: AuthViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    var isInputVisible by remember { mutableStateOf(false) }
    var selectedTaskForDialog by remember { mutableStateOf<ToDo?>(null) }
    var showProfileDialog by remember { mutableStateOf(false) }

    // State for temporary date/time selection and validation alert
    var selectedDueDateMillis by remember { mutableStateOf<Long?>(null) }
    var showTimeRequiredAlert by remember { mutableStateOf(false) }

    val displayedItems = when (uiState.currentScreen) {
        Screen.HOME -> uiState.items.filter { !it.isDone }
        Screen.COMPLETED -> uiState.items.filter { it.isDone }
    }

    fun submitTask(dueTimestamp: Long? = selectedDueDateMillis) {
        if (uiState.input.trim().isEmpty()) return

        // MANDATORY CHECK: If time is not selected, prompt the user
        val finalTimestamp = dueTimestamp ?: uiState.editingTodo?.dueDateMillis
        if (finalTimestamp == null) {
            showTimeRequiredAlert = true
            return
        }

        viewModel.addOrUpdateTodo(finalTimestamp)
        isInputVisible = false
        selectedDueDateMillis = null
        focusManager.clearFocus()
    }

    fun showDateTimePickerAndSubmit() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)

                        val timeInMillis = calendar.timeInMillis
                        selectedDueDateMillis = timeInMillis
                        submitTask(timeInMillis)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    LaunchedEffect(isInputVisible, uiState.editingTodo) {
        if (isInputVisible || uiState.editingTodo != null) {
            focusRequester.requestFocus()
            if (uiState.editingTodo != null) {
                selectedDueDateMillis = uiState.editingTodo?.dueDateMillis
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.currentScreen == Screen.HOME) "✨ My Tasks" else "🏆 Completed",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFC107)
                    )
                },
                actions = {
                    IconButton(onClick = { showProfileDialog = true }) {
                        val imageUri = authState.profileImageUri
                        if (!imageUri.isNullOrBlank()) {
                            SubcomposeAsyncImage(
                                model = imageUri,
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                error = {
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = "Fallback Profile",
                                        tint = Color(0xFFFFC107),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = "Profile",
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1E2C))
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF1E1E2C),
                // DO NOT add .imePadding() here!
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isHomeActive = uiState.currentScreen == Screen.HOME
                    IconButton(onClick = { viewModel.selectScreen(Screen.HOME); isInputVisible = false }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home",
                                tint = if (isHomeActive) Color(0xFFFFC107) else Color.Gray
                            )
                            Text(
                                "Home",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isHomeActive) Color(0xFFFFC107) else Color.Gray
                            )
                        }
                    }

                    if (uiState.currentScreen == Screen.HOME) {
                        FloatingActionButton(
                            onClick = {
                                if (!isInputVisible && uiState.editingTodo == null) {
                                    isInputVisible = true
                                } else {
                                    submitTask()
                                }
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = Color(0xFF673AB7),
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = if (isInputVisible || uiState.editingTodo != null) Icons.Default.Check else Icons.Default.Add,
                                contentDescription = "Add or Save"
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    val isCompletedActive = uiState.currentScreen == Screen.COMPLETED
                    IconButton(onClick = { viewModel.selectScreen(Screen.COMPLETED); isInputVisible = false }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = if (isCompletedActive) Color(0xFFFFC107) else Color.Gray
                            )
                            Text(
                                "Completed",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isCompletedActive) Color(0xFFFFC107) else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_bg),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (uiState.currentScreen == Screen.HOME && (isInputVisible || uiState.editingTodo != null)) {
                    Column {
                        OutlinedTextField(
                            value = uiState.input,
                            onValueChange = { viewModel.onInputChanged(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            label = { Text(if (uiState.editingTodo != null) "Edit task" else "Enter task") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = { submitTask() }
                            ),
                            trailingIcon = {
                                IconButton(onClick = { showDateTimePickerAndSubmit() }) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = "Pick Date/Time",
                                        tint = if (selectedDueDateMillis != null) Color(0xFFFFC107) else Color.White
                                    )
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (displayedItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No tasks found!", color = Color.White, fontSize = 18.sp)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(displayedItems, key = { it.id }) { item ->
                            ToDoListRow(
                                item = item,
                                onClick = { selectedTaskForDialog = item }
                            )
                        }
                    }
                }
            }
        }

        // Time Required Alert Dialog
        if (showTimeRequiredAlert) {
            AlertDialog(
                onDismissRequest = { showTimeRequiredAlert = false },
                title = { Text("Time Required", fontWeight = FontWeight.Bold) },
                text = { Text("Please choose time for the task before adding it to the list.") },
                confirmButton = {
                    Button(onClick = {
                        showTimeRequiredAlert = false
                        showDateTimePickerAndSubmit()
                    }) {
                        Text("Choose Time")
                    }
                },
//                dismissButton = {
//                    Button(onClick = { showTimeRequiredAlert = false }) {
//                        Text("Cancel")
//                    }
//                }
            )
        }

        selectedTaskForDialog?.let { task ->
            TaskActionDialog(
                item = task,
                onDismiss = { selectedTaskForDialog = null },
                onToggleComplete = { viewModel.toggleTodo(task) },
                onEdit = {
                    viewModel.startEditing(task)
                    isInputVisible = true
                },
                onDelete = { viewModel.deleteTodo(task) }
            )
        }
        if (showProfileDialog) {
            ProfileDialog(
                currentImageUri = authState.profileImageUri,
                userEmail = authState.emailInput,
                onDismiss = { showProfileDialog = false },
                onSaveProfile = { name, newImageUri ->
                    authViewModel.updateProfile(name, newImageUri)
                },
                onLogout = {
                    authViewModel.logout()
                }
            )
        }
    }
}
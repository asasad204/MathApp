package com.example.projekat

import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LandingScreen(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(129, 162, 99))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.start_image), // Replace with your actual image resource
            contentDescription = "Start Image",
            modifier = Modifier.size(400.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onStartClick,
            colors = ButtonDefaults.buttonColors(Color(231, 211, 127)),
            modifier = Modifier.size(width = 200.dp, height = 60.dp)
        ) {
            Text(stringResource(R.string.start_game_button), fontSize = 20.sp)
        }
    }
}

@Composable
fun MathApp() {
    var score by rememberSaveable { mutableStateOf(0) }
    var numPair by rememberSaveable { mutableStateOf(generateRandomNumbers()) }
    var operation by rememberSaveable { mutableStateOf(generateOperation()) }
    var userAnswer by rememberSaveable { mutableStateOf("") }
    var isCorrect by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var timeLeft by rememberSaveable { mutableStateOf(30) }
    var gameEnded by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val shareLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    // Timer logic
    LaunchedEffect(timeLeft) {
        if (timeLeft > 0 && !gameEnded) {
            delay(1000L)
            timeLeft--
        } else if (timeLeft == 0) {
            gameEnded = true
        }
    }

    // Effect to handle correct answer display for 3 seconds
    LaunchedEffect(isCorrect) {
        if (isCorrect == true) {
            numPair = generateRandomNumbers()
            operation = generateOperation()
            userAnswer = ""
            timeLeft = 30
            delay(2000L)
            isCorrect = null
        }
    }

    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            AppBar {
                val shareText = context.getString(R.string.share_score_text, score)
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                val shareTitle = context.getString(R.string.share_title)
                val chooserIntent = Intent.createChooser(shareIntent, shareTitle)
                shareLauncher.launch(chooserIntent)
            }
        },
        content = { paddingValues ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(129, 162, 99))
                    .padding(paddingValues)
                    .imePadding()
            ) {
                if (isLandscape) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                    ) {
                        ScoreDisplay(score)
                        TimerDisplay(timeLeft)
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                    ScoreDisplay(score)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!gameEnded) {
                        TimerDisplay(timeLeft)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                isCorrect?.let { ShowMessage(it) }
                Spacer(modifier = Modifier.height(16.dp))

                if (!gameEnded) {
                    val (first, second) = if (numPair.first > numPair.second) {
                        numPair.first to numPair.second
                    } else {
                        numPair.second to numPair.first
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp, bottom = 30.dp)
                    ) {
                        NumberImage(number = first)
                        Spacer(modifier = Modifier.width(8.dp))
                        OperationImage(operation = operation)
                        Spacer(modifier = Modifier.width(8.dp))
                        NumberImage(number = second)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        TextField(
                            value = userAnswer,
                            onValueChange = { userAnswer = it },
                            label = { Text(stringResource(R.string.answer_label)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        IconButton(
                            onClick = {
                                val correctAnswer = if (operation == 1) {
                                    first + second
                                } else {
                                    first - second
                                }
                                isCorrect = userAnswer.toIntOrNull() == correctAnswer
                                userAnswer = "" // Clear the text field immediately
                                if (isCorrect == true) {
                                    score++
                                } else {
                                    gameEnded = true
                                }
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = stringResource(R.string.done_icon)
                            )
                        }
                    }
                }

                if (gameEnded) {
                    RestartGame {
                        score = 0
                        numPair = generateRandomNumbers()
                        operation = generateOperation()
                        userAnswer = ""
                        isCorrect = null
                        timeLeft = 30
                        gameEnded = false
                    }
                }
            }
        }
    )
}

fun generateRandomNumbers(): Pair<Int, Int> {
    val num1 = (1..9).random()
    val num2 = (1..9).random()
    return Pair(num1, num2)
}

fun generateOperation(): Int {
    return (1..2).random()
}

@Composable
fun NumberImage(number: Int) {
    val imageRes = when (number) {
        1 -> R.drawable.p1
        2 -> R.drawable.p2
        3 -> R.drawable.p3
        4 -> R.drawable.p4
        5 -> R.drawable.p5
        6 -> R.drawable.p6
        7 -> R.drawable.p7
        8 -> R.drawable.p8
        9 -> R.drawable.p9
        else -> R.drawable.p1 // Default image
    }
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Number $number",
        modifier = Modifier
            .size(110.dp)
            .padding(4.dp)
    )
}

@Composable
fun OperationImage(operation: Int) {
    val imageRes = if (operation == 1) {
        R.drawable.plus
    } else {
        R.drawable.minus
    }
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = if (operation == 1) "Plus" else "Minus",
        modifier = Modifier
            .size(50.dp)
            .padding(2.dp)
    )
}

@Composable
fun ScoreDisplay(score: Int) {
    Text(stringResource(R.string.score_label, score), style = MaterialTheme.typography.titleLarge)
}

@Composable
fun RestartGame(onRestart: () -> Unit) {
    Button(
        onClick = { onRestart() },
        colors = ButtonDefaults.buttonColors(Color(231, 211, 127))
    ) {
        Text(stringResource(R.string.restart_button), style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun ShowMessage(isCorrect: Boolean) {
    val message = if (isCorrect) stringResource(R.string.correct_message) else stringResource(R.string.try_again_message)
    Text(text = message, style = MaterialTheme.typography.titleLarge)
}

@Composable
fun TimerDisplay(timeLeft: Int) {
    Text(stringResource(R.string.time_left_label, timeLeft), style = MaterialTheme.typography.titleLarge)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(onShare: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.math_game_title), fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(54, 94, 50)
        ),
        modifier = Modifier.fillMaxWidth(),
        actions = {
            IconButton(onClick = { onShare() }, colors = IconButtonDefaults.iconButtonColors(Color.Black)) {
                Icon(Icons.Default.Share, contentDescription = "Share")
            }
        }
    )
}

@Composable
fun MainScreen() {
    var gameStarted by rememberSaveable { mutableStateOf(false) }

    if (gameStarted) {
        MathApp()
    } else {
        LandingScreen(onStartClick = { gameStarted = true })
    }
}

/* src/main/resources/static/js/main.js */

// Global State
let currentSessionId = null;
let myNickname = null;
let currentHostToken = null;
let myPlayerId = null;

document.addEventListener('DOMContentLoaded', () => {

    // --- PLAYER PAGE LOGIC ---
    const joinBtn = document.getElementById('btnJoin');
    if (joinBtn) {
        joinBtn.addEventListener('click', async () => {
            const pin = document.getElementById('inputPin').value;
            const nick = document.getElementById('inputNick').value;
            const errorEl = document.getElementById('errorMsg');

            if (!pin || !nick) {
                errorEl.innerText = "Please enter both PIN and Nickname";
                return;
            }

            try {
                // 1. API Call
                const data = await GameLogic.joinSession(pin, nick);

                currentSessionId = data.sessionId;
                myNickname = nick;
                myPlayerId = data.playerId;

                // 2. Update UI
                document.getElementById('displayNick').innerText = nick;
                UI.showScreen('lobbyScreen');

                if (data.currentPlayers && Array.isArray(data.currentPlayers)) {
                    data.currentPlayers.forEach(player => {
                        UI.addPlayerBadge(player.nickname);
                    });
                }

                // 3. Connect WebSocket
                gameSocket.connect(() => {
                    gameSocket.subscribeToSession(currentSessionId, handleGameEvent);
                });

            } catch (err) {
                errorEl.innerText = err.message;
            }
        });
    }

    // --- HOST PAGE LOGIC ---
    const createBtn = document.getElementById('btnCreate');
    if (createBtn) {
        createBtn.addEventListener('click', async () => {
            const quizId = document.getElementById('inputQuizId').value;
            const token = document.getElementById('inputToken').value;
            const errorEl = document.getElementById('hostErrorMsg');

            try {
                currentHostToken = token;
                // 1. API Call
                const data = await GameLogic.createSession(quizId, token);

                currentSessionId = data.sessionId;

                // 2. Update UI
                document.getElementById('displayPin').innerText = data.gamePin;
                UI.showScreen('hostLobbyScreen');

                // 3. Connect WebSocket
                gameSocket.connect(() => {
                    gameSocket.subscribeToSession(currentSessionId, handleGameEvent);
                });

            } catch (err) {
                errorEl.innerText = "Error: " + err.message;
            }
        });
    }

    const btnNextQ = document.getElementById('btnStart'); // "Start Game" becomes "Next Question" logic
    if (btnNextQ) {
        btnNextQ.addEventListener('click', async () => {
            try {
                // Determine if we are Starting Game or Moving to Next Question
                // For simplicity, let's assume the Start button calls 'next-question' 
                // OR we add a specific 'btnNextQuestion' in the Host Question Screen
                await GameLogic.nextQuestion(currentSessionId, currentHostToken);
            } catch (err) {
                console.error(err);
            }
        });
    }

    const btnNextRound = document.getElementById('btnNextQuestion');
    if (btnNextRound) {
        btnNextRound.addEventListener('click', async () => {
            UI.showScreen('hostQuestionScreen'); // Reset UI?
            await GameLogic.nextQuestion(currentSessionId, currentHostToken);
            document.getElementById('hostControls').classList.add('hidden');
        });
    }

    const endBtn = document.getElementById('btnEnd');
    if (endBtn) {
        endBtn.addEventListener('click', async () => {
            try {
                if (!currentHostToken) return alert("Missing Token");

                await GameLogic.endGame(currentSessionId, currentHostToken);
                console.log("End Game command sent!");
            } catch (err) {
                alert("Could not end game: " + err.message);
            }
        });
    }
});

// --- SHARED EVENT HANDLER ---
function handleGameEvent(event) {
    console.log("Event Received:", event);

    switch (event.type) {
        case 'PLAYER_JOINED':
            UI.addPlayerBadge(event.nickname);
            break;
        case 'PLAYER_LEFT':
            UI.removePlayerBadge(event.nickname);
            break;
        case 'GAME_STARTED': // This is usually the first question
            // We can trigger nextQuestion automatically or wait for host
            break;
        case 'NEXT_QUESTION':
            handleNextQuestion(event);
            break;

        case 'ANSWER_RECEIVED':
            // Update Host Counter
            if (document.getElementById('hostAnswerCount')) {
                // The DTO.HostUpdate has 'answersCount'
                document.getElementById('hostAnswerCount').innerText = event.answersCount;
            }
            break;
        case 'GAME_ENDED':
            if (document.getElementById('hostGameOverScreen')) {
                UI.showScreen('hostGameOverScreen');
            } else {
                UI.showScreen('playerGameOverScreen');
            }
            // Optional: Disconnect socket to save resources
            if (typeof gameSocket !== 'undefined') gameSocket.disconnect();
            break;
    }
}

function handleNextQuestion(data) {
    // 1. Determine Role
    const isHost = !!document.getElementById('hostQuestionScreen');

    if (isHost) {
        // --- HOST VIEW ---
        UI.showScreen('hostQuestionScreen');
        document.getElementById('hostQuestionText').innerText = data.text;
        document.getElementById('qCurrent').innerText = data.currentQuestionNumber;
        document.getElementById('qTotal').innerText = data.totalQuestions;
        document.getElementById('hostAnswerCount').innerText = "0";
        document.getElementById('hostTotalPlayers').innerText = document.getElementById('playerCount').innerText;

        // Render Read-Only Options
        GameUI.renderOptions(data.options, true, null);

        // Start Timer
        GameUI.startTimer(data.timeLimitSeconds, 'hostTimer');

    } else {
        // --- PLAYER VIEW ---
        UI.showScreen('playerQuestionScreen');
        document.getElementById('pQNum').innerText = data.currentQuestionNumber;

        // Render Clickable Options
        GameUI.renderOptions(data.options, false, async (selectedOptionId) => {
            // Player Clicked an Option
            try {
                // 1. Disable buttons to prevent double click
                document.querySelectorAll('.option-btn').forEach(b => b.disabled = true);

                // 2. Send API Request
                // (Assuming we saved myPlayerId during Join)
                const result = await GameLogic.submitAnswer(currentSessionId, myPlayerId, data.questionId, selectedOptionId);

                // 3. Show Result Screen
                UI.showScreen('playerResultScreen');

                const msgBox = document.getElementById('resultBox');
                const msgText = document.getElementById('resultMessage');
                const scoreText = document.getElementById('pScore');

                msgText.innerText = result.isCorrect ? "Correct! 🎉" : "Wrong 😞";
                msgBox.className = "feedback-box " + (result.isCorrect ? "feedback-correct" : "feedback-wrong");
                scoreText.innerText = result.totalScore;

            } catch (err) {
                alert("Error: " + err.message);
            }
        });

        // Start Timer
        GameUI.startTimer(data.timeLimitSeconds, 'pTimer');
    }
}
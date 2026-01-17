/* src/main/resources/static/js/main.js */

// Global State
let currentSessionId = null;
let myNickname = null;
let currentHostToken = null;

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

                // 2. Update UI
                document.getElementById('displayNick').innerText = nick;
                UI.showScreen('lobbyScreen');

                // 3. Connect WebSocket
                gameSocket.connect(() => {
                    UI.addPlayerBadge(myNickname); // Add myself
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

        document.getElementById('btnStart').addEventListener('click', async () => {
            try {
                if (!currentHostToken) {
                    alert("Missing Host Token!");
                    return;
                }
                await GameLogic.startGame(currentSessionId, currentHostToken);
                console.log("Game Started command sent!");
            } catch (err) {
                alert("Could not start game: " + err.message);
            }
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
        case 'GAME_STARTED':
            // Logic differs slightly for Host vs Player
            if (document.getElementById('hostGameScreen')) {
                UI.showScreen('hostGameScreen');
            } else {
                UI.showScreen('playerGameScreen');
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
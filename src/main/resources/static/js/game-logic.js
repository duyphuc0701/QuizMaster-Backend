/* src/main/resources/static/js/game-logic.js */

const API_BASE = '/api/sessions';

const GameLogic = {
    // HOST: Create a new session
    async createSession(quizId, token) {
        if (!token) {
            throw new Error("You must provide a JWT Token to host!");
        }

        const response = await fetch(API_BASE, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token // <--- THIS IS KEY
            },
            body: JSON.stringify({ quizId: quizId })
        });

        if (!response.ok) {
            // Helper to read the error message from server
            const text = await response.text();
            throw new Error("Server Error: " + text);
        }

        return await response.json();
    },

    // HOST: Start the game
    async startGame(sessionId, token) {
        const response = await fetch(`${API_BASE}/${sessionId}/start`, {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });
        if (!response.ok) throw new Error("Failed to start game");
        return await response.json();
    },

    async endGame(sessionId, token) {
        const response = await fetch(`${API_BASE}/${sessionId}/end`, {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (!response.ok) {
            const text = await response.text();
            throw new Error(text || "Failed to end game");
        }

        return await response.json();
    },

    // PLAYER: Join a session
    async joinSession(pin, nickname) {
        const response = await fetch(`${API_BASE}/join`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ gamePin: pin, nickname: nickname })
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Failed to join");
        }
        return await response.json();
    }
};

const UI = {
    // Helper to switch visible screens (divs)
    showScreen(screenId) {
        document.querySelectorAll('.card > div').forEach(div => div.classList.add('hidden'));
        document.getElementById(screenId).classList.remove('hidden');
    },

    // Helper to add a player badge to the grid
    addPlayerBadge(nickname) {
        const grid = document.getElementById('playerGrid');
        if (!document.getElementById(`badge-${nickname}`)) {
            const badge = document.createElement('div');
            badge.className = 'player-badge';
            badge.id = `badge-${nickname}`;
            badge.innerText = nickname;
            grid.appendChild(badge);
            this.updateCount(1);
        }
    },

    // Helper to remove a player badge
    removePlayerBadge(nickname) {
        const badge = document.getElementById(`badge-${nickname}`);
        if (badge) {
            badge.remove();
            this.updateCount(-1);
        }
    },

    updateCount(change) {
        const countEl = document.getElementById('playerCount');
        if (countEl) {
            let current = parseInt(countEl.innerText) || 0;
            countEl.innerText = Math.max(0, current + change);
        }
    }
};
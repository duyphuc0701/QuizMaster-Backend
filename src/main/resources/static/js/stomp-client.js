/* src/main/resources/static/js/stomp-client.js */

class GameStompClient {
    constructor() {
        this.stompClient = null;
    }

    /**
     * Connects to the WebSocket endpoint
     * @param {Function} onConnectedCallback - Function to run once connected
     */
    connect(onConnectedCallback) {
        // Must match your Spring Boot config: registry.addEndpoint("/ws")
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);

        // Disable debug logs for cleaner console
        this.stompClient.debug = null;

        this.stompClient.connect({}, (frame) => {
            console.log('✅ Connected to WebSocket');
            if (onConnectedCallback) onConnectedCallback(frame);
        }, (error) => {
            console.error('❌ WebSocket Error:', error);
            alert("Connection lost. Please refresh.");
        });
    }

    /**
     * Subscribes to a specific game session topic
     * @param {String} sessionId 
     * @param {Function} onMessageCallback - Function to run when message arrives
     */
    subscribeToSession(sessionId, onMessageCallback) {
        if (!this.stompClient || !this.stompClient.connected) {
            console.error("Cannot subscribe: Client not connected.");
            return;
        }

        const topic = `/topic/session/${sessionId}/players`;
        console.log(`📡 Subscribing to: ${topic}`);

        this.stompClient.subscribe(topic, (message) => {
            const payload = JSON.parse(message.body);
            onMessageCallback(payload);
        });
    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect();
        }
    }
}

// Export a single instance for usage
const gameSocket = new GameStompClient();
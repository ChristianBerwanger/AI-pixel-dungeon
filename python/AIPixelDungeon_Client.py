import socket
import json
import time

class AIPixelDungeon_Client:
    def __init__(self, host='localhost', port=5000):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
            self.sock.connect((host, port))
            print(f"Connected to Game on port {port}")
        except ConnectionRefusedError:
            print("Could not connect. Run Game first.")
            exit()

    def send_command(self, command_dict):
        # 1. Convert dict to JSON string with newline
        msg = json.dumps(command_dict) + "\n"
        self.sock.sendall(msg.encode('utf-8'))

        # 2. Wait for response (Blocking)
        response = self.sock.recv(4096).decode('utf-8')
        return json.loads(response)

client = AIPixelDungeon_Client()
while True:
    response = client.send_command({"action": "MOVE"})
    print("Game Response:", response)

    time.sleep(1)
